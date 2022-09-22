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
package org.l2jmobius.gameserver.network.clientpackets.equipmentupgrade;

import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.EquipmentUpgradeData;
import org.l2jmobius.gameserver.enums.UpgradeType;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.EquipmentUpgradeHolder;
import org.l2jmobius.gameserver.model.holders.ItemEnchantHolder;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.equipmentupgrade.ExUpgradeSystemNormalResult;
import org.l2jmobius.gameserver.network.serverpackets.equipmentupgrade.ExUpgradeSystemResult;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

public class ExUpgradeSystemNormalRequest implements IClientIncomingPacket
{
	private int _objectId;
	private UpgradeType _type;
	private int _upgradeId;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_objectId = packet.readD();
		_type = UpgradeType.ofId(packet.readD());
		_upgradeId = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if ((player == null) || (_type == null))
		{
			return;
		}
		
		final Item existingItem = player.getInventory().getItemByObjectId(_objectId);
		if (existingItem == null)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.FAILED_BECAUSE_THE_TARGET_ITEM_DOES_NOT_EXIST));
			player.sendPacket(new ExUpgradeSystemResult(0, 0));
			return;
		}
		
		final EquipmentUpgradeHolder upgradeHolder = EquipmentUpgradeData.getInstance().getUpgrade(_type, _upgradeId);
		if (upgradeHolder == null)
		{
			player.sendPacket(new SystemMessage(SystemMessageId.FAILED_THE_OPERATION));
			player.sendPacket(new ExUpgradeSystemResult(0, 0));
			return;
		}
		
		for (ItemHolder material : upgradeHolder.getMaterials())
		{
			if (player.getInventory().getInventoryItemCount(material.getId(), -1) < material.getCount())
			{
				player.sendPacket(new SystemMessage(SystemMessageId.FAILED_BECAUSE_THERE_ARE_NOT_ENOUGH_INGREDIENTS));
				player.sendPacket(new ExUpgradeSystemResult(0, 0));
				return;
			}
		}
		
		final long adena = upgradeHolder.getAdena();
		if ((adena > 0) && (player.getAdena() < adena))
		{
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
			player.sendPacket(new ExUpgradeSystemResult(0, 0));
			return;
		}
		
		if ((existingItem.getTemplate().getId() != upgradeHolder.getRequiredItem().getId()) || (existingItem.getEnchantLevel() != upgradeHolder.getRequiredItem().getEnchantLevel()) || existingItem.isAugmented() || (existingItem.getAttributes() != null))
		{
			player.sendPacket(new SystemMessage(SystemMessageId.FAILED_THE_OPERATION));
			player.sendPacket(new ExUpgradeSystemResult(0, 0));
			return;
		}
		
		player.destroyItem("UpgradeEquipment", _objectId, 1, player, true);
		for (ItemHolder material : upgradeHolder.getMaterials())
		{
			player.destroyItemByItemId("UpgradeEquipment", material.getId(), material.getCount(), player, true);
		}
		if (adena > 0)
		{
			player.reduceAdena("UpgradeEquipment", adena, player, true);
		}
		
		final double random = (Rnd.nextDouble() * 100);
		final boolean success = random <= upgradeHolder.getChance();
		IntObjectMap<Item> items = new IntObjectHashMap<>();
		
		if (success)
		{
			for (ItemEnchantHolder item : upgradeHolder.getResult())
			{
				final Item addItem = player.addItem("UpgradeEquipment", item.getId(), item.getCount(), player, false);
				items.put(addItem.getObjectId(), addItem);
				if (item.getEnchantLevel() > 0)
				{
					addItem.setEnchantLevel(item.getEnchantLevel());
				}
				player.sendPacket(new SystemMessage(SystemMessageId.C1_HAS_SUCCEEDED_IN_UPGRADING_EQUIPMENT_AND_OBTAINED_A_S2).addPcName(player).addItemName(addItem));
			}
			if (upgradeHolder.getBonus() != null)
			{
				final double randomBonus = (Rnd.nextDouble() * 100);
				final boolean successBonus = randomBonus <= upgradeHolder.getBonusChance();
				if (successBonus)
				{
					for (ItemEnchantHolder item : upgradeHolder.getBonus())
					{
						final Item addItem = player.addItem("UpgradeEquipment", item.getId(), item.getCount(), player, false);
						items.put(addItem.getObjectId(), addItem);
						if (item.getEnchantLevel() > 0)
						{
							addItem.setEnchantLevel(item.getEnchantLevel());
						}
						player.sendPacket(new SystemMessage(SystemMessageId.C1_HAS_SUCCEEDED_IN_UPGRADING_EQUIPMENT_AND_OBTAINED_A_S2).addPcName(player).addItemName(addItem));
					}
				}
			}
		}
		else
		{
			if (upgradeHolder.getOnFail() != null)
			{
				for (ItemEnchantHolder item : upgradeHolder.getOnFail())
				{
					final Item addItem = player.addItem("UpgradeEquipment", item.getId(), item.getCount(), player, false);
					items.put(addItem.getObjectId(), addItem);
					if (item.getEnchantLevel() > 0)
					{
						addItem.setEnchantLevel(item.getEnchantLevel());
					}
					player.sendPacket(new SystemMessage(SystemMessageId.C1_HAS_SUCCEEDED_IN_UPGRADING_EQUIPMENT_AND_OBTAINED_A_S2).addPcName(player).addItemName(addItem));
				}
			}
		}
		items.forEach((id, item) ->
		{
			item.updateDatabase(true);
		});
		
		player.sendPacket(new InventoryUpdate());
		player.sendPacket(new ExUpgradeSystemNormalResult(upgradeHolder, success, items));
	}
}
