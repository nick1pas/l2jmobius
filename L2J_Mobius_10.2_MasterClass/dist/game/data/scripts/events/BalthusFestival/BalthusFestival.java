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
package events.BalthusFestival;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.gameserver.instancemanager.events.BalthusEventManager;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.OnCreatureSkillFinishCast;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.quest.LongTimeEvent;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.network.serverpackets.balthusevent.ExBalthusEvent;

/**
 * @author Index
 */
public class BalthusFestival extends LongTimeEvent
{
	// NPC
	private static final int FESTIVAL_FAIRY = 34330;
	// Item
	private static final ItemHolder GOOD_LUCK_BAG = new ItemHolder(60011, 1); // Festival Fairy's Good Luck Bag
	// Skills
	private static final List<SkillHolder> SKILLS = new ArrayList<>(8);
	static
	{
		SKILLS.add(new SkillHolder(29441, 1)); // Fairy_Coupon_1_Hour
		SKILLS.add(new SkillHolder(39171, 1)); // Fairy_Coupon_2_Hour
		SKILLS.add(new SkillHolder(39171, 2)); // Fairy_Coupon_3_Hour
		SKILLS.add(new SkillHolder(39171, 3)); // Fairy_Coupon_6_Hour
		SKILLS.add(new SkillHolder(39171, 4)); // Fairy_Coupon_8_Hour
		SKILLS.add(new SkillHolder(39171, 5)); // Fairy_Coupon_Unlimited_Hour
		SKILLS.add(new SkillHolder(27859, 1)); // Balthus_Coupon_2_Hour
		SKILLS.add(new SkillHolder(48853, 1)); // Balthus_Coupon_2_Hour
	}
	// Misc
	private static final String BALTHUS_BAG_VAR = "BALTHUS_BAG";
	
	private BalthusFestival()
	{
		addStartNpc(FESTIVAL_FAIRY);
		addFirstTalkId(FESTIVAL_FAIRY);
		addTalkId(FESTIVAL_FAIRY);
		if (isEventPeriod())
		{
			BalthusEventManager.getInstance();
		}
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		if (!isEventPeriod())
		{
			return;
		}
		
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		for (SkillHolder skill : SKILLS)
		{
			final BuffInfo buff = player.getEffectList().getBuffInfoBySkillId(skill.getSkillId());
			if (buff != null)
			{
				cancelQuestTimer("balthusEventBuff" + player.getObjectId(), null, player);
				startQuestTimer("balthusEventBuff" + player.getObjectId(), buff.getTime() * 1000, null, player);
				BalthusEventManager.getInstance().addPlayer(player);
			}
		}
		player.sendPacket(new ExBalthusEvent(player));
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGOUT)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogout(OnPlayerLogout event)
	{
		if (!isEventPeriod())
		{
			return;
		}
		
		final Player player = event.getPlayer();
		if ((player == null))
		{
			return;
		}
		
		cancelQuestTimer("balthusEventBuff" + player.getObjectId(), null, player);
		BalthusEventManager.getInstance().removePlayer(player);
	}
	
	@RegisterEvent(EventType.ON_CREATURE_SKILL_FINISH_CAST)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onCreatureSkillFinishCast(OnCreatureSkillFinishCast event)
	{
		if (!isEventPeriod())
		{
			return;
		}
		
		final Player player = event.getCaster().getActingPlayer();
		if (player == null)
		{
			return;
		}
		
		for (SkillHolder skill : SKILLS)
		{
			if (event.getSkill() == skill.getSkill())
			{
				startQuestTimer("balthusEventBuff" + player.getObjectId(), skill.getSkill().getAbnormalTime() * 1000, null, player);
				BalthusEventManager.getInstance().addPlayer(player);
				player.sendPacket(new ExBalthusEvent(player));
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (!isEventPeriod())
		{
			return "Balthus Event disabled.";
		}
		
		if (event.equals("balthusEventBuff" + player.getObjectId()))
		{
			BalthusEventManager.getInstance().removePlayer(player);
			player.sendPacket(new ExBalthusEvent(player));
		}
		
		if (event.equals("get_event_bag"))
		{
			if (player.getLevel() < BalthusEventManager.getInstance().getMinLevel())
			{
				return getHtm(player, "34330-7.htm").replace("%require_level%", String.valueOf(BalthusEventManager.getInstance().getMinLevel()));
			}
			else if (player.getVariables().getBoolean(BALTHUS_BAG_VAR, false))
			{
				return "34330-3.htm";
			}
			else
			{
				player.getVariables().set(BALTHUS_BAG_VAR, true);
				player.addItem("Balthus Event", GOOD_LUCK_BAG.getId(), GOOD_LUCK_BAG.getCount(), null, true);
				return "34330-2.htm";
			}
		}
		else if (event.equals("34330.htm"))
		{
			return event;
		}
		else if (event.equals("34330-1.htm"))
		{
			return event;
		}
		else if (event.equals("34330-4.htm"))
		{
			return event;
		}
		else if (event.startsWith("exchange_coupon_"))
		{
			switch (event.substring(16))
			{
				case ("1"):
				{
					if (!player.destroyItemByItemId("Destroy Coupon", 81726, 1, null, true))
					{
						return "34330-5.htm";
					}
					player.addItem("Balthus Coupon", 81711, 1, null, true);
					return "34330-6.htm";
				}
				case ("2"):
				{
					if (!player.destroyItemByItemId("Destroy Coupon", 81726, 1, null, true))
					{
						return "34330-5.htm";
					}
					player.addItem("Balthus Coupon", 81710, 1, null, true);
					return "34330-6.htm";
				}
				case ("3"):
				{
					if (!player.destroyItemByItemId("Destroy Coupon", 81726, 1, null, true))
					{
						return "34330-5.htm";
					}
					player.addItem("Balthus Coupon", 81709, 1, null, true);
					return "34330-6.htm";
				}
			}
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getId() + ".htm";
	}
	
	public static void main(String[] args)
	{
		new BalthusFestival();
	}
}
