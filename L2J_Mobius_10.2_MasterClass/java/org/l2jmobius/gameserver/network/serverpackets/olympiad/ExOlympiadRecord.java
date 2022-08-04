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
package org.l2jmobius.gameserver.network.serverpackets.olympiad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.instancemanager.RankManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.olympiad.Olympiad;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

public class ExOlympiadRecord implements IClientOutgoingPacket
{
	private static final String GET_PREVIOUS_CYCLE_DATA = "SELECT charId, class_id, olympiad_points, competitions_won, competitions_lost FROM olympiad_nobles_eom WHERE class_id = ? ORDER BY olympiad_points DESC LIMIT " + RankManager.PLAYER_LIMIT;
	
	private final Player _player;
	private final int _gameRuleType;
	private final int _type;
	
	public ExOlympiadRecord(Player player, int cGameRuleType, int type)
	{
		_player = player;
		_gameRuleType = cGameRuleType;
		_type = type;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_OLYMPIAD_RECORD.writeId(packet);
		packet.writeD(Olympiad.getInstance().getNoblePoints(_player)); // nPoint
		packet.writeD(Olympiad.getInstance().getCompetitionWon(_player.getObjectId())); // nWinCount
		packet.writeD(Olympiad.getInstance().getCompetitionLost(_player.getObjectId())); // nLoseCount
		packet.writeD(Olympiad.getInstance().getRemainingWeeklyMatches(_player.getObjectId())); // nMatchCount
		// Previous Cycle
		int previousPlace = 0;
		int previousWins = 0;
		int previousLoses = 0;
		int previousPoints = 0;
		int previousClass = 0;
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement(GET_PREVIOUS_CYCLE_DATA))
		{
			statement.setInt(1, _player.getBaseClass());
			try (ResultSet rset = statement.executeQuery())
			{
				int i = 1;
				while (rset.next())
				{
					if (rset.getInt("charId") == _player.getObjectId())
					{
						previousPlace = i;
						previousWins = rset.getInt("competitions_won");
						previousLoses = rset.getInt("competitions_lost");
						previousPoints = rset.getInt("olympiad_points");
						previousClass = rset.getInt("class_id");
					}
					i++;
				}
			}
		}
		catch (SQLException e)
		{
			PacketLogger.warning("Olympiad my ranking: Couldnt load data: " + e.getMessage());
		}
		
		packet.writeD(previousClass); // nPrevClassType
		packet.writeD(1); // nPrevRank in all servers
		packet.writeD(2); // nPrevRankCount number of participants with 25+ matches
		packet.writeD(previousPlace); // nPrevClassRank in all servers
		packet.writeD(4); // nPrevClassRankCount number of participants with 25+ matches
		packet.writeD(5); // nPrevClassRankByServer in current server
		packet.writeD(6); // nPrevClassRankByServerCount number of participants with 25+ matches
		packet.writeD(previousPoints); // nPrevPoint
		packet.writeD(previousWins); // nPrevWinCount
		packet.writeD(previousLoses); // nPrevLoseCount
		packet.writeD(previousPlace); // nPrevGrade
		packet.writeD(Calendar.getInstance().get(Calendar.YEAR)); // nSeasonYear
		packet.writeD(Calendar.getInstance().get(Calendar.MONTH) + 1); // nSeasonMonth
		packet.writeC(Olympiad.getInstance().inCompPeriod() ? 1 : 0); // bMatchOpen
		packet.writeD(Olympiad.getInstance().getCurrentCycle()); // nSeason
		packet.writeC(_type); // bRegistered
		packet.writeD(_gameRuleType); // cGameRuleType
		return true;
	}
}
