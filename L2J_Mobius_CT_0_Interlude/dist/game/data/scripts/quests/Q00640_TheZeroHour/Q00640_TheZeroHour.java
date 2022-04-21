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
package quests.Q00640_TheZeroHour;

import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.util.Util;

import quests.Q00109_InSearchOfTheNest.Q00109_InSearchOfTheNest;

public class Q00640_TheZeroHour extends Quest
{
	// NPC
	private static final int KAHMAN = 31554;
	// Item
	private static final int FANG_OF_STAKATO = 8085;
	private static final int[][] REWARDS =
	{
		// @formatter:off
		{12, 4042, 1},
		{6, 4043, 1},
		{6, 4044, 1},
		{81, 1887, 10},
		{33, 1888, 5},
		{30, 1889, 10},
		{150, 5550, 10},
		{131, 1890, 10},
		{123, 1893, 5}
		// @formatter:on
	};
	
	public Q00640_TheZeroHour()
	{
		super(640);
		registerQuestItems(FANG_OF_STAKATO);
		addStartNpc(KAHMAN);
		addTalkId(KAHMAN);
		// All "spiked" stakatos types, except babies and cannibalistic followers.
		addKillId(22105, 22106, 22107, 22108, 22109, 22110, 22111, 22113, 22114, 22115, 22116, 22117, 22118, 22119, 22121);
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
		
		if (event.equals("31554-02.htm"))
		{
			st.startQuest();
		}
		else if (event.equals("31554-05.htm"))
		{
			if (!hasQuestItems(player, FANG_OF_STAKATO))
			{
				htmltext = "31554-06.htm";
			}
		}
		else if (event.equals("31554-08.htm"))
		{
			st.exitQuest(true, true);
		}
		else if (Util.isDigit(event))
		{
			final int[] reward = REWARDS[Integer.parseInt(event)];
			if (getQuestItemsCount(player, FANG_OF_STAKATO) >= reward[0])
			{
				htmltext = "31554-09.htm";
				takeItems(player, FANG_OF_STAKATO, reward[0]);
				rewardItems(player, reward[1], reward[2]);
			}
			else
			{
				htmltext = "31554-06.htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmltext = getNoQuestMsg(player);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (st.getState())
		{
			case State.CREATED:
			{
				if (player.getLevel() < 66)
				{
					htmltext = "31554-00.htm";
				}
				else
				{
					final QuestState st2 = player.getQuestState(Q00109_InSearchOfTheNest.class.getSimpleName());
					htmltext = ((st2 != null) && st2.isCompleted()) ? "31554-01.htm" : "31554-10.htm";
				}
				break;
			}
			case State.STARTED:
			{
				htmltext = (hasQuestItems(player, FANG_OF_STAKATO)) ? "31554-04.htm" : "31554-03.htm";
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
		
		giveItems(partyMember, FANG_OF_STAKATO, 1);
		playSound(partyMember, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		
		return super.onKill(npc, killer, isSummon);
	}
}