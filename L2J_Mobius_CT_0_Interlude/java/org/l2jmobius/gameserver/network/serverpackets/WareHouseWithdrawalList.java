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

import java.util.Collection;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.PacketLogger;

public class WareHouseWithdrawalList implements IClientOutgoingPacket
{
	public static final int PRIVATE = 1;
	public static final int CLAN = 4;
	public static final int CASTLE = 3; // not sure
	public static final int FREIGHT = 1;
	
	private long _playerAdena;
	private Collection<Item> _items;
	/**
	 * <ul>
	 * <li>0x01-Private Warehouse</li>
	 * <li>0x02-Clan Warehouse</li>
	 * <li>0x03-Castle Warehouse</li>
	 * <li>0x04-Warehouse</li>
	 * </ul>
	 */
	private int _whType;
	
	public WareHouseWithdrawalList(Player player, int type)
	{
		if (player.getActiveWarehouse() == null)
		{
			PacketLogger.warning("Error while sending withdraw request to: " + player.getName());
			return;
		}
		_playerAdena = player.getAdena();
		_items = player.getActiveWarehouse().getItems();
		_whType = type;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.WAREHOUSE_WITHDRAW_LIST.writeId(packet);
		packet.writeH(_whType);
		packet.writeD((int) _playerAdena);
		packet.writeH(_items.size());
		for (Item item : _items)
		{
			packet.writeH(item.getTemplate().getType1());
			packet.writeD(item.getObjectId());
			packet.writeD(item.getId());
			packet.writeD(item.getCount());
			packet.writeH(item.getTemplate().getType2());
			packet.writeH(item.getCustomType1());
			packet.writeD(item.getTemplate().getBodyPart());
			packet.writeH(item.getEnchantLevel());
			packet.writeH(0);
			packet.writeH(item.getCustomType2());
			packet.writeD(item.getObjectId());
			if (item.isAugmented())
			{
				packet.writeD(0x0000FFFF & item.getAugmentation().getAugmentationId());
				packet.writeD(item.getAugmentation().getAugmentationId() >> 16);
			}
			else
			{
				packet.writeQ(0);
			}
		}
		return true;
	}
}
