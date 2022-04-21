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
package quests.Q00375_WhisperOfDreamsPart2;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00375_WhisperOfDreamsPart2 extends Quest
{
	// NPCs
	private static final int MANAKIA = 30515;
	// Monsters
	private static final int KARIK = 20629;
	private static final int CAVE_HOWLER = 20624;
	// Items
	private static final int MYSTERIOUS_STONE = 5887;
	private static final int KARIK_HORN = 5888;
	private static final int CAVE_HOWLER_SKULL = 5889;
	// Rewards : A grade robe recipes
	private static final int[] REWARDS =
	{
		5348,
		5350,
		5352
	};
	
	public Q00375_WhisperOfDreamsPart2()
	{
		super(375);
		registerQuestItems(KARIK_HORN, CAVE_HOWLER_SKULL);
		addStartNpc(MANAKIA);
		addTalkId(MANAKIA);
		addKillId(KARIK, CAVE_HOWLER);
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
		
		// Manakia
		if (event.equals("30515-03.htm"))
		{
			st.startQuest();
			takeItems(player, MYSTERIOUS_STONE, 1);
		}
		else if (event.equals("30515-07.htm"))
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
				htmltext = (!hasQuestItems(player, MYSTERIOUS_STONE) || (player.getLevel() < 60)) ? "30515-01.htm" : "30515-02.htm";
				break;
			}
			case State.STARTED:
			{
				if ((getQuestItemsCount(player, KARIK_HORN) >= 100) && (getQuestItemsCount(player, CAVE_HOWLER_SKULL) >= 100))
				{
					htmltext = "30515-05.htm";
					playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					takeItems(player, KARIK_HORN, 100);
					takeItems(player, CAVE_HOWLER_SKULL, 100);
					giveItems(player, REWARDS[getRandom(REWARDS.length)], 1);
				}
				else
				{
					htmltext = "30515-04.htm";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		// Drop horn or skull to anyone.
		final Player partyMember = getRandomPartyMemberState(player, State.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		final QuestState st = partyMember.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case KARIK:
			{
				giveItems(st.getPlayer(), KARIK_HORN, 1);
				break;
			}
			case CAVE_HOWLER:
			{
				if (Rnd.get(10) < 9)
				{
					giveItems(st.getPlayer(), CAVE_HOWLER_SKULL, 1);
				}
				break;
			}
		}
		
		return null;
	}
}