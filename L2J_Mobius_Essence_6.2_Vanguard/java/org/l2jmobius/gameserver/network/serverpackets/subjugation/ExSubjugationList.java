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
package org.l2jmobius.gameserver.network.serverpackets.subjugation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.holders.PurgePlayerHolder;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * Written by Berezkin Nikolay, 40 keys max according to retail.
 */
public class ExSubjugationList implements IClientOutgoingPacket
{
	private final List<Entry<Integer, PurgePlayerHolder>> _playerHolder;
	
	public ExSubjugationList(Map<Integer, PurgePlayerHolder> playerHolder)
	{
		_playerHolder = playerHolder.entrySet().stream().filter(it -> it.getValue() != null).collect(Collectors.toList());
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SUBJUGATION_LIST.writeId(packet);
		packet.writeD(_playerHolder.size());
		for (Entry<Integer, PurgePlayerHolder> integerPurgePlayerHolderEntry : _playerHolder)
		{
			packet.writeD(integerPurgePlayerHolderEntry.getKey());
			packet.writeD(integerPurgePlayerHolderEntry.getValue() != null ? integerPurgePlayerHolderEntry.getValue().getPoints() : 0);
			packet.writeD(integerPurgePlayerHolderEntry.getValue() != null ? integerPurgePlayerHolderEntry.getValue().getKeys() : 0);
			packet.writeD(integerPurgePlayerHolderEntry.getValue() != null ? integerPurgePlayerHolderEntry.getValue().getRemainingKeys() : 40);
		}
		return true;
	}
}
