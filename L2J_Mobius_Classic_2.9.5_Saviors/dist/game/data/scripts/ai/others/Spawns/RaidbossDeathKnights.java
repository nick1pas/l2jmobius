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
package ai.others.Spawns;

import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.ListenerRegisterType;
import org.l2jmobius.gameserver.model.events.annotations.NpcLevelRange;
import org.l2jmobius.gameserver.model.events.annotations.RegisterEvent;
import org.l2jmobius.gameserver.model.events.annotations.RegisterType;
import org.l2jmobius.gameserver.model.events.impl.creature.OnCreatureDeath;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class RaidbossDeathKnights extends AbstractNpcAI
{
	private static final int CHANCE = 10;
	
	private RaidbossDeathKnights()
	{
	}
	
	@RegisterEvent(EventType.ON_CREATURE_DEATH)
	@RegisterType(ListenerRegisterType.NPC)
	@NpcLevelRange(from = 20, to = 79)
	private void onCreatureDeath(OnCreatureDeath event)
	{
		final Creature creature = event.getTarget();
		if ((creature == null) || !creature.isRaid() || creature.isInInstance())
		{
			return;
		}
		
		final Creature attacker = event.getAttacker();
		if ((attacker == null) || !attacker.isPlayable())
		{
			return;
		}
		
		if (getRandom(100) >= CHANCE)
		{
			return;
		}
		
		addSpawn(25785 + (creature.getLevel() / 10), creature.getLocation(), true, 900000);
	}
	
	public static void main(String[] args)
	{
		new RaidbossDeathKnights();
	}
}
