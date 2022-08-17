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

import java.util.List;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.olympiad.OlympiadInfo;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author JIV
 */
public class ExOlympiadMatchResult implements IClientOutgoingPacket
{
	private final boolean _tie;
	private int _winTeam; // 1,2
	private int _loseTeam = 2;
	private final List<OlympiadInfo> _winnerList;
	private final List<OlympiadInfo> _loserList;
	private final int _round1winner;
	private final int _round2winner;
	private final int _round3winner;
	
	public ExOlympiadMatchResult(boolean tie, int winTeam, List<OlympiadInfo> winnerList, List<OlympiadInfo> loserList, int round1winner, int round2winner, int round3winner)
	{
		_tie = tie;
		_winTeam = winTeam;
		_winnerList = winnerList;
		_loserList = loserList;
		if (_winTeam == 2)
		{
			_loseTeam = 1;
		}
		else if (_winTeam == 0)
		{
			_winTeam = 1;
		}
		_round1winner = round1winner;
		_round2winner = round2winner;
		_round3winner = round3winner;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_RECEIVE_OLYMPIAD.writeId(packet);
		packet.writeD(2); // Type 0 = Match List, 1 = Match Result, 2 = New Oly look, 3 = 3v3
		packet.writeD(_tie ? 1 : 0); // 0 - win, 1 - tie
		packet.writeS(_winnerList.get(0).getName());
		packet.writeD(_winTeam);
		packet.writeD(_winnerList.size());
		for (OlympiadInfo info : _winnerList)
		{
			packet.writeS(info.getName());
			
			// New UI doesn't support clan name/id
			packet.writeH(0); // clan name
			packet.writeD(0); // clan id
			
			packet.writeD(info.getClassId());
			packet.writeD(info.getDamage());
			packet.writeD(info.getCurrentPoints());
			packet.writeD(info.getDiffPoints());
			packet.writeD(1); // Helios
		}
		packet.writeD(_loseTeam);
		packet.writeD(_loserList.size());
		for (OlympiadInfo info : _loserList)
		{
			packet.writeS(info.getName());
			
			// New UI doesn't support clan name/id
			packet.writeH(0); // clan name
			packet.writeD(0); // clan id
			
			packet.writeD(info.getClassId());
			packet.writeD(info.getDamage());
			packet.writeD(info.getCurrentPoints());
			packet.writeD(info.getDiffPoints());
			packet.writeD(1); // Helios
		}
		packet.writeC(_round1winner); // Round 1 outcome
		packet.writeC(_round2winner); // Round 2 outcome
		packet.writeC(_round3winner); // Round 3 outcome
		packet.writeD(15); // Bonus Reward
		packet.writeD(0); // Bonus Reward for looser
		return true;
	}
}
