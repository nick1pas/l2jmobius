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
package org.l2jmobius.gameserver.network.serverpackets.equipmentupgradenormal;

import java.util.Collections;
import java.util.List;

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.holders.UniqueItemEnchantHolder;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.serverpackets.AbstractItemPacket;

/**
 * @author Index
 */
public class ExUpgradeSystemNormalResult extends AbstractItemPacket
{
	public static final ExUpgradeSystemNormalResult FAIL = new ExUpgradeSystemNormalResult(0, 0, false, Collections.emptyList(), Collections.emptyList());
	
	private final int _result;
	private final int _upgradeId;
	private final boolean _success;
	private final List<UniqueItemEnchantHolder> _resultItems;
	private final List<UniqueItemEnchantHolder> _bonusItems;
	
	public ExUpgradeSystemNormalResult(int result, int upgradeId, boolean success, List<UniqueItemEnchantHolder> resultItems, List<UniqueItemEnchantHolder> bonusItems)
	{
		_result = result;
		_upgradeId = upgradeId;
		_success = success;
		_resultItems = resultItems;
		_bonusItems = bonusItems;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_UPGRADE_SYSTEM_NORMAL_RESULT.writeId(packet);
		packet.writeH(_result); // Result ID
		packet.writeD(_upgradeId); // Upgrade ID
		packet.writeC(_success ? 1 : 0); // Success
		packet.writeD(_resultItems.size()); // Array of result items (success/failure) start.
		for (UniqueItemEnchantHolder item : _resultItems)
		{
			packet.writeD(item.getObjectId());
			packet.writeD(item.getId());
			packet.writeD(item.getEnchantLevel());
			packet.writeD(Math.toIntExact(item.getCount()));
		}
		packet.writeC(0); // Is bonus? Do not see any effect.
		packet.writeD(_bonusItems.size()); // Array of bonus items start.
		for (UniqueItemEnchantHolder bonus : _bonusItems)
		{
			packet.writeD(bonus.getObjectId());
			packet.writeD(bonus.getId());
			packet.writeD(bonus.getEnchantLevel());
			packet.writeD(Math.toIntExact(bonus.getCount()));
		}
		return true;
	}
}
