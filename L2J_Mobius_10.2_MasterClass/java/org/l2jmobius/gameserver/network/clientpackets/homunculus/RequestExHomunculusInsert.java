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

import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.gameserver.data.xml.HomunculusCreationData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusHPSPVP;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusInsertResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusBirthInfo;

/**
 * @author Mobius
 */
public class RequestExHomunculusInsert implements IClientIncomingPacket
{
	private int _type;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_type = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		int hpPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_HP_POINTS, 0);
		int spPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_SP_POINTS, 0);
		int vpPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_VP_POINTS, 0);
		switch (_type)
		{
			case 0:
			{
				if ((player.getCurrentHp() > HomunculusCreationData.getInstance().getDefaultTemplate().getHPFeeCountByUse()) && (hpPoints < HomunculusCreationData.getInstance().getDefaultTemplate().getHPFeeCount()))
				{
					int newHp = (int) (player.getCurrentHp()) - HomunculusCreationData.getInstance().getDefaultTemplate().getHPFeeCountByUse();
					player.setCurrentHp(newHp, true);
					hpPoints += 1;
					player.getVariables().set(PlayerVariables.HOMUNCULUS_HP_POINTS, hpPoints);
				}
				else
				{
					return;
				}
				break;
			}
			case 1:
			{
				if ((player.getSp() >= HomunculusCreationData.getInstance().getDefaultTemplate().getSPFeeCountByUse()) && (spPoints < HomunculusCreationData.getInstance().getDefaultTemplate().getSPFeeCount()))
				{
					player.setSp(player.getSp() - HomunculusCreationData.getInstance().getDefaultTemplate().getSPFeeCountByUse());
					spPoints += 1;
					player.getVariables().set(PlayerVariables.HOMUNCULUS_SP_POINTS, spPoints);
				}
				else
				{
					return;
				}
				break;
			}
			case 2:
			{
				if ((player.getVitalityPoints() >= HomunculusCreationData.getInstance().getDefaultTemplate().getVPFeeByUse()) && (vpPoints < HomunculusCreationData.getInstance().getDefaultTemplate().getVPFeeCount()))
				{
					int newVitality = player.getVitalityPoints() - HomunculusCreationData.getInstance().getDefaultTemplate().getVPFeeByUse();
					player.setVitalityPoints(newVitality, true);
					vpPoints += 1;
					player.getVariables().set(PlayerVariables.HOMUNCULUS_VP_POINTS, vpPoints);
				}
				else
				{
					return;
				}
				break;
			}
		}
		player.getHomunculusList().refreshStats(true);
		
		player.sendPacket(new ExShowHomunculusBirthInfo(player));
		player.sendPacket(new ExHomunculusHPSPVP(player));
		player.sendPacket(new ExHomunculusInsertResult(_type));
	}
}