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
import org.l2jmobius.gameserver.model.Shortcut;
import org.l2jmobius.gameserver.network.OutgoingPackets;

public class ShortCutRegister implements IClientOutgoingPacket
{
	private final Shortcut _shortcut;
	
	/**
	 * Register new skill shortcut
	 * @param shortcut
	 */
	public ShortCutRegister(Shortcut shortcut)
	{
		_shortcut = shortcut;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.SHORT_CUT_REGISTER.writeId(packet);
		packet.writeD(_shortcut.getType().ordinal());
		packet.writeD(_shortcut.getSlot() + (_shortcut.getPage() * 12)); // C4 Client
		switch (_shortcut.getType())
		{
			case ITEM:
			{
				packet.writeD(_shortcut.getId());
				break;
			}
			case SKILL:
			{
				packet.writeD(_shortcut.getId());
				packet.writeD(_shortcut.getLevel());
				packet.writeC(0); // C5
				break;
			}
			case ACTION:
			case MACRO:
			case RECIPE:
			{
				packet.writeD(_shortcut.getId());
			}
		}
		packet.writeD(1); // ??
		return true;
	}
}
