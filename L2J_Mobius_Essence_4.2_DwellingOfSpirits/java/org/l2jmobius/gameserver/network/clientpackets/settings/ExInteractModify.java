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
package org.l2jmobius.gameserver.network.clientpackets.settings;

import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.gameserver.model.ClientSettings;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;

/**
 * @author Index
 */
public class ExInteractModify implements IClientIncomingPacket
{
	private int _type;
	private int _settings;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_type = packet.readC();
		_settings = packet.readC();
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
		
		final ClientSettings clientSettings = player.getClientSettings();
		switch (_type)
		{
			case 0:
			{
				clientSettings.setPartyRequestRestrictedFromOthers((_settings & 1) == 1);
				clientSettings.setPartyRequestRestrictedFromClan((_settings & 2) == 2);
				clientSettings.setPartyRequestRestrictedFromFriends((_settings & 4) == 4);
				clientSettings.storeSettings();
				break;
			}
			case 1:
			{
				clientSettings.setFriendRequestRestrictedFromOthers((_settings & 1) == 1);
				clientSettings.setFriendRequestRestrictionFromClan((_settings & 2) == 2);
				clientSettings.storeSettings();
				break;
			}
		}
	}
}
