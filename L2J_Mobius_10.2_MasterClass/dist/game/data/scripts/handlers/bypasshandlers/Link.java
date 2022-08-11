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
package handlers.bypasshandlers;

import java.util.HashSet;
import java.util.Set;

import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.handler.IBypassHandler;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.instance.Teleporter;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;

public class Link implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"Link"
	};
	
	private static final Set<String> VALID_LINKS = new HashSet<>();
	static
	{
		VALID_LINKS.add("common/imbue_soul_crystal.htm");
		VALID_LINKS.add("common/extract_soul_crystal.htm");
		VALID_LINKS.add("common/attribute_info.htm");
		VALID_LINKS.add("common/augmentation_01.htm");
		VALID_LINKS.add("common/augmentation_02.htm");
		VALID_LINKS.add("common/augmentation_03.htm");
		VALID_LINKS.add("common/augmentation_03_01.htm");
		VALID_LINKS.add("common/augmentation_03_02.htm");
		VALID_LINKS.add("common/augmentation_03_03.htm");
		VALID_LINKS.add("common/augmentation_03_04.htm");
		VALID_LINKS.add("common/augmentation_03_05.htm");
		VALID_LINKS.add("common/augmentation_03_06.htm");
		VALID_LINKS.add("common/augmentation_03_07.htm");
		VALID_LINKS.add("common/augmentation_03_08.htm");
		VALID_LINKS.add("common/augmentation_03_09.htm");
		VALID_LINKS.add("common/augmentation_03_10.htm");
		VALID_LINKS.add("common/augmentation_03_11.htm");
		VALID_LINKS.add("common/augmentation_exchange.htm");
		VALID_LINKS.add("common/crafting_01.htm");
		VALID_LINKS.add("common/crafting_02.htm");
		VALID_LINKS.add("common/crafting_03.htm");
		VALID_LINKS.add("common/cursed_to_unidentified.htm");
		VALID_LINKS.add("common/duals_01.htm");
		VALID_LINKS.add("common/duals_02.htm");
		VALID_LINKS.add("common/duals_03.htm");
		VALID_LINKS.add("common/g_cube_warehouse001.htm");
		VALID_LINKS.add("common/skill_enchant_help.htm");
		VALID_LINKS.add("common/skill_enchant_help_01.htm");
		VALID_LINKS.add("common/skill_enchant_help_02.htm");
		VALID_LINKS.add("common/skill_enchant_help_03.htm");
		VALID_LINKS.add("common/smelting_trade001.htm");
		VALID_LINKS.add("common/upgrade_equipment_01.htm");
		VALID_LINKS.add("common/upgrade_equipment_02.htm");
		VALID_LINKS.add("common/upgrade_equipment_03.htm");
		VALID_LINKS.add("common/upgrade_equipment_04.htm");
		VALID_LINKS.add("common/weapon_sa_01.htm");
		VALID_LINKS.add("common/welcomeback002.htm");
		VALID_LINKS.add("common/welcomeback003.htm");
		VALID_LINKS.add("default/BlessingOfProtection.htm");
		VALID_LINKS.add("default/SupportMagic.htm");
		VALID_LINKS.add("default/SupportMagicServitor.htm");
		VALID_LINKS.add("fisherman/exchange_old_items.htm");
		VALID_LINKS.add("fisherman/fish_appearance_exchange.htm");
		VALID_LINKS.add("fisherman/fishing_manual001.htm");
		VALID_LINKS.add("fisherman/fishing_manual002.htm");
		VALID_LINKS.add("fisherman/fishing_manual003.htm");
		VALID_LINKS.add("fisherman/fishing_manual004.htm");
		VALID_LINKS.add("fisherman/fishing_manual008.htm");
		VALID_LINKS.add("fisherman/fishing_manual009.htm");
		VALID_LINKS.add("fisherman/fishing_manual010.htm");
		VALID_LINKS.add("fortress/foreman.htm");
		VALID_LINKS.add("guard/kamaloka_help.htm");
		VALID_LINKS.add("guard/kamaloka_level.htm");
		VALID_LINKS.add("petmanager/evolve.htm");
		VALID_LINKS.add("petmanager/exchange.htm");
		VALID_LINKS.add("petmanager/instructions.htm");
		VALID_LINKS.add("teleporter/separatedsoul.htm");
		VALID_LINKS.add("warehouse/clanwh.htm");
		VALID_LINKS.add("warehouse/privatewh.htm");
	}
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		final String htmlPath = command.substring(4).trim();
		if (htmlPath.isEmpty())
		{
			LOGGER.warning(player + " sent empty link html!");
			return false;
		}
		
		if (htmlPath.contains(".."))
		{
			LOGGER.warning(player + " sent invalid link html: " + htmlPath);
			return false;
		}
		
		String content = VALID_LINKS.contains(htmlPath) ? HtmCache.getInstance().getHtm(player, "data/html/" + htmlPath) : null;
		// Precaution.
		if (htmlPath.startsWith("teleporter/") && !(player.getTarget() instanceof Teleporter))
		{
			content = null;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(target != null ? target.getObjectId() : 0);
		if (content != null)
		{
			html.setHtml(content.replace("%objectId%", String.valueOf(target != null ? target.getObjectId() : 0)));
		}
		player.sendPacket(html);
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
