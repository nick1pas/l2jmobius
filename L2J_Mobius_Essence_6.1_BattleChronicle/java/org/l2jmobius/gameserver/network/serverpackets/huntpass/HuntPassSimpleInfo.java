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
package org.l2jmobius.gameserver.network.serverpackets.huntpass;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.HuntPass;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Serenitty
 */
public class HuntPassSimpleInfo implements IClientOutgoingPacket
{
	private final HuntPass _huntPassInfo;
	
	public HuntPassSimpleInfo(Player player)
	{
		_huntPassInfo = player.getHuntPass();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_L2PASS_SIMPLE_INFO.writeId(packet);
		
		packet.writeD(1); // passInfos
		packet.writeC(0);
		packet.writeC(1); // isOn
		
		packet.writeC(_huntPassInfo.rewardAlert() ? 1 : 0);
		packet.writeD(0);
		return true;
	}
}
