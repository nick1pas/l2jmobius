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
package org.l2jmobius.gameserver.network.serverpackets.huntpass;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.HuntPass;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Serenitty
 */
public class HuntPassInfo implements IClientOutgoingPacket
{
	private final int _interfaceType;
	private final HuntPass _huntPass;
	private final int _timeEnd;
	private final int _isPremium;
	private final int _points;
	private final int _step;
	private final int _rewardStep;
	private final int _premiumRewardStep;
	
	public HuntPassInfo(Player player, int interfaceType)
	{
		
		_interfaceType = interfaceType;
		_huntPass = player.getHuntPass();
		_timeEnd = _huntPass.getHuntPassDayEnd();
		_isPremium = _huntPass.isPremium() ? 1 : 0;
		_points = _huntPass.getPoints();
		_step = _huntPass.getCurrentStep();
		_rewardStep = _huntPass.getRewardStep();
		_premiumRewardStep = _huntPass.getPremiumRewardStep();
		
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_L2PASS_INFO.writeId(packet);
		packet.writeC(_interfaceType);
		packet.writeD(_timeEnd); // LeftTime
		packet.writeC(_isPremium); // Premium
		packet.writeD(_points); // Points
		packet.writeD(_step); // CurrentStep
		packet.writeD(_rewardStep); // Reward
		packet.writeD(_premiumRewardStep); // PremiumReward
		return true;
	}
}
