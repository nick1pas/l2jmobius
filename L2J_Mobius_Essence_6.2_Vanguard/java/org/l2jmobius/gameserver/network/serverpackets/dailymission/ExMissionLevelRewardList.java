package org.l2jmobius.gameserver.network.serverpackets.dailymission;

import java.util.List;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.data.xml.MissionLevel;
import org.l2jmobius.gameserver.model.MissionLevelHolder;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.MissionLevelPlayerDataHolder;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Index
 */
public class ExMissionLevelRewardList implements IClientOutgoingPacket
{
	private final String _currentSeason = String.valueOf(MissionLevel.getInstance().getCurrentSeason());
	private final MissionLevelHolder _holder = MissionLevel.getInstance().getMissionBySeason(MissionLevel.getInstance().getCurrentSeason());
	
	private final Player _player;
	private final int _maxNormalLevel;
	
	private List<Integer> _collectedNormalRewards;
	private List<Integer> _collectedKeyRewards;
	private List<Integer> _collectedBonusRewards;
	
	public ExMissionLevelRewardList(Player player)
	{
		_player = player;
		// After normal rewards there will be bonus.
		_maxNormalLevel = _holder.getBonusLevel();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		final MissionLevelPlayerDataHolder info = _player.getMissionLevelProgress();
		_collectedNormalRewards = info.getCollectedNormalRewards();
		_collectedKeyRewards = info.getCollectedKeyRewards();
		_collectedBonusRewards = info.getListOfCollectedBonusRewards();
		
		OutgoingPackets.EX_MISSION_LEVEL_REWARD_LIST.writeId(packet);
		if (info.getCurrentLevel() == 0)
		{
			packet.writeD(1); // 0 -> does not work, -1 -> game crushed
			packet.writeD(3); // Type
			packet.writeD(-1); // Level
			packet.writeD(0); // State
		}
		else
		{
			sendAvailableRewardsList(packet, info);
		}
		packet.writeD(info.getCurrentLevel()); // Level
		packet.writeD(getPercent(info)); // PointPercent
		String year = _currentSeason.substring(0, 4);
		packet.writeD(Integer.parseInt(year)); // SeasonYear
		String month = _currentSeason.substring(4, 6);
		packet.writeD(Integer.parseInt(month)); // SeasonMonth
		packet.writeD(getAvailableRewards(info)); // TotalRewardsAvailable
		if (_holder.getBonusRewardIsAvailable() && _holder.getBonusRewardByLevelUp())
		{
			boolean check = false;
			for (int level = _maxNormalLevel; level <= _holder.getMaxLevel(); level++)
			{
				if ((level <= info.getCurrentLevel()) && !_collectedBonusRewards.contains(level))
				{
					check = true;
					break;
				}
			}
			packet.writeD(check ? 1 : 0); // ExtraRewardsAvailable
		}
		else
		{
			if (_holder.getBonusRewardIsAvailable() && info.getCollectedSpecialReward() && !info.getCollectedBonusReward())
			{
				packet.writeD(1); // ExtraRewardsAvailable
			}
			else
			{
				packet.writeD(0); // ExtraRewardsAvailable
			}
		}
		packet.writeD(0); // RemainSeasonTime / does not work? / not used?
		return true;
	}
	
	private int getAvailableRewards(MissionLevelPlayerDataHolder info)
	{
		int availableRewards = 0;
		for (int level : _holder.getNormalRewards().keySet())
		{
			if ((level <= info.getCurrentLevel()) && !_collectedNormalRewards.contains(level))
			{
				availableRewards++;
			}
		}
		for (int level : _holder.getKeyRewards().keySet())
		{
			if ((level <= info.getCurrentLevel()) && !_collectedKeyRewards.contains(level))
			{
				availableRewards++;
			}
		}
		if (_holder.getBonusRewardIsAvailable() && _holder.getBonusRewardByLevelUp() && info.getCollectedSpecialReward())
		{
			final List<Integer> collectedBonusRewards = info.getListOfCollectedBonusRewards();
			for (int level = _maxNormalLevel; level <= _holder.getMaxLevel(); level++)
			{
				if ((level <= info.getCurrentLevel()) && !collectedBonusRewards.contains(level))
				{
					availableRewards++;
					break;
				}
			}
		}
		else if (_holder.getBonusRewardIsAvailable() && _holder.getBonusRewardByLevelUp() && (info.getCurrentLevel() >= _maxNormalLevel))
		{
			availableRewards++;
		}
		else if (_holder.getBonusRewardIsAvailable() && (info.getCurrentLevel() >= _holder.getMaxLevel()) && !info.getCollectedBonusReward() && info.getCollectedSpecialReward())
		{
			availableRewards++;
		}
		else if ((info.getCurrentLevel() >= _holder.getMaxLevel()) && !info.getCollectedBonusReward())
		{
			availableRewards++;
		}
		return availableRewards;
	}
	
	private int getTotalRewards(MissionLevelPlayerDataHolder info)
	{
		int totalRewards = 0;
		for (int level : _holder.getNormalRewards().keySet())
		{
			if (level <= info.getCurrentLevel())
			{
				totalRewards++;
			}
		}
		for (int level : _holder.getKeyRewards().keySet())
		{
			if (level <= info.getCurrentLevel())
			{
				totalRewards++;
			}
		}
		if (_holder.getBonusRewardByLevelUp() && info.getCollectedSpecialReward() && _holder.getBonusRewardIsAvailable() && (_maxNormalLevel <= info.getCurrentLevel()))
		{
			for (int level = _maxNormalLevel; level <= _holder.getMaxLevel(); level++)
			{
				if (level <= info.getCurrentLevel())
				{
					totalRewards++;
					break;
				}
			}
		}
		else if (info.getCollectedSpecialReward() && _holder.getBonusRewardIsAvailable() && (_maxNormalLevel <= info.getCurrentLevel()))
		{
			totalRewards++;
		}
		else if (_maxNormalLevel <= info.getCurrentLevel())
		{
			totalRewards++;
		}
		return totalRewards;
	}
	
	private int getPercent(MissionLevelPlayerDataHolder info)
	{
		if (info.getCurrentLevel() >= _holder.getMaxLevel())
		{
			return 100;
		}
		return (int) Math.floor(((double) info.getCurrentEXP() / (double) _holder.getXPForSpecifiedLevel(info.getCurrentLevel())) * 100.0);
	}
	
	private void sendAvailableRewardsList(PacketWriter packet, MissionLevelPlayerDataHolder info)
	{
		packet.writeD(getTotalRewards(info)); // PkMissionLevelReward
		for (int level : _holder.getNormalRewards().keySet())
		{
			if (level <= info.getCurrentLevel())
			{
				packet.writeD(1); // Type
				packet.writeD(level); // Level
				packet.writeD(_collectedNormalRewards.contains(level) ? 2 : 1); // State
			}
		}
		for (int level : _holder.getKeyRewards().keySet())
		{
			if (level <= info.getCurrentLevel())
			{
				packet.writeD(2); // Type
				packet.writeD(level); // Level
				packet.writeD(_collectedKeyRewards.contains(level) ? 2 : 1); // State
			}
		}
		if (_holder.getBonusRewardByLevelUp() && info.getCollectedSpecialReward() && _holder.getBonusRewardIsAvailable() && (_maxNormalLevel <= info.getCurrentLevel()))
		{
			packet.writeD(3); // Type
			int sendLevel = 0;
			for (int level = _maxNormalLevel; level <= _holder.getMaxLevel(); level++)
			{
				if ((level <= info.getCurrentLevel()) && !_collectedBonusRewards.contains(level))
				{
					sendLevel = level;
					break;
				}
			}
			packet.writeD(sendLevel == 0 ? _holder.getMaxLevel() : sendLevel); // Level
			packet.writeD(2); // State
		}
		else if (info.getCollectedSpecialReward() && _holder.getBonusRewardIsAvailable() && (_maxNormalLevel <= info.getCurrentLevel()))
		{
			packet.writeD(3); // Type
			packet.writeD(_holder.getMaxLevel()); // Level
			packet.writeD(2); // State
		}
		else if (_maxNormalLevel <= info.getCurrentLevel())
		{
			packet.writeD(3); // Type
			packet.writeD(_holder.getMaxLevel()); // Level
			packet.writeD(info.getCollectedSpecialReward() ? 0 : 1); // State
		}
	}
}
