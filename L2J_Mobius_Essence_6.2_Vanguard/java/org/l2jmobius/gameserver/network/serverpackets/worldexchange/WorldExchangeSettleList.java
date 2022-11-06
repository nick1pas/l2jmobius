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

import java.util.List;
import java.util.Map;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.enums.AttributeType;
import org.l2jmobius.gameserver.enums.WorldExchangeItemStatusType;
import org.l2jmobius.gameserver.instancemanager.WorldExchangeManager;
import org.l2jmobius.gameserver.model.VariationInstance;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.ensoul.EnsoulOption;
import org.l2jmobius.gameserver.model.holders.WorldExchangeHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Index
 */
public class WorldExchangeSettleList implements IClientOutgoingPacket
{
	private final Player _player;
	
	public WorldExchangeSettleList(Player player)
	{
		_player = player;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_WORLD_EXCHANGE_SETTLE_LIST.writeId(packet);
		
		final Map<WorldExchangeItemStatusType, List<WorldExchangeHolder>> holders = WorldExchangeManager.getInstance().getPlayerBids(_player.getObjectId());
		if (holders.isEmpty())
		{
			packet.writeD(0); // RegiItemDataList
			packet.writeD(0); // RecvItemDataList
			packet.writeD(0); // TimeOutItemDataList
			return false;
		}
		
		packet.writeD(holders.get(WorldExchangeItemStatusType.WORLD_EXCHANGE_REGISTERED).size());
		for (WorldExchangeHolder holder : holders.get(WorldExchangeItemStatusType.WORLD_EXCHANGE_REGISTERED))
		{
			getItemInfo(packet, holder);
		}
		
		packet.writeD(holders.get(WorldExchangeItemStatusType.WORLD_EXCHANGE_SOLD).size());
		for (WorldExchangeHolder holder : holders.get(WorldExchangeItemStatusType.WORLD_EXCHANGE_SOLD))
		{
			getItemInfo(packet, holder);
		}
		
		packet.writeD(holders.get(WorldExchangeItemStatusType.WORLD_EXCHANGE_OUT_TIME).size());
		for (WorldExchangeHolder holder : holders.get(WorldExchangeItemStatusType.WORLD_EXCHANGE_OUT_TIME))
		{
			getItemInfo(packet, holder);
		}
		return true;
	}
	
	private void getItemInfo(PacketWriter packet, WorldExchangeHolder holder)
	{
		packet.writeQ(holder.getWorldExchangeId());
		packet.writeQ(holder.getPrice());
		packet.writeD((int) (holder.getEndTime() / 1000L));
		Item item = holder.getItemInstance();
		packet.writeD(item.getId());
		packet.writeQ(item.getCount());
		packet.writeD(item.getEnchantLevel() < 1 ? 0 : item.getEnchantLevel());
		VariationInstance iv = item.getAugmentation();
		packet.writeD(iv != null ? iv.getOption1Id() : 0);
		packet.writeD(iv != null ? iv.getOption2Id() : 0);
		packet.writeD(-1); // IntensiveItemClassID
		packet.writeH(item.getAttackAttribute() != null ? item.getAttackAttribute().getType().getClientId() : 0);
		packet.writeH(item.getAttackAttribute() != null ? item.getAttackAttribute().getValue() : 0);
		packet.writeH(item.getDefenceAttribute(AttributeType.FIRE));
		packet.writeH(item.getDefenceAttribute(AttributeType.WATER));
		packet.writeH(item.getDefenceAttribute(AttributeType.WIND));
		packet.writeH(item.getDefenceAttribute(AttributeType.EARTH));
		packet.writeH(item.getDefenceAttribute(AttributeType.HOLY));
		packet.writeH(item.getDefenceAttribute(AttributeType.DARK));
		packet.writeD(item.getVisualId());
		
		final List<EnsoulOption> soul = (List<EnsoulOption>) holder.getItemInfo().getSoulCrystalOptions();
		try
		{
			packet.writeD(soul.get(0).getId());
		}
		catch (IndexOutOfBoundsException ignored)
		{
			packet.writeD(0);
		}
		
		try
		{
			packet.writeD(soul.get(1).getId());
		}
		catch (IndexOutOfBoundsException ignored)
		{
			packet.writeD(0);
		}
		
		final List<EnsoulOption> specialSoul = (List<EnsoulOption>) holder.getItemInfo().getSoulCrystalSpecialOptions();
		try
		{
			packet.writeD(specialSoul.get(0).getId());
		}
		catch (IndexOutOfBoundsException ignored)
		{
			packet.writeD(0);
		}
		
		packet.writeH(item.isBlessed() ? 1 : 0);
	}
}
