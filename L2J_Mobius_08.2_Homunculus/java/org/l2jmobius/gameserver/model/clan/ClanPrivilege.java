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
package org.l2jmobius.gameserver.model.clan;

/**
 * This enum is used for clan privileges.<br>
 * The ordinal of each entry is the bit index in the privilege bitmask.
 * @author HorridoJoho
 */
public enum ClanPrivilege
{
	/** dummy entry */
	DUMMY,
	
	/** System Privileges */
	CL_INVITE,
	CL_MANAGE_TITLES,
	CL_WAREHOUSE_SEARCH,
	CL_MANAGE_RANKS,
	CL_CLAN_WAR,
	CL_DISMISS,
	CL_EDIT_CREST,
	CL_USE_FUNCTIONS,
	CL_SETTINGS,
	CL_THRONE_OF_HEROES,
	
	/** Clan Hall Privileges */
	CH_ENTRY_EXIT_RIGHTS,
	CH_USE_FUNCTIONS,
	CH_AUCTION,
	CH_DISMISS,
	CH_SETTTINGS,
	
	/** Castle/Fortress Privileges */
	CS_ENTRY_EXIT_RIGHTS,
	CS_SIEGE_WAR,
	CS_USE_FUNCTIONS,
	CS_SETTINGS,
	CS_DISMISS,
	CS_MANAGE_TAXES,
	CS_MERCENARIES,
	CS_MANOR_ADMIN;
	
	public int getBitmask()
	{
		return 1 << ordinal();
	}
}
