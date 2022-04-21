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
package quests.Q00617_GatherTheFlames;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.util.Util;

public class Q00617_GatherTheFlames extends Quest
{
	// NPCs
	private static final int HILDA = 31271;
	private static final int VULCAN = 31539;
	private static final int ROONEY = 32049;
	// Items
	private static final int TORCH = 7264;
	// Drop chances
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(21381, 510000);
		CHANCES.put(21653, 510000);
		CHANCES.put(21387, 530000);
		CHANCES.put(21655, 530000);
		CHANCES.put(21390, 560000);
		CHANCES.put(21656, 690000);
		CHANCES.put(21389, 550000);
		CHANCES.put(21388, 530000);
		CHANCES.put(21383, 510000);
		CHANCES.put(21392, 560000);
		CHANCES.put(21382, 600000);
		CHANCES.put(21654, 520000);
		CHANCES.put(21384, 640000);
		CHANCES.put(21394, 510000);
		CHANCES.put(21395, 560000);
		CHANCES.put(21385, 520000);
		CHANCES.put(21391, 550000);
		CHANCES.put(21393, 580000);
		CHANCES.put(21657, 570000);
		CHANCES.put(21386, 520000);
		CHANCES.put(21652, 490000);
		CHANCES.put(21378, 490000);
		CHANCES.put(21376, 480000);
		CHANCES.put(21377, 480000);
		CHANCES.put(21379, 590000);
		CHANCES.put(21380, 490000);
	}
	// Rewards
	private static final int[] REWARD =
	{
		6881,
		6883,
		6885,
		6887,
		6891,
		6893,
		6895,
		6897,
		6899,
		7580
	};
	
	public Q00617_GatherTheFlames()
	{
		super(617);
		registerQuestItems(TORCH);
		addStartNpc(VULCAN, HILDA);
		addTalkId(VULCAN, HILDA, ROONEY);
		addKillId(CHANCES.keySet());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("31539-03.htm") || event.equals("31271-03.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("31539-05.htm"))
		{
			if (getQuestItemsCount(player, TORCH) >= 1000)
			{
				htmltext = "31539-07.htm";
				takeItems(player, TORCH, 1000);
				giveItems(player, REWARD[getRandom(REWARD.length)], 1);
			}
		}
		else if (event.equals("31539-08.htm"))
		{
			takeItems(player, TORCH, -1);
			st.exitQuest(true, true);
		}
		else if (Util.isDigit(event))
		{
			if (getQuestItemsCount(player, TORCH) >= 1200)
			{
				htmltext = "32049-03.htm";
				takeItems(player, TORCH, 1200);
				giveItems(player, Integer.parseInt(event), 1);
			}
			else
			{
				htmltext = "32049-02.htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg(player);
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				htmltext = npc.getId() + ((player.getLevel() >= 74) ? "-01.htm" : "-02.htm");
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case VULCAN:
					{
						htmltext = (getQuestItemsCount(player, TORCH) >= 1000) ? "31539-04.htm" : "31539-05.htm";
						break;
					}
					case HILDA:
					{
						htmltext = "31271-04.htm";
						break;
					}
					case ROONEY:
					{
						htmltext = (getQuestItemsCount(player, TORCH) >= 1200) ? "32049-01.htm" : "32049-02.htm";
						break;
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Player partyMember = getRandomPartyMemberState(killer, State.STARTED);
		if (partyMember == null)
		{
			return super.onKill(npc, killer, isSummon);
		}
		
		if (getRandom(1000) < CHANCES.get(npc.getId()))
		{
			giveItems(partyMember, TORCH, 2);
		}
		else
		{
			giveItems(partyMember, TORCH, 1);
		}
		playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		
		return super.onKill(npc, killer, isSummon);
	}
}