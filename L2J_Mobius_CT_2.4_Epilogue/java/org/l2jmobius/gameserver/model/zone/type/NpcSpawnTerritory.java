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
package org.l2jmobius.gameserver.model.zone.type;

import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.zone.ZoneForm;

/**
 * Just dummy zone, needs only for geometry calculations
 * @author GKR, Mobius
 */
public class NpcSpawnTerritory
{
	private final String _name;
	private final ZoneForm _territory;
	private ZoneForm _banned;
	
	public NpcSpawnTerritory(String name, ZoneForm territory)
	{
		_name = name;
		_territory = territory;
	}
	
	public void setBannedTerritory(ZoneForm banned)
	{
		_banned = banned;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public Location getRandomPoint()
	{
		if (_banned != null)
		{
			int count = 0; // Prevent infinite loop from wrongly written data.
			Location location;
			while (count++ < 1000)
			{
				location = _territory.getRandomPoint();
				if (!_banned.isInsideZone(location.getX(), location.getY(), location.getZ()))
				{
					return location;
				}
			}
		}
		return _territory.getRandomPoint();
	}
	
	public boolean isInsideZone(int x, int y, int z)
	{
		return _territory.isInsideZone(x, y, z);
	}
	
	public void visualizeZone(int z)
	{
		_territory.visualizeZone(z);
	}
}