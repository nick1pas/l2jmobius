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
package org.l2jmobius.gameserver.network.serverpackets.homunculus;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.data.xml.HomunculusCreationData;
import org.l2jmobius.gameserver.data.xml.HomunculusData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Index
 */
public class ExHomunculusCreateProbabilityList implements IClientOutgoingPacket
{
	private final Player _player;
	
	public ExHomunculusCreateProbabilityList(Player player)
	{
		_player = player;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		if (_player == null)
		{
			return false;
		}
		
		OutgoingPackets.EX_HOMUNCULUS_CREATE_PROB_LIST.writeId(packet);
		packet.writeD(HomunculusCreationData.getInstance().getDefaultTemplate().getCreationChance().size());
		for (int type = 0; type < 3; type++)
		{
			for (Double[] homunculusChance : HomunculusCreationData.getInstance().getDefaultTemplate().getCreationChance())
			{
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
