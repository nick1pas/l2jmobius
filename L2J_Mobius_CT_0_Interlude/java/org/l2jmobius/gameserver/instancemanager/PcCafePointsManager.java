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
package org.l2jmobius.gameserver.instancemanager;

import org.l2jmobius.Config;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.zone.ZoneId;
import org.l2jmobius.gameserver.network.serverpackets.ExPCCafePointInfo;

public class PcCafePointsManager
{
	public void givePcCafePoint(Player player, double exp)
	{
		if (!Config.PC_CAFE_ENABLED || player.isInsideZone(ZoneId.PEACE) || player.isInsideZone(ZoneId.PVP) || player.isInsideZone(ZoneId.SIEGE) || (player.isOnlineInt() == 0) || player.isJailed())
		{
			return;
		}
		
		// PC-points only premium accounts
		if (Config.PC_CAFE_ONLY_PREMIUM && !player.hasPremiumStatus())
		{
			return;
		}
		
		if (player.getPcCafePoints() >= Config.PC_CAFE_MAX_POINTS)
		{
			player.sendMessage("The maximum accumulation allowed of PC cafe points has been exceeded. You can no longer acquire PC cafe points.");
			return;
		}
		
		int points = (int) (exp * 0.0001 * Config.PC_CAFE_POINT_RATE);
		if (Config.PC_CAFE_RANDOM_POINT)
		{
			points = Rnd.get(points / 2, points);
		}
		
		if ((points == 0) && (exp > 0) && Config.PC_CAFE_REWARD_LOW_EXP_KILLS && (Rnd.get(100) < Config.PC_CAFE_LOW_EXP_KILLS_CHANCE))
		{
			points = 1; // minimum points
		}
		
		if (points <= 0)
		{
			return;
		}
		
		if (Config.PC_CAFE_ENABLE_DOUBLE_POINTS && (Rnd.get(100) < Config.PC_CAFE_DOUBLE_POINTS_CHANCE))
		{
			points *= 2;
			player.sendMessage("Double points! You acquired " + points + " PC Bang Point.");
		}
		else
		{
			player.sendMessage("You have acquired " + points + " PC Cafe points.");
		}
		if ((player.getPcCafePoints() + points) > Config.PC_CAFE_MAX_POINTS)
		{
			points = Config.PC_CAFE_MAX_POINTS - player.getPcCafePoints();
		}
		player.setPcCafePoints(player.getPcCafePoints() + points);
		player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), points, 0));
	}
	
	/**
	 * Gets the single instance of {@code PcCafePointsManager}.
	 * @return single instance of {@code PcCafePointsManager}
	 */
	public static PcCafePointsManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PcCafePointsManager INSTANCE = new PcCafePointsManager();
	}
}