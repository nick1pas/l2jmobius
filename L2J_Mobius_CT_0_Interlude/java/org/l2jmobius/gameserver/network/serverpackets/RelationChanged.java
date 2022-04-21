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

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.actor.Playable;
import org.l2jmobius.gameserver.network.OutgoingPackets;

/**
 * @author Luca Baldi
 */
public class RelationChanged implements IClientOutgoingPacket
{
	public static final int RELATION_PVP_FLAG = 0x00002; // pvp ???
	public static final int RELATION_HAS_KARMA = 0x00004; // karma ???
	public static final int RELATION_LEADER = 0x00080; // leader
	public static final int RELATION_INSIEGE = 0x00200; // true if in siege
	public static final int RELATION_ATTACKER = 0x00400; // true when attacker
	public static final int RELATION_ALLY = 0x00800; // blue siege icon, cannot have if red
	public static final int RELATION_ENEMY = 0x01000; // true when red icon, doesn't matter with blue
	public static final int RELATION_MUTUAL_WAR = 0x08000; // double fist
	public static final int RELATION_1SIDED_WAR = 0x10000; // single fist
	
	protected static class Relation
	{
		int _objId;
		int _relation;
		int _autoAttackable;
		int _karma;
		int _pvpFlag;
	}
	
	private Relation _singled;
	private List<Relation> _multi;
	
	public RelationChanged(Playable activeChar, int relation, boolean autoattackable)
	{
		_singled = new Relation();
		_singled._objId = activeChar.getObjectId();
		_singled._relation = relation;
		_singled._autoAttackable = autoattackable ? 1 : 0;
		_singled._karma = activeChar.getKarma();
		_singled._pvpFlag = activeChar.getPvpFlag();
	}
	
	public RelationChanged()
	{
		_multi = new ArrayList<>();
	}
	
	public void addRelation(Playable activeChar, int relation, boolean autoattackable)
	{
		if (activeChar.isInvisible())
		{
			throw new IllegalArgumentException("Cannot add insivisble character to multi relation packet");
		}
		final Relation r = new Relation();
		r._objId = activeChar.getObjectId();
		r._relation = relation;
		r._autoAttackable = autoattackable ? 1 : 0;
		r._karma = activeChar.getKarma();
		r._pvpFlag = activeChar.getPvpFlag();
		_multi.add(r);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.RELATION_CHANGED.writeId(packet);
		if (_multi == null)
		{
			packet.writeD(1);
			writeRelation(packet, _singled);
		}
		else
		{
			packet.writeD(_multi.size());
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
		packet.writeD(relation._relation);
		packet.writeD(relation._autoAttackable);
		packet.writeD(relation._karma);
		packet.writeD(relation._pvpFlag);
	}
}
