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
import org.l2jmobius.gameserver.data.xml.HomunculusCreationData;
import org.l2jmobius.gameserver.data.xml.HomunculusData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.HomunculusCreationTemplate;
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
		
		HomunculusCreationTemplate creationTemplate = null;
		for (int i = 1; i < HomunculusCreationData.getInstance().size(); i++)
		{
			if (HomunculusCreationData.getInstance().getTemplate(i).isInstanceHaveCoupon(_couponId))
			{
				creationTemplate = HomunculusCreationData.getInstance().getTemplate(i);
			}
		}
		if (creationTemplate == null)
		{
			return false;
		}
		
		OutgoingPackets.EX_HOMUNCULUS_COUPON_PROB_LIST.writeId(packet);
		final int size = creationTemplate.getCreationChance().size();
		packet.writeD(_couponId);
		packet.writeD(size);
		for (int type = 0; type < 3; type++)
		{
			for (int i = 0; i < size; i++)
			{
				final Double[] homunculusChance = creationTemplate.getCreationChance().get(i);
				if (HomunculusData.getInstance().getTemplate(homunculusChance[0].intValue()).getType() == type)
				{
					packet.writeD(homunculusChance[0].intValue());
					packet.writeD((int) (homunculusChance[1] * 1000000));
				}
			}
		}
		return true;
	}
}
