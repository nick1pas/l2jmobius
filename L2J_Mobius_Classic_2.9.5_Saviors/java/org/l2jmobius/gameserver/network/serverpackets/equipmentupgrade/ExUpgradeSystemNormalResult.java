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
package org.l2jmobius.gameserver.network.serverpackets.equipmentupgrade;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.holders.EquipmentUpgradeHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import io.netty.util.collection.IntObjectMap;

public class ExUpgradeSystemNormalResult implements IClientOutgoingPacket
{
	private final int _success;
	private final EquipmentUpgradeHolder _upgrade;
	private final IntObjectMap<Item> _items;
	
	public ExUpgradeSystemNormalResult(EquipmentUpgradeHolder upgrade, boolean success, IntObjectMap<Item> items)
	{
		_upgrade = upgrade;
		_success = success ? 1 : 0;
		_items = items;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_UPGRADE_SYSTEM_NORMAL_RESULT.writeId(packet);
		packet.writeH(1);
		packet.writeD(_upgrade.getId());
		packet.writeC(_success);
		
		packet.writeD(_items.size());
		_items.forEach((objectId, item) ->
		{
			packet.writeD(objectId);
			packet.writeD(item.getId());
			packet.writeD(item.getEnchantLevel());
			packet.writeD((int) item.getCount());
		});
		
		packet.writeH(0);
		packet.writeD(0);
		return true;
	}
}
