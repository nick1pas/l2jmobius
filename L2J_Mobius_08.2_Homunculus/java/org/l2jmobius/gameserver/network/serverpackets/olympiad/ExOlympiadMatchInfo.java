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

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Mobius
 */
public class ExOlympiadMatchInfo implements IClientOutgoingPacket
{
	private final String _name1;
	private final String _name2;
	private final int _wins1;
	private final int _wins2;
	private final int _round;
	private final int _time;
	
	public ExOlympiadMatchInfo(String name1, String name2, int wins1, int wins2, int round, int time)
	{
		_name1 = name1;
		_name2 = name2;
		_wins1 = wins1;
		_wins2 = wins2;
		_round = round;
		_time = time;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_OLYMPIAD_MATCH_INFO.writeId(packet);
		packet.writeS(String.format("%1$-" + 23 + "s", _name2));
		packet.writeD(_wins2);
		packet.writeS(String.format("%1$-" + 23 + "s", _name1));
		packet.writeD(_wins1);
		packet.writeD(_round);
		packet.writeD(_time); // Seconds
		return true;
	}
}
