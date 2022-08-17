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
package org.l2jmobius.gameserver.model.olympiad;

import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;

/**
 * @author DS, Zoey76
 */
public class Participant
{
	private final int _objectId;
	private Player _player;
	private final String _name;
	private final int _side;
	private final int _baseClass;
	private boolean _disconnected = false;
	private boolean _defaulted = false;
	private final StatSet _stats;
	private final int _level;
	private final String _clanName;
	private final int _clanId;
	
	public Participant(Player plr, int olympiadSide)
	{
		_objectId = plr.getObjectId();
		_player = plr;
		_name = plr.getName();
		_side = olympiadSide;
		_baseClass = plr.getBaseClass();
		_stats = Olympiad.getNobleStats(_objectId);
		_clanName = plr.getClan() != null ? plr.getClan().getName() : "";
		_clanId = plr.getClanId();
		_level = plr.getLevel();
	}
	
	public Participant(int objId, int olympiadSide)
	{
		_objectId = objId;
		_player = null;
		_name = "-";
		_side = olympiadSide;
		_baseClass = 0;
		_stats = null;
		_clanName = "";
		_clanId = 0;
		_level = 0;
	}
	
	public int getLevel()
	{
		return _level;
	}
	
	/**
	 * Updates the reference to {@link #_player}, if it's null or appears off-line.
	 * @return {@code true} if after the update the player isn't null, {@code false} otherwise.
	 */
	public boolean updatePlayer()
	{
		if ((_player == null) || !_player.isOnline())
		{
			_player = World.getInstance().getPlayer(getObjectId());
		}
		return (_player != null);
	}
	
	/**
	 * @param statName
	 * @param increment
	 */
	public void updateStat(String statName, int increment)
	{
		_stats.set(statName, Math.max(_stats.getInt(statName) + increment, 0));
	}
	
	/**
	 * @return the name the player's name.
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * @return the name the player's clan name.
	 */
	public String getClanName()
	{
		return _clanName;
	}
	
	/**
	 * @return the name the player's id.
	 */
	public int getClanId()
	{
		return _clanId;
	}
	
	/**
	 * @return the player
	 */
	public Player getPlayer()
	{
		return _player;
	}
	
	/**
	 * @return the objectId
	 */
	public int getObjectId()
	{
		return _objectId;
	}
	
	/**
	 * @return the stats
	 */
	public StatSet getStats()
	{
		return _stats;
	}
	
	/**
	 * @param noble the player to set
	 */
	public void setPlayer(Player noble)
	{
		_player = noble;
	}
	
	/**
	 * @return the side
	 */
	public int getSide()
	{
		return _side;
	}
	
	/**
	 * @return the baseClass
	 */
	public int getBaseClass()
	{
		return _baseClass;
	}
	
	/**
	 * @return the disconnected
	 */
	public boolean isDisconnected()
	{
		return _disconnected;
	}
	
	/**
	 * @param value the disconnected to set
	 */
	public void setDisconnected(boolean value)
	{
		_disconnected = value;
	}
	
	/**
	 * @return the defaulted
	 */
	public boolean isDefaulted()
	{
		return _defaulted;
	}
	
	/**
	 * @param value the value to set.
	 */
	public void setDefaulted(boolean value)
	{
		_defaulted = value;
	}
}