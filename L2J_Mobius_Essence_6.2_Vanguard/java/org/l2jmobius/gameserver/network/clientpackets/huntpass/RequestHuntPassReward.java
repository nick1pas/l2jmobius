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
package org.l2jmobius.gameserver.network.clientpackets.huntpass;

import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.gameserver.data.ItemTable;
import org.l2jmobius.gameserver.data.xml.HuntPassData;
import org.l2jmobius.gameserver.model.HuntPass;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.huntpass.HuntPassInfo;
import org.l2jmobius.gameserver.network.serverpackets.huntpass.HuntPassSayhasSupportInfo;
import org.l2jmobius.gameserver.network.serverpackets.huntpass.HuntPassSimpleInfo;

/**
 * @author Serenitty
 */
public class RequestHuntPassReward implements IClientIncomingPacket
{
	private int _huntPassType;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_huntPassType = packet.readC();
		packet.readC(); // is Premium?
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
		
		final HuntPass huntPass = player.getHuntPass();
		final int normalReward = huntPass.getRewardStep();
		final ItemHolder reward = HuntPassData.getInstance().getRewards().get(normalReward);
		final ItemTemplate itemTemplate = ItemTable.getInstance().getTemplate(reward.getId());
		
		final long weight = itemTemplate.getWeight() * reward.getCount();
		final long slots = itemTemplate.isStackable() ? 1 : reward.getCount();
		if (!player.getInventory().validateWeight(weight) || !player.getInventory().validateCapacity(slots))
		{
			player.sendPacket(SystemMessageId.YOUR_INVENTORY_S_WEIGHT_LIMIT_HAS_BEEN_EXCEEDED_SO_YOU_CAN_T_RECEIVE_THE_REWARD_PLEASE_FREE_UP_SOME_SPACE_AND_TRY_AGAIN);
			return;
		}
		
		updateSayhaTime(player);
		premiumReward(player);
		huntPass.setRewardStep(normalReward + 1);
		huntPass.setRewardAlert(false);
		player.sendPacket(new HuntPassInfo(player, _huntPassType));
		player.sendPacket(new HuntPassSayhasSupportInfo(player));
		player.sendPacket(new HuntPassSimpleInfo(player));
	}
	
	private void updateSayhaTime(Player player)
	{
		final HuntPass huntpass = player.getHuntPass();
		final int normalreward = huntpass.getRewardStep();
		final int premiumreward = huntpass.getPremiumRewardStep();
		final ItemHolder reward = HuntPassData.getInstance().getRewards().get(normalreward);
		
		final int sayhaPoints = 0; // TODO: 0?
		final long count = reward.getCount();
		final int calc = (int) (sayhaPoints + count);
		final boolean isPremium = huntpass.isPremium();
		if (isPremium && (premiumreward < normalreward))
		{
			return;
		}
		
		huntpass.addSayhaTime(calc);
		if (reward.getId() != 72286) // Sayha's Grace Sustention Points
		{
			player.addItem("HuntPassReward", reward, player, true);
		}
		
		final SystemMessage msg = new SystemMessage(SystemMessageId.YOU_RECEIVED_S1_SAYHA_S_GRACE_SUSTENTION_POINTS);
		msg.addInt((int) (count));
		player.sendPacket(msg);
	}
	
	private void premiumReward(Player player)
	{
		final HuntPass huntPass = player.getHuntPass();
		final int rewardStep = huntPass.getPremiumRewardStep();
		final ItemHolder premiumReward = HuntPassData.getInstance().getPremiumRewards().get(rewardStep);
		final boolean isPremium = huntPass.isPremium();
		if (isPremium)
		{
			player.addItem("SeasonPassReward", premiumReward, player, true);
			huntPass.setPremiumRewardStep(rewardStep + 1);
		}
	}
}
