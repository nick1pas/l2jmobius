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
import org.l2jmobius.gameserver.enums.ClanHallGrade;
import org.l2jmobius.gameserver.model.residences.AbstractResidence;
import org.l2jmobius.gameserver.model.residences.ResidenceFunction;
import org.l2jmobius.gameserver.model.residences.ResidenceFunctionType;
import org.l2jmobius.gameserver.network.OutgoingPackets;

/**
 * @author Steuf
 */
public class AgitDecoInfo implements IClientOutgoingPacket
{
	private final AbstractResidence _residense;
	
	public AgitDecoInfo(AbstractResidence residense)
	{
		_residense = residense;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.AGIT_DECO_INFO.writeId(packet);
		packet.writeD(_residense.getResidenceId());
		// Fireplace
		ResidenceFunction function = _residense.getFunction(ResidenceFunctionType.HP_REGEN);
		if ((function == null) || (function.getLevel() == 0))
		{
			packet.writeC(0);
		}
		else if (((_residense.getGrade() == ClanHallGrade.GRADE_NONE) && (function.getLevel() < 2)) || ((_residense.getGrade() == ClanHallGrade.GRADE_D) && (function.getLevel() < 3)) || ((_residense.getGrade() == ClanHallGrade.GRADE_C) && (function.getLevel() < 4)) || ((_residense.getGrade() == ClanHallGrade.GRADE_B) && (function.getLevel() < 5)))
		{
			packet.writeC(1);
		}
		else
		{
			packet.writeC(2);
		}
		// Carpet - Statue
		function = _residense.getFunction(ResidenceFunctionType.MP_REGEN);
		if ((function == null) || (function.getLevel() == 0))
		{
			packet.writeC(0);
			packet.writeC(0);
		}
		else if ((((_residense.getGrade() == ClanHallGrade.GRADE_NONE) || (_residense.getGrade() == ClanHallGrade.GRADE_D)) && (function.getLevel() < 2)) || ((_residense.getGrade() == ClanHallGrade.GRADE_C) && (function.getLevel() < 3)) || ((_residense.getGrade() == ClanHallGrade.GRADE_B) && (function.getLevel() < 4)))
		{
			packet.writeC(1);
			packet.writeC(1);
		}
		else
		{
			packet.writeC(2);
			packet.writeC(2);
		}
		// Chandelier
		function = _residense.getFunction(ResidenceFunctionType.EXP_RESTORE);
		if ((function == null) || (function.getLevel() == 0))
		{
			packet.writeC(0);
		}
		else if (function.getLevel() < 2)
		{
			packet.writeC(1);
		}
		else
		{
			packet.writeC(2);
		}
		// Mirror
		function = _residense.getFunction(ResidenceFunctionType.TELEPORT);
		if ((function == null) || (function.getLevel() == 0))
		{
			packet.writeC(0);
		}
		else if (function.getLevel() < 2)
		{
			packet.writeC(1);
		}
		else
		{
			packet.writeC(2);
		}
		// Crystal
		packet.writeC(0);
		// Curtain
		function = _residense.getFunction(ResidenceFunctionType.CURTAIN);
		if ((function == null) || (function.getLevel() == 0))
		{
			packet.writeC(0);
		}
		else if (function.getLevel() < 2)
		{
			packet.writeC(1);
		}
		else
		{
			packet.writeC(2);
		}
		// Magic Curtain
		function = _residense.getFunction(ResidenceFunctionType.ITEM);
		if ((function == null) || (function.getLevel() == 0))
		{
			packet.writeC(0);
		}
		else if (((_residense.getGrade() == ClanHallGrade.GRADE_NONE) && (function.getLevel() < 2)) || (function.getLevel() < 3))
		{
			packet.writeC(1);
		}
		else
		{
			packet.writeC(2);
		}
		// Support
		function = _residense.getFunction(ResidenceFunctionType.BUFF);
		if ((function == null) || (function.getLevel() == 0))
		{
			packet.writeC(0);
		}
		else if (((_residense.getGrade() == ClanHallGrade.GRADE_NONE) && (function.getLevel() < 2)) || ((_residense.getGrade() == ClanHallGrade.GRADE_D) && (function.getLevel() < 4)) || ((_residense.getGrade() == ClanHallGrade.GRADE_C) && (function.getLevel() < 5)) || ((_residense.getGrade() == ClanHallGrade.GRADE_B) && (function.getLevel() < 8)))
		{
			packet.writeC(1);
		}
		else
		{
			packet.writeC(2);
		}
		// Flag
		function = _residense.getFunction(ResidenceFunctionType.OUTERFLAG);
		if ((function == null) || (function.getLevel() == 0))
		{
			packet.writeC(0);
		}
		else if (function.getLevel() < 2)
		{
			packet.writeC(1);
		}
		else
		{
			packet.writeC(2);
		}
		// Front platform
		function = _residense.getFunction(ResidenceFunctionType.PLATFORM);
		if ((function == null) || (function.getLevel() == 0))
		{
			packet.writeC(0);
		}
		else if (function.getLevel() < 2)
		{
			packet.writeC(1);
		}
		else
		{
			packet.writeC(2);
		}
		// Item create?
		function = _residense.getFunction(ResidenceFunctionType.ITEM);
		if ((function == null) || (function.getLevel() == 0))
		{
			packet.writeC(0);
		}
		else if (((_residense.getGrade() == ClanHallGrade.GRADE_NONE) && (function.getLevel() < 2)) || (function.getLevel() < 3))
		{
			packet.writeC(1);
		}
		else
		{
			packet.writeC(2);
		}
		return true;
	}
}