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
package org.l2jmobius.gameserver.network.serverpackets.enchant.single;

import static org.l2jmobius.gameserver.model.stats.Stat.ENCHANT_RATE;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.data.xml.EnchantItemData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.request.EnchantItemRequest;
import org.l2jmobius.gameserver.model.item.enchant.EnchantScroll;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Index
 */
public class ChangedEnchantTargetItemProbabilityList implements IClientOutgoingPacket
{
	private final Player _player;
	private final boolean _isMulti;
	
	public ChangedEnchantTargetItemProbabilityList(Player player, Boolean isMulti)
	{
		_player = player;
		_isMulti = isMulti;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		if (_player.getRequest(EnchantItemRequest.class) == null)
		{
			return false;
		}
		final EnchantItemRequest request = _player.getRequest(EnchantItemRequest.class);
		
		if ((!_isMulti && (request.getEnchantingItem() == null)) || request.isProcessing() || (request.getEnchantingScroll() == null))
		{
			return false;
		}
		
		int count = 1;
		if (_isMulti)
		{
			count = request.getMultiEnchantingItemsCount();
		}
		
		final double supportRate = getSupportRate(request);
		final double passiveRate = getPassiveRate();
		
		OutgoingPackets.EX_CHANGED_ENCHANT_TARGET_ITEM_PROB_LIST.writeId(packet);
		packet.writeD(count);
		for (int i = 1; i <= count; i++)
		{
			// 100,00 % = 10000, because last 2 numbers going after float comma.
			double baseRate;
			if (!_isMulti || (request.getMultiEnchantingItemsBySlot(i) != 0))
			{
				baseRate = getBaseRate(request, i);
			}
			else
			{
				baseRate = 0;
			}
			double totalRate = baseRate + supportRate + passiveRate;
			if (totalRate >= 10000)
			{
				totalRate = 10000;
			}
			if (!_isMulti)
			{
				packet.writeD(request.getEnchantingItem().getObjectId());
			}
			else
			{
				packet.writeD(request.getMultiEnchantingItemsBySlot(i));
			}
			packet.writeD((int) totalRate); // Total success.
			packet.writeD((int) baseRate); // Base success.
			packet.writeD((int) supportRate); // Support success.
			packet.writeD((int) passiveRate); // Passive success (items, skills).
		}
		return true;
	}
	
	private int getBaseRate(EnchantItemRequest request, int iteration)
	{
		final EnchantScroll enchantScroll = EnchantItemData.getInstance().getEnchantScroll(request.getEnchantingScroll());
		return (int) Math.min(100, enchantScroll.getChance(_player, _isMulti ? _player.getInventory().getItemByObjectId(request.getMultiEnchantingItemsBySlot(iteration)) : request.getEnchantingItem()) + enchantScroll.getBonusRate()) * 100;
	}
	
	private int getSupportRate(EnchantItemRequest request)
	{
		double supportRate = 0;
		if (!_isMulti && (request.getSupportItem() != null))
		{
			supportRate = EnchantItemData.getInstance().getSupportItem(request.getSupportItem()).getBonusRate();
			supportRate = supportRate * 100;
		}
		return (int) supportRate;
	}
	
	private int getPassiveRate()
	{
		double passiveRate = 0;
		if (_player.getStat().getValue(ENCHANT_RATE) != 0)
		{
			passiveRate = _player.getStat().getValue(ENCHANT_RATE);
			passiveRate = passiveRate * 100;
		}
		return (int) passiveRate;
	}
}