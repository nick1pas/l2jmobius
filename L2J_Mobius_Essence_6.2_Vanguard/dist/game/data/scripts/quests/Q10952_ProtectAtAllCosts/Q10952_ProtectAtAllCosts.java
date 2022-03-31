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
package quests.Q10952_ProtectAtAllCosts;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.holders.NpcLogListHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Protect At All Costs (10952)
 * @author Ayurechko
 * @Notee: Based on NA server on March, 31 2022
 */
public class Q10952_ProtectAtAllCosts extends Quest
{
	// NPC
	private static final int TANAI = 30602;
	// Monsters
	private static final int KASHA_WOLF = 20475;
	private static final int KASHA_TIMBER_WOLF = 20477; // NOTE: Kasha Forest Wolf in old client
	private static final int GOBLIN_TOMB_RAIDER = 20319;
	private static final int RAKECLAW_IMP_HUNTER = 20312;
	// Items
	private static final ItemHolder SOE_TO_TANAI = new ItemHolder(97230, 1);
	private static final ItemHolder SOE_NOVICE = new ItemHolder(10650, 10);
	private static final ItemHolder RING_NOVICE = new ItemHolder(49041, 2);
	private static final ItemHolder EARRING_NOVICE = new ItemHolder(49040, 2);
	private static final ItemHolder NECKLACE_NOVICE = new ItemHolder(49039, 1);
	// Misc
	private static final int MIN_LEVEL = 2;
	private static final int MAX_LEVEL = 15;
	private static final String KILL_COUNT_VAR = "KillCount";
	
	public Q10952_ProtectAtAllCosts()
	{
		super(10952);
		addStartNpc(TANAI);
		addTalkId(TANAI);
		addKillId(KASHA_WOLF, KASHA_TIMBER_WOLF, GOBLIN_TOMB_RAIDER, RAKECLAW_IMP_HUNTER);
		addCondMinLevel(MIN_LEVEL, "no_lvl.htm");
		addCondMaxLevel(MAX_LEVEL, "no_lvl.htm");
		setQuestNameNpcStringId(NpcStringId.LV_2_15_PROTECT_AT_ALL_COSTS);
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
			case "30602-00.htm":
			{
				if (qs.isStarted())
				{
					addExpAndSp(player, 260000, 6000);
					giveItems(player, SOE_NOVICE);
					giveItems(player, RING_NOVICE);
					giveItems(player, EARRING_NOVICE);
					giveItems(player, NECKLACE_NOVICE);
					qs.exitQuest(false, true);
					htmltext = event;
					break;
				}
			}
			case "TELEPORT_TO_VALLEY_OF_HEROES":
			{
				player.teleToLocation(-40897, -119022, -1912);
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
			if (qs.isCond(1))
			{
				htmltext = "30602-04.htm";
			}
			else if (qs.isCond(2))
			{
				htmltext = "30602-05.htm";
			}
		}
		else if (qs.isCompleted())
		{
			htmltext = getAlreadyCompletedMsg(player);
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
			if (killCount < 20)
			{
				qs.set(KILL_COUNT_VAR, killCount);
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				sendNpcLogList(killer);
				
			}
			else
			{
				qs.setCond(2, true);
				qs.unset(KILL_COUNT_VAR);
				giveItems(killer, SOE_TO_TANAI);
				showOnScreenMsg(killer, NpcStringId.THE_TRAINING_IN_OVER_USE_A_SCROLL_OF_ESCAPE_IN_YOUR_INVENTORY_TO_GO_BACK_TO_TANAI, ExShowScreenMessage.TOP_CENTER, 10000);
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
			holder.add(new NpcLogListHolder(NpcStringId.SUBJUGATION_IN_THE_VALLEY_OF_HEROES.getId(), true, qs.getInt(KILL_COUNT_VAR)));
			return holder;
		}
		return super.getNpcLogList(player);
	}
}
