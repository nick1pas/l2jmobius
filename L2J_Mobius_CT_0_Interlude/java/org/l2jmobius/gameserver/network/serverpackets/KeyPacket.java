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
package org.l2jmobius.gameserver.network.serverpackets;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.network.OutgoingPackets;

public class KeyPacket implements IClientOutgoingPacket
{
	private final byte[] _key;
	private final int _result;
	
	public KeyPacket(byte[] key, int result)
	{
		_key = key;
		_result = result;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.KEY_PACKET.writeId(packet);
		packet.writeC(_result); // 0 - wrong protocol, 1 - protocol ok
		for (int i = 0; i < 8; i++)
		{
			packet.writeC(_key[i]); // key
		}
		packet.writeD(1);
		packet.writeD(Config.SERVER_ID); // server id
		packet.writeC(1);
		packet.writeD(0); // obfuscation key
		return true;
	}
}
