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
package ai.others;

import java.util.ArrayList;
import java.util.List;

import org.l2jmobius.gameserver.instancemanager.GlobalVariablesManager;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.ItemHolder;

import ai.AbstractNpcAI;

/**
 * Greedy Treasure Chests AI.
 * @URL https://dev.l2central.info/main/articles/355.html
 * @author CostyKiller, NasSeKa
 */
public final class GreedyTreasureChests extends AbstractNpcAI
{
	// NPCs
	private static final int CHEST_LV110 = 8710;
	private static final int CHEST_LV120 = 8711;
	//@formatter:off
	private static final int[] MONSTERS = 
	{
		// Silent Valley
		24506, 24507, 24508, 24509, 24510,
		// Ivory Tower Crater
		24421, 24422, 24423, 24424, 24425, 24426,
		// Tanor Canyon
		20936, 20937, 20938, 20939, 20940, 20941, 20942, 20943, 24587,
		// Alligator Island
		24373, 24376, 24377,	
		// Field of Silence
		24517, 24520, 24521, 24522, 24523,
		// Forest of Mirrors
		24461, 24462, 24463, 24464, 24465, 24466,
		// Varka Silenos Barracks
		24636, 24637, 24638, 24639, 24640, 				  
		// Ketra Orc Outpost
		24631, 24632, 24633, 24634, 24635,
		// Field of Whispers
		24304, 24305, 24306, 24307, 24308,
		// Isle of Prayer
		24445, 24446, 24447, 24448, 24449, 24450, 24451,
		// Breka's Stronghold
		24415, 24416, 24417, 24418, 24419, 24420,
		// Sel Mahum Training Grounds
		24492, 24493, 24494, 24495,
		// Plains of Lizardman
		24496, 24497, 24498, 24499,
		// Fields of Massacre
		24486, 24487, 24488, 24489, 24490, 24491,
		// Sea Of Spores
		24621, 24622, 24623, 24624,
		// Dragon Valley
		24481, 24482,
		// Fafurion Temple
		24318, 24322, 24323, 24325, 24329,
		// Wastelands
		24500, 24501, 24502, 24503, 24504, 24505,
		// Beast Farm
		24651, 24652, 24653, 24654, 24655, 24656, 24657, 24658, 24659,
		// Valley of Saints
		24876, 24877, 24878, 24879, 24880,
		// Hot Spirits
		24881, 24882, 24883, 24884, 24885, 24886,
		// Argos Wall 116 lvl (not 122)
		24606, 24607, 24608, 24609, 24610, 24611,
		// Neutral Zone (108)
		24641, 24642, 24643, 24644,
	};
	//@formatter:on
	// Items
	private static final List<ItemHolder> DROPLIST_LV110_CHEST = new ArrayList<>();
	static
	{
		DROPLIST_LV110_CHEST.add(new ItemHolder(27538, 1)); // +7 Bloody Helios Pack
		DROPLIST_LV110_CHEST.add(new ItemHolder(48204, 1)); // Radiant Warrior's Circlet
		DROPLIST_LV110_CHEST.add(new ItemHolder(48207, 1)); // Radiant Wizard's Circlet
		DROPLIST_LV110_CHEST.add(new ItemHolder(48210, 1)); // Radiant Knight's Circlet
		DROPLIST_LV110_CHEST.add(new ItemHolder(48493, 1)); // Dragon Rind Leather Shirt
		DROPLIST_LV110_CHEST.add(new ItemHolder(48876, 1)); // Forgotten Spellbook Chapter 1
		DROPLIST_LV110_CHEST.add(new ItemHolder(48877, 1)); // Forgotten Spellbook Chapter 2
		DROPLIST_LV110_CHEST.add(new ItemHolder(48878, 1)); // Forgotten Spellbook Chapter 3
		DROPLIST_LV110_CHEST.add(new ItemHolder(48324, 1)); // Agathion Master's Box
		DROPLIST_LV110_CHEST.add(new ItemHolder(48910, 1)); // Shillien's Soul Crystal Box
		DROPLIST_LV110_CHEST.add(new ItemHolder(80996, 1)); // Storm Isle's Time Stone
		DROPLIST_LV110_CHEST.add(new ItemHolder(80997, 1)); // Primeval Isle's Time Stone
		DROPLIST_LV110_CHEST.add(new ItemHolder(80998, 1)); // Golden Altar's Time Stone
		DROPLIST_LV110_CHEST.add(new ItemHolder(81358, 1)); // Abandoned Coal Mines' Time Stone
		DROPLIST_LV110_CHEST.add(new ItemHolder(47751, 1)); // +8 Eternal Heavy Armor Capsule
		DROPLIST_LV110_CHEST.add(new ItemHolder(47752, 1)); // +8 Eternal Light Armor Capsule
		DROPLIST_LV110_CHEST.add(new ItemHolder(47753, 1)); // +8 Eternal Robe Capsule
	}
	private static final List<ItemHolder> DROPLIST_LV120_CHEST = new ArrayList<>();
	static
	{
		DROPLIST_LV120_CHEST.add(new ItemHolder(38859, 1)); // Ruby Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(47686, 1)); // Blue Cat's Eye Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(47681, 1)); // Red Cat's Eye Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(38889, 1)); // Aquamarine Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(46684, 1)); // Tanzanite Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(38884, 1)); // Emerald Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(38931, 1)); // Sapphire Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(38874, 1)); // Obsidian Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(38879, 1)); // Opal Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(38899, 1)); // Pearl Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(46674, 1)); // Vital Stone Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(46679, 1)); // Garnet Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(38854, 1)); // Topaz Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(38894, 1)); // Diamond Lv. 5
		DROPLIST_LV120_CHEST.add(new ItemHolder(81453, 1)); // Improved Rune Stone
		DROPLIST_LV120_CHEST.add(new ItemHolder(80419, 1)); // Artifact Pack
		DROPLIST_LV120_CHEST.add(new ItemHolder(82229, 1)); // Tower of Insolence's Time Stone
		DROPLIST_LV120_CHEST.add(new ItemHolder(81449, 1)); // Angel's Earring Pack
		DROPLIST_LV120_CHEST.add(new ItemHolder(81450, 1)); // Angel's Ring Pack
		DROPLIST_LV120_CHEST.add(new ItemHolder(81448, 1)); // Angel's Necklace Pack
		DROPLIST_LV120_CHEST.add(new ItemHolder(48860, 1)); // Fallen Angel's Ring Pack
	}
	// Misc
	private static final int TREASURE_CHEST_CHANCE = 15; // 15% chance to spawn
	private static final int RND_OFFSET = 10800000; // 3 hours = 10.800.000 milliseconds
	private static final long RESPAWN_DELAY = 43200000; // 12 hours = 43.200.000 milliseconds
	
	private GreedyTreasureChests()
	{
		super();
		addStartNpc(CHEST_LV110);
		addStartNpc(CHEST_LV120);
		addTalkId(CHEST_LV110);
		addTalkId(CHEST_LV120);
		addKillId(CHEST_LV110);
		addKillId(CHEST_LV120);
		addKillId(MONSTERS);
	}
	
	@Override
	public String onKill(Npc npc, Player killer, boolean isSummon)
	{
		if (npc.getId() == CHEST_LV110)
		{
			giveItems(killer, getRandomEntry(DROPLIST_LV110_CHEST));
		}
		else if (npc.getId() == CHEST_LV120)
		{
			giveItems(killer, getRandomEntry(DROPLIST_LV120_CHEST));
		}
		else if (getRandom(150) == TREASURE_CHEST_CHANCE)
		{
			final long currentTime = System.currentTimeMillis();
			switch (npc.getId())
			{
				// Silent Valley
				case 24506:
				case 24507:
				case 24508:
				case 24509:
				case 24510:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SV_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SV_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SV_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SV_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Ivory Tower Crater
				case 24421:
				case 24422:
				case 24423:
				case 24424:
				case 24425:
				case 24426:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_IT_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_IT_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_IT_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_IT_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Tanor Canyon
				case 20936:
				case 20937:
				case 20938:
				case 20939:
				case 20940:
				case 20941:
				case 20942:
				case 20943:
				case 24587:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_TC_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_TC_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_TC_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_TC_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Alligator Island
				case 24373:
				case 24376:
				case 24377:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AI_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AI_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AI_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AI_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Field of Silence
				case 24517:
				case 24520:
				case 24521:
				case 24522:
				case 24523:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FS_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FS_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Forest of Mirrors
				case 24461:
				case 24462:
				case 24463:
				case 24464:
				case 24465:
				case 24466:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FM_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FM_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FM_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FM_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FM_3", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FM_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FM_4", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FM_4", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Varka Silenos Barracks
				case 24636:
				case 24637:
				case 24638:
				case 24639:
				case 24640:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Ketra Orc Outpost
				case 24631:
				case 24632:
				case 24633:
				case 24634:
				case 24635:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_KO_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_KO_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_KO_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_KO_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Field of Whispers
				case 24304:
				case 24305:
				case 24306:
				case 24307:
				case 24308:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FW_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FW_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FW_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FW_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Isle of Prayer
				case 24445:
				case 24446:
				case 24447:
				case 24448:
				case 24449:
				case 24450:
				case 24451:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_IP_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_IP_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_IP_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_IP_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Breka's Stronghold
				case 24415:
				case 24416:
				case 24417:
				case 24418:
				case 24419:
				case 24420:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_BS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_BS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_BS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_BS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Sel Mahum Training Grounds
				case 24492:
				case 24493:
				case 24494:
				case 24495:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SM_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SM_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SM_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SM_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Plains of Lizardman
				case 24496:
				case 24497:
				case 24498:
				case 24499:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_PL_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_PL_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_PL_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_PL_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Fields of Massacre
				case 24486:
				case 24487:
				case 24488:
				case 24489:
				case 24490:
				case 24491:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FOM_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FOM_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FOM_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FOM_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Sea Of Spores
				case 24621:
				case 24622:
				case 24623:
				case 24624:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_SS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_SS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Dragon Valley
				case 24481:
				case 24482:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_DV_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_DV_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_DV_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_DV_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Fafurion Temple
				case 24318:
				case 24322:
				case 24323:
				case 24325:
				case 24329:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FT_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FT_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_FT_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FT_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Wastelands
				case 24500:
				case 24501:
				case 24502:
				case 24503:
				case 24504:
				case 24505:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_WS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_WS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_WS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_WS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_WS_3", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_WS_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Beast Farm
				case 24651:
				case 24652:
				case 24653:
				case 24654:
				case 24655:
				case 24656:
				case 24657:
				case 24658:
				case 24659:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_BF_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_BF_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_BF_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_BF_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_BF_3", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_BF_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Valley of Saints
				case 24876:
				case 24877:
				case 24878:
				case 24879:
				case 24880:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VoS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VoS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VoS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VoS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VoS_3", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VoS_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_VoS_4", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_VoS_4", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Hot Spirits
				case 24881:
				case 24882:
				case 24883:
				case 24884:
				case 24885:
				case 24886:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_HS_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_HS_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_HS_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_HS_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_HS_3", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_HS_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_HS_4", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_HS_4", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Argos Wall 116 lvl (not 122)
				case 24606:
				case 24607:
				case 24608:
				case 24609:
				case 24610:
				case 24611:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AW_1", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AW_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AW_2", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AW_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AW_3", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AW_3", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_AW_4", 0))
					{
						addSpawn(CHEST_LV120, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_AW_4", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
				// Neutral Zone (108)
				case 24641:
				case 24642:
				case 24643:
				case 24644:
				{
					if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_NZ_1", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_NZ_1", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					else if (currentTime > GlobalVariablesManager.getInstance().getLong("TREASURE_CHEST_RESPAWN_NZ_2", 0))
					{
						addSpawn(CHEST_LV110, npc, true, 0, true);
						GlobalVariablesManager.getInstance().set("TREASURE_CHEST_RESPAWN_FM_2", Long.toString(currentTime + RESPAWN_DELAY + getRandom(RND_OFFSET)));
					}
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	public static void main(String[] args)
	{
		new GreedyTreasureChests();
	}
}
