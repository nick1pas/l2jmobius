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
package quests.Q10953_ValiantOrcs;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.data.xml.CategoryData;
import org.l2jmobius.gameserver.enums.CategoryType;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.classchange.ExClassChangeSetAlarm;

/**
 * Valiant Orcs (10953)
 * @author Elison
 * @Notee: Based on NA server on April, 1 2022
 */
public class Q10953_ValiantOrcs extends Quest
{
	// NPCs
	private static final int TANAI = 30602;
	private static final int GANTAKI_ZU_URUTU = 30587;
	// Monsters
	private static final int KASHA_SPIDER = 20474;
	private static final int KASHA_BLADE_SPIDER = 20478;
	private static final int MARAKU_WEREVOLF_CHIEFTAIN = 20364;
	private static final int EVIL_EYE_PATROL = 20428;
	// Items
	private static final ItemHolder SOE_TO_GANTAKI_ZU_URUTU = new ItemHolder(97231, 1);
	private static final ItemHolder SOE_NOVICE = new ItemHolder(10650, 20);
	private static final ItemHolder SPIRIT_ORE = new ItemHolder(3031, 50);
	private static final ItemHolder HP_POTS = new ItemHolder(91912, 50);
	private static final ItemHolder XP_GROWTH_SCROLL = new ItemHolder(49674, 1);
	// Misc
	private static final int MIN_LEVEL = 15;
	private static final int MAX_LEVEL = 20;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10953_ValiantOrcs()
	{
		super(10953);
		addStartNpc(TANAI);
		addTalkId(TANAI, GANTAKI_ZU_URUTU);
		addKillId(KASHA_SPIDER, KASHA_BLADE_SPIDER, MARAKU_WEREVOLF_CHIEFTAIN, EVIL_EYE_PATROL);
		addCondMinLevel(MIN_LEVEL, "no_lvl.htm");
		addCondMaxLevel(MAX_LEVEL, "no_lvl.htm");
		setQuestNameNpcStringId(NpcStringId.LV_15_20_VALIANT_ORCS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return htmltext;
		}
		
		switch (event)
		{
			case "30602-01.htm":
			case "30602-02.htm":
			case "30602-03.htm":
			{
				htmltext = event;
				break;
			}
			case "30602-04.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "30587-00.htm":
			{
				if (qs.isStarted())
				{
					addExpAndSp(player, 600000, 13500);
					giveItems(player, SOE_NOVICE);
					giveItems(player, SPIRIT_ORE);
					giveItems(player, HP_POTS);
					giveItems(player, XP_GROWTH_SCROLL);
					giveStoryBuffReward(npc, player);
					if (CategoryData.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId()))
					{
						showOnScreenMsg(player, NpcStringId.YOU_VE_FINISHED_THE_TUTORIAL_TAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER, ExShowScreenMessage.TOP_CENTER, 10000);
						player.sendPacket(ExClassChangeSetAlarm.STATIC_PACKET);
					}
					qs.exitQuest(false, true);
				}
				break;
			}
			case "TELEPORT_TO_THE_NORTH_OF_IMMORTAL_PLATEAU":
			{
				giveStoryBuffReward(npc, player);
				player.teleToLocation(13138, -131638, -1312);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs.isCreated())
		{
			htmltext = "30602-01.htm";
		}
		else if (qs.isStarted())
		{
			switch (npc.getId())
			{
				case TANAI:
				{
					if (qs.isCond(1))
					{
						htmltext = "30602-04.htm";
					}
					break;
				}
				case GANTAKI_ZU_URUTU:
				{
					if (qs.isCond(2))
					{
						htmltext = "30587-06.htm";
					}
					break;
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if ((qs != null) && qs.isCond(1))
		{
			final int killCount = qs.getInt(KILL_COUNT_VAR) + 1;
			if (killCount < 30)
			{
				qs.set(KILL_COUNT_VAR, killCount);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				sendNpcLogList(killer);
			}
			else
			{
				qs.setCond(2, true);
				qs.unset(KILL_COUNT_VAR);
				giveItems(killer, SOE_TO_GANTAKI_ZU_URUTU);
				showOnScreenMsg(killer, NpcStringId.THE_TRAINING_IN_OVER_USE_A_SCROLL_OF_ESCAPE_IN_YOUR_INVENTORY_TO_GO_BACK_TO_GANTAKI_ZU_URUTU, ExShowScreenMessage.TOP_CENTER, 10000);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public Set<NpcLogListHolder> getNpcLogList(Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if ((qs != null) && qs.isCond(1))
		{
			final Set<NpcLogListHolder> holder = new HashSet<>();
			holder.add(new NpcLogListHolder(NpcStringId.SUBJUGATION_IN_THE_NORTHERN_AREA_OF_THE_IMMORTAL_PLATEAU.getId(), true, qs.getInt(KILL_COUNT_VAR)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!CategoryData.getInstance().isInCategory(CategoryType.FIRST_CLASS_GROUP, player.getClassId().getId()))
		{
			return;
		}
		
		final QuestState qs = getQuestState(player, false);
		if (Config.DISABLE_TUTORIAL || ((qs != null) && qs.isCompleted()))
		{
			player.sendPacket(ExClassChangeSetAlarm.STATIC_PACKET);
		}
	}
}