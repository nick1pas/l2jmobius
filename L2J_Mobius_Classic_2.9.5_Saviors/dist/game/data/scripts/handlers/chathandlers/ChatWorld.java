/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.chathandlers;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.enums.PlayerCondOverride;
import org.l2jmobius.gameserver.handler.IChatHandler;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.CreatureSay;
import org.l2jmobius.gameserver.network.serverpackets.ExWorldChatCnt;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * World chat handler.
 * @author UnAfraid
 */
public class ChatWorld implements IChatHandler
{
	private static final Map<Integer, Instant> REUSE = new ConcurrentHashMap<>();
	
	private static final ChatType[] CHAT_TYPES =
	{
		ChatType.WORLD,
	};
	
	@Override
	public void handleChat(ChatType type, Player activeChar, String target, String text)
	{
		if (!Config.ENABLE_WORLD_CHAT)
		{
			return;
		}
		
		final Instant now = Instant.now();
		if (!REUSE.isEmpty())
		{
			REUSE.values().removeIf(now::isAfter);
		}
		
		if (activeChar.getLevel() < Config.WORLD_CHAT_MIN_LEVEL)
		{
			final SystemMessage msg = new SystemMessage(SystemMessageId.YOU_MUST_BE_LV_S1_OR_HIGHER_TO_USE_WORLD_CHAT_YOU_CAN_ALSO_USE_IT_WITH_VIP_BENEFITS);
			msg.addInt(Config.WORLD_CHAT_MIN_LEVEL);
			activeChar.sendPacket(msg);
		}
		else if (activeChar.isChatBanned() && Config.BAN_CHAT_CHANNELS.contains(type))
		{
			activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER);
		}
		else if (Config.JAIL_DISABLE_CHAT && activeChar.isJailed() && !activeChar.canOverrideCond(PlayerCondOverride.CHAT_CONDITIONS))
		{
			activeChar.sendPacket(SystemMessageId.CHATTING_IS_CURRENTLY_PROHIBITED);
		}
		else if (activeChar.getWorldChatUsed() >= activeChar.getWorldChatPoints())
		{
			activeChar.sendPacket(SystemMessageId.YOU_USED_WORLD_CHAT_UP_TO_TODAY_S_LIMIT_THE_USAGE_COUNT_OF_WORLD_CHAT_IS_RESET_EVERY_DAY_AT_6_30);
		}
		else
		{
			// Verify if player is not spaming.
			if (Config.WORLD_CHAT_INTERVAL.getSeconds() > 0)
			{
				final Instant instant = REUSE.getOrDefault(activeChar.getObjectId(), null);
				if ((instant != null) && instant.isAfter(now))
				{
					final Duration timeDiff = Duration.between(now, instant);
					final SystemMessage msg = new SystemMessage(SystemMessageId.YOU_HAVE_S1_SEC_UNTIL_YOU_ARE_ABLE_TO_USE_WORLD_CHAT);
					msg.addInt((int) timeDiff.getSeconds());
					activeChar.sendPacket(msg);
					return;
				}
			}
			
			final CreatureSay cs = new CreatureSay(activeChar, type, activeChar.getName(), text);
			if (Config.FACTION_SYSTEM_ENABLED && Config.FACTION_SPECIFIC_CHAT)
			{
				if (activeChar.isGood())
				{
					for (Player player : World.getInstance().getAllGoodPlayers())
					{
						if (player.isNotBlocked(activeChar))
						{
							player.sendPacket(cs);
						}
					}
				}
				if (activeChar.isEvil())
				{
					for (Player player : World.getInstance().getAllEvilPlayers())
					{
						if (player.isNotBlocked(activeChar))
						{
							player.sendPacket(cs);
						}
					}
				}
			}
			else
			{
				for (Player player : World.getInstance().getPlayers())
				{
					if (player.isNotBlocked(activeChar))
					{
						player.sendPacket(cs);
					}
				}
			}
			
			activeChar.setWorldChatUsed(activeChar.getWorldChatUsed() + 1);
			activeChar.sendPacket(new ExWorldChatCnt(activeChar));
			if (Config.WORLD_CHAT_INTERVAL.getSeconds() > 0)
			{
				REUSE.put(activeChar.getObjectId(), now.plus(Config.WORLD_CHAT_INTERVAL));
			}
		}
	}
	
	@Override
	public ChatType[] getChatTypeList()
	{
		return CHAT_TYPES;
	}
}