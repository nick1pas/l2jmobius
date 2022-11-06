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
package org.l2jmobius.gameserver.network.serverpackets.worldexchange;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Index
 */
public class WorldExchangeSettleRecvResult implements IClientOutgoingPacket
{
	public static final WorldExchangeSettleRecvResult FAIL = new WorldExchangeSettleRecvResult(-1, -1L, (byte) 0);
	
	private final int _itemObjectId;
	private final long _itemAmount;
	private final byte _type;
	
	public WorldExchangeSettleRecvResult(int itemObjectId, long itemAmount, byte type)
	{
		_itemObjectId = itemObjectId;
		_itemAmount = itemAmount;
		_type = type;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_WORLD_EXCHANGE_SETTLE_RECV_RESULT.writeId(packet);
		packet.writeD(_itemObjectId);
		packet.writeQ(_itemAmount);
		packet.writeC(_type);
		return true;
	}
}
