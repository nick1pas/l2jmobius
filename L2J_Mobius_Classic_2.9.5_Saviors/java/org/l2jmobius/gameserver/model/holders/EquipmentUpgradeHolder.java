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
package org.l2jmobius.gameserver.model.holders;

import java.util.List;

/**
 * @author Mobius
 */
public class EquipmentUpgradeHolder
{
	private final int _id;
	private final ItemEnchantHolder _requiredItem;
	private final long _adena;
	private final List<ItemEnchantHolder> _results;
	private final int _chance;
	private final List<ItemHolder> _materials;
	private final List<ItemEnchantHolder> _onFail;
	private final List<ItemEnchantHolder> _bonus;
	private final int _bonusChance;
	
	public EquipmentUpgradeHolder(int id, ItemEnchantHolder requiredItem, long adena, List<ItemEnchantHolder> results, int chance, List<ItemHolder> materials, List<ItemEnchantHolder> onFail, List<ItemEnchantHolder> bonus, int bonusChance)
	{
		_id = id;
		_requiredItem = requiredItem;
		_materials = materials;
		_adena = adena;
		_results = results;
		_chance = chance;
		_onFail = onFail;
		_bonus = bonus;
		_bonusChance = bonusChance;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public List<ItemHolder> getMaterials()
	{
		return _materials;
	}
	
	public long getAdena()
	{
		return _adena;
	}
	
	public List<ItemEnchantHolder> getResult()
	{
		return _results;
	}
	
	public int getChance()
	{
		return _chance;
	}
	
	public List<ItemEnchantHolder> getBonus()
	{
		return _bonus;
	}
	
	public List<ItemEnchantHolder> getOnFail()
	{
		return _onFail;
	}
	
	public int getBonusChance()
	{
		return _bonusChance;
	}
	
	public ItemEnchantHolder getRequiredItem()
	{
		return _requiredItem;
	}
}
