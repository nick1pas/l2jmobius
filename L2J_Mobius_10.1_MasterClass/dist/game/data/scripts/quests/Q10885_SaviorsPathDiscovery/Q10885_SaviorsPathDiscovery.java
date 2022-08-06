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
package quests.Q10885_SaviorsPathDiscovery;

import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;

/**
 * Savior's Path - Discovery (10885)
 * @URL https://l2wiki.com/Savior%27s_Path_-_Discovery
 * @author CostyKiller
 */
public class Q10885_SaviorsPathDiscovery extends Quest
{
	// NPCs
	private static final int LEONA_BLACKBIRD = 34425;
	private static final int ELIKIA = 34057;
	// Item
	private static final int LEONA_BLACKBIRDS_MESSAGE = 48545;
	// Misc
	private static final int MIN_LEVEL = 103;
	
	public Q10885_SaviorsPathDiscovery()
	{
		super(10885);
		addStartNpc(ELIKIA);
		addTalkId(LEONA_BLACKBIRD, ELIKIA);
		addCondMinLevel(MIN_LEVEL, "34057-00.html");
		registerQuestItems(LEONA_BLACKBIRDS_MESSAGE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "34057-03.html":
			{
				if (qs.isCond(1))
				{
					takeItems(player, -1, LEONA_BLACKBIRDS_MESSAGE);
					qs.setCond(2);
				}
				htmltext = event;
				break;
			}
			case "34057-05.html":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3);
				}
				htmltext = event;
				break;
			}
			case "34425-03.html":
			{
				if (qs.isCond(3))
				{
					if ((player.getLevel() >= MIN_LEVEL))
					{
						addExpAndSp(player, 906387492, 906387);
						qs.exitQuest(false, true);
						htmltext = event;
					}
					else
					{
						htmltext = getNoQuestLevelRewardMsg(player);
					}
				}
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
		switch (qs.getState())
		{
			case State.CREATED:
			{
				if (npc.getId() == ELIKIA)
				{
					htmltext = "34057-01.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getId())
				{
					case LEONA_BLACKBIRD:
					{
						if (qs.isCond(3))
						{
							htmltext = "34425-01.htm";
						}
						else
						{
							htmltext = "34425-02.html";
						}
						break;
					}
					case ELIKIA:
					{
						if (qs.isCond(1))
						{
							htmltext = "34057-01.htm";
						}
						else if (qs.isCond(2))
						{
							htmltext = "34057-04.htm";
						}
						break;
					}
				}
				break;
			}
			case State.COMPLETED:
			{
				htmltext = getAlreadyCompletedMsg(player);
				break;
			}
		}
		return htmltext;
	}
}
