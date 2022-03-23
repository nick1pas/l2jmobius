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
package org.l2jmobius.gameserver.network.serverpackets.balthusevent;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.instancemanager.events.BalthusEventManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * Consolation prize changing in SysTextures/ui6.ugx file "RewardClip.as" -> configUI -> this.tokenItemID = 49783;
 * @author Index
 */
public class ExBalthusEvent implements IClientOutgoingPacket
{
	private final Player _player;
	
	public ExBalthusEvent(Player player)
	{
		_player = player;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_BALTHUS_EVENT.writeId(packet);
		packet.writeD(BalthusEventManager.getInstance().getCurrentState()); // CurrentState (max 24, because 1 state going 1 hour)
		packet.writeD(BalthusEventManager.getInstance().getCurrentProgress()); // Progress
		packet.writeD(BalthusEventManager.getInstance().getCurrRewardItem()); // CurrentRewardItem (current event item, what can be rewarded)
		packet.writeD(_player.getVariables().getInt(PlayerVariables.BALTHUS_REWARD, 0)); // RewardTokenCount (current items for withdraw (available rewards))
		packet.writeD((int) BalthusEventManager.getInstance().getConsolation().getCount()); // CurrentTokenCount (current count of "consolation prize")
		packet.writeD(BalthusEventManager.getInstance().isPlayerParticipant(_player) ? 1 : 0); // Participated (player in event?)
		packet.writeC(BalthusEventManager.getInstance().isRunning() ? 0 : 1); // Running (0 - already someone get this reward ? / 1 - item can be rewarded)
		packet.writeD(BalthusEventManager.getInstance().getTime()); // Time (in seconds)
		return true;
	}
	
}
