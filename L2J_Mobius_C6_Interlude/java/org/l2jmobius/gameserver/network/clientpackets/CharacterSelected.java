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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.ConnectionState;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.PacketLogger;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.CharSelected;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

@SuppressWarnings("unused")
public class CharacterSelected implements IClientIncomingPacket
{
	private int _charSlot;
	private int _unk1; // new in C4
	private int _unk2;
	private int _unk3;
	private int _unk4;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_charSlot = packet.readD();
		_unk1 = packet.readH();
		_unk2 = packet.readD();
		_unk3 = packet.readD();
		_unk4 = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		// if there is a playback.dat file in the current directory, it will be sent to the client instead of any regular packets
		// to make this work, the first packet in the playback.dat has to be a [S]0x21 packet
		// after playback is done, the client will not work correct and need to exit
		// playLogFile(getConnection()); // try to play LOGGER file
		if (!client.getFloodProtectors().canSelectCharacter())
		{
			return;
		}
		
		// we should always be abble to acquire the lock but if we cant lock then nothing should be done (ie repeated packet)
		if (client.getPlayerLock().tryLock())
		{
			try
			{
				// should always be null but if not then this is repeated packet and nothing should be done here
				if (client.getPlayer() == null)
				{
					// Load up character from disk
					final Player cha = client.loadCharFromDisk(_charSlot);
					if (cha == null)
					{
						PacketLogger.warning("Character could not be loaded (slot:" + _charSlot + ")");
						client.sendPacket(ActionFailed.STATIC_PACKET);
						return;
					}
					
					if (cha.getAccessLevel().getLevel() < 0)
					{
						cha.deleteMe();
						return;
					}
					
					if (Config.FACTION_SYSTEM_ENABLED && Config.FACTION_BALANCE_ONLINE_PLAYERS)
					{
						if (cha.isGood() && (World.getInstance().getAllGoodPlayers().size() >= (World.getInstance().getAllEvilPlayers().size() + Config.FACTION_BALANCE_PLAYER_EXCEED_LIMIT)))
						{
							final NpcHtmlMessage msg = new NpcHtmlMessage(0);
							msg.setFile("data/html/mods/Faction/ExceededOnlineLimit.htm");
							msg.replace("%more%", Config.FACTION_GOOD_TEAM_NAME);
							msg.replace("%less%", Config.FACTION_EVIL_TEAM_NAME);
							client.sendPacket(msg);
							return;
						}
						if (cha.isEvil() && (World.getInstance().getAllEvilPlayers().size() >= (World.getInstance().getAllGoodPlayers().size() + Config.FACTION_BALANCE_PLAYER_EXCEED_LIMIT)))
						{
							final NpcHtmlMessage msg = new NpcHtmlMessage(0);
							msg.setFile("data/html/mods/Faction/ExceededOnlineLimit.htm");
							msg.replace("%more%", Config.FACTION_EVIL_TEAM_NAME);
							msg.replace("%less%", Config.FACTION_GOOD_TEAM_NAME);
							client.sendPacket(msg);
							return;
						}
					}
					
					cha.setClient(client);
					client.setPlayer(cha);
					client.setConnectionState(ConnectionState.ENTERING);
					client.sendPacket(new CharSelected(cha, client.getSessionId().playOkID1));
				}
			}
			catch (Exception e)
			{
				PacketLogger.warning(getClass().getSimpleName() + ": " + e.getMessage());
			}
			finally
			{
				client.getPlayerLock().unlock();
			}
		}
	}
}