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
package org.l2jmobius.gameserver.network.clientpackets.dailymission;

import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.gameserver.data.xml.MissionLevel;
import org.l2jmobius.gameserver.model.MissionLevelHolder;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.holders.MissionLevelPlayerDataHolder;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jmobius.gameserver.network.serverpackets.dailymission.ExMissionLevelRewardList;

/**
 * @author Index
 */
public class RequestMissionLevelReceiveReward implements IClientIncomingPacket
{
	private final MissionLevelHolder _holder = MissionLevel.getInstance().getMissionBySeason(MissionLevel.getInstance().getCurrentSeason());
	private int _level;
	private int _rewardType;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_level = packet.readD();
		_rewardType = packet.readD();
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
		
		final MissionLevelPlayerDataHolder info = player.getMissionLevelProgress();
		switch (_rewardType)
		{
			case 1:
			{
				if (!_holder.getNormalRewards().containsKey(_level) || info.getCollectedNormalRewards().contains(_level) || ((info.getCurrentLevel() != _level) && (info.getCurrentLevel() < _level)))
				{
					return;
				}
				
				final ItemHolder reward = _holder.getNormalRewards().get(_level);
				player.addItem("Mission Level", reward.getId(), reward.getCount(), null, true);
				info.addToCollectedNormalRewards(_level);
				info.storeInfoInVariable(player);
				break;
			}
			case 2:
			{
				if (!_holder.getKeyRewards().containsKey(_level) || info.getCollectedKeyRewards().contains(_level) || ((info.getCurrentLevel() != _level) && (info.getCurrentLevel() < _level)))
				{
					return;
				}
				
				final ItemHolder reward = _holder.getKeyRewards().get(_level);
				player.addItem("Mission Level", reward.getId(), reward.getCount(), null, true);
				info.addToCollectedKeyReward(_level);
				info.storeInfoInVariable(player);
				break;
			}
			case 3:
			{
				if ((_holder.getSpecialReward() == null) || info.getCollectedSpecialReward() || ((info.getCurrentLevel() != _level) && (info.getCurrentLevel() < _level)))
				{
					return;
				}
				
				final ItemHolder reward = _holder.getSpecialReward();
				player.addItem("Mission Level", reward.getId(), reward.getCount(), null, true);
				info.setCollectedSpecialReward(true);
				info.storeInfoInVariable(player);
				break;
			}
			case 4:
			{
				if (!_holder.getBonusRewardIsAvailable() || (_holder.getBonusReward() == null) || !info.getCollectedSpecialReward() || info.getCollectedBonusReward() || ((info.getCurrentLevel() != _level) && (info.getCurrentLevel() < _level)))
				{
					return;
				}
				
				if (_holder.getBonusRewardByLevelUp())
				{
					int maxNormalLevel = _holder.getBonusLevel();
					int availableReward = -1;
					for (int level = maxNormalLevel; level <= _holder.getMaxLevel(); level++)
					{
						if ((level <= info.getCurrentLevel()) && !info.getListOfCollectedBonusRewards().contains(level))
						{
							availableReward = level;
							break;
						}
					}
					if (availableReward != -1)
					{
						info.addToListOfCollectedBonusRewards(availableReward);
					}
					else
					{
						return;
					}
				}
				else
				{
					info.setCollectedBonusReward(true);
				}
				
				final ItemHolder reward = _holder.getBonusReward();
				player.addItem("Mission Level", reward.getId(), reward.getCount(), null, true);
				info.storeInfoInVariable(player);
				break;
			}
		}
		
		player.sendPacket(new ExMissionLevelRewardList(player));
	}
}
