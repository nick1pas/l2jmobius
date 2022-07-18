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

public class HomunculusCreationTemplate
{
	private final int _slotId;
	private final boolean _isEnabled;
	private final int _grade;
	private final boolean _isEvent;
	private final List<ItemHolder> _itemsFee;
	private final Integer[] _hpFee;
	private final Long[] _spFee;
	private final Integer[] _vpFee;
	private final long _time;
	private final List<Double[]> _createChances;
	
	public HomunculusCreationTemplate(int slotId, boolean isEnabled, int grade, boolean isEvent, List<ItemHolder> itemsFee, Integer[] hpFee, Long[] spFee, Integer[] vpFee, long time, List<Double[]> createChances)
	{
		_slotId = slotId;
		_isEnabled = isEnabled;
		_grade = grade;
		_isEvent = isEvent;
		_itemsFee = itemsFee;
		_hpFee = hpFee;
		_spFee = spFee;
		_vpFee = vpFee;
		_time = time;
		_createChances = createChances;
	}
	
	public int getSlotId()
	{
		return _slotId;
	}
	
	public boolean isEnabled()
	{
		return _isEnabled;
	}
	
	public int getGrade()
	{
		return _grade;
	}
	
	public boolean isEvent()
	{
		return _isEvent;
	}
	
	public List<ItemHolder> getItemFee()
	{
		return _itemsFee;
	}
	
	public boolean haveAnotherFee()
	{
		return !_itemsFee.isEmpty();
	}
	
	public int getHPFeeCountByUse()
	{
		return _hpFee[1];
	}
	
	public int getHPFeeCount()
	{
		return _hpFee[0];
	}
	
	public long getSPFeeCountByUse()
	{
		return _spFee[1];
	}
	
	public long getSPFeeCount()
	{
		return _spFee[0];
	}
	
	public int getVPFeeByUse()
	{
		return _vpFee[1];
	}
	
	public int getVPFeeCount()
	{
		return _vpFee[0];
	}
	
	public double getMaxChance()
	{
		double result = 0;
		for (int i = 0; i < _createChances.size(); i++)
		{
			Double[] chance = _createChances.get(i);
			result = result + chance[1];
		}
		return result;
	}
	
	public boolean isInstanceHaveCoupon(int itemId)
	{
		for (ItemHolder humu : _itemsFee)
		{
			if (humu.getId() == itemId)
			{
				return true;
			}
		}
		return false;
	}
	
	public long getCreationTime()
	{
		return _time;
	}
	
	public List<Double[]> getCreationChance()
	{
		return _createChances;
	}
}
