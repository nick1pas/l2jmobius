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
package org.l2jmobius.gameserver.taskmanager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.l2jmobius.gameserver.model.actor.Creature;

/**
 * Movement task manager class.
 * @author Forsaiken, Mobius
 */
public class MovementTaskManager extends Thread
{
	private static final Set<Creature> MOVING_OBJECTS = ConcurrentHashMap.newKeySet();
	
	protected MovementTaskManager()
	{
		super("MovementTaskManager");
		super.setDaemon(true);
		super.setPriority(MAX_PRIORITY);
		super.start();
	}
	
	/**
	 * Add a Creature to moving objects of MovementTaskManager.
	 * @param creature The Creature to add to moving objects of MovementTaskManager.
	 */
	public void registerMovingObject(Creature creature)
	{
		if (creature == null)
		{
			return;
		}
		
		MOVING_OBJECTS.add(creature);
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				MOVING_OBJECTS.removeIf(Creature::updatePosition);
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				// Ingore.
			}
		}
	}
	
	public static final MovementTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MovementTaskManager INSTANCE = new MovementTaskManager();
	}
}
