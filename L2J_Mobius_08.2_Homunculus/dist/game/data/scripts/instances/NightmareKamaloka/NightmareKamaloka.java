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
package instances.NightmareKamaloka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2jmobius.gameserver.instancemanager.InstanceManager;
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;

import instances.AbstractInstance;

/**
 * Nightmare Kamaloka instance zone.
 * @author St3eT
 */
public class NightmareKamaloka extends AbstractInstance
{
	// NPCs
	private static final int BENUSTA = 34542;
	private static final int DARK_RIDER = 26102;
	private static final int INVISIBLE_NPC = 18919;
	// Items
	private static final ItemHolder BENUSTAS_REWARD_BOX = new ItemHolder(81151, 1);
	private static final ItemHolder BENUSTAS_SHINING_REWARD_BOX = new ItemHolder(81452, 1);
	// Misc
	private static final Map<Integer, Integer> BOSS_MAP = new HashMap<>();
	static
	{
		BOSS_MAP.put(26093, 18170002); // Mino
		BOSS_MAP.put(26094, 18170004); // Sola
		BOSS_MAP.put(26096, 18170006); // Ariarc
		BOSS_MAP.put(26099, 18170008); // Sirra
		BOSS_MAP.put(DARK_RIDER, -1); // Dark Rider
	}
	private static final int TEMPLATE_ID = 258;
	
	public NightmareKamaloka()
	{
		super(TEMPLATE_ID);
		addStartNpc(BENUSTA);
		addTalkId(BENUSTA);
		addSpawnId(INVISIBLE_NPC);
		addKillId(BOSS_MAP.keySet());
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "enterInstance":
			{
				if (player.isInParty())
				{
					final Party party = player.getParty();
					if (!party.isLeader(player))
					{
						player.sendPacket(SystemMessageId.ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER);
						return null;
					}
					
					if (player.isInCommandChannel())
					{
						player.sendPacket(SystemMessageId.YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS);
						return null;
					}
					
					final long currentTime = System.currentTimeMillis();
					final List<Player> members = party.getMembers();
					for (Player member : members)
					{
						if (!member.isInsideRadius3D(npc, 1000))
						{
							player.sendMessage("Player " + member.getName() + " must come closer.");
							return null;
						}
						
						if (currentTime < InstanceManager.getInstance().getInstanceTime(member, TEMPLATE_ID))
						{
							final SystemMessage msg = new SystemMessage(SystemMessageId.SINCE_C1_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_THIS_DUNGEON);
							msg.addString(member.getName());
							party.broadcastToPartyMembers(member, msg);
							return null;
						}
					}
					
					for (Player member : members)
					{
						enterInstance(member, npc, TEMPLATE_ID);
					}
				}
				else if (player.isGM())
				{
					enterInstance(player, npc, TEMPLATE_ID);
				}
				else
				{
					player.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
				}
				break;
			}
			case "SPAWN_BOSSES":
			{
				final Instance instance = npc.getInstanceWorld();
				if (isInInstance(instance))
				{
					instance.spawnGroup("BOSSES");
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		final Instance instance = npc.getInstanceWorld();
		if (isInInstance(instance) && (npc.getId() == INVISIBLE_NPC))
		{
			startQuestTimer("SPAWN_BOSSES", 10000, npc, null);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public void onInstanceCreated(Instance instance, Player player)
	{
		instance.getParameters().set("INITIAL_PARTY_MEMBERS", player.getParty() != null ? player.getParty().getMemberCount() : 1);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		if (isInInstance(world))
		{
			final int nextDoorId = BOSS_MAP.getOrDefault(npc.getId(), -1);
			if (nextDoorId == -1)
			{
				for (Player member : world.getPlayers())
				{
					giveItems(member, BENUSTAS_REWARD_BOX);
				}
				final Party party = world.getFirstPlayer().getParty();
				final Player randomPlayer = party != null ? party.getRandomPlayer() : null;
				if ((randomPlayer != null) && (getRandom(100) < 80) && (world.getPlayersCount() == world.getParameters().getInt("INITIAL_PARTY_MEMBERS", 0)))
				{
					giveItems(randomPlayer, BENUSTAS_SHINING_REWARD_BOX);
				}
				world.finishInstance();
			}
			else
			{
				world.openCloseDoor(nextDoorId, true);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new NightmareKamaloka();
	}
}