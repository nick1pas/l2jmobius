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

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.stats.Stat;
import org.l2jmobius.gameserver.network.OutgoingPackets;

public class RecipeShopItemInfo implements IClientOutgoingPacket
{
	private final Player _player;
	private final int _recipeId;
	private final double _craftRate;
	private final double _craftCritical;
	
	public RecipeShopItemInfo(Player player, int recipeId)
	{
		_player = player;
		_recipeId = recipeId;
		_craftRate = _player.getStat().getValue(Stat.CRAFT_RATE, 0);
		_craftCritical = _player.getStat().getValue(Stat.CRAFTING_CRITICAL, 0);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.RECIPE_SHOP_ITEM_INFO.writeId(packet);
		packet.writeD(_player.getObjectId());
		packet.writeD(_recipeId);
		packet.writeD((int) _player.getCurrentMp());
		packet.writeD(_player.getMaxMp());
		packet.writeD(0xffffffff); // item creation none/success/failed
		packet.writeQ(0); // manufacturePrice
		packet.writeC(0); // Trigger offering window if 1
		packet.writeQ(0); // Adena worth of items for maximum offering.
		packet.writeF(Math.min(_craftRate, 100.0));
		packet.writeC(_craftCritical > 0 ? 1 : 0);
		packet.writeF(Math.min(_craftCritical, 100.0));
		packet.writeC(0); // find me
		return true;
	}
}
