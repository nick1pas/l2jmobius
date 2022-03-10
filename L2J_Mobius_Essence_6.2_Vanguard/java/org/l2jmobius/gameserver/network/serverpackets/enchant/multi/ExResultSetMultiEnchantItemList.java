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
package org.l2jmobius.gameserver.network.serverpackets.enchant.multi;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.request.EnchantItemRequest;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Index
 */
public class ExResultSetMultiEnchantItemList implements IClientOutgoingPacket
{
	private final Player _player;
	private final int _resultType;
	
	public ExResultSetMultiEnchantItemList(Player player, int resultType)
	{
		_player = player;
		_resultType = resultType;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		if (_player.getRequest(EnchantItemRequest.class) == null)
		{
			return false;
		}
		_player.getRequest(EnchantItemRequest.class);
		
		OutgoingPackets.EX_RES_SET_MULTI_ENCHANT_ITEM_LIST.writeId(packet);
		packet.writeD(_resultType);
		return true;
	}
}
