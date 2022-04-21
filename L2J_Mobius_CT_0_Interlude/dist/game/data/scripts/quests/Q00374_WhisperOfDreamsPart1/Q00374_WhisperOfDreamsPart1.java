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
package quests.Q00374_WhisperOfDreamsPart1;

import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.QuestSound;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

public class Q00374_WhisperOfDreamsPart1 extends Quest
{
	// NPCs
	private static final int MANAKIA = 30515;
	private static final int TORAI = 30557;
	// Monsters
	private static final int CAVE_BEAST = 20620;
	private static final int DEATH_WAVE = 20621;
	// Items
	private static final int CAVE_BEAST_TOOTH = 5884;
	private static final int DEATH_WAVE_LIGHT = 5885;
	private static final int SEALED_MYSTERIOUS_STONE = 5886;
	private static final int MYSTERIOUS_STONE = 5887;
	// Rewards
	private static final int[][] REWARDS =
	{
		// @formatter:off
		{5486, 3, 2950}, // Dark Crystal, 3x, 2950 adena
		{5487, 2, 18050}, // Nightmare, 2x, 18050 adena
		{5488, 2, 18050}, // Majestic, 2x, 18050 adena
		{5485, 4, 10450}, // Tallum Tunic, 4, 10450 adena
		{5489, 6, 15550}, // Tallum Stockings, 6, 15550 adena
		// @formatter:on
	};
	
	public Q00374_WhisperOfDreamsPart1()
	{
		super(374);
		registerQuestItems(DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH, SEALED_MYSTERIOUS_STONE, MYSTERIOUS_STONE);
		addStartNpc(MANAKIA);
		addTalkId(MANAKIA, TORAI);
		addKillId(CAVE_BEAST, DEATH_WAVE);
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
		
		// Manakia
		if (event.equals("30515-03.htm"))
		{
			st.startQuest();
			st.set("condStone", "1");
		}
		else if (event.startsWith("30515-06-"))
		{
			if ((getQuestItemsCount(player, CAVE_BEAST_TOOTH) >= 65) && (getQuestItemsCount(player, DEATH_WAVE_LIGHT) >= 65))
			{
				htmltext = "30515-06.htm";
				playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
				
				final int[] reward = REWARDS[Integer.parseInt(event.substring(9, 10))];
				
				takeItems(player, CAVE_BEAST_TOOTH, -1);
				takeItems(player, DEATH_WAVE_LIGHT, -1);
				
				rewardItems(player, 57, reward[2]);
				giveItems(player, reward[0], reward[1]);
			}
			else
			{
				htmltext = "30515-07.htm";
			}
		}
		else if (event.equals("30515-08.htm"))
		{
			st.exitQuest(true, true);
		}
		// Torai
		else if (event.equals("30557-02.htm"))
		{
			if (st.isCond(2) && hasQuestItems(player, SEALED_MYSTERIOUS_STONE))
			{
				st.setCond(3, true);
				takeItems(player, SEALED_MYSTERIOUS_STONE, -1);
				giveItems(player, MYSTERIOUS_STONE, 1);
			}
			else
			{
				htmltext = "30557-03.htm";
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
				htmltext = (player.getLevel() < 56) ? "30515-01.htm" : "30515-02.htm";
				break;
			}
			case State.STARTED:
			{
				final int cond = st.getCond();
				switch (npc.getId())
				{
					case MANAKIA:
					{
						if (!hasQuestItems(player, SEALED_MYSTERIOUS_STONE))
						{
							if ((getQuestItemsCount(player, CAVE_BEAST_TOOTH) >= 65) && (getQuestItemsCount(player, DEATH_WAVE_LIGHT) >= 65))
							{
								htmltext = "30515-05.htm";
							}
							else
							{
								htmltext = "30515-04.htm";
							}
						}
						else
						{
							if (cond == 1)
							{
								htmltext = "30515-09.htm";
								st.setCond(2, true);
							}
							else
							{
								htmltext = "30515-10.htm";
							}
						}
						break;
					}
					case TORAI:
					{
						if ((cond == 2) && hasQuestItems(player, SEALED_MYSTERIOUS_STONE))
						{
							htmltext = "30557-01.htm";
						}
						break;
					}
				}
				break;
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isPet)
	{
		// Drop tooth or light to anyone.
		Player partyMember = getRandomPartyMemberState(player, State.STARTED);
		if (partyMember == null)
		{
			return null;
		}
		
		QuestState st = partyMember.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		if (getRandomBoolean())
		{
			giveItems(st.getPlayer(), (npc.getId() == CAVE_BEAST) ? CAVE_BEAST_TOOTH : DEATH_WAVE_LIGHT, Rnd.get(1, 65));
		}
		
		// Drop sealed mysterious stone to party member who still need it.
		partyMember = getRandomPartyMember(player, "condStone", "1");
		if (partyMember == null)
		{
			return null;
		}
		
		st = partyMember.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		if (Rnd.get(100) < 1)
		{
			giveItems(st.getPlayer(), SEALED_MYSTERIOUS_STONE, 1);
			st.unset("condStone");
		}
		
		return null;
	}
}