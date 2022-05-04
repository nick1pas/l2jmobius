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
package quests.Q00353_PowerOfDarkness;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00353_PowerOfDarkness extends Quest
{
	// Item
	private static final int STONE = 5862;
	
	public Q00353_PowerOfDarkness()
	{
		super(353);
		registerQuestItems(STONE);
		addStartNpc(31044); // Galman
		addTalkId(31044);
		addKillId(20244, 20245, 20283, 20284);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final String htmltext = event;
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		if (event.equals("31044-04.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("31044-08.htm"))
		{
			st.exitQuest(true, true);
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
				htmltext = (player.getLevel() < 55) ? "31044-01.htm" : "31044-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int stones = getQuestItemsCount(player, STONE);
				if (stones == 0)
				{
					htmltext = "31044-05.htm";
				}
				else
				{
					htmltext = "31044-06.htm";
					takeItems(player, STONE, -1);
					rewardItems(player, 57, 2500 + (230 * stones));
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isPet)
	{
		final QuestState st = getQuestState(killer, false);
		if (st == null)
		{
			return null;
		}
		
		if (st.isCond(1) && (Rnd.get(100) < ((npc.getId() == 20244) || (npc.getId() == 20283) ? 48 : 50)))
		{
			giveItems(killer, STONE, 1);
		}
		
		return null;
	}
}