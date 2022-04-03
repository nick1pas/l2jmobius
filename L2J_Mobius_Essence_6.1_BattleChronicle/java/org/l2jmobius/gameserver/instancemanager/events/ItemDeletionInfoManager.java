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
package org.l2jmobius.gameserver.instancemanager.events;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Mobius
 */
public class ItemDeletionInfoManager
{
	protected static final Logger LOGGER = Logger.getLogger(ItemDeletionInfoManager.class.getName());
	
	private final Map<Integer, Date> _items = new HashMap<>();
	
	protected ItemDeletionInfoManager()
	{
	}
	
	public void addItemInfo(int itemId, Date date)
	{
		_items.put(itemId, date);
	}
	
	public Map<Integer, Date> getInfo()
	{
		return _items;
	}
	
	public static ItemDeletionInfoManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemDeletionInfoManager INSTANCE = new ItemDeletionInfoManager();
	}
}
