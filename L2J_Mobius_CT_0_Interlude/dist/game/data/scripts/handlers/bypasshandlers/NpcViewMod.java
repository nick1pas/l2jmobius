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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.l2jmobius.Config;
import org.l2jmobius.gameserver.cache.HtmCache;
import org.l2jmobius.gameserver.data.ItemTable;
import org.l2jmobius.gameserver.enums.DropType;
import org.l2jmobius.gameserver.handler.IBypassHandler;
import org.l2jmobius.gameserver.model.Spawn;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.DropGroupHolder;
import org.l2jmobius.gameserver.model.holders.DropHolder;
import org.l2jmobius.gameserver.model.item.ItemTemplate;
import org.l2jmobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2jmobius.gameserver.util.HtmlUtil;
import org.l2jmobius.gameserver.util.Util;

/**
 * @author NosBit
 */
public class NpcViewMod implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"NpcViewMod"
	};
	
	private static final int DROP_LIST_ITEMS_PER_PAGE = 10;
	
	@Override
	public boolean useBypass(String command, Player player, Creature bypassOrigin)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		if (!st.hasMoreTokens())
		{
			LOGGER.warning("Bypass[NpcViewMod] used without enough parameters.");
			return false;
		}
		
		final String actualCommand = st.nextToken();
		switch (actualCommand.toLowerCase())
		{
			case "view":
			{
				final WorldObject target;
				if (st.hasMoreElements())
				{
					try
					{
						target = World.getInstance().findObject(Integer.parseInt(st.nextToken()));
					}
					catch (NumberFormatException e)
					{
						return false;
					}
				}
				else
				{
					target = player.getTarget();
				}
				
				final Npc npc = target instanceof Npc ? (Npc) target : null;
				if (npc == null)
				{
					return false;
				}
				
				sendNpcView(player, npc);
				break;
			}
			case "droplist":
			{
				if (st.countTokens() < 2)
				{
					LOGGER.warning("Bypass[NpcViewMod] used without enough parameters.");
					return false;
				}
				
				final String dropListTypeString = st.nextToken();
				try
				{
					final DropType dropListType = Enum.valueOf(DropType.class, dropListTypeString);
					final WorldObject target = World.getInstance().findObject(Integer.parseInt(st.nextToken()));
					final Npc npc = target instanceof Npc ? (Npc) target : null;
					if (npc == null)
					{
						return false;
					}
					final int page = st.hasMoreElements() ? Integer.parseInt(st.nextToken()) : 0;
					sendNpcDropList(player, npc, dropListType, page);
				}
				catch (NumberFormatException e)
				{
					return false;
				}
				catch (IllegalArgumentException e)
				{
					LOGGER.warning("Bypass[NpcViewMod] unknown drop list scope: " + dropListTypeString);
					return false;
				}
				break;
			}
		}
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
	
	public static void sendNpcView(Player player, Npc npc)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage();
		html.setFile(player, "data/html/mods/NpcView/Info.htm");
		html.replace("%name%", npc.getName());
		html.replace("%hpGauge%", HtmlUtil.getHpGauge(250, (long) npc.getCurrentHp(), npc.getMaxHp(), false));
		html.replace("%mpGauge%", HtmlUtil.getMpGauge(250, (long) npc.getCurrentMp(), npc.getMaxMp(), false));
		
		final Spawn npcSpawn = npc.getSpawn();
		if ((npcSpawn == null) || (npcSpawn.getRespawnMinDelay() == 0))
		{
			html.replace("%respawn%", "None");
		}
		else
		{
			TimeUnit timeUnit = TimeUnit.MILLISECONDS;
			long min = Long.MAX_VALUE;
			for (TimeUnit tu : TimeUnit.values())
			{
				final long minTimeFromMillis = tu.convert(npcSpawn.getRespawnMinDelay(), TimeUnit.MILLISECONDS);
				final long maxTimeFromMillis = tu.convert(npcSpawn.getRespawnMaxDelay(), TimeUnit.MILLISECONDS);
				if ((TimeUnit.MILLISECONDS.convert(minTimeFromMillis, tu) == npcSpawn.getRespawnMinDelay()) && (TimeUnit.MILLISECONDS.convert(maxTimeFromMillis, tu) == npcSpawn.getRespawnMaxDelay()) && (min > minTimeFromMillis))
				{
					min = minTimeFromMillis;
					timeUnit = tu;
				}
			}
			final long minRespawnDelay = timeUnit.convert(npcSpawn.getRespawnMinDelay(), TimeUnit.MILLISECONDS);
			final long maxRespawnDelay = timeUnit.convert(npcSpawn.getRespawnMaxDelay(), TimeUnit.MILLISECONDS);
			final String timeUnitName = timeUnit.name().charAt(0) + timeUnit.name().toLowerCase().substring(1);
			if (npcSpawn.hasRespawnRandom())
			{
				html.replace("%respawn%", minRespawnDelay + "-" + maxRespawnDelay + " " + timeUnitName);
			}
			else
			{
				html.replace("%respawn%", minRespawnDelay + " " + timeUnitName);
			}
		}
		
		html.replace("%atktype%", Util.capitalizeFirst(npc.getAttackType().name().toLowerCase()));
		html.replace("%atkrange%", npc.getStat().getPhysicalAttackRange());
		html.replace("%patk%", (int) npc.getPAtk(player));
		html.replace("%pdef%", (int) npc.getPDef(player));
		html.replace("%matk%", (int) npc.getMAtk(player, null));
		html.replace("%mdef%", (int) npc.getMDef(player, null));
		html.replace("%atkspd%", npc.getPAtkSpd());
		html.replace("%castspd%", npc.getMAtkSpd());
		html.replace("%critrate%", npc.getStat().getCriticalHit(player, null));
		html.replace("%evasion%", npc.getEvasionRate(player));
		html.replace("%accuracy%", npc.getStat().getAccuracy());
		html.replace("%speed%", (int) npc.getStat().getMoveSpeed());
		html.replace("%dropListButtons%", getDropListButtons(npc));
		player.sendPacket(html);
	}
	
	private static String getDropListButtons(Npc npc)
	{
		final StringBuilder sb = new StringBuilder();
		final List<DropGroupHolder> dropListGroups = npc.getTemplate().getDropGroups();
		final List<DropHolder> dropListDeath = npc.getTemplate().getDropList();
		final List<DropHolder> dropListSpoil = npc.getTemplate().getSpoilList();
		if ((dropListGroups != null) || (dropListDeath != null) || (dropListSpoil != null))
		{
			sb.append("<table width=275 cellpadding=0 cellspacing=0><tr>");
			if ((dropListGroups != null) || (dropListDeath != null))
			{
				sb.append("<td align=center><button value=\"Show Drop\" width=95 height=21 action=\"bypass NpcViewMod dropList DROP " + npc.getObjectId() + "\" back=\"bigbutton_over\" fore=\"bigbutton\"></td>");
			}
			
			if (dropListSpoil != null)
			{
				sb.append("<td align=center><button value=\"Show Spoil\" width=95 height=21 action=\"bypass NpcViewMod dropList SPOIL " + npc.getObjectId() + "\" back=\"bigbutton_over\" fore=\"bigbutton\"></td>");
			}
			
			sb.append("</tr></table>");
		}
		return sb.toString();
	}
	
	private void sendNpcDropList(Player player, Npc npc, DropType dropType, int pageValue)
	{
		List<DropHolder> dropList = null;
		if (dropType == DropType.SPOIL)
		{
			dropList = new ArrayList<>(npc.getTemplate().getSpoilList());
		}
		else
		{
			final List<DropHolder> drops = npc.getTemplate().getDropList();
			if (drops != null)
			{
				dropList = new ArrayList<>(drops);
			}
			final List<DropGroupHolder> dropGroups = npc.getTemplate().getDropGroups();
			if (dropGroups != null)
			{
				if (dropList == null)
				{
					dropList = new ArrayList<>();
				}
				for (DropGroupHolder dropGroup : dropGroups)
				{
					final double chance = dropGroup.getChance() / 100;
					for (DropHolder dropHolder : dropGroup.getDropList())
					{
						dropList.add(new DropHolder(dropHolder.getDropType(), dropHolder.getItemId(), dropHolder.getMin(), dropHolder.getMax(), dropHolder.getChance() * chance));
					}
				}
			}
		}
		if (dropList == null)
		{
			return;
		}
		
		Collections.sort(dropList, (d1, d2) -> Integer.valueOf(d1.getItemId()).compareTo(Integer.valueOf(d2.getItemId())));
		
		int pages = dropList.size() / DROP_LIST_ITEMS_PER_PAGE;
		if ((DROP_LIST_ITEMS_PER_PAGE * pages) < dropList.size())
		{
			pages++;
		}
		
		final StringBuilder pagesSb = new StringBuilder();
		if (pages > 1)
		{
			pagesSb.append("<table><tr>");
			for (int i = 0; i < pages; i++)
			{
				pagesSb.append("<td align=center><button value=\"" + (i + 1) + "\" width=20 height=15 action=\"bypass NpcViewMod dropList " + dropType + " " + npc.getObjectId() + " " + i + "\" back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
			}
			pagesSb.append("</tr></table>");
		}
		
		int page = pageValue;
		if (page >= pages)
		{
			page = pages - 1;
		}
		
		final int start = page > 0 ? page * DROP_LIST_ITEMS_PER_PAGE : 0;
		int end = (page * DROP_LIST_ITEMS_PER_PAGE) + DROP_LIST_ITEMS_PER_PAGE;
		if (end > dropList.size())
		{
			end = dropList.size();
		}
		
		final DecimalFormat amountFormat = new DecimalFormat("#,###");
		final DecimalFormat chanceFormat = new DecimalFormat("0.00##");
		final StringBuilder totalSb = new StringBuilder();
		String limitReachedMsg = "";
		for (int i = start; i < end; i++)
		{
			final StringBuilder sb = new StringBuilder();
			final DropHolder dropItem = dropList.get(i);
			final ItemTemplate item = ItemTable.getInstance().getTemplate(dropItem.getItemId());
			
			// real time server rate calculations
			double rateChance = 1;
			double rateAmount = 1;
			if (dropType == DropType.SPOIL)
			{
				rateChance = Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER;
				rateAmount = Config.RATE_SPOIL_DROP_AMOUNT_MULTIPLIER;
				
				// also check premium rates if available
				if (Config.PREMIUM_SYSTEM_ENABLED && player.hasPremiumStatus())
				{
					rateChance *= Config.PREMIUM_RATE_SPOIL_CHANCE;
					rateAmount *= Config.PREMIUM_RATE_SPOIL_AMOUNT;
				}
			}
			else
			{
				if (Config.RATE_DROP_CHANCE_BY_ID.get(dropItem.getItemId()) != null)
				{
					rateChance *= Config.RATE_DROP_CHANCE_BY_ID.get(dropItem.getItemId());
				}
				else if (item.hasExImmediateEffect())
				{
					rateChance *= Config.RATE_HERB_DROP_CHANCE_MULTIPLIER;
				}
				else if (npc.isRaid())
				{
					rateChance *= Config.RATE_RAID_DROP_CHANCE_MULTIPLIER;
				}
				else
				{
					rateChance *= Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER;
				}
				
				if (Config.RATE_DROP_AMOUNT_BY_ID.get(dropItem.getItemId()) != null)
				{
					rateAmount *= Config.RATE_DROP_AMOUNT_BY_ID.get(dropItem.getItemId());
				}
				else if (item.hasExImmediateEffect())
				{
					rateAmount *= Config.RATE_HERB_DROP_AMOUNT_MULTIPLIER;
				}
				else if (npc.isRaid())
				{
					rateAmount *= Config.RATE_RAID_DROP_AMOUNT_MULTIPLIER;
				}
				else
				{
					rateAmount *= Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
				}
				
				// also check premium rates if available
				if (Config.PREMIUM_SYSTEM_ENABLED && player.hasPremiumStatus())
				{
					if (Config.PREMIUM_RATE_DROP_CHANCE_BY_ID.get(dropItem.getItemId()) != null)
					{
						rateChance *= Config.PREMIUM_RATE_DROP_CHANCE_BY_ID.get(dropItem.getItemId());
					}
					else if (item.hasExImmediateEffect())
					{
						// TODO: Premium herb chance? :)
					}
					else if (npc.isRaid())
					{
						// TODO: Premium raid chance? :)
					}
					else
					{
						rateChance *= Config.PREMIUM_RATE_DROP_CHANCE;
					}
					
					if (Config.PREMIUM_RATE_DROP_AMOUNT_BY_ID.get(dropItem.getItemId()) != null)
					{
						rateAmount *= Config.PREMIUM_RATE_DROP_AMOUNT_BY_ID.get(dropItem.getItemId());
					}
					else if (item.hasExImmediateEffect())
					{
						// TODO: Premium herb amount? :)
					}
					else if (npc.isRaid())
					{
						// TODO: Premium raid amount? :)
					}
					else
					{
						rateAmount *= Config.PREMIUM_RATE_DROP_AMOUNT;
					}
				}
			}
			
			sb.append("<table width=295 cellpadding=2 cellspacing=0 background=\"000000\">");
			sb.append("<tr><td width=32 valign=top>");
			sb.append("<img src=\"" + (item.getIcon() == null ? "icon.NOIMAGE" : item.getIcon()) + "\" width=32 height=32>");
			sb.append("</td><td fixwidth=80 align=left><font name=\"hs9\" color=\"CD9000\">");
			sb.append(item.getName());
			sb.append("</font></td><td width=0></td><td width=50><table width=150 cellpadding=0 cellspacing=0>");
			sb.append("<tr><td width=48 align=right valign=top><font color=\"LEVEL\">Amount:</font></td>");
			sb.append("<td width=90 align=center>");
			
			final long min = (long) (dropItem.getMin() * rateAmount);
			final long max = (long) (dropItem.getMax() * rateAmount);
			if (min == max)
			{
				sb.append(amountFormat.format(min));
			}
			else
			{
				sb.append(amountFormat.format(min));
				sb.append(" - ");
				sb.append(amountFormat.format(max));
			}
			
			sb.append("</td></tr><tr><td width=48 align=right valign=top><font color=\"LEVEL\">Chance:</font></td>");
			sb.append("<td width=90 align=center>");
			sb.append(chanceFormat.format(Math.min(dropItem.getChance() * rateChance, 100)));
			sb.append("%</td></tr></table></td></tr><tr><td width=32></td><td width=95>&nbsp;</td></tr></table>");
			if ((sb.length() + totalSb.length()) < 16000) // limit of 32766?
			{
				totalSb.append(sb);
			}
			else
			{
				limitReachedMsg = "<br><center>Too many drops! Could not display them all!</center>";
			}
		}
		
		final StringBuilder bodySb = new StringBuilder();
		bodySb.append("<table><tr>");
		bodySb.append("<td>");
		bodySb.append(totalSb.toString());
		bodySb.append("</td>");
		bodySb.append("</tr></table>");
		
		String html = HtmCache.getInstance().getHtm(player, "data/html/mods/NpcView/DropList.htm");
		if (html == null)
		{
			LOGGER.warning(NpcViewMod.class.getSimpleName() + ": The html file data/html/mods/NpcView/DropList.htm could not be found.");
			return;
		}
		html = html.replace("%name%", npc.getName());
		html = html.replace("%dropListButtons%", getDropListButtons(npc));
		html = html.replace("%pages%", pagesSb.toString());
		html = html.replace("%items%", bodySb.toString() + limitReachedMsg);
		Util.sendHtml(player, html);
	}
}
