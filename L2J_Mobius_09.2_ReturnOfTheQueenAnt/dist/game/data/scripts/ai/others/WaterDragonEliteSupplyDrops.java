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
package ai.others;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.logging.Level;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.SystemMessageId;

import ai.AbstractNpcAI;

/**
 * @author Mobius
 */
public class WaterDragonEliteSupplyDrops extends AbstractNpcAI
{
	// Monsters
	private static final int[] MONSTERS =
	{
		24600, // Wyrn
		24599, // SwordMan
		24598, // Rider
		24603, // Pikeman
		24605, // Krotanian
		24604, // Swordman
	};
	// Item
	private static final int WATER_DRAGON_ELITE_SUPPLIES = 81758;
	// Misc
	private static final String WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR = "WATER_DRAGON_SUPPLIES_DROP_COUNT";
	private static final int PLAYER_LEVEL = 100;
	private static final int DROP_DAILY = 1;
	private static final int DROP_MIN = 1;
	private static final int DROP_MAX = 1;
	private static final double CHANCE = 10;
	
	private WaterDragonEliteSupplyDrops()
	{
		addKillId(MONSTERS);
		startQuestTimer("schedule", 1000, null, null);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if ((npc != null) || (player != null))
		{
			return null;
		}
		
		if (event.equals("schedule"))
		{
			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 6);
			calendar.set(Calendar.MINUTE, 30);
			
			cancelQuestTimers("reset");
			startQuestTimer("reset", calendar.getTimeInMillis() - System.currentTimeMillis(), null, null);
		}
		else if (event.equals("reset"))
		{
			// Update data for offline players.
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM character_variables WHERE var=?"))
			{
				ps.setString(1, WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR);
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOGGER.log(Level.SEVERE, "Could not reset Corroded Fields drop count: ", e);
			}
			
			// Update data for online players.
			for (Player plr : World.getInstance().getPlayers())
			{
				plr.getVariables().remove(WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR);
				plr.getVariables().storeMe();
			}
			
			cancelQuestTimers("schedule");
			startQuestTimer("schedule", 1000, null, null);
		}
		
		return null;
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		final Player player = getRandomPartyMember(killer);
		if ((player.getLevel() >= PLAYER_LEVEL) && (getRandom(100) < CHANCE))
		{
			final int count = player.getVariables().getInt(WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR, 0);
			if (count < DROP_DAILY)
			{
				player.getVariables().set(WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR, count + 1);
				giveItems(player, WATER_DRAGON_ELITE_SUPPLIES, getRandom(DROP_MIN, DROP_MAX));
			}
			else
			{
				if (count == DROP_DAILY)
				{
					player.getVariables().set(WATER_DRAGON_ELITE_SUPPLIES_COUNT_VAR, count + 1);
					player.sendPacket(SystemMessageId.YOU_EXCEEDED_THE_LIMIT_AND_CANNOT_COMPLETE_THE_TASK);
				}
				player.sendMessage("You obtained all available Water Dragon's Elite Supplies for this day!");
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new WaterDragonEliteSupplyDrops();
	}
}