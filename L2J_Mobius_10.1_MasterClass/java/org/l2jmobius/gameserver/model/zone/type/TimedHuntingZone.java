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

import org.l2jmobius.gameserver.data.xml.TimedHuntingZoneData;
import org.l2jmobius.gameserver.enums.TeleportWhereType;
import org.l2jmobius.gameserver.instancemanager.MapRegionManager;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.TimedHuntingZoneHolder;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.huntingzones.TimedHuntingZoneExit;

/**
 * @author Mobius
 * @author dontknowdontcare
 */
public class TimedHuntingZone extends ZoneType
{
	public TimedHuntingZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (!creature.isPlayer())
		{
			if (creature.isPlayable())
			{
				Playable summon = (Playable) creature;
				for (TimedHuntingZoneHolder holder : TimedHuntingZoneData.getInstance().getAllHuntingZones())
				{
					if (!summon.isInTimedHuntingZone(holder.getZoneId()))
					{
						continue;
					}
					if (holder.isPvpZone())
					{
						summon.setInsideZone(ZoneId.PVP, true);
					}
					else if (holder.isNoPvpZone())
					{
						summon.setInsideZone(ZoneId.NO_PVP, true);
					}
					break;
				}
			}
			return;
		}
		// Check summons spawning or porting inside.
		final Player player = creature.getActingPlayer();
		if (player != null)
		{
			player.setInsideZone(ZoneId.TIMED_HUNTING, true);
			
			for (TimedHuntingZoneHolder holder : TimedHuntingZoneData.getInstance().getAllHuntingZones())
			{
				if (!player.isInTimedHuntingZone(holder.getZoneId()))
				{
					continue;
				}
				final int remainingTime = player.getTimedHuntingZoneRemainingTime(holder.getZoneId());
				if (remainingTime > 0)
				{
					player.setLastTimeZone(holder);
					player.startTimedHuntingZone(holder.getZoneId(), remainingTime);
					if (holder.isPvpZone())
					{
						player.setInsideZone(ZoneId.PVP, true);
						player.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
						player.updateRelationsToVisiblePlayers(true);
					}
					else if (holder.isNoPvpZone())
					{
						player.setInsideZone(ZoneId.NO_PVP, true);
					}
					return;
				}
				break;
			}
			if (!player.isGM())
			{
				player.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN));
			}
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (!creature.isPlayer())
		{
			if (creature.isPlayable())
			{
				final Playable summon = (Playable) creature;
				for (final TimedHuntingZoneHolder holder : TimedHuntingZoneData.getInstance().getAllHuntingZones())
				{
					if (!summon.isInTimedHuntingZone(holder.getZoneId()))
					{
						continue;
					}
					if (holder.isPvpZone())
					{
						summon.setInsideZone(ZoneId.PVP, false);
					}
					else if (holder.isNoPvpZone())
					{
						summon.setInsideZone(ZoneId.NO_PVP, false);
					}
					break;
				}
			}
			return;
		}
		
		final Player player = creature.getActingPlayer();
		if (player == null)
		{
			return;
		}
		
		// We default to zone 6 aka Primeval Isle so we spawn in rune town by default.
		int nZoneId = 6;
		
		final TimedHuntingZoneHolder lastTimeZone = player.getLastTimeZone();
		if (lastTimeZone != null)
		{
			nZoneId = lastTimeZone.getZoneId();
			if (lastTimeZone.isPvpZone())
			{
				player.setInsideZone(ZoneId.PVP, false);
				player.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
				player.updateRelationsToVisiblePlayers(true);
			}
			else if (lastTimeZone.isNoPvpZone())
			{
				player.setInsideZone(ZoneId.NO_PVP, false);
			}
			player.setLastTimeZone(null);
		}
		
		player.setInsideZone(ZoneId.TIMED_HUNTING, false);
		player.sendPacket(new TimedHuntingZoneExit(nZoneId));
	}
}
