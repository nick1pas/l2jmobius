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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.w3c.dom.Document;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.instancemanager.events.BalthusEventManager;
import org.l2jmobius.gameserver.instancemanager.events.BalthusEventManager.BalthusEventHolder;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.Id;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogout;
import org.l2jmobius.gameserver.model.events.impl.item.OnItemUse;
import org.l2jmobius.gameserver.model.holders.ItemChanceHolder;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.holders.ItemSkillHolder;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.quest.LongTimeEvent;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.network.serverpackets.balthusevent.ExBalthusEvent;

/**
 * @author Index
 */
public class BalthusFestival extends LongTimeEvent implements IXmlReader
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
			load();
			BalthusEventManager.getInstance().init();
		}
	}
	
	@Override
	public synchronized void load()
	{
		parseDatapackFile("data/scripts/events/BalthusFestival/rewards.xml");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		final AtomicInteger i = new AtomicInteger();
		forEach(doc, "list", listNode ->
		{
			final StatSet set = new StatSet(parseAttributes(listNode));
			if (BalthusEventManager.getInstance().getMinLevel() == 0)
			{
				BalthusEventManager.getInstance().setEasyMode(set.getBoolean("easyMode", false));
				BalthusEventManager.getInstance().setMinLevel(set.getInt("minLevel", 85));
				BalthusEventManager.getInstance().setConsolation(new ItemHolder(set.getInt("id", 49783), set.getInt("count", 100)));
				BalthusEventManager.getInstance().setMailSubject(set.getString("mailSubject", "Balthus Knight Lottery"));
				BalthusEventManager.getInstance().setMailContent(set.getString("mailContent", "You win reward in Balthus Event!"));
			}
			
			forEach(listNode, "reward", reward ->
			{
				final AtomicInteger j = new AtomicInteger();
				final Map<Integer, Map<ItemChanceHolder, Double>> tempRewardList = new HashMap<>();
				final Map<Integer, Integer> rewardTime = new HashMap<>();
				final StatSet rewardSet = new StatSet(parseAttributes(reward));
				rewardTime.put(rewardSet.getInt("from"), rewardSet.getInt("to", rewardSet.getInt("from")));
				forEach(reward, "items", itemNode ->
				{
					forEach(itemNode, "item", item ->
					{
						final Map<ItemChanceHolder, Double> tempChanceRewardList = new HashMap<>();
						j.getAndIncrement();
						final StatSet itemSet = new StatSet(parseAttributes(item));
						ItemChanceHolder itemChanceHolder = new ItemChanceHolder(itemSet.getInt("id", 57), itemSet.getDouble("chance", 100), itemSet.getInt("count", 100));
						tempChanceRewardList.put(itemChanceHolder, itemSet.getDouble("lotteryChance", 0.0));
						tempRewardList.put(j.intValue(), tempChanceRewardList);
					});
					i.getAndIncrement();
					BalthusEventManager.getInstance().addTemplate(i.intValue(), new BalthusEventHolder(rewardTime, tempRewardList));
				});
			});
		});
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
	
	@RegisterEvent(EventType.ON_ITEM_USE)
	@RegisterType(ListenerRegisterType.ITEM)
	@Id(28609) // Balthus Knights' High-grade Mark
	@Id(47385) // Lucky Gift Coupon (2-hour)
	@Id(48223) // Lucky Gift Coupon (3-hour)
	@Id(48224) // Lucky Gift Coupon (6-hour)
	@Id(48853) // Lucky Gift Coupon (8-hour)
	@Id(60009) // Festival Fairy's Lucky Ticket
	public void onItemUse(OnItemUse event)
	{
		if (!isEventPeriod())
		{
			return;
		}
		
		final List<ItemSkillHolder> skills = event.getItem().getTemplate().getAllSkills();
		if (skills != null)
		{
			final Player player = event.getPlayer();
			for (SkillHolder skill : skills)
			{
				if (SKILLS.contains(skill))
				{
					startQuestTimer("balthusEventBuff" + player.getObjectId(), skill.getSkill().getAbnormalTime() * 1000, null, player);
					BalthusEventManager.getInstance().addPlayer(player);
					player.sendPacket(new ExBalthusEvent(player));
					break;
				}
			}
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
		if (player == null)
		{
			return;
		}
		
		cancelQuestTimer("balthusEventBuff" + player.getObjectId(), null, player);
		BalthusEventManager.getInstance().removePlayer(player);
	}
	
	public static void main(String[] args)
	{
		new BalthusFestival();
	}
}
