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
package org.l2jmobius.gameserver.network.serverpackets.enchant.multi;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.request.EnchantItemRequest;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Index
 */
public class ExResultMultiEnchantItemList implements IClientOutgoingPacket
{
	public static final int SUCCESS = 0;
	public static final int FAIL = 1;
	public static final int ERROR = 2;
	private final Player _player;
	private boolean _error;
	private boolean _isResult;
	private Map<Integer, int[]> _successEnchant = new HashMap<>();
	private Map<Integer, Integer> _failureEnchant = new HashMap<>();
	private Map<Integer, ItemHolder> _failureReward = new HashMap<>();
	
	public ExResultMultiEnchantItemList(Player player, boolean error)
	{
		_player = player;
		_error = error;
	}
	
	public ExResultMultiEnchantItemList(Player player, Map<Integer, ItemHolder> failureReward)
	{
		_player = player;
		_failureReward = failureReward;
	}
	
	public ExResultMultiEnchantItemList(Player player, Map<Integer, int[]> successEnchant, Map<Integer, Integer> failureEnchant)
	{
		_player = player;
		_successEnchant = successEnchant;
		_failureEnchant = failureEnchant;
	}
	
	public ExResultMultiEnchantItemList(Player player, Map<Integer, int[]> successEnchant, Map<Integer, Integer> failureEnchant, boolean isResult)
	{
		_player = player;
		_successEnchant = successEnchant;
		_failureEnchant = failureEnchant;
		_isResult = isResult;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		if (_player.getRequest(EnchantItemRequest.class) == null)
		{
			return false;
		}
		final EnchantItemRequest request = _player.getRequest(EnchantItemRequest.class);
		
		OutgoingPackets.EX_RES_MULTI_ENCHANT_ITEM_LIST.writeId(packet);
		
		if (_error)
		{
			packet.writeC(0);
			return true;
		}
		packet.writeC(1);
		
		/* EnchantSuccessItem */
		if (_failureReward.size() == 0)
		{
			packet.writeD(_successEnchant.size());
			if (_successEnchant.size() != 0)
			{
				for (int i : _successEnchant.keySet())
				{
					int[] intArray = _successEnchant.get(i);
					packet.writeD(intArray[0]);
					packet.writeD(intArray[1]);
				}
			}
		}
		else
		{
			packet.writeD(0);
		}
		
		/* EnchantFailItem */
		packet.writeD(_failureEnchant.size());
		if (_failureEnchant.size() != 0)
		{
			for (int i : _failureEnchant.keySet())
			{
				packet.writeD(_failureEnchant.get(i));
				packet.writeD(0);
			}
		}
		else
		{
			packet.writeD(0);
		}
		
		/* EnchantFailRewardItem */
		if (((_successEnchant.size() == 0) && (request.getMultiFailItemsCount() != 0)) || (_isResult && (request.getMultiFailItemsCount() != 0)))
		{
			packet.writeD(request.getMultiFailItemsCount());
			_failureReward = request.getMultiEnchantFailItems();
			for (int i : _failureReward.keySet())
			{
				ItemHolder itemHolder = _failureReward.get(i);
				packet.writeD(itemHolder.getId());
				packet.writeD((int) itemHolder.getCount());
			}
			if (_isResult)
			{
				request.clearMultiSuccessEnchantList();
				request.clearMultiFailureEnchantList();
			}
			request.clearMultiFailReward();
		}
		else
		{
			packet.writeD(0);
		}
		
		/* EnchantFailChallengePointInfo */
		packet.writeD(1);
		packet.writeD(0);
		packet.writeD(0);
		
		return true;
	}
}
