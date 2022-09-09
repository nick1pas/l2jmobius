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
package org.l2jmobius.gameserver.model.olympiad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.l2jmobius.Config;
import org.l2jmobius.commons.database.DatabaseFactory;
import org.l2jmobius.commons.threads.ThreadPool;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.enums.OlympiadMode;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.events.EventDispatcher;
import org.l2jmobius.gameserver.model.events.EventType;
import org.l2jmobius.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.olympiad.ExOlympiadMatchInfo;
import org.l2jmobius.gameserver.network.serverpackets.olympiad.ExOlympiadMatchResult;
import org.l2jmobius.gameserver.network.serverpackets.olympiad.ExOlympiadUserInfo;

/**
 * @author GodKratos, Pere, DS
 */
public abstract class OlympiadGameNormal extends AbstractOlympiadGame
{
	protected int _damageP1 = 0;
	protected int _damageP2 = 0;
	protected int _damageP1Final = 0;
	protected int _damageP2Final = 0;
	
	protected Participant _playerOne;
	protected Participant _playerTwo;
	
	String _winnerRound1 = "";
	String _winnerRound2 = "";
	String _winnerRound3 = "";
	int _player1Wins = 0;
	int _player2Wins = 0;
	boolean _matchEnd;
	
	protected OlympiadGameNormal(int id, Participant[] opponents)
	{
		super(id);
		
		_playerOne = opponents[0];
		_playerTwo = opponents[1];
		
		_playerOne.getPlayer().setOlympiadGameId(id);
		_playerTwo.getPlayer().setOlympiadGameId(id);
	}
	
	protected static Participant[] createListOfParticipants(Set<Integer> set)
	{
		if ((set == null) || set.isEmpty() || (set.size() < 2))
		{
			return null;
		}
		int playerOneObjectId = 0;
		int playerTwoObjectId = 0;
		Player playerOne = null;
		Player playerTwo = null;
		
		while (set.size() > 1)
		{
			int random = Rnd.get(set.size());
			Iterator<Integer> iter = set.iterator();
			while (iter.hasNext())
			{
				playerOneObjectId = iter.next();
				if (--random < 0)
				{
					iter.remove();
					break;
				}
			}
			
			playerOne = World.getInstance().getPlayer(playerOneObjectId);
			if ((playerOne == null) || !playerOne.isOnline())
			{
				continue;
			}
			
			random = Rnd.get(set.size());
			iter = set.iterator();
			while (iter.hasNext())
			{
				playerTwoObjectId = iter.next();
				if (--random < 0)
				{
					iter.remove();
					break;
				}
			}
			
			playerTwo = World.getInstance().getPlayer(playerTwoObjectId);
			if ((playerTwo == null) || !playerTwo.isOnline())
			{
				set.add(playerOneObjectId);
				continue;
			}
			
			final Participant[] result = new Participant[2];
			result[0] = new Participant(playerOne, 1);
			result[1] = new Participant(playerTwo, 2);
			
			return result;
		}
		return null;
	}
	
	@Override
	public boolean containsParticipant(int playerId)
	{
		return ((_playerOne != null) && (_playerOne.getObjectId() == playerId)) || ((_playerTwo != null) && (_playerTwo.getObjectId() == playerId));
	}
	
	@Override
	public void sendOlympiadInfo(Creature creature)
	{
		creature.sendPacket(new ExOlympiadUserInfo(_playerOne));
		creature.sendPacket(new ExOlympiadUserInfo(_playerTwo));
	}
	
	@Override
	public void broadcastOlympiadInfo(OlympiadStadium stadium)
	{
		stadium.broadcastPacket(new ExOlympiadUserInfo(_playerOne));
		stadium.broadcastPacket(new ExOlympiadUserInfo(_playerTwo));
	}
	
	@Override
	protected final void broadcastPacket(IClientOutgoingPacket packet)
	{
		if (_playerOne.updatePlayer())
		{
			_playerOne.getPlayer().sendPacket(packet);
		}
		
		if (_playerTwo.updatePlayer())
		{
			_playerTwo.getPlayer().sendPacket(packet);
		}
	}
	
	@Override
	protected final boolean portPlayersToArena(List<Location> spawns, Instance instance)
	{
		boolean result = true;
		try
		{
			result &= portPlayerToArena(_playerOne, spawns.get(0), _stadiumId, instance, OlympiadMode.BLUE);
			result &= portPlayerToArena(_playerTwo, spawns.get(spawns.size() / 2), _stadiumId, instance, OlympiadMode.RED);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "", e);
			return false;
		}
		return result;
	}
	
	@Override
	protected final boolean portPlayersToSpots(List<Location> spawns, Instance instance)
	{
		boolean result = true;
		try
		{
			result &= portPlayerToSpot(_playerOne, spawns.get(0), _stadiumId);
			result &= portPlayerToSpot(_playerTwo, spawns.get(spawns.size() / 2), _stadiumId);
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "", e);
			return false;
		}
		return result;
	}
	
	@Override
	protected boolean needBuffers()
	{
		return true;
	}
	
	@Override
	protected final void removals()
	{
		if (_aborted)
		{
			return;
		}
		
		removals(_playerOne.getPlayer(), true);
		removals(_playerTwo.getPlayer(), true);
	}
	
	@Override
	protected final boolean makeCompetitionStart()
	{
		if (!super.makeCompetitionStart())
		{
			return false;
		}
		
		if ((_playerOne.getPlayer() == null) || (_playerTwo.getPlayer() == null))
		{
			return false;
		}
		
		_playerOne.getPlayer().setOlympiadStart(true);
		_playerOne.getPlayer().updateEffectIcons();
		_playerTwo.getPlayer().setOlympiadStart(true);
		_playerTwo.getPlayer().updateEffectIcons();
		return true;
	}
	
	@Override
	protected final void cleanEffects()
	{
		if ((_playerOne.getPlayer() != null) && !_playerOne.isDefaulted() && !_playerOne.isDisconnected() && (_playerOne.getPlayer().getOlympiadGameId() == _stadiumId))
		{
			cleanEffects(_playerOne.getPlayer());
		}
		
		if ((_playerTwo.getPlayer() != null) && !_playerTwo.isDefaulted() && !_playerTwo.isDisconnected() && (_playerTwo.getPlayer().getOlympiadGameId() == _stadiumId))
		{
			cleanEffects(_playerTwo.getPlayer());
		}
	}
	
	@Override
	protected final void portPlayersBack()
	{
		if ((_playerOne.getPlayer() != null) && !_playerOne.isDefaulted() && !_playerOne.isDisconnected())
		{
			portPlayerBack(_playerOne.getPlayer());
		}
		if ((_playerTwo.getPlayer() != null) && !_playerTwo.isDefaulted() && !_playerTwo.isDisconnected())
		{
			portPlayerBack(_playerTwo.getPlayer());
		}
	}
	
	@Override
	protected final void playersStatusBack()
	{
		if ((_playerOne.getPlayer() != null) && !_playerOne.isDefaulted() && !_playerOne.isDisconnected() && (_playerOne.getPlayer().getOlympiadGameId() == _stadiumId))
		{
			playerStatusBack(_playerOne.getPlayer());
		}
		
		if ((_playerTwo.getPlayer() != null) && !_playerTwo.isDefaulted() && !_playerTwo.isDisconnected() && (_playerTwo.getPlayer().getOlympiadGameId() == _stadiumId))
		{
			playerStatusBack(_playerTwo.getPlayer());
		}
	}
	
	@Override
	protected final void clearPlayers()
	{
		_playerOne.setPlayer(null);
		_playerOne = null;
		_playerTwo.setPlayer(null);
		_playerTwo = null;
	}
	
	@Override
	protected final void handleDisconnect(Player player)
	{
		if (player.getObjectId() == _playerOne.getObjectId())
		{
			_playerOne.setDisconnected(true);
		}
		else if (player.getObjectId() == _playerTwo.getObjectId())
		{
			_playerTwo.setDisconnected(true);
		}
	}
	
	@Override
	protected final boolean checkBattleStatus()
	{
		if (_aborted)
		{
			return false;
		}
		
		if ((_playerOne.getPlayer() == null) || _playerOne.isDisconnected())
		{
			return false;
		}
		
		if ((_playerTwo.getPlayer() == null) || _playerTwo.isDisconnected())
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	protected final boolean haveWinner()
	{
		if (!checkBattleStatus())
		{
			return true;
		}
		
		boolean playerOneLost = true;
		try
		{
			if (_playerOne.getPlayer().getOlympiadGameId() == _stadiumId)
			{
				playerOneLost = _playerOne.getPlayer().isDead();
			}
		}
		catch (Exception e)
		{
			playerOneLost = true;
		}
		
		boolean playerTwoLost = true;
		try
		{
			if (_playerTwo.getPlayer().getOlympiadGameId() == _stadiumId)
			{
				playerTwoLost = _playerTwo.getPlayer().isDead();
			}
		}
		catch (Exception e)
		{
			playerTwoLost = true;
		}
		
		return playerOneLost || playerTwoLost;
	}
	
	@Override
	public final boolean roundWinner()
	{
		if (!checkBattleStatus())
		{
			return true;
		}
		
		boolean playerOneLost = true;
		try
		{
			if (_playerOne.getPlayer().getOlympiadGameId() == _stadiumId)
			{
				playerOneLost = _playerOne.getPlayer().isDead();
			}
		}
		catch (Exception e)
		{
			playerOneLost = true;
		}
		
		boolean playerTwoLost = true;
		try
		{
			if (_playerTwo.getPlayer().getOlympiadGameId() == _stadiumId)
			{
				playerTwoLost = _playerTwo.getPlayer().isDead();
			}
		}
		catch (Exception e)
		{
			playerTwoLost = true;
		}
		
		return playerOneLost || playerTwoLost;
	}
	
	@Override
	public void matchEnd(boolean value)
	{
		_matchEnd = value;
	}
	
	@Override
	public boolean isMatchEnd()
	{
		return _matchEnd;
	}
	
	@Override
	protected void validateRound1Winner(OlympiadStadium stadium)
	{
		if (_aborted)
		{
			return;
		}
		
		SystemMessage sm;
		
		try
		{
			double playerOneHp = 0;
			if ((_playerOne.getPlayer() != null) && !_playerOne.getPlayer().isDead())
			{
				playerOneHp = _playerOne.getPlayer().getCurrentHp() + _playerOne.getPlayer().getCurrentCp();
				if (playerOneHp < 0.5)
				{
					playerOneHp = 0;
				}
			}
			
			double playerTwoHp = 0;
			if ((_playerTwo.getPlayer() != null) && !_playerTwo.getPlayer().isDead())
			{
				playerTwoHp = _playerTwo.getPlayer().getCurrentHp() + _playerTwo.getPlayer().getCurrentCp();
				if (playerTwoHp < 0.5)
				{
					playerTwoHp = 0;
				}
			}
			
			// if players crashed, search if they've relogged
			_playerOne.updatePlayer();
			_playerTwo.updatePlayer();
			
			_damageP1Final += _damageP1;
			_damageP2Final += _damageP2;
			
			if (((_playerOne.getPlayer() == null) || !_playerOne.getPlayer().isOnline()) && ((_playerTwo.getPlayer() == null) || !_playerTwo.getPlayer().isOnline()))
			{
				_playerOne.updateStat(COMP_DRAWN, 1);
				_playerTwo.updateStat(COMP_DRAWN, 1);
				sm = new SystemMessage(SystemMessageId.THE_DUEL_HAS_ENDED_IN_A_TIE);
				stadium.broadcastPacket(sm);
			}
			else if ((_playerTwo.getPlayer() == null) || !_playerTwo.getPlayer().isOnline() || ((playerTwoHp == 0) && (playerOneHp != 0)) || ((_damageP1 > _damageP2) && (playerTwoHp != 0) && (playerOneHp != 0)))
			{
				final SystemMessage KO = new SystemMessage(((_damageP1 > _damageP2) && (playerTwoHp != 0) && (playerOneHp != 0)) ? SystemMessageId.HIDDEN_MSG_OLYMPIAD_TIME_OVER : SystemMessageId.HIDDEN_MSG_OLYMPIAD_KNOCK_DOWN);
				_playerOne.getPlayer().sendPacket(KO);
				_playerTwo.getPlayer().sendPacket(KO);
				final SystemMessage win = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_VICTORY);
				_playerOne.getPlayer().sendPacket(win);
				final SystemMessage loose = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_DEFEAT);
				_playerTwo.getPlayer().setCurrentCp(_playerTwo.getPlayer().getMaxCp() * 0.7);
				_playerTwo.getPlayer().setCurrentHp(_playerTwo.getPlayer().getMaxHp() * 0.7);
				_playerTwo.getPlayer().setCurrentMp(_playerTwo.getPlayer().getMaxMp() * 0.7);
				_playerTwo.getPlayer().sendPacket(loose);
				_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 0, 1, 20));
				_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 0, 1, 20));
				ThreadPool.schedule(() ->
				{
					SkillCaster.triggerCast(_playerOne.getPlayer(), _playerOne.getPlayer(), CommonSkill.OLYMPIAD_WIN.getSkill());
				}, 2000);
				ThreadPool.schedule(() ->
				{
					_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 0, 1, 100));
					_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 0, 1, 100));
				}, 20000);
				_winnerRound1 = _playerOne.getName();
				_player1Wins += 1;
			}
			else if ((_playerOne.getPlayer() == null) || !_playerOne.getPlayer().isOnline() || ((playerOneHp == 0) && (playerTwoHp != 0)) || ((_damageP2 > _damageP1) && (playerOneHp != 0) && (playerTwoHp != 0)))
			{
				final SystemMessage KO = new SystemMessage(((_damageP2 > _damageP1) && (playerTwoHp != 0) && (playerOneHp != 0)) ? SystemMessageId.HIDDEN_MSG_OLYMPIAD_TIME_OVER : SystemMessageId.HIDDEN_MSG_OLYMPIAD_KNOCK_DOWN);
				_playerOne.getPlayer().sendPacket(KO);
				_playerTwo.getPlayer().sendPacket(KO);
				final SystemMessage win = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_VICTORY);
				_playerTwo.getPlayer().sendPacket(win);
				final SystemMessage loose = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_DEFEAT);
				_playerOne.getPlayer().sendPacket(loose);
				_playerOne.getPlayer().setCurrentCp(_playerOne.getPlayer().getMaxCp() * 0.7);
				_playerOne.getPlayer().setCurrentHp(_playerOne.getPlayer().getMaxHp() * 0.7);
				_playerOne.getPlayer().setCurrentMp(_playerOne.getPlayer().getMaxMp() * 0.7);
				_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 0, 1, 1, 20));
				_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 0, 1, 1, 20));
				ThreadPool.schedule(() ->
				{
					_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 0, 1, 1, 100));
					_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 0, 1, 1, 100));
				}, 20000);
				ThreadPool.schedule(() ->
				{
					_playerTwo.getPlayer().useMagic(CommonSkill.OLYMPIAD_WIN.getSkill(), null, false, false);
				}, 2000);
				_winnerRound1 = _playerTwo.getName();
				_player2Wins += 1;
			}
			else
			{
				_winnerRound1 = "";
				final SystemMessage tie = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_TIE);
				_playerTwo.getPlayer().broadcastPacket(tie);
				_playerOne.getPlayer().broadcastPacket(tie);
			}
			resetDamage();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Exception on validateRound1Winner(): " + e.getMessage(), e);
		}
	}
	
	@Override
	protected void validateRound2Winner(OlympiadStadium stadium)
	{
		if (_aborted)
		{
			return;
		}
		
		SystemMessage sm;
		
		try
		{
			double playerOneHp = 0;
			if ((_playerOne.getPlayer() != null) && !_playerOne.getPlayer().isDead())
			{
				playerOneHp = _playerOne.getPlayer().getCurrentHp() + _playerOne.getPlayer().getCurrentCp();
				if (playerOneHp < 0.5)
				{
					playerOneHp = 0;
				}
			}
			
			double playerTwoHp = 0;
			if ((_playerTwo.getPlayer() != null) && !_playerTwo.getPlayer().isDead())
			{
				playerTwoHp = _playerTwo.getPlayer().getCurrentHp() + _playerTwo.getPlayer().getCurrentCp();
				if (playerTwoHp < 0.5)
				{
					playerTwoHp = 0;
				}
			}
			
			// if players crashed, search if they've relogged
			_playerOne.updatePlayer();
			_playerTwo.updatePlayer();
			
			_damageP1Final += _damageP1;
			_damageP2Final += _damageP2;
			
			if (((_playerOne.getPlayer() == null) || !_playerOne.getPlayer().isOnline()) && ((_playerTwo.getPlayer() == null) || !_playerTwo.getPlayer().isOnline()))
			{
				_playerOne.updateStat(COMP_DRAWN, 1);
				_playerTwo.updateStat(COMP_DRAWN, 1);
				sm = new SystemMessage(SystemMessageId.THE_DUEL_HAS_ENDED_IN_A_TIE);
				stadium.broadcastPacket(sm);
			}
			else if ((_playerTwo.getPlayer() == null) || !_playerTwo.getPlayer().isOnline() || ((playerTwoHp == 0) && (playerOneHp != 0)) || ((_damageP1 > _damageP2) && (playerTwoHp != 0) && (playerOneHp != 0)))
			{
				final SystemMessage KO = new SystemMessage(((_damageP1 > _damageP2) && (playerTwoHp != 0) && (playerOneHp != 0)) ? SystemMessageId.HIDDEN_MSG_OLYMPIAD_TIME_OVER : SystemMessageId.HIDDEN_MSG_OLYMPIAD_KNOCK_DOWN);
				_playerOne.getPlayer().sendPacket(KO);
				_playerTwo.getPlayer().sendPacket(KO);
				final SystemMessage win = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_VICTORY);
				_playerOne.getPlayer().sendPacket(win);
				final SystemMessage loose = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_DEFEAT);
				_playerTwo.getPlayer().sendPacket(loose);
				_winnerRound2 = _playerOne.getName();
				_player1Wins += 1;
				
				if (_winnerRound1.toLowerCase().equalsIgnoreCase(_playerOne.getName()))
				{
					_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 2, 0, 2, 20));
					_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 2, 0, 2, 20));
					matchEnd(true);
				}
				else
				{
					_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 1, 2, 20));
					_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 1, 2, 20));
					ThreadPool.schedule(() ->
					{
						_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 1, 2, 100));
						_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 1, 2, 100));
					}, 20000);
				}
				ThreadPool.schedule(() ->
				{
					SkillCaster.triggerCast(_playerOne.getPlayer(), _playerOne.getPlayer(), CommonSkill.OLYMPIAD_WIN.getSkill());
				}, 2000);
			}
			else if ((_playerOne.getPlayer() == null) || !_playerOne.getPlayer().isOnline() || ((playerOneHp == 0) && (playerTwoHp != 0)) || ((_damageP2 > _damageP1) && (playerOneHp != 0) && (playerTwoHp != 0)))
			{
				final SystemMessage KO = new SystemMessage(((_damageP2 > _damageP1) && (playerTwoHp != 0) && (playerOneHp != 0)) ? SystemMessageId.HIDDEN_MSG_OLYMPIAD_TIME_OVER : SystemMessageId.HIDDEN_MSG_OLYMPIAD_KNOCK_DOWN);
				_playerOne.getPlayer().sendPacket(KO);
				_playerTwo.getPlayer().sendPacket(KO);
				final SystemMessage win = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_VICTORY);
				_playerTwo.getPlayer().sendPacket(win);
				final SystemMessage loose = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_DEFEAT);
				_playerOne.getPlayer().sendPacket(loose);
				_winnerRound2 = _playerTwo.getName();
				_player2Wins += 1;
				
				if (_winnerRound1.toLowerCase().equalsIgnoreCase(_playerTwo.getName()))
				{
					_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 0, 2, 2, 20));
					_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 0, 2, 2, 20));
					matchEnd(true);
				}
				else
				{
					_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 1, 2, 20));
					_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 1, 2, 20));
					ThreadPool.schedule(() ->
					{
						_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 1, 2, 100));
						_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 1, 2, 100));
					}, 20000);
				}
				ThreadPool.schedule(() ->
				{
					_playerTwo.getPlayer().useMagic(CommonSkill.OLYMPIAD_WIN.getSkill(), null, false, false);
				}, 2000);
			}
			else
			{
				final SystemMessage tie = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_TIE);
				_playerTwo.getPlayer().broadcastPacket(tie);
				_playerOne.getPlayer().broadcastPacket(tie);
			}
			resetDamage();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Exception on validateRound2Winner(): " + e.getMessage(), e);
		}
	}
	
	@Override
	protected void validateRound3Winner(OlympiadStadium stadium)
	{
		if (_aborted)
		{
			return;
		}
		
		SystemMessage sm;
		
		try
		{
			double playerOneHp = 0;
			if ((_playerOne.getPlayer() != null) && !_playerOne.getPlayer().isDead())
			{
				playerOneHp = _playerOne.getPlayer().getCurrentHp() + _playerOne.getPlayer().getCurrentCp();
				if (playerOneHp < 0.5)
				{
					playerOneHp = 0;
				}
			}
			
			double playerTwoHp = 0;
			if ((_playerTwo.getPlayer() != null) && !_playerTwo.getPlayer().isDead())
			{
				playerTwoHp = _playerTwo.getPlayer().getCurrentHp() + _playerTwo.getPlayer().getCurrentCp();
				if (playerTwoHp < 0.5)
				{
					playerTwoHp = 0;
				}
			}
			
			// if players crashed, search if they've relogged
			_playerOne.updatePlayer();
			_playerTwo.updatePlayer();
			
			_damageP1Final += _damageP1;
			_damageP2Final += _damageP2;
			
			if (((_playerOne.getPlayer() == null) || !_playerOne.getPlayer().isOnline()) && ((_playerTwo.getPlayer() == null) || !_playerTwo.getPlayer().isOnline()))
			{
				_playerOne.updateStat(COMP_DRAWN, 1);
				_playerTwo.updateStat(COMP_DRAWN, 1);
				sm = new SystemMessage(SystemMessageId.THE_DUEL_HAS_ENDED_IN_A_TIE);
				stadium.broadcastPacket(sm);
			}
			else if ((_playerTwo.getPlayer() == null) || !_playerTwo.getPlayer().isOnline() || ((playerTwoHp == 0) && (playerOneHp != 0)) || ((_damageP1 > _damageP2) && (playerTwoHp != 0) && (playerOneHp != 0)))
			{
				final SystemMessage KO = new SystemMessage(((_damageP1 > _damageP2) && (playerTwoHp != 0) && (playerOneHp != 0)) ? SystemMessageId.HIDDEN_MSG_OLYMPIAD_TIME_OVER : SystemMessageId.HIDDEN_MSG_OLYMPIAD_KNOCK_DOWN);
				_playerOne.getPlayer().sendPacket(KO);
				_playerTwo.getPlayer().sendPacket(KO);
				final SystemMessage win = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_VICTORY);
				_playerOne.getPlayer().sendPacket(win);
				final SystemMessage loose = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_DEFEAT);
				_playerTwo.getPlayer().sendPacket(loose);
				_winnerRound3 = _playerOne.getName();
				_player1Wins += 1;
				if (_winnerRound3.toLowerCase().equalsIgnoreCase(_playerOne.getName()))
				{
					_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 2, 1, 3, 0));
					_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 2, 1, 3, 0));
				}
				else
				{
					_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 2, 3, 0));
					_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 2, 3, 0));
				}
				ThreadPool.schedule(() ->
				{
					SkillCaster.triggerCast(_playerOne.getPlayer(), _playerOne.getPlayer(), CommonSkill.OLYMPIAD_WIN.getSkill());
				}, 2000);
				
			}
			else if ((_playerOne.getPlayer() == null) || !_playerOne.getPlayer().isOnline() || ((playerOneHp == 0) && (playerTwoHp != 0)) || ((_damageP2 > _damageP1) && (playerOneHp != 0) && (playerTwoHp != 0)))
			{
				final SystemMessage KO = new SystemMessage(((_damageP2 > _damageP1) && (playerTwoHp != 0) && (playerOneHp != 0)) ? SystemMessageId.HIDDEN_MSG_OLYMPIAD_TIME_OVER : SystemMessageId.HIDDEN_MSG_OLYMPIAD_KNOCK_DOWN);
				_playerOne.getPlayer().sendPacket(KO);
				_playerTwo.getPlayer().sendPacket(KO);
				final SystemMessage win = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_VICTORY);
				_playerTwo.getPlayer().sendPacket(win);
				final SystemMessage loose = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_DEFEAT);
				_playerOne.getPlayer().sendPacket(loose);
				_winnerRound3 = _playerTwo.getName();
				_player2Wins += 1;
				if (_winnerRound3.toLowerCase().equalsIgnoreCase(_playerTwo.getName()))
				{
					_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 2, 3, 0));
					_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 1, 2, 3, 0));
				}
				else
				{
					_playerOne.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 2, 1, 3, 0));
					_playerTwo.getPlayer().sendPacket(new ExOlympiadMatchInfo(_playerOne.getName(), _playerTwo.getName(), 2, 1, 3, 0));
				}
				ThreadPool.schedule(() ->
				{
					SkillCaster.triggerCast(_playerTwo.getPlayer(), _playerTwo.getPlayer(), CommonSkill.OLYMPIAD_WIN.getSkill());
				}, 2000);
			}
			else
			{
				final SystemMessage tie = new SystemMessage(SystemMessageId.HIDDEN_MSG_OLYMPIAD_TIE);
				_playerTwo.getPlayer().broadcastPacket(tie);
				_playerOne.getPlayer().broadcastPacket(tie);
			}
			resetDamage();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Exception on validateRound3Winner(): " + e.getMessage(), e);
		}
	}
	
	@Override
	protected void validateWinner(OlympiadStadium stadium)
	{
		if (_aborted)
		{
			return;
		}
		
		ExOlympiadMatchResult result = null;
		
		boolean tie = _winnerRound1.isEmpty();
		int winside = 0;
		
		final List<OlympiadInfo> list1 = new ArrayList<>(1);
		final List<OlympiadInfo> list2 = new ArrayList<>(1);
		
		final boolean _pOneCrash = ((_playerOne.getPlayer() == null) || _playerOne.isDisconnected());
		final boolean _pTwoCrash = ((_playerTwo.getPlayer() == null) || _playerTwo.isDisconnected());
		
		final int playerOnePoints = _playerOne.getStats().getInt(POINTS);
		final int playerTwoPoints = _playerTwo.getStats().getInt(POINTS);
		int pointDiff = Math.min(playerOnePoints, playerTwoPoints) / getDivider();
		if (pointDiff <= 0)
		{
			pointDiff = 1;
		}
		else if (pointDiff > Config.OLYMPIAD_MAX_POINTS)
		{
			pointDiff = Config.OLYMPIAD_MAX_POINTS;
		}
		
		int points;
		SystemMessage sm;
		
		// Check for if a player defaulted before battle started
		if (_playerOne.isDefaulted() || _playerTwo.isDefaulted())
		{
			try
			{
				if (_playerOne.isDefaulted())
				{
					try
					{
						points = Math.min(playerOnePoints / 3, Config.OLYMPIAD_MAX_POINTS);
						removePointsFromParticipant(_playerOne, points);
						list1.add(new OlympiadInfo(_playerOne.getName(), _playerOne.getClanName(), _playerOne.getClanId(), _playerOne.getBaseClass(), _damageP1, playerOnePoints - points, -points));
						
						winside = 2;
						
						if (Config.OLYMPIAD_LOG_FIGHTS)
						{
							LOGGER_OLYMPIAD.info(_playerOne.getName() + " default," + _playerOne + "," + _playerTwo + ",0,0,0,0," + points + "," + getType());
						}
					}
					catch (Exception e)
					{
						LOGGER.log(Level.WARNING, "Exception on validateWinner(): " + e.getMessage(), e);
					}
				}
				if (_playerTwo.isDefaulted())
				{
					try
					{
						points = Math.min(playerTwoPoints / 3, Config.OLYMPIAD_MAX_POINTS);
						removePointsFromParticipant(_playerTwo, points);
						list2.add(new OlympiadInfo(_playerTwo.getName(), _playerTwo.getClanName(), _playerTwo.getClanId(), _playerTwo.getBaseClass(), _damageP2, playerTwoPoints - points, -points));
						
						if (winside == 2)
						{
							tie = true;
						}
						else
						{
							winside = 1;
						}
						
						if (Config.OLYMPIAD_LOG_FIGHTS)
						{
							LOGGER_OLYMPIAD.info(_playerTwo.getName() + " default," + _playerOne + "," + _playerTwo + ",0,0,0,0," + points + "," + getType());
						}
					}
					catch (Exception e)
					{
						LOGGER.log(Level.WARNING, "Exception on validateWinner(): " + e.getMessage(), e);
					}
				}
				if (winside == 1)
				{
					result = new ExOlympiadMatchResult(tie, winside, list1, list2, 0, 0, 0);
				}
				else
				{
					result = new ExOlympiadMatchResult(tie, winside, list2, list1, 0, 0, 0);
				}
				stadium.broadcastPacket(result);
				return;
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "Exception on validateWinner(): " + e.getMessage(), e);
				return;
			}
		}
		
		// Create results for players if a player crashed
		if (_pOneCrash || _pTwoCrash)
		{
			try
			{
				if (_pTwoCrash && !_pOneCrash)
				{
					sm = new SystemMessage(SystemMessageId.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH);
					sm.addString(_playerOne.getName());
					stadium.broadcastPacket(sm);
					
					_playerOne.updateStat(COMP_WON, 1);
					addPointsToParticipant(_playerOne, pointDiff);
					list1.add(new OlympiadInfo(_playerOne.getName(), _playerOne.getClanName(), _playerOne.getClanId(), _playerOne.getBaseClass(), _damageP1Final, playerOnePoints + pointDiff, pointDiff));
					
					_playerTwo.updateStat(COMP_LOST, 1);
					removePointsFromParticipant(_playerTwo, pointDiff);
					list2.add(new OlympiadInfo(_playerTwo.getName(), _playerTwo.getClanName(), _playerTwo.getClanId(), _playerTwo.getBaseClass(), _damageP2Final, playerTwoPoints - pointDiff, -pointDiff));
					
					winside = 1;
					
					rewardParticipant(_playerOne.getPlayer(), Config.OLYMPIAD_WINNER_REWARD); // Winner
					
					if (Config.OLYMPIAD_LOG_FIGHTS)
					{
						LOGGER_OLYMPIAD.info(_playerTwo.getName() + " crash," + _playerOne + "," + _playerTwo + ",0,0,0,0," + pointDiff + "," + getType());
					}
					
					// Notify to scripts
					if (EventDispatcher.getInstance().hasListener(EventType.ON_OLYMPIAD_MATCH_RESULT, Olympiad.getInstance()))
					{
						EventDispatcher.getInstance().notifyEventAsync(new OnOlympiadMatchResult(_playerOne, _playerTwo, getType()), Olympiad.getInstance());
					}
				}
				else if (_pOneCrash && !_pTwoCrash)
				{
					sm = new SystemMessage(SystemMessageId.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH);
					sm.addString(_playerTwo.getName());
					stadium.broadcastPacket(sm);
					
					_playerTwo.updateStat(COMP_WON, 1);
					addPointsToParticipant(_playerTwo, pointDiff);
					list2.add(new OlympiadInfo(_playerTwo.getName(), _playerTwo.getClanName(), _playerTwo.getClanId(), _playerTwo.getBaseClass(), _damageP2Final, playerTwoPoints + pointDiff, pointDiff));
					
					_playerOne.updateStat(COMP_LOST, 1);
					removePointsFromParticipant(_playerOne, pointDiff);
					list1.add(new OlympiadInfo(_playerOne.getName(), _playerOne.getClanName(), _playerOne.getClanId(), _playerOne.getBaseClass(), _damageP1Final, playerOnePoints - pointDiff, -pointDiff));
					
					winside = 2;
					
					rewardParticipant(_playerTwo.getPlayer(), Config.OLYMPIAD_WINNER_REWARD); // Winner
					
					if (Config.OLYMPIAD_LOG_FIGHTS)
					{
						LOGGER_OLYMPIAD.info(_playerOne.getName() + " crash," + _playerOne + "," + _playerTwo + ",0,0,0,0," + pointDiff + "," + getType());
					}
					
					// Notify to scripts
					if (EventDispatcher.getInstance().hasListener(EventType.ON_OLYMPIAD_MATCH_RESULT, Olympiad.getInstance()))
					{
						EventDispatcher.getInstance().notifyEventAsync(new OnOlympiadMatchResult(_playerTwo, _playerOne, getType()), Olympiad.getInstance());
					}
				}
				else if (_pOneCrash && _pTwoCrash)
				{
					stadium.broadcastPacket(new SystemMessage(SystemMessageId.THE_DUEL_HAS_ENDED_IN_A_TIE));
					
					_playerOne.updateStat(COMP_LOST, 1);
					removePointsFromParticipant(_playerOne, pointDiff);
					list1.add(new OlympiadInfo(_playerOne.getName(), _playerOne.getClanName(), _playerOne.getClanId(), _playerOne.getBaseClass(), _damageP1Final, playerOnePoints - pointDiff, -pointDiff));
					
					_playerTwo.updateStat(COMP_LOST, 1);
					removePointsFromParticipant(_playerTwo, pointDiff);
					list2.add(new OlympiadInfo(_playerTwo.getName(), _playerTwo.getClanName(), _playerTwo.getClanId(), _playerTwo.getBaseClass(), _damageP2Final, playerTwoPoints - pointDiff, -pointDiff));
					
					tie = true;
					
					if (Config.OLYMPIAD_LOG_FIGHTS)
					{
						LOGGER_OLYMPIAD.info("both crash," + _playerOne.getName() + "," + _playerOne + ",0,0,0,0," + _playerTwo + "," + pointDiff + "," + getType());
					}
				}
				
				_playerOne.updateStat(COMP_DONE, 1);
				_playerTwo.updateStat(COMP_DONE, 1);
				_playerOne.updateStat(COMP_DONE_WEEK, 1);
				_playerTwo.updateStat(COMP_DONE_WEEK, 1);
				
				if (winside == 1)
				{
					result = new ExOlympiadMatchResult(tie, winside, list1, list2, 0, 0, 0);
				}
				else
				{
					result = new ExOlympiadMatchResult(tie, winside, list2, list1, 0, 0, 0);
				}
				stadium.broadcastPacket(result);
				
				// Notify to scripts
				if (EventDispatcher.getInstance().hasListener(EventType.ON_OLYMPIAD_MATCH_RESULT, Olympiad.getInstance()))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnOlympiadMatchResult(null, _playerOne, getType()), Olympiad.getInstance());
					EventDispatcher.getInstance().notifyEventAsync(new OnOlympiadMatchResult(null, _playerTwo, getType()), Olympiad.getInstance());
				}
				return;
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, "Exception on validateWinner(): " + e.getMessage(), e);
				return;
			}
		}
		
		try
		{
			String winner = "draw";
			
			// Calculate Fight time
			final long _fightTime = (System.currentTimeMillis() - _startTime);
			
			double playerOneHp = 0;
			if ((_playerOne.getPlayer() != null) && !_playerOne.getPlayer().isDead())
			{
				playerOneHp = _playerOne.getPlayer().getCurrentHp() + _playerOne.getPlayer().getCurrentCp();
				if (playerOneHp < 0.5)
				{
					playerOneHp = 0;
				}
			}
			
			double playerTwoHp = 0;
			if ((_playerTwo.getPlayer() != null) && !_playerTwo.getPlayer().isDead())
			{
				playerTwoHp = _playerTwo.getPlayer().getCurrentHp() + _playerTwo.getPlayer().getCurrentCp();
				if (playerTwoHp < 0.5)
				{
					playerTwoHp = 0;
				}
			}
			
			// if players crashed, search if they've relogged
			_playerOne.updatePlayer();
			_playerTwo.updatePlayer();
			
			if (((_playerOne.getPlayer() == null) || !_playerOne.getPlayer().isOnline()) && ((_playerTwo.getPlayer() == null) || !_playerTwo.getPlayer().isOnline()))
			{
				_playerOne.updateStat(COMP_DRAWN, 1);
				_playerTwo.updateStat(COMP_DRAWN, 1);
				sm = new SystemMessage(SystemMessageId.THE_DUEL_HAS_ENDED_IN_A_TIE);
				stadium.broadcastPacket(sm);
			}
			else if ((_player1Wins == 2) || (_playerTwo.getPlayer() == null) || !_playerTwo.getPlayer().isOnline() || ((playerTwoHp == 0) && (playerOneHp != 0)) || ((_damageP1 > _damageP2) && (playerTwoHp != 0) && (playerOneHp != 0)))
			{
				sm = new SystemMessage(SystemMessageId.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH);
				sm.addString(_playerOne.getName());
				stadium.broadcastPacket(sm);
				
				_playerOne.updateStat(COMP_WON, 1);
				_playerTwo.updateStat(COMP_LOST, 1);
				
				addPointsToParticipant(_playerOne, pointDiff);
				list1.add(new OlympiadInfo(_playerOne.getName(), _playerOne.getClanName(), _playerOne.getClanId(), _playerOne.getBaseClass(), _damageP1Final, playerOnePoints + pointDiff, pointDiff));
				
				removePointsFromParticipant(_playerTwo, pointDiff);
				list2.add(new OlympiadInfo(_playerTwo.getName(), _playerTwo.getClanName(), _playerTwo.getClanId(), _playerTwo.getBaseClass(), _damageP2Final, playerTwoPoints - pointDiff, -pointDiff));
				winner = _playerOne.getName() + " won";
				
				winside = 1;
				
				// Save Fight Result
				saveResults(_playerOne, _playerTwo, 1, _startTime, _fightTime, getType());
				
				rewardParticipant(_playerOne.getPlayer(), Config.OLYMPIAD_WINNER_REWARD); // Winner
				rewardParticipant(_playerTwo.getPlayer(), Config.OLYMPIAD_LOSER_REWARD); // Loser
				
				// Notify to scripts
				if (EventDispatcher.getInstance().hasListener(EventType.ON_OLYMPIAD_MATCH_RESULT, Olympiad.getInstance()))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnOlympiadMatchResult(_playerOne, _playerTwo, getType()), Olympiad.getInstance());
				}
			}
			else if ((_player2Wins == 2) || (_playerOne.getPlayer() == null) || !_playerOne.getPlayer().isOnline() || ((playerOneHp == 0) && (playerTwoHp != 0)) || ((_damageP2 > _damageP1) && (playerOneHp != 0) && (playerTwoHp != 0)))
			{
				sm = new SystemMessage(SystemMessageId.CONGRATULATIONS_C1_YOU_WIN_THE_MATCH);
				sm.addString(_playerTwo.getName());
				stadium.broadcastPacket(sm);
				
				_playerTwo.updateStat(COMP_WON, 1);
				_playerOne.updateStat(COMP_LOST, 1);
				
				addPointsToParticipant(_playerTwo, pointDiff);
				list2.add(new OlympiadInfo(_playerTwo.getName(), _playerTwo.getClanName(), _playerTwo.getClanId(), _playerTwo.getBaseClass(), _damageP2Final, playerTwoPoints + pointDiff, pointDiff));
				
				removePointsFromParticipant(_playerOne, pointDiff);
				list1.add(new OlympiadInfo(_playerOne.getName(), _playerOne.getClanName(), _playerOne.getClanId(), _playerOne.getBaseClass(), _damageP1Final, playerOnePoints - pointDiff, -pointDiff));
				
				winner = _playerTwo.getName() + " won";
				winside = 2;
				
				// Save Fight Result
				saveResults(_playerOne, _playerTwo, 2, _startTime, _fightTime, getType());
				
				rewardParticipant(_playerTwo.getPlayer(), Config.OLYMPIAD_WINNER_REWARD); // Winner
				rewardParticipant(_playerOne.getPlayer(), Config.OLYMPIAD_LOSER_REWARD); // Loser
				
				// Notify to scripts
				if (EventDispatcher.getInstance().hasListener(EventType.ON_OLYMPIAD_MATCH_RESULT, Olympiad.getInstance()))
				{
					EventDispatcher.getInstance().notifyEventAsync(new OnOlympiadMatchResult(_playerTwo, _playerOne, getType()), Olympiad.getInstance());
				}
			}
			else
			{
				// Save Fight Result
				saveResults(_playerOne, _playerTwo, 0, _startTime, _fightTime, getType());
				
				sm = new SystemMessage(SystemMessageId.THE_DUEL_HAS_ENDED_IN_A_TIE);
				stadium.broadcastPacket(sm);
				
				int value = Math.min(playerOnePoints / getDivider(), Config.OLYMPIAD_MAX_POINTS);
				
				removePointsFromParticipant(_playerOne, value);
				list1.add(new OlympiadInfo(_playerOne.getName(), _playerOne.getClanName(), _playerOne.getClanId(), _playerOne.getBaseClass(), _damageP1Final, playerOnePoints - value, -value));
				
				value = Math.min(playerTwoPoints / getDivider(), Config.OLYMPIAD_MAX_POINTS);
				removePointsFromParticipant(_playerTwo, value);
				list2.add(new OlympiadInfo(_playerTwo.getName(), _playerTwo.getClanName(), _playerTwo.getClanId(), _playerTwo.getBaseClass(), _damageP2Final, playerTwoPoints - value, -value));
				
				tie = true;
			}
			
			_playerOne.updateStat(COMP_DONE, 1);
			_playerTwo.updateStat(COMP_DONE, 1);
			_playerOne.updateStat(COMP_DONE_WEEK, 1);
			_playerTwo.updateStat(COMP_DONE_WEEK, 1);
			
			if (winside == 1)
			{
				result = new ExOlympiadMatchResult(tie, winside, list1, list2, _winnerRound1.equalsIgnoreCase(_playerOne.getName()) ? 2 : 3, _winnerRound2.equalsIgnoreCase(_playerOne.getName()) ? 2 : 3, !_winnerRound3.isEmpty() && _winnerRound3.equalsIgnoreCase(_playerOne.getName()) ? 2 : _winnerRound3.isEmpty() ? 0 : 3);
			}
			else
			{
				result = new ExOlympiadMatchResult(tie, winside, list2, list1, _winnerRound1.equalsIgnoreCase(_playerTwo.getName()) ? 3 : 2, _winnerRound2.equalsIgnoreCase(_playerTwo.getName()) ? 3 : 2, !_winnerRound3.isEmpty() && _winnerRound3.equalsIgnoreCase(_playerTwo.getName()) ? 3 : _winnerRound3.isEmpty() ? 0 : 2);
			}
			
			stadium.broadcastPacket(result);
			
			if (Config.OLYMPIAD_LOG_FIGHTS)
			{
				LOGGER_OLYMPIAD.info(winner + "," + _playerOne.getName() + "," + _playerOne + "," + _playerTwo + "," + playerOneHp + "," + playerTwoHp + "," + _damageP1 + "," + _damageP2 + "," + pointDiff + "," + getType());
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "Exception on validateWinner(): " + e.getMessage(), e);
		}
	}
	
	@Override
	protected void addDamage(Player player, int damage)
	{
		final Player player1 = _playerOne.getPlayer();
		final Player player2 = _playerTwo.getPlayer();
		if ((player1 == null) || (player2 == null))
		{
			return;
		}
		
		if (player == player1)
		{
			if (!player2.isInvul() && !player2.isHpBlocked())
			{
				_damageP1 += damage;
			}
		}
		else if (player == player2)
		{
			if (!player1.isInvul() && !player1.isHpBlocked())
			{
				_damageP2 += damage;
			}
		}
	}
	
	@Override
	public String[] getPlayerNames()
	{
		return new String[]
		{
			_playerOne.getName(),
			_playerTwo.getName()
		};
	}
	
	@Override
	public boolean checkDefaulted()
	{
		SystemMessage reason;
		_playerOne.updatePlayer();
		_playerTwo.updatePlayer();
		
		reason = checkDefaulted(_playerOne.getPlayer());
		if (reason != null)
		{
			_playerOne.setDefaulted(true);
			if (_playerTwo.getPlayer() != null)
			{
				_playerTwo.getPlayer().sendPacket(reason);
			}
		}
		
		reason = checkDefaulted(_playerTwo.getPlayer());
		if (reason != null)
		{
			_playerTwo.setDefaulted(true);
			if (_playerOne.getPlayer() != null)
			{
				_playerOne.getPlayer().sendPacket(reason);
			}
		}
		
		return _playerOne.isDefaulted() || _playerTwo.isDefaulted();
	}
	
	@Override
	public void resetDamage()
	{
		_damageP1 = 0;
		_damageP2 = 0;
	}
	
	@Override
	public void resetDamageFinal()
	{
		_damageP1Final = 0;
		_damageP2Final = 0;
	}
	
	protected void saveResults(Participant one, Participant two, int winner, long startTime, long fightTime, CompetitionType type)
	{
		final int winnerFormat1 = one.getPlayer().getOlympiadFightHistory().getWinnerFormat(one, two, winner);
		one.getPlayer().getOlympiadFightHistory().addOlympiadFight(two, winnerFormat1);
		final int winnerFormat2 = two.getPlayer().getOlympiadFightHistory().getWinnerFormat(one, two, winner);
		two.getPlayer().getOlympiadFightHistory().addOlympiadFight(one, winnerFormat2);
		
		try (Connection con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO olympiad_fights (charOneId, charTwoId, charOneClass, charTwoClass, charOneLevel, charTwoLevel, winner, start, time, classed) values(?,?,?,?,?,?,?,?,?,?)"))
		{
			statement.setInt(1, one.getObjectId());
			statement.setInt(2, two.getObjectId());
			statement.setInt(3, one.getBaseClass());
			statement.setInt(4, two.getBaseClass());
			statement.setInt(5, one.getLevel());
			statement.setInt(6, two.getLevel());
			statement.setInt(7, winner);
			statement.setLong(8, startTime != 0 ? startTime : System.currentTimeMillis());
			statement.setLong(9, fightTime);
			statement.setInt(10, (type == CompetitionType.CLASSED ? 1 : 0));
			statement.execute();
		}
		catch (SQLException e)
		{
			LOGGER.log(Level.SEVERE, "SQL exception while saving olympiad fight.", e);
		}
	}
	
	@Override
	protected void healPlayers()
	{
		final Player player1 = _playerOne.getPlayer();
		if (player1 != null)
		{
			player1.setCurrentCp(player1.getMaxCp());
			player1.setCurrentHp(player1.getMaxHp());
			player1.setCurrentMp(player1.getMaxMp());
		}
		
		final Player player2 = _playerTwo.getPlayer();
		if (player2 != null)
		{
			player2.setCurrentCp(player2.getMaxCp());
			player2.setCurrentHp(player2.getMaxHp());
			player2.setCurrentMp(player2.getMaxMp());
		}
	}
	
	@Override
	protected void buffPlayers()
	{
		final Player player1 = _playerOne.getPlayer();
		if (player1 != null)
		{
			SkillCaster.triggerCast(player1, player1, CommonSkill.OLYMPIAD_HARMONY.getSkill());
			SkillCaster.triggerCast(player1, player1, CommonSkill.OLYMPIAD_MELODY.getSkill());
		}
		
		final Player player2 = _playerTwo.getPlayer();
		if (player2 != null)
		{
			SkillCaster.triggerCast(player2, player2, CommonSkill.OLYMPIAD_HARMONY.getSkill());
			SkillCaster.triggerCast(player2, player2, CommonSkill.OLYMPIAD_MELODY.getSkill());
		}
	}
	
	@Override
	protected void roundTwoCleanUp()
	{
		ThreadPool.schedule(() ->
		{
			if ((_playerOne.getPlayer() != null) && !_playerOne.isDefaulted() && !_playerOne.isDisconnected() && (_playerOne.getPlayer().getOlympiadGameId() == _stadiumId))
			{
				roundTwoClean(_playerOne.getPlayer());
			}
			
			if ((_playerTwo.getPlayer() != null) && !_playerTwo.isDefaulted() && !_playerTwo.isDisconnected() && (_playerTwo.getPlayer().getOlympiadGameId() == _stadiumId))
			{
				roundTwoClean(_playerTwo.getPlayer());
			}
		}, 1000);
	}
	
	@Override
	protected void untransformPlayers()
	{
		final Player player1 = _playerOne.getPlayer();
		if ((player1 != null) && player1.isTransformed())
		{
			player1.stopTransformation(true);
		}
		
		final Player player2 = _playerTwo.getPlayer();
		if ((player2 != null) && player2.isTransformed())
		{
			player2.stopTransformation(true);
		}
	}
	
	@Override
	public void makePlayersInvul()
	{
		if (_playerOne.getPlayer() != null)
		{
			_playerOne.getPlayer().setInvul(true);
		}
		if (_playerTwo.getPlayer() != null)
		{
			_playerTwo.getPlayer().setInvul(true);
		}
	}
	
	@Override
	public void removePlayersInvul()
	{
		if (_playerOne.getPlayer() != null)
		{
			_playerOne.getPlayer().setInvul(false);
		}
		if (_playerTwo.getPlayer() != null)
		{
			_playerTwo.getPlayer().setInvul(false);
		}
	}
}
