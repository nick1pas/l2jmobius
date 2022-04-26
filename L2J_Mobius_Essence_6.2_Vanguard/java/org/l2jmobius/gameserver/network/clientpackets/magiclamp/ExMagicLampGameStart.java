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
package org.l2jmobius.gameserver.network.clientpackets.magiclamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.MagicLampData;
import org.l2jmobius.gameserver.enums.LampMode;
import org.l2jmobius.gameserver.enums.LampType;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.MagicLampDataHolder;
import org.l2jmobius.gameserver.model.holders.MagicLampHolder;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jmobius.gameserver.network.serverpackets.magiclamp.ExMagicLampExpInfoUI;
import org.l2jmobius.gameserver.network.serverpackets.magiclamp.ExMagicLampGameInfoUI;
import org.l2jmobius.gameserver.network.serverpackets.magiclamp.ExMagicLampGameResult;

/**
 * @author L2CCCP
 */
public class ExMagicLampGameStart implements IClientIncomingPacket
{
	private int _count;
	private byte _mode;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_count = packet.readD(); // MagicLampGameCCount
		_mode = (byte) packet.readC(); // GameMode
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final LampMode lampMode = LampMode.getByMode(_mode);
		final int consume = calcConsume(lampMode, _count);
		final int have = player.getLampCount();
		if (have >= consume)
		{
			final Map<LampType, MagicLampHolder> rewards = new HashMap<>();
			for (int x = _count; x > 0; x--)
			{
				final List<MagicLampDataHolder> available = MagicLampData.getInstance().getLamps().stream().filter(lamp -> (lamp.getMode() == lampMode) && chance(lamp.getChance())).collect(Collectors.toList());
				final MagicLampDataHolder random = getRandom(available);
				if (random != null)
				{
					rewards.computeIfAbsent(random.getType(), list -> new MagicLampHolder(random)).inc();
				}
			}
			
			// Consume.
			player.setLampCount(have - consume);
			if (lampMode == LampMode.GREATER)
			{
				player.destroyItemByItemId("Magic Lamp", 91641, Config.MAGIC_LAMP_GREATER_SAYHA_CONSUME_COUNT * _count, player, true);
			}
			
			// Reward.
			rewards.values().forEach(lamp -> player.addExpAndSp(lamp.getExp(), lamp.getSp()));
			
			// Update.
			final int left = player.getLampCount();
			player.sendPacket(new ExMagicLampGameInfoUI(player, _mode, left > consume ? _count : left));
			player.sendPacket(new ExMagicLampExpInfoUI(player));
			player.sendPacket(new ExMagicLampGameResult(rewards.values()));
		}
	}
	
	private boolean chance(double chance)
	{
		return (chance > 0) && ((chance >= 100) || (Rnd.get(100d) <= chance));
	}
	
	private <E> E getRandom(List<E> list)
	{
		if (list.isEmpty())
		{
			return null;
		}
		if (list.size() == 1)
		{
			return list.get(0);
		}
		return list.get(Rnd.get(list.size()));
	}
	
	private int calcConsume(LampMode mode, int count)
	{
		switch (mode)
		{
			case NORMAL:
			{
				return Config.MAGIC_LAMP_CONSUME_COUNT * count;
			}
			case GREATER:
			{
				return Config.MAGIC_LAMP_GREATER_CONSUME_COUNT * count;
			}
			default:
			{
				return 0;
			}
		}
	}
}
