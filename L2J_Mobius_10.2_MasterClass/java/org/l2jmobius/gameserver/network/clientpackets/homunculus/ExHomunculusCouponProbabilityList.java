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

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.data.xml.HomunculusData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Index
 */
public class ExHomunculusCouponProbabilityList implements IClientOutgoingPacket
{
	private final Player _player;
	private final int _couponId;
	
	public ExHomunculusCouponProbabilityList(Player player, int couponId)
	{
		_player = player;
		_couponId = couponId;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		if (_player == null)
		{
			return false;
		}
		
		OutgoingPackets.EX_HOMUNCULUS_COUPON_PROB_LIST.writeId(packet);
		final int size = HomunculusData.getInstance().size();
		packet.writeD(_couponId);
		packet.writeD(size);
		for (int i = 1; i < size; i++)
		{
			if (HomunculusData.getInstance().getTemplate(i).getType() == 0)
			{
				packet.writeD(i);
				packet.writeD(7000000);
			}
		}
		for (int i = 1; i < size; i++)
		{
			if (HomunculusData.getInstance().getTemplate(i).getType() == 1)
			{
				packet.writeD(i);
				packet.writeD(2990000);
			}
		}
		for (int i = 1; i <= size; i++)
		{
			if (HomunculusData.getInstance().getTemplate(i).getType() == 2)
			{
				packet.writeD(i);
				packet.writeD(10000);
			}
		}
		return true;
	}
}
