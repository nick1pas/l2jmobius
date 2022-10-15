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
package ai.areas.GardenOfSpirits;

import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Monster;

import ai.AbstractNpcAI;

/**
 * Isabella Raid Boss AI
 * @URL https://www.youtube.com/watch?v=3M73b6Kre6Y
 * @author Gigi, Mobius
 */
public class Isabella extends AbstractNpcAI
{
	// NPC
	private static final int ISABELLA = 26131;
	private static final int CROA = 26132;
	private static final int AMIS = 26133;
	private static final int CROAMIS = 23563;
	// Doors
	private static final int DOOR1 = 18200001;
	private static final int DOOR2 = 18200002;
	// Location
	private static final Location CROAMIS_SPAWN_LOCATION = new Location(-51033, 82405, -4882, 44107);
	
	public Isabella()
	{
		addAttackId(ISABELLA);
		addSpawnId(ISABELLA, CROAMIS);
		addKillId(ISABELLA, CROAMIS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.equals("SPAWN"))
		{
			final Npc minion1 = addSpawn(CROA, -51104, 83436, -5120, 61567, false, 300000, false);
			addAttackPlayerDesire(minion1, player);
			final Npc minion2 = addSpawn(AMIS, -50307, 83662, -5120, 45183, false, 300000, false);
			addAttackPlayerDesire(minion2, player);
			final Npc minion3 = addSpawn(CROA, -50259, 82825, -5120, 23684, false, 300000, false);
			addAttackPlayerDesire(minion3, player);
			final Npc minion4 = addSpawn(AMIS, -50183, 82901, -5120, 28180, false, 300000, false);
			addAttackPlayerDesire(minion4, player);
			final Npc minion5 = addSpawn(CROA, -50387, 83732, -5112, 45183, false, 300000, false);
			addAttackPlayerDesire(minion5, player);
			final Npc minion6 = addSpawn(AMIS, -51157, 83298, -5112, 64987, true, 300000, false);
			addAttackPlayerDesire(minion6, player);
		}
		return null;
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		if (!npc.isDead())
		{
			if ((npc.getCurrentHpPercent() <= 80) && npc.isScriptValue(0))
			{
				startQuestTimer("SPAWN", 500, npc, attacker);
				npc.setScriptValue(1);
			}
			else if ((npc.getCurrentHpPercent() <= 50) && npc.isScriptValue(1))
			{
				startQuestTimer("SPAWN", 500, npc, attacker);
				npc.setScriptValue(2);
			}
			else if ((npc.getCurrentHpPercent() <= 10) && npc.isScriptValue(2))
			{
				startQuestTimer("SPAWN", 500, npc, attacker);
				npc.setScriptValue(3);
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (npc.getId() == ISABELLA)
		{
			addSpawn(CROAMIS, CROAMIS_SPAWN_LOCATION, false, 0);
		}
		else // CROAMIS
		{
			npc.setRandomWalking(false);
			npc.teleToLocation(CROAMIS_SPAWN_LOCATION); // No random spawn.
			closeDoor(DOOR1, 0);
			closeDoor(DOOR2, 0);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.getId() == ISABELLA)
		{
			World.getInstance().forEachVisibleObjectInRange(npc, Monster.class, 1500, minion ->
			{
				if ((minion != null) && !minion.isAlikeDead() && ((minion.getId() == CROA) || (minion.getId() == AMIS)))
				{
					minion.deleteMe();
				}
			});
		}
		else // CROAMIS
		{
			openDoor(DOOR1, 0);
			openDoor(DOOR2, 0);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new Isabella();
	}
}