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
package org.l2jmobius.gameserver.network.serverpackets.magiclamp;

import java.util.Collection;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.holders.MagicLampHolder;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Mobius
 */
public class ExMagicLampGameResult implements IClientOutgoingPacket
{
	private final Collection<MagicLampHolder> _rewards;
	
	public ExMagicLampGameResult(Collection<MagicLampHolder> rewards)
	{
		_rewards = rewards;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_MAGICLAMP_GAME_RESULT.writeId(packet);
		packet.writeD(_rewards.size());
		for (MagicLampHolder lamp : _rewards)
		{
			packet.writeC(lamp.getType().getGrade());
			packet.writeD(lamp.getCount());
			packet.writeQ(lamp.getExp());
			packet.writeQ(lamp.getSp());
		}
		return true;
	}
}