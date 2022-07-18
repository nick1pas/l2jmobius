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
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.HomunculusCreationData;
import org.l2jmobius.gameserver.data.xml.HomunculusData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.homunculus.Homunculus;
import org.l2jmobius.gameserver.model.homunculus.HomunculusTemplate;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusSummonResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusBirthInfo;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;

/**
 * @author Mobius
 */
public class RequestExHomunculusSummon implements IClientIncomingPacket
{
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		// packet.readC();
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
		
		final int hpPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_HP_POINTS, 0);
		final int spPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_SP_POINTS, 0);
		final int vpPoints = player.getVariables().getInt(PlayerVariables.HOMUNCULUS_VP_POINTS, 0);
		final int homunculusCreateTime = (int) (player.getVariables().getLong(PlayerVariables.HOMUNCULUS_CREATION_TIME, 0) / 1000);
		
		if ((homunculusCreateTime > 0) && ((System.currentTimeMillis() / 1000) >= homunculusCreateTime) && (hpPoints == HomunculusCreationData.getInstance().getDefaultTemplate().getHPFeeCount()) && (spPoints == HomunculusCreationData.getInstance().getDefaultTemplate().getSPFeeCount()) && (vpPoints == HomunculusCreationData.getInstance().getDefaultTemplate().getVPFeeCount()))
		{
			double chance = Rnd.get(100.0);
			double current = 0;
			int homunculusId = 0;
			while (homunculusId == 0)
			{
				if (chance > HomunculusCreationData.getInstance().getDefaultTemplate().getMaxChance())
				{
					player.sendMessage("Homunculus is not created!");
					player.sendPacket(new ExHomunculusSummonResult(0));
					return;
				}
				for (Double[] homuHolder : HomunculusCreationData.getInstance().getDefaultTemplate().getCreationChance())
				{
					current += homuHolder[1];
					if (current >= chance)
					{
						homunculusId = homuHolder[0].intValue();
						break;
					}
				}
			}
			
			final HomunculusTemplate template = HomunculusData.getInstance().getTemplate(homunculusId);
			if (template == null)
			{
				PacketLogger.warning("Counld not find Homunculus template " + homunculusId + ".");
				return;
			}
			
			final Homunculus homunculus = new Homunculus(template, player.getHomunculusList().size(), 1, 0, 0, 0, 0, 0, 0, false);
			if (player.getHomunculusList().add(homunculus))
			{
				player.getVariables().set(PlayerVariables.HOMUNCULUS_CREATION_TIME, 0);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_HP_POINTS, 0);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_SP_POINTS, 0);
				player.getVariables().set(PlayerVariables.HOMUNCULUS_VP_POINTS, 0);
				player.sendPacket(new ExShowHomunculusBirthInfo(player));
				player.sendPacket(new ExHomunculusSummonResult(1));
				player.sendPacket(new ExShowHomunculusList(player));
			}
		}
	}
}
