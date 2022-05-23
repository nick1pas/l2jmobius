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

import java.util.LinkedList;
import java.util.List;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.network.OutgoingPackets;

/**
 * @author Luca Baldi
 */
public class RelationChanged implements IClientOutgoingPacket
{
	public static final int RELATION_PARTY1 = 1; // party member
	public static final int RELATION_PARTY2 = 2; // party member
	public static final int RELATION_PARTY3 = 4; // party member
	public static final int RELATION_PARTY4 = 8; // party member (for information, see Player.getRelation())
	public static final int RELATION_PARTYLEADER = 16; // true if is party leader
	public static final int RELATION_HAS_PARTY = 32; // true if is in party
	public static final int RELATION_CLAN_MEMBER = 64; // true if is in clan
	public static final int RELATION_LEADER = 128; // true if is clan leader
	public static final int RELATION_CLAN_MATE = 256; // true if is in same clan
	public static final int RELATION_INSIEGE = 512; // true if in siege
	public static final int RELATION_ATTACKER = 1024; // true when attacker
	public static final int RELATION_ALLY = 2048; // blue siege icon, cannot have if red
	public static final int RELATION_ENEMY = 4096; // true when red icon, doesn't matter with blue
	public static final int RELATION_DECLARED_WAR = 8192; // single sword
	public static final int RELATION_MUTUAL_WAR = 24576; // double swords
	public static final int RELATION_ALLY_MEMBER = 65536; // clan is in alliance
	public static final int RELATION_TERRITORY_WAR = 524288; // show Territory War icon
	// Masks
	public static final byte SEND_DEFAULT = 1;
	public static final byte SEND_ONE = 2;
	public static final byte SEND_MULTI = 4;
	
	protected static class Relation
	{
		int _objId;
		long _relation;
		int _autoAttackable;
		int _reputation;
		int _pvpFlag;
	}
	
	private Relation _singled;
	private final List<Relation> _multi;
	private byte _mask = 0;
	
	public RelationChanged(Playable activeChar, long relation, boolean autoattackable)
	{
		_mask |= SEND_ONE;
		_singled = new Relation();
		_singled._objId = activeChar.getObjectId();
		_singled._relation = relation;
		_singled._autoAttackable = autoattackable ? 1 : 0;
		_singled._reputation = activeChar.getReputation();
		_singled._pvpFlag = activeChar.getPvpFlag();
		_multi = null;
	}
	
	public RelationChanged()
	{
		_mask |= SEND_MULTI;
		_multi = new LinkedList<>();
	}
	
	public void addRelation(Playable activeChar, long relation, boolean autoattackable)
	{
		if (activeChar.isInvisible())
		{
			return;
		}
		final Relation r = new Relation();
		r._objId = activeChar.getObjectId();
		r._relation = relation;
		r._autoAttackable = autoattackable ? 1 : 0;
		r._reputation = activeChar.getReputation();
		r._pvpFlag = activeChar.getPvpFlag();
		_multi.add(r);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.RELATION_CHANGED.writeId(packet);
		packet.writeC(_mask);
		if (_multi == null)
		{
			writeRelation(packet, _singled);
		}
		else
		{
			packet.writeH(_multi.size());
			for (Relation r : _multi)
			{
				writeRelation(packet, r);
			}
		}
		return true;
	}
	
	private void writeRelation(PacketWriter packet, Relation relation)
	{
		packet.writeD(relation._objId);
		if ((_mask & SEND_DEFAULT) != SEND_DEFAULT)
		{
			packet.writeQ(relation._relation);
			packet.writeC(relation._autoAttackable);
			packet.writeD(relation._reputation);
			packet.writeC(relation._pvpFlag);
		}
	}
}
