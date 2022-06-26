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
package org.l2jmobius.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.instancemanager.CastleManager;
import org.l2jmobius.gameserver.instancemanager.FortManager;
import org.l2jmobius.gameserver.model.SiegeClan;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.holders.ResurrectByPaymentHolder;
import org.l2jmobius.gameserver.model.siege.Castle;
import org.l2jmobius.gameserver.model.siege.Fort;
import org.l2jmobius.gameserver.model.skill.BuffInfo;
import org.l2jmobius.gameserver.model.skill.CommonSkill;
import org.l2jmobius.gameserver.model.variables.PlayerVariables;
import org.l2jmobius.gameserver.network.OutgoingPackets;

/**
 * @author Mobius
 */
public class Die implements IClientOutgoingPacket
{
	private final int _objectId;
	private final boolean _isSweepable;
	private int _flags = 1; // To nearest village.
	private int _delayFeather = 0;
	private Player _player;
	
	public Die(Creature creature)
	{
		_objectId = creature.getObjectId();
		_isSweepable = creature.isAttackable() && creature.isSweepActive();
		if (creature.isPlayer())
		{
			_player = creature.getActingPlayer();
			final Clan clan = _player.getClan();
			boolean isInCastleDefense = false;
			boolean isInFortDefense = false;
			SiegeClan siegeClan = null;
			final Castle castle = CastleManager.getInstance().getCastle(creature);
			final Fort fort = FortManager.getInstance().getFort(creature);
			if ((castle != null) && castle.getSiege().isInProgress())
			{
				siegeClan = castle.getSiege().getAttackerClan(clan);
				isInCastleDefense = (siegeClan == null) && castle.getSiege().checkIsDefender(clan);
			}
			else if ((fort != null) && fort.getSiege().isInProgress())
			{
				siegeClan = fort.getSiege().getAttackerClan(clan);
				isInFortDefense = (siegeClan == null) && fort.getSiege().checkIsDefender(clan);
			}
			
			for (BuffInfo effect : creature.getEffectList().getEffects())
			{
				if (effect.getSkill().getId() == CommonSkill.FEATHER_OF_BLESSING.getId())
				{
					_delayFeather = effect.getTime();
					break;
				}
			}
			
			// ClanHall check.
			if ((clan != null) && (clan.getHideoutId() > 0))
			{
				_flags += 2;
			}
			// Castle check.
			if (((clan != null) && (clan.getCastleId() > 0)) || isInCastleDefense)
			{
				_flags += 4;
			}
			// Fortress check.
			if (((clan != null) && (clan.getFortId() > 0)) || isInFortDefense)
			{
				_flags += 8;
			}
			// Outpost check.
			if (((siegeClan != null) && !isInCastleDefense && !isInFortDefense && !siegeClan.getFlag().isEmpty()))
			{
				_flags += 16;
			}
			// Feather check.
			if (creature.getAccessLevel().allowFixedRes() || creature.getInventory().haveItemForSelfResurrection())
			{
				_flags += 32;
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.DIE.writeId(packet);
		packet.writeD(_objectId);
		packet.writeQ(_flags);
		packet.writeD(_isSweepable ? 1 : 0);
		packet.writeD(_delayFeather); // Feather item time.
		packet.writeC(0); // Hide die animation.
		packet.writeD(0);
		if ((_player != null) && Config.RESURRECT_BY_PAYMENT_ENABLED)
		{
			int resurrectTimes = _player.getVariables().getInt(PlayerVariables.RESURRECT_BY_PAYMENT_COUNT, 0) + 1;
			int originalValue = resurrectTimes - 1;
			if (originalValue < Config.RESURRECT_BY_PAYMENT_MAX_FREE_TIMES)
			{
				packet.writeD(Config.RESURRECT_BY_PAYMENT_MAX_FREE_TIMES - originalValue); // free round resurrection
				packet.writeD(0); // Adena resurrection
				packet.writeD(0); // Adena count%
				packet.writeD(0); // L-Coin resurrection
				packet.writeD(0); // L-Coin count%
			}
			else
			{
				packet.writeD(0);
				getValues(_player, packet, originalValue);
			}
		}
		else
		{
			packet.writeD(1); // free round resurrection
			packet.writeD(0); // Adena resurrection
			packet.writeD(-1); // Adena count%
			packet.writeD(0); // L-Coin resurrection
			packet.writeD(-1); // L-Coin count%
		}
		packet.writeD(0);
		return true;
	}
	
	private void getValues(Player player, PacketWriter packet, int originalValue)
	{
		if ((Config.RESURRECT_BY_PAYMENT_FIRST_RESURRECT_VALUES == null) || (Config.RESURRECT_BY_PAYMENT_SECOND_RESURRECT_VALUES == null))
		{
			packet.writeD(0); // Adena resurrection
			packet.writeD(-1); // Adena count%
			packet.writeD(0); // L-Coin resurrection
			packet.writeD(-1); // L-Coin count%
			return;
		}
		
		final List<Integer> levelListFirst = new ArrayList<>(Config.RESURRECT_BY_PAYMENT_FIRST_RESURRECT_VALUES.keySet());
		final List<Integer> levelListSecond = new ArrayList<>(Config.RESURRECT_BY_PAYMENT_SECOND_RESURRECT_VALUES.keySet());
		for (int level : levelListSecond)
		{
			if (Config.RESURRECT_BY_PAYMENT_SECOND_RESURRECT_VALUES.isEmpty())
			{
				packet.writeD(0); // Adena resurrection
				packet.writeD(-1); // Adena count%
				break;
			}
			
			if ((player.getLevel() >= level) && (levelListSecond.lastIndexOf(level) != (levelListSecond.size() - 1)))
			{
				continue;
			}
			
			int maxResTime;
			try
			{
				maxResTime = Config.RESURRECT_BY_PAYMENT_SECOND_RESURRECT_VALUES.get(level).keySet().stream().max(Integer::compareTo).get();
			}
			catch (Exception e)
			{
				packet.writeD(0); // Adena resurrection
				packet.writeD(-1); // Adena count%
				return;
			}
			
			int getValue = maxResTime <= originalValue ? maxResTime : originalValue + 1;
			ResurrectByPaymentHolder rbph = Config.RESURRECT_BY_PAYMENT_SECOND_RESURRECT_VALUES.get(level).get(getValue);
			packet.writeD(rbph.getAmount()); // Adena resurrection
			packet.writeD(Math.toIntExact(Math.round(rbph.getResurrectPercent()))); // Adena count%
			break;
		}
		
		for (int level : levelListFirst)
		{
			if (Config.RESURRECT_BY_PAYMENT_FIRST_RESURRECT_VALUES.isEmpty())
			{
				packet.writeD(0); // L-Coin resurrection
				packet.writeD(-1); // L-Coin count%
				break;
			}
			
			if ((player.getLevel() >= level) && (levelListFirst.lastIndexOf(level) != (levelListFirst.size() - 1)))
			{
				continue;
			}
			
			int maxResTime;
			try
			{
				maxResTime = Config.RESURRECT_BY_PAYMENT_FIRST_RESURRECT_VALUES.get(level).keySet().stream().max(Integer::compareTo).get();
			}
			catch (Exception e)
			{
				packet.writeD(0); // L-Coin resurrection
				packet.writeD(-1); // L-Coin count%
				return;
			}
			
			final int getValue = maxResTime <= originalValue ? maxResTime : originalValue + 1;
			ResurrectByPaymentHolder rbph = Config.RESURRECT_BY_PAYMENT_FIRST_RESURRECT_VALUES.get(level).get(getValue);
			packet.writeD(rbph.getAmount()); // L-Coin resurrection
			packet.writeD(Math.toIntExact(Math.round(rbph.getResurrectPercent()))); // L-Coin count%
			break;
		}
	}
}
