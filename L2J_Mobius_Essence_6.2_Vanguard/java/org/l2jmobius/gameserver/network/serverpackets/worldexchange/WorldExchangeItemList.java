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

import java.util.Collections;
import java.util.List;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.enums.AttributeType;
import org.l2jmobius.gameserver.enums.WorldExchangeItemSubType;
import org.l2jmobius.gameserver.model.VariationInstance;
import org.l2jmobius.gameserver.model.ensoul.EnsoulOption;
import org.l2jmobius.gameserver.model.holders.WorldExchangeHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Index
 */
public class WorldExchangeItemList implements IClientOutgoingPacket
{
	public static final WorldExchangeItemList EMPTY_LIST = new WorldExchangeItemList(Collections.emptyList(), null);
	
	private final List<WorldExchangeHolder> _holders;
	private final WorldExchangeItemSubType _type;
	
	public WorldExchangeItemList(List<WorldExchangeHolder> holders, WorldExchangeItemSubType type)
	{
		_holders = holders;
		_type = type;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		if (_holders.isEmpty())
		{
			packet.writeH(0); // Category
			packet.writeC(0); // SortType
			packet.writeD(0); // Page
			packet.writeD(0); // ItemIDList
			return false;
		}
		
		OutgoingPackets.EX_WORLD_EXCHANGE_ITEM_LIST.writeId(packet);
		packet.writeH(_type.getId());
		packet.writeC(0);
		packet.writeD(0);
		packet.writeD(_holders.size());
		for (WorldExchangeHolder holder : _holders)
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
		packet.writeD(-1);
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
			packet.writeD(soul != null ? soul.get(0).getId() : 0);
		}
		catch (IndexOutOfBoundsException ignored)
		{
			packet.writeD(0);
		}
		
		try
		{
			packet.writeD(soul != null ? soul.get(1).getId() : 0);
		}
		catch (IndexOutOfBoundsException ignored)
		{
			packet.writeD(0);
		}
		
		final List<EnsoulOption> specialSoul = (List<EnsoulOption>) holder.getItemInfo().getSoulCrystalSpecialOptions();
		try
		{
			packet.writeD(specialSoul != null ? specialSoul.get(0).getId() : 0);
		}
		catch (IndexOutOfBoundsException ignored)
		{
			packet.writeD(0);
		}
		
		packet.writeH(item.isBlessed() ? 1 : 0);
	}
}
