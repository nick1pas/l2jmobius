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
package org.l2jmobius.gameserver.network.clientpackets;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.gameserver.data.sql.EnchantSkillGroupsTable;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.model.EnchantSkillLearn;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Folk;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.serverpackets.ExEnchantSkillInfo;

/**
 * Format chdd c: (id) 0xD0 h: (subid) 0x06 d: skill id d: skill level
 * @author -Wooden-
 */
public class RequestExEnchantSkillInfo implements IClientIncomingPacket
{
	private int _skillId;
	private int _skillLevel;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_skillId = packet.readD();
		_skillLevel = packet.readD();
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		if ((_skillId <= 0) || (_skillLevel <= 0))
		{
			return;
		}
		
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		if (player.getLevel() < 76)
		{
			return;
		}
		
		final Npc trainer = player.getLastFolkNPC();
		if (!(trainer instanceof Folk))
		{
			return;
		}
		
		if (!player.isInsideRadius2D(trainer, Npc.INTERACTION_DISTANCE) && !player.isGM())
		{
			return;
		}
		
		final Skill skill = SkillData.getInstance().getSkill(_skillId, _skillLevel);
		if ((skill == null) || (skill.getId() != _skillId))
		{
			return;
		}
		
		if (!trainer.getTemplate().canTeach(player.getClassId()))
		{
			return; // cheater
		}
		
		boolean canTeach = false;
		for (EnchantSkillLearn s : EnchantSkillGroupsTable.getInstance().getAvailableEnchantSkills(player))
		{
			if ((s.getId() == _skillId) && (s.getLevel() == _skillLevel))
			{
				canTeach = true;
				break;
			}
		}
		
		if (!canTeach)
		{
			return; // cheater
		}
		
		final int requiredSp = EnchantSkillGroupsTable.getInstance().getSkillSpCost(player, skill);
		final int requiredExp = EnchantSkillGroupsTable.getInstance().getSkillExpCost(player, skill);
		final byte rate = EnchantSkillGroupsTable.getInstance().getSkillRate(player, skill);
		final ExEnchantSkillInfo asi = new ExEnchantSkillInfo(_skillId, _skillLevel, requiredSp, requiredExp, rate);
		if (Config.ES_SP_BOOK_NEEDED && ((_skillLevel == 101) || (_skillLevel == 141))) // only first level requires book
		{
			asi.addRequirement(4, 6622, 1, 0);
		}
		player.sendPacket(asi);
	}
}
