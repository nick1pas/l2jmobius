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
package org.l2jmobius.gameserver.network.clientpackets.homunculus;

import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.HomunculusCreationData;
import org.l2jmobius.gameserver.data.xml.HomunculusData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.model.homunculus.HomunculusCreationTemplate;
import org.l2jmobius.gameserver.model.homunculus.HomunculusTemplate;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusBirthInfo;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExSummonHomunculusCouponResult;

/**
 * @author NasSeKa, Manax
 */
public class RequestExSummonHomunculusCouponResult implements IClientIncomingPacket
{
	private int _itemId;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_itemId = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.getHomunculusList().size() == player.getAvailableHomunculusSlotCount())
		{
			player.sendPacket(new ExSummonHomunculusCouponResult(0, 0));
			PacketLogger.info("Player " + player.getObjectId() + " " + player.getName() + ", trying create homunculus withouts avaible slots!");
			return;
		}
		
		final HomunculusCreationTemplate creationTemplate = HomunculusCreationData.getInstance().getTemplateByItemId(_itemId);
		if ((creationTemplate == null) || creationTemplate.getItemFee().isEmpty())
		{
			PacketLogger.info("Player " + player.getObjectId() + " " + player.getName() + ", trying create homunculus with not existing coupon!");
			return;
		}
		
		// Take items.
		for (ItemHolder itemHolder : creationTemplate.getItemFee())
		{
			final Item item = player.getInventory().getItemByItemId(itemHolder.getId());
			if ((item == null) || (item.getCount() < itemHolder.getCount()))
			{
				return;
			}
		}
		for (ItemHolder itemHolder : creationTemplate.getItemFee())
		{
			if (!player.destroyItemByItemId("Homunculus Coupon Creation", itemHolder.getId(), itemHolder.getCount(), player, true))
			{
				PacketLogger.info("Player " + player.getObjectId() + " " + player.getName() + ", trying create homunculus without " + itemHolder + "!");
				return;
			}
		}
		
		double chance = Rnd.get(100.0);
		double current = 0;
		int homunculusId = 0;
		while (homunculusId == 0)
		{
			if (chance > creationTemplate.getMaxChance())
			{
				player.sendMessage("Homunculus is not created!");
				player.sendPacket(new ExSummonHomunculusCouponResult(0, 0));
				return;
			}
			for (Double[] homuHolder : creationTemplate.getCreationChance())
			{
				current += homuHolder[1];
				if (current >= chance)
				{
					homunculusId = homuHolder[0].intValue();
					break;
				}
			}
		}
		
		final HomunculusTemplate template = HomunculusData.getInstance().getTemplate(homunculusId);
		if (template == null)
		{
			PacketLogger.warning("Could not find Homunculus template " + homunculusId + ".");
			return;
		}
		
		final Homunculus homunculus = new Homunculus(template, player.getHomunculusList().size(), 1, 0, 0, 0, 0, 0, 0, false);
		if (player.getHomunculusList().add(homunculus))
		{
			player.sendPacket(new ExShowHomunculusBirthInfo(player));
			player.sendPacket(new ExShowHomunculusList(player));
			player.sendPacket(new ExSummonHomunculusCouponResult(1, homunculus.getSlot()));
		}
	}
}