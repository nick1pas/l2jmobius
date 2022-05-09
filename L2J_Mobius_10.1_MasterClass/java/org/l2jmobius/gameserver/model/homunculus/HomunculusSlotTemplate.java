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
package org.l2jmobius.gameserver.model.homunculus;

import java.util.List;

import org.l2jmobius.gameserver.model.holders.ItemHolder;

public class HomunculusSlotTemplate
{
	private final int _slotId;
	private final List<ItemHolder> _price;
	private final boolean _isEnabled;
	
	public HomunculusSlotTemplate(int slotId, List<ItemHolder> price, boolean isEnabled)
	{
		_slotId = slotId;
		_price = price;
		_isEnabled = isEnabled;
	}
	
	public int getSlotId()
	{
		return _slotId;
	}
	
	public List<ItemHolder> getPrice()
	{
		return _price;
	}
	
	public boolean getSlotEnabled()
	{
		return _isEnabled;
	}
}
