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
package quests.Q10951_NewFlameOfOrcs;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.player.OnPlayerLogin;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.quest.Quest;
import org.l2jmobius.gameserver.model.quest.QuestState;
import org.l2jmobius.gameserver.model.quest.State;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * New Flame Of Orcs (10951)
 * @author A.Yurechko
 */
public class Q10951_NewFlameOfOrcs extends Quest
{
	// NPCs
	private static final int SABITUS = 34335;
	private static final int TANAI = 30602;
	// Monsters
	private static final int TRAINING_DUMMY = 22183;
	// Items
	private static final ItemHolder SOULSHOT_REWARD = new ItemHolder(91927, 400);
	private static final ItemHolder SOE_REWARD = new ItemHolder(10650, 5);
	private static final ItemHolder WW_POTION_REWARD = new ItemHolder(49036, 5);
	private static final ItemHolder HP_POTION_REWARD = new ItemHolder(91912, 50);
	// Misc
	private static final String REWARD_CHECK_VAR1 = "Q10951_REWARD_1";
	private static final String REWARD_CHECK_VAR2 = "Q10951_REWARD_2";
	private static final int MIN_LEVEL = 1;
	private static final int MAX_LEVEL = 2;
	
	public Q10951_NewFlameOfOrcs()
	{
		super(10951);
		addStartNpc(SABITUS);
		addTalkId(SABITUS, TANAI);
		addKillId(TRAINING_DUMMY);
		addCondMinLevel(MIN_LEVEL, "no_lvl.html");
		addCondMaxLevel(MAX_LEVEL, "no_lvl.html");
		setQuestNameNpcStringId(NpcStringId.LV_1_2_NEW_FLAME_OF_ORCS);
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
			case "34335-02.htm":
			case "34335-03.htm":
			case "34335-04.htm":
			{
				htmltext = event;
				break;
			}
			case "34335-05.htm":
			{
				qs.startQuest();
				htmltext = event;
				break;
			}
			case "34335-07.htm":
			{
				if (qs.isCond(2))
				{
					qs.setCond(3);
					if (!player.getVariables().getBoolean(REWARD_CHECK_VAR1, false))
					{
						player.getVariables().set(REWARD_CHECK_VAR1, true);
						giveItems(player, SOULSHOT_REWARD);
					}
				}
				htmltext = event;
				break;
			}
			case "34335-09.htm":
			{
				if (qs.isCond(4))
				{
					qs.setCond(5);
				}
				htmltext = event;
				break;
			}
			case "30602-00.htm":
			{
				if (qs.isCond(5))
				{
					player.getVariables().set(REWARD_CHECK_VAR2, true);
					giveItems(player, SOE_REWARD);
					giveItems(player, WW_POTION_REWARD);
					giveItems(player, HP_POTION_REWARD);
					giveItems(player, SOULSHOT_REWARD);
					
					addExpAndSp(player, 224, 4);
					giveStoryBuffReward(npc, player);
					
					qs.exitQuest(false, true);
					// TODO: Return Q10952 start htm
					htmltext = event;
				}
				break;
			}
			case "TELEPORT_TO_TANAI":
			{
				player.teleToLocation(-45079, -113511, -208);
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (!player.isVanguard())
		{
			return "no_race.html";
		}
		
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		
		switch (qs.getState())
		{
			case State.CREATED:
			{
				htmltext = "34335-02.htm";
				break;
			}
			case State.STARTED:
			{
				switch (qs.getCond())
				{
					case 1:
					{
						htmltext = "no_dummy-01.html";
						break;
					}
					case 2:
					{
						htmltext = "34335-06.htm";
						break;
					}
					case 3:
					{
						htmltext = "no_dummy-02.html";
						break;
					}
					case 4:
					{
						htmltext = "34335-08.htm";
						break;
					}
					case 5:
					{
						htmltext = "30602-10.htm";
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
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final QuestState qs = getQuestState(killer, false);
		if (qs != null)
		{
			if (qs.isCond(1))
			{
				qs.setCond(2, true);
			}
			else if (qs.isCond(3))
			{
				qs.setCond(4, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@RegisterEvent(EventType.ON_PLAYER_LOGIN)
	@RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
	public void onPlayerLogin(OnPlayerLogin event)
	{
		if (Config.DISABLE_TUTORIAL)
		{
			return;
		}
		
		final Player player = event.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (!player.isVanguard())
		{
			return;
		}
		
		final QuestState qs = getQuestState(player, false);
		if ((qs == null) || (player.getLevel() < 20))
		{
			showOnScreenMsg(player, NpcStringId.TALK_TO_FIRST_VANGUARD_RIDER_SABITUS, ExShowScreenMessage.TOP_CENTER, 10000, player.getName());
		}
	}
}
