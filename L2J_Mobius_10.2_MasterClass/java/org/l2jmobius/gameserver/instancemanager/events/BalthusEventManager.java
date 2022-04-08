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
package org.l2jmobius.gameserver.instancemanager.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.MailType;
import org.l2jmobius.gameserver.instancemanager.MailManager;
import org.l2jmobius.gameserver.model.Message;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemChanceHolder;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.model.itemcontainer.Mail;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.balthusevent.ExBalthusEvent;
import org.l2jmobius.gameserver.network.serverpackets.balthusevent.ExBalthusEventJackpotUser;
import org.l2jmobius.gameserver.util.Broadcast;

/**
 * @author Index
 */
public class BalthusEventManager
{
	protected static final Logger LOGGER = Logger.getLogger(BalthusEventManager.class.getName());
	
	private final Set<Player> _players = ConcurrentHashMap.newKeySet();
	private final Map<Integer, BalthusEventHolder> _templates = new HashMap<>();
	private boolean _isEasyMode;
	private int _minLevel = 0;
	private ItemHolder _consolation = null;
	private String _mailSubject = null;
	private String _mailContent = null;
	private boolean _isRunning = false;
	private int _currProgress;
	private int _currState;
	private int _avoidMinutesIssue;
	private Player _winner = null;
	private ItemChanceHolder _rewardItem;
	
	protected BalthusEventManager()
	{
	}
	
	public void init()
	{
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _templates.size() + " rewards.");
		
		final Calendar calendar = Calendar.getInstance();
		final long currentTime = System.currentTimeMillis();
		final long hours = calendar.get(Calendar.HOUR_OF_DAY);
		final long mins = calendar.get(Calendar.MINUTE);
		long startDelay;
		if (mins <= 11)
		{
			calendar.set(Calendar.MINUTE, 12);
			calendar.set(Calendar.SECOND, 0);
			startDelay = calendar.getTimeInMillis() - currentTime;
			_currProgress = 20;
			_avoidMinutesIssue = 1;
		}
		else if (mins <= 23)
		{
			calendar.set(Calendar.MINUTE, 24);
			calendar.set(Calendar.SECOND, 0);
			startDelay = calendar.getTimeInMillis() - currentTime;
			_currProgress = 40;
			_avoidMinutesIssue = 2;
		}
		else if (mins <= 35)
		{
			calendar.set(Calendar.MINUTE, 36);
			calendar.set(Calendar.SECOND, 0);
			startDelay = calendar.getTimeInMillis() - currentTime;
			_currProgress = 60;
			_avoidMinutesIssue = 3;
		}
		else if (mins <= 47)
		{
			calendar.set(Calendar.MINUTE, 48);
			calendar.set(Calendar.SECOND, 0);
			startDelay = calendar.getTimeInMillis() - currentTime;
			_currProgress = 80;
			_avoidMinutesIssue = 4;
		}
		else
		{
			calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			startDelay = calendar.getTimeInMillis() - currentTime;
			_currProgress = 100;
			_avoidMinutesIssue = 5;
		}
		if (((hours > 11) && (hours < 23)) || (hours == 23))
		{
			_currState = (int) hours - 11;
		}
		else
		{
			_currState = (int) hours + 13;
		}
		getNewRewardItem();
		ThreadPool.scheduleAtFixedRate(this::tryToGetWinner, startDelay, 720000);
	}
	
	public void addPlayer(Player player)
	{
		_players.add(player);
	}
	
	public void removePlayer(Player player)
	{
		_players.remove(player);
	}
	
	public Set<Player> getPlayers()
	{
		return _players;
	}
	
	public void removeParticipant(Player player)
	{
		_players.remove(player);
	}
	
	public boolean isPlayerParticipant(Player player)
	{
		return _players.contains(player);
	}
	
	public void setEasyMode(boolean isEasyMode)
	{
		_isEasyMode = isEasyMode;
	}
	
	public int getMinLevel()
	{
		return _minLevel;
	}
	
	public void setMinLevel(int minLevel)
	{
		_minLevel = minLevel;
	}
	
	public ItemHolder getConsolation()
	{
		return _consolation;
	}
	
	public void setConsolation(ItemHolder consolation)
	{
		_consolation = consolation;
	}
	
	public void setMailSubject(String mailSubject)
	{
		_mailSubject = mailSubject;
	}
	
	public void setMailContent(String mailContent)
	{
		_mailContent = mailContent;
	}
	
	public int getCurrentProgress()
	{
		return _currProgress;
	}
	
	public int getCurrentState()
	{
		return _currState;
	}
	
	public int getCurrRewardItem()
	{
		return _rewardItem.getId();
	}
	
	public boolean isRunning()
	{
		return _isRunning;
	}
	
	public int getTime()
	{
		return Calendar.getInstance().get(Calendar.MINUTE) * 60; // client makes 3600 - time
	}
	
	private void tryToGetWinner()
	{
		_avoidMinutesIssue++;
		if (_isRunning && (_avoidMinutesIssue != 6))
		{
			return;
		}
		
		final int random = Rnd.get(100);
		if (!_isRunning && (random >= _rewardItem.getChance()) && !_players.isEmpty())
		{
			final List<Player> playerList = new ArrayList<>(_players);
			Collections.shuffle(playerList);
			for (Player player : playerList)
			{
				if (player.getLevel() >= _minLevel)
				{
					_winner = player;
				}
			}
			if (_winner != null)
			{
				LOGGER.info(getClass().getSimpleName() + ": New winner for " + _currState + " of Balthus Event is " + _winner.getName() + " - " + _winner.getObjectId() + ". Player win " + new Item(_rewardItem.getId()).getItemName() + " - " + _rewardItem.getId() + " count: " + _rewardItem.getCount() + ".");
				Broadcast.toAllOnlinePlayers(new ExBalthusEventJackpotUser());
				Broadcast.toAllOnlinePlayers(new SystemMessage(SystemMessageId.S1_HAS_OBTAINED_S2_FROM_THE_FESTIVAL_FAIRY).addPcName(_winner).addItemName(_rewardItem.getId()));
				_isRunning = true;
				sendConsolationItemsToAll();
				sendRewardToPlayer(_winner);
			}
			else
			{
				_currProgress += 20;
			}
		}
		else
		{
			_currProgress += 20;
		}
		if (_avoidMinutesIssue == 6)
		{
			resetCurrentStage();
		}
		for (Player player : World.getInstance().getPlayers())
		{
			player.sendPacket(new ExBalthusEvent(player));
		}
	}
	
	private void resetCurrentStage()
	{
		_currState += 1;
		_winner = null;
		if (_currState == 25)
		{
			_currState = 1;
		}
		_currProgress = 20;
		if (!_isRunning && _isEasyMode)
		{
			sendConsolationItemsToAll();
		}
		_isRunning = false;
		_rewardItem = null;
		_avoidMinutesIssue = 1;
		getNewRewardItem();
	}
	
	private void sendConsolationItemsToAll()
	{
		for (Player player : _players)
		{
			if (player == _winner)
			{
				continue;
			}
			player.getVariables().set(PlayerVariables.BALTHUS_REWARD, player.getVariables().getInt(PlayerVariables.BALTHUS_REWARD, 0) + _consolation.getCount());
			player.sendPacket(new SystemMessage(SystemMessageId.YOU_VE_OBTAINED_S1_FAIRY_S_LUCKY_COINS).addInt((int) _consolation.getCount()));
		}
	}
	
	private void getNewRewardItem()
	{
		while (_rewardItem == null)
		{
			for (BalthusEventHolder holder : _templates.values())
			{
				if (holder.getRewardTime() == null)
				{
					continue;
				}
				
				final Map<Integer, Integer> rewardTime = holder.getRewardTime();
				final int firstParam = rewardTime.keySet().iterator().hasNext() ? rewardTime.keySet().iterator().next() : 0;
				final Calendar calendar = Calendar.getInstance();
				if ((firstParam <= calendar.get(Calendar.HOUR_OF_DAY)) && (calendar.get(Calendar.HOUR_OF_DAY) <= rewardTime.get(firstParam)))
				{
					final double random = Rnd.get(100d);
					double chance = 0;
					for (Map<ItemChanceHolder, Double> map : holder.getRewardList().values())
					{
						for (Entry<ItemChanceHolder, Double> entry : map.entrySet())
						{
							chance += entry.getValue();
							if (chance >= random)
							{
								_rewardItem = entry.getKey();
								LOGGER.info(getClass().getSimpleName() + ": Reward for " + _currState + " stage set. Next reward item is " + new Item(_rewardItem.getId()).getItemName() + " - " + _rewardItem.getId() + " count: " + _rewardItem.getCount() + ".");
								break;
							}
						}
						if (_rewardItem != null)
						{
							break;
						}
					}
				}
			}
		}
	}
	
	private void sendRewardToPlayer(Player player)
	{
		final Message msg = new Message(player.getObjectId(), _mailSubject, _mailContent, MailType.NEWS_INFORMER);
		final Mail attachments = msg.createAttachments();
		attachments.addItem("Balthus Knight Lottery", _rewardItem.getId(), _rewardItem.getCount(), null, null);
		MailManager.getInstance().sendMessage(msg);
	}
	
	public void addTemplate(int value, BalthusEventHolder holder)
	{
		_templates.put(value, holder);
	}
	
	public static class BalthusEventHolder
	{
		// From hour to hour.
		private final Map<Integer, Integer> _rewardTime;
		// Time reward - Reward item, chance to put in lottery.
		private final Map<Integer, Map<ItemChanceHolder, Double>> _rewardList;
		
		public BalthusEventHolder(Map<Integer, Integer> rewardTime, Map<Integer, Map<ItemChanceHolder, Double>> rewardList)
		{
			_rewardTime = rewardTime;
			_rewardList = rewardList;
		}
		
		public Map<Integer, Integer> getRewardTime()
		{
			return _rewardTime;
		}
		
		public Map<Integer, Map<ItemChanceHolder, Double>> getRewardList()
		{
			return _rewardList;
		}
	}
	
	public static BalthusEventManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final BalthusEventManager INSTANCE = new BalthusEventManager();
	}
}
