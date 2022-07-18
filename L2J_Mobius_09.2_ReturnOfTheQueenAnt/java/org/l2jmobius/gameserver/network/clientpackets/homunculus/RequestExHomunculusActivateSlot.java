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

import java.util.List;
import java.util.logging.Logger;

import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.gameserver.data.xml.HomunculusSlotData;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.homunculus.HomunculusSlotTemplate;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExActivateHomunculusResult;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExHomunculusPointInfo;
import org.l2jmobius.gameserver.network.serverpackets.homunculus.ExShowHomunculusList;

/**
 * @author Manax
 */
public class RequestExHomunculusActivateSlot implements IClientIncomingPacket
{
	private int _slot;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_slot = packet.readD();
		// _activate = packet.readC() == 1; // enabled?
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player activeChar = client.getPlayer();
		if (activeChar == null)
		{
			return;
		}
		
		final int size = activeChar.getHomunculusList().size();
		final HomunculusSlotTemplate template = HomunculusSlotData.getInstance().getTemplate(_slot);
		if ((size != 0) && ((activeChar.getHomunculusList().get(_slot) != null) || (_slot == activeChar.getAvailableHomunculusSlotCount())))
		{
			PacketLogger.info(getClass().getSimpleName() + " player " + activeChar.getName() + " " + activeChar.getObjectId() + " trying unlock already unlocked slot!");
			activeChar.sendPacket(new ExActivateHomunculusResult(false));
			return;
		}
		if (!template.getSlotEnabled())
		{
			Logger.getLogger(getClass().getSimpleName() + " player " + activeChar.getName() + " " + activeChar.getObjectId() + " trying unlock disabled slot!");
			activeChar.sendPacket(new ExActivateHomunculusResult(false));
			return;
		}
		
		final List<ItemHolder> fee = template.getPrice();
		for (ItemHolder feeHolder : fee)
		{
			if ((activeChar.getInventory().getItemByItemId(feeHolder.getId()) == null) || ((activeChar.getInventory().getItemByItemId(feeHolder.getId()) != null) && (activeChar.getInventory().getItemByItemId(feeHolder.getId()).getCount() < feeHolder.getCount())))
			{
				activeChar.sendPacket(new ExActivateHomunculusResult(false));
				return;
			}
		}
		for (ItemHolder feeHolder : fee)
		{
			if (!activeChar.destroyItemByItemId("Homunclus slot unlock", feeHolder.getId(), feeHolder.getCount(), activeChar, true))
			{
				Logger.getLogger(getClass().getSimpleName() + " player " + activeChar.getName() + " " + activeChar.getObjectId() + " trying unlock slot without items!");
				activeChar.sendPacket(new ExActivateHomunculusResult(false));
				return;
			}
		}
		
		activeChar.broadcastUserInfo();
		activeChar.getVariables().set(PlayerVariables.HOMUNCULUS_OPENED_SLOT_COUNT, _slot);
		activeChar.sendPacket(new ExHomunculusPointInfo(activeChar));
		activeChar.sendPacket(new ExShowHomunculusList(activeChar));
		activeChar.sendPacket(new ExActivateHomunculusResult(true));
	}
}
