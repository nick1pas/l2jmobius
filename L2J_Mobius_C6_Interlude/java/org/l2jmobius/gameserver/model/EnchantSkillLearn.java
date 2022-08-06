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
package org.l2jmobius.gameserver.model;

import org.l2jmobius.gameserver.model.actor.Player;

public class EnchantSkillLearn
{
	// These two build the primary key.
	private final int _id;
	private final int _level;
	// Not needed, just for easier debugging.
	private final String _name;
	private final int _spCost;
	private final int _baseLevel;
	private final int _minSkillLevel;
	private final int _exp;
	private final byte _rate76;
	private final byte _rate77;
	private final byte _rate78;
	private final byte _rate79;
	private final byte _rate80;
	
	public EnchantSkillLearn(int id, int level, int minSkillLevel, int baseLevel, String name, int spCost, int exp, byte rate76, byte rate77, byte rate78, byte rate79, byte rate80)
	{
		_id = id;
		_level = level;
		_baseLevel = baseLevel;
		_minSkillLevel = minSkillLevel;
		_name = name.intern();
		_spCost = spCost;
		_exp = exp;
		_rate76 = rate76;
		_rate77 = rate77;
		_rate78 = rate78;
		_rate79 = rate79;
		_rate80 = rate80;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getLevel()
	{
		return _level;
	}
	
	public int getBaseLevel()
	{
		return _baseLevel;
	}
	
	public int getMinSkillLevel()
	{
		return _minSkillLevel;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getSpCost()
	{
		return _spCost;
	}
	
	public int getExp()
	{
		return _exp;
	}
	
	public byte getRate(Player player)
	{
		switch (player.getLevel())
		{
			case 76:
			{
				return _rate76;
			}
			case 77:
			{
				return _rate77;
			}
			case 78:
			{
				return _rate78;
			}
			case 79:
			{
				return _rate79;
			}
			default:
			{
				return _rate80;
			}
		}
	}
}
