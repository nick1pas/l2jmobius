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
package quests.Q00645_GhostsOfBatur;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.util.Util;

public class Q00645_GhostsOfBatur extends Quest
{
	// NPC
	private static final int KARUDA = 32017;
	// Item
	private static final int CURSED_GRAVE_GOODS = 8089;
	// Rewards
	private static final int[][] REWARDS =
	{
		// @formatter:off
		{1878, 18},
		{1879, 7},
		{1880, 4},
		{1881, 6},
		{1882, 10},
		{1883, 2}
		// @formatter:on
	};
	
	public Q00645_GhostsOfBatur()
	{
		super(645);
		addStartNpc(KARUDA);
		addTalkId(KARUDA);
		addKillId(22007, 22009, 22010, 22011, 22012, 22013, 22014, 22015, 22016);
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
		
		if (event.equals("32017-03.htm"))
		{
			st.startQuest();
		}
		else if (Util.isDigit(event))
		{
			htmltext = "32017-07.htm";
			takeItems(player, CURSED_GRAVE_GOODS, -1);
			
			final int[] reward = REWARDS[Integer.parseInt(event)];
			giveItems(player, reward[0], reward[1]);
			
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
				htmltext = (player.getLevel() < 23) ? "32017-02.htm" : "32017-01.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				if (cond == 1)
				{
					htmltext = "32017-04.htm";
				}
				else if (cond == 2)
				{
					htmltext = "32017-05.htm";
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Player player = getRandomPartyMember(killer, 1);
		if ((player != null) && Util.checkIfInRange(Config.ALT_PARTY_RANGE, npc, player, false) && (getRandom(100) < 75))
		{
			final QuestState qs = getQuestState(player, false);
			giveItems(killer, CURSED_GRAVE_GOODS, 1);
			if (qs.isCond(1) && (getQuestItemsCount(killer, CURSED_GRAVE_GOODS) >= 500))
			{
				qs.setCond(2, true);
			}
			else
			{
				playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
}