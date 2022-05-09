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
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Mobius
 */
public class ExShowHomunculusList implements IClientOutgoingPacket
{
	private final Player _player;
	
	public ExShowHomunculusList(Player player)
	{
		_player = player;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		if ((_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_OPENED_SLOT_COUNT, 0) == 0) || (_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_OPENED_SLOT_COUNT) == 1) || (_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_OPENED_SLOT_COUNT) == 2))
		{
			if ((_player.getHomunculusList() != null) && (_player.getHomunculusList().size() != 0) && (_player.getHomunculusList().size() < 2))
			{
				if (_player.getVariables().getInt(PlayerVariables.HOMUNCULUS_CREATION_TIME, 0) >= 0)
				{
					_player.getVariables().set(PlayerVariables.HOMUNCULUS_OPENED_SLOT_COUNT, _player.getHomunculusList().size() + 1);
				}
				else
				{
					_player.getVariables().set(PlayerVariables.HOMUNCULUS_OPENED_SLOT_COUNT, _player.getHomunculusList().size());
				}
			}
			else
			{
				_player.getVariables().set(PlayerVariables.HOMUNCULUS_OPENED_SLOT_COUNT, 3);
			}
		}
		
		final int slotCount = _player.getVariables().getInt(PlayerVariables.HOMUNCULUS_OPENED_SLOT_COUNT);
		OutgoingPackets.EX_SHOW_HOMUNCULUS_LIST.writeId(packet);
		packet.writeD(slotCount);
		int counter = 0;
		for (int i = 0; i <= slotCount; i++)
		{
			if (_player.getHomunculusList().get(i) != null)
			{
				final Homunculus homunculus = _player.getHomunculusList().get(i);
				packet.writeD(counter); // slot
				packet.writeD(homunculus.getId()); // homunculus id
				packet.writeD(homunculus.getType());
				packet.writeC(homunculus.isActive() ? 1 : 0);
				packet.writeD(homunculus.getTemplate().getBasicSkillId());
				packet.writeD(homunculus.getSkillLevel1() > 0 ? homunculus.getTemplate().getSkillId1() : 0);
				packet.writeD(homunculus.getSkillLevel2() > 0 ? homunculus.getTemplate().getSkillId2() : 0);
				packet.writeD(homunculus.getSkillLevel3() > 0 ? homunculus.getTemplate().getSkillId3() : 0);
				packet.writeD(homunculus.getSkillLevel4() > 0 ? homunculus.getTemplate().getSkillId4() : 0);
				packet.writeD(homunculus.getSkillLevel5() > 0 ? homunculus.getTemplate().getSkillId5() : 0);
				packet.writeD(homunculus.getTemplate().getBasicSkillLevel());
				packet.writeD(homunculus.getSkillLevel1());
				packet.writeD(homunculus.getSkillLevel2());
				packet.writeD(homunculus.getSkillLevel3());
				packet.writeD(homunculus.getSkillLevel4());
				packet.writeD(homunculus.getSkillLevel5());
				packet.writeD(homunculus.getLevel());
				packet.writeD(homunculus.getExp());
				packet.writeD(homunculus.getHp());
				packet.writeD(homunculus.getAtk());
				packet.writeD(homunculus.getDef());
				packet.writeD(homunculus.getCritRate());
			}
			else
			{
				packet.writeD(counter); // slot
				packet.writeD(0); // homunculus id
				packet.writeD(0);
				packet.writeC(0);
				packet.writeD(0);
				for (int j = 1; j <= 5; j++)
				{
					packet.writeD(0);
				}
				packet.writeD(0);
				for (int j = 1; j <= 5; j++)
				{
					packet.writeD(0);
				}
				packet.writeD(0);// m_nLevel
				packet.writeD(0);// m_nHP
				packet.writeD(0);// m_nHP
				packet.writeD(0);// m_nAttack
				packet.writeD(0);// m_nDefence
				packet.writeD(0);// m_nCritical
			}
			counter++;
		}
		
		return true;
	}
}
