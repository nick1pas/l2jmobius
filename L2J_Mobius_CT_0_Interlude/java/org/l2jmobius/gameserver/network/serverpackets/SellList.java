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

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.OutgoingPackets;

/**
 * @author Mobius
 */
public class SellList implements IClientOutgoingPacket
{
	private final int _money;
	private final List<Item> _items = new ArrayList<>();
	
	public SellList(Player player)
	{
		_money = player.getAdena();
		for (Item item : player.getInventory().getItems())
		{
			if (!item.isEquipped() && item.getTemplate().isSellable() && ((player.getSummon() == null) || (item.getObjectId() != player.getSummon().getControlObjectId())))
			{
				_items.add(item);
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.SELL_LIST.writeId(packet);
		packet.writeD(_money);
		packet.writeD(0);
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
			packet.writeH(item.getCustomType2());
			packet.writeH(0);
			packet.writeD(item.getTemplate().getReferencePrice() / 2);
		}
		return true;
	}
}
