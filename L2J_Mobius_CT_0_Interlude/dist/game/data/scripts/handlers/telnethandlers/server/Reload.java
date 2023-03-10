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
package handlers.telnethandlers.server;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.l2jmobius.Config;
import org.l2jmobius.commons.enums.ServerMode;
import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.data.ItemTable;
import org.l2jmobius.gameserver.data.sql.CrestTable;
import org.l2jmobius.gameserver.data.xml.AdminData;
import org.l2jmobius.gameserver.data.xml.ArmorSetData;
import org.l2jmobius.gameserver.data.xml.BuyListData;
import org.l2jmobius.gameserver.data.xml.DoorData;
import org.l2jmobius.gameserver.data.xml.EnchantItemData;
import org.l2jmobius.gameserver.data.xml.EnchantItemGroupsData;
import org.l2jmobius.gameserver.data.xml.MultisellData;
import org.l2jmobius.gameserver.data.xml.NpcData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.instancemanager.QuestManager;
import org.l2jmobius.gameserver.instancemanager.WalkingManager;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.network.telnet.ITelnetCommand;
import org.l2jmobius.gameserver.scripting.ScriptEngineManager;
import org.l2jmobius.gameserver.util.Util;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Reload implements ITelnetCommand
{
	private static final Logger LOGGER = Logger.getLogger(Reload.class.getName());
	
	@Override
	public String getCommand()
	{
		return "reload";
	}
	
	@Override
	public String getUsage()
	{
		return "Reload <zone/multisell/teleport/skill/npc/htm/item/config/npcwalkers/access/quests/door/html/script>";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length == 0) || args[0].isEmpty())
		{
			return null;
		}
		switch (args[0])
		{
			case "config":
			{
				Config.load(ServerMode.GAME);
				return "Telnet Admin: Reloaded Configs.";
			}
			case "access":
			{
				AdminData.getInstance().load();
				return "Telnet Admin: Reloaded Access.";
			}
			case "npc":
			{
				NpcData.getInstance().load();
				return "Telnet Admin: Reloaded Npcs.";
			}
			case "quest":
			{
				if (args.length > 1)
				{
					final String value = args[1];
					if (!Util.isDigit(value))
					{
						QuestManager.getInstance().reload(value);
						return "Telnet Admin: Reloaded Quest Name:" + value + ".";
					}
					final int questId = Integer.parseInt(value);
					QuestManager.getInstance().reload(questId);
					return "Telnet Admin: Reloaded Quest ID:" + questId + ".";
				}
				QuestManager.getInstance().reloadAllScripts();
				return "Telnet Admin: Reloaded Quests.";
			}
			case "walker":
			{
				WalkingManager.getInstance().load();
				return "Telnet Admin: Reloaded Walkers.";
			}
			case "htm":
			case "html":
			{
				if (args.length > 1)
				{
					final String path = args[1];
					final File file = new File(Config.DATAPACK_ROOT, "data/html/" + path);
					if (file.exists())
					{
						HtmCache.getInstance().reload(file);
						return "Telnet Admin: Reloaded Htm File:" + file.getName() + ".";
					}
					return "File or Directory does not exist.";
				}
				HtmCache.getInstance().reload();
				return "Cache[HTML]: " + HtmCache.getInstance().getMemoryUsage() + " megabytes on " + HtmCache.getInstance().getLoadedFiles() + " files loaded";
			}
			case "multisell":
			{
				MultisellData.getInstance().load();
				return "Telnet Admin: Reloaded Multisells.";
			}
			case "buylist":
			{
				BuyListData.getInstance().load();
				return "Telnet Admin: Reloaded Buylists.";
			}
			case "skill":
			{
				SkillData.getInstance().reload();
				return "Telnet Admin: Reloaded Skills.";
			}
			case "item":
			{
				ItemTable.getInstance().reload();
				return "Telnet Admin: Reloaded Items.";
			}
			case "door":
			{
				DoorData.getInstance().load();
				return "Telnet Admin: Reloaded Doors.";
			}
			case "zone":
			{
				ZoneManager.getInstance().reload();
				return "Telnet Admin: Reloaded Zones.";
			}
			case "crest":
			{
				CrestTable.getInstance().load();
				return "Telnet Admin: Reloaded Crests.";
			}
			case "effect":
			{
				try
				{
					ScriptEngineManager.getInstance().executeScript(ScriptEngineManager.EFFECT_MASTER_HANDLER_FILE);
					return "Telnet Admin: Reloaded effect master handler.";
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "Failed executing effect master handler!", e);
					return "Error reloading effect master handler: " + e.getMessage();
				}
			}
			case "handler":
			{
				try
				{
					ScriptEngineManager.getInstance().executeScript(ScriptEngineManager.MASTER_HANDLER_FILE);
					return "Telnet Admin: Reloaded master handler.";
				}
				catch (Exception e)
				{
					LOGGER.log(Level.WARNING, "Failed executing master handler!", e);
					return "Error reloading master handler: " + e.getMessage();
				}
			}
			case "enchant":
			{
				EnchantItemGroupsData.getInstance().load();
				EnchantItemData.getInstance().load();
				return "Telnet Admin: Reloaded item enchanting data.";
			}
			case "sets":
			{
				ArmorSetData.getInstance().load();
				return "Telnet Admin: Reloaded Armor sets data.";
			}
			case "script":
			{
				if (args.length < 2)
				{
					return "Syntax: reload script <path>";
				}
				try
				{
					ScriptEngineManager.getInstance().executeScript(Paths.get(args[1]));
					return "Script " + args[1] + " has been reloaded successfuly.";
				}
				catch (Exception e)
				{
					return "Couldn't reload script: " + args[1] + " err: " + e.getMessage();
				}
			}
		}
		return null;
	}
}
