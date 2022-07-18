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
package org.l2jmobius.gameserver.data.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.l2jmobius.commons.util.IXmlReader;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.homunculus.HomunculusSlotTemplate;

/**
 * @author Index
 */
public class HomunculusSlotData implements IXmlReader
{
	private static final Map<Integer, HomunculusSlotTemplate> TEMPLATES = new HashMap<>();
	
	protected HomunculusSlotData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		TEMPLATES.clear();
		parseDatapackFile("data/HomunculusSlotData.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + TEMPLATES.size() + " templates.");
	}
	
	@Override
	public void parseDocument(Document doc, File f)
	{
		StatSet set;
		Node att;
		NamedNodeMap attrs;
		for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if ("homunculusSlot".equalsIgnoreCase(d.getNodeName()))
					{
						attrs = d.getAttributes();
						set = new StatSet();
						for (int i = 0; i < attrs.getLength(); i++)
						{
							att = attrs.item(i);
							set.set(att.getNodeName(), att.getNodeValue());
						}
						
						List<ItemHolder> fee = Collections.emptyList();
						for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
						{
							if ("fee".equalsIgnoreCase(c.getNodeName()))
							{
								fee = getItemList(c);
								break;
							}
						}
						final int slotId = set.getInt("slotId");
						TEMPLATES.put(slotId, new HomunculusSlotTemplate(slotId, fee, set.getBoolean("isEnabled", false)));
					}
				}
			}
		}
	}
	
	private List<ItemHolder> getItemList(Node c)
	{
		final List<ItemHolder> items = new ArrayList<>();
		for (Node b = c.getFirstChild(); b != null; b = b.getNextSibling())
		{
			if ("item".equalsIgnoreCase(b.getNodeName()))
			{
				final int itemId = Integer.parseInt(b.getAttributes().getNamedItem("id").getNodeValue());
				final long itemCount = Long.parseLong(b.getAttributes().getNamedItem("count").getNodeValue());
				items.add(new ItemHolder(itemId, itemCount));
			}
		}
		return items;
	}
	
	public HomunculusSlotTemplate getTemplate(int id)
	{
		return TEMPLATES.get(id);
	}
	
	public static HomunculusSlotData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HomunculusSlotData INSTANCE = new HomunculusSlotData();
	}
}
