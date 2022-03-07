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
package instances.DwellingOfSpirits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.enums.SkillFinishType;
import org.l2jmobius.gameserver.enums.TeleportWhereType;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.Party;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.skill.AbnormalVisualEffect;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.zone.ZoneType;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.OnEventTrigger;

import instances.AbstractInstance;

/**
 * @author Serenitty
 */
public class DwellingOfSpirits extends AbstractInstance
{
	// CRITICAL NPCs
	private static final int RUIP = 22273;
	private static final int SEALSTONE = 34178;
	private static final int ANIMAEL = 34176;
	private static final int PORTAL_EVENT_FIRE = 15969;
	private static final int PORTAL_EVENT_WATER = 15970;
	private static final int PORTAL_EVENT_EARTH = 15972;
	private static final int PORTAL_EVENT_WIND = 15971;
	
	// PROCELLA BOSS WIND
	private static final int KING_PROCELLA = 29107;
	private static final int PROCELLA_GUARDIAN_1 = 29112;
	private static final int PROCELLA_STORM = 29115;
	
	private static final SkillHolder HURRICANE_SUMMON = new SkillHolder(50042, 1);
	private static final int HURRICANE_BOLT = 50043;
	private static final SkillHolder HURRICANE_BOLT_LV_1 = new SkillHolder(50043, 1);
	private static final int STORM_MAX_COUNT = 20; //
	
	// PETRAM BOSS EARTH
	private static final int KING_PETRAM = 29108;
	private static final int PETRAM_PIECE = 29116;
	private static final int PETRAM_FRAGMENT = 29117;
	// Skills
	private static final SkillHolder EARTH_ENERGY = new SkillHolder(50066, 1);
	private static final SkillHolder EARTH_FURY = new SkillHolder(50059, 1);
	private static final SkillHolder TEST = new SkillHolder(5712, 1);
	
	// IGNIS BOSS FIRE
	// NPCs
	private static final int KING_IGNIS = 29105;
	// Skills
	private static final SkillHolder FIRE_RAG_2 = new SkillHolder(50050, 2);
	private static final SkillHolder FIRE_RAG_4 = new SkillHolder(50050, 4);
	private static final SkillHolder FIRE_RAG_6 = new SkillHolder(50050, 6);
	private static final SkillHolder FIRE_RAG_8 = new SkillHolder(50050, 8);
	
	// NEBULA BOSS WATER
	// NPCs
	private static final int KING_NEBULA = 29106;
	private static final int WATER_SLIME = 29111;
	// Skills
	private static final int AQUA_RAGE = 50036;
	private static final SkillHolder AQUA_RAGE_1 = new SkillHolder(AQUA_RAGE, 1);
	private static final SkillHolder AQUA_RAGE_2 = new SkillHolder(AQUA_RAGE, 2);
	private static final SkillHolder AQUA_RAGE_3 = new SkillHolder(AQUA_RAGE, 3);
	private static final SkillHolder AQUA_RAGE_4 = new SkillHolder(AQUA_RAGE, 4);
	private static final SkillHolder AQUA_RAGE_5 = new SkillHolder(AQUA_RAGE, 5);
	private static final SkillHolder AQUA_SUMMON = new SkillHolder(50037, 1);
	
	// Entrace Portal Triggers
	private static final int WIND_FIRST_TRIGGER_1 = 16158880;
	private static final int WIND_FIRST_TRIGGER_2 = 16158882;
	private static final int EARTH_FIRST_TRIGGER_1 = 16156660;
	private static final int EARTH_FIRST_TRIGGER_2 = 16156662;
	private static final int FIRE_FIRST_TRIGGER_1 = 16155550;
	private static final int FIRE_FIRST_TRIGGER_2 = 16155552;
	private static final int WATER_FIRST_TRIGGER_1 = 16157770;
	private static final int WATER_FIRST_TRIGGER_2 = 16157772;
	
	// Statue Kill Indicator Triggers
	private static final int KILL_INDICATOR_TRIGGER_1 = 16159990;
	private static final int KILL_INDICATOR_TRIGGER_2 = 16159994;
	private static final int KILL_INDICATOR_TRIGGER_3 = 16159996;
	private static final int KILL_INDICATOR_TRIGGER_4 = 16159998;
	private static final int KILL_INDICATOR_TRIGGER_5 = 16159992;
	
	private static final int TEMPLATE_ID = 214;
	
	private static final Map<Integer, NpcStringId> PORTAL_MSG = new HashMap<>();
	static
	{
		PORTAL_MSG.put(1, NpcStringId.DIMENSIONAL_DOOR_TO_THE_SPIRIT_OF_EARTH_IS_OPEN);
		PORTAL_MSG.put(2, NpcStringId.DIMENSIONAL_DOOR_TO_THE_SPIRIT_OF_WATER_IS_OPEN);
		PORTAL_MSG.put(3, NpcStringId.DIMENSIONAL_DOOR_TO_THE_SPIRIT_OF_WIND_IS_OPEN);
		PORTAL_MSG.put(4, NpcStringId.DIMENSIONAL_DOOR_TO_THE_SPIRIT_OF_FIRE_IS_OPEN);
		PORTAL_MSG.put(5, NpcStringId.SEAL_STONE_DISAPPEARS_AFTER_RESONATING_WITH_THE_STATUE);
	}
	
	public DwellingOfSpirits()
	{
		super(TEMPLATE_ID);
		addKillId(KING_PETRAM, KING_NEBULA, KING_PROCELLA, KING_IGNIS);
		addKillId(PROCELLA_GUARDIAN_1);
		addKillId(PETRAM_PIECE, PETRAM_FRAGMENT);
		addKillId(WATER_SLIME);
		addAttackId(KING_PETRAM, KING_NEBULA, KING_PROCELLA, KING_IGNIS);
		addSpawnId(KING_NEBULA);
		addFirstTalkId(ANIMAEL, SEALSTONE);
		addTalkId(ANIMAEL);
		addStartNpc(SEALSTONE);
		addCreatureSeeId(RUIP, ANIMAEL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "ENTER":
			{
				if (player.isInParty())
				{
					final Party party = player.getParty();
					final boolean isInCC = party.isInCommandChannel();
					final List<Player> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
					for (Player member : members)
					{
						if (!member.isInsideRadius3D(npc, 1000))
						{
							player.sendMessage("Player " + member.getName() + " must go closer to Jay.");
						}
						enterInstance(member, npc, TEMPLATE_ID);
					}
				}
				else if (player.isGM())
				{
					enterInstance(player, npc, TEMPLATE_ID);
					player.sendMessage("SYS: You have entered as GM/Admin to Dwelling.");
				}
				else
				{
					if (!player.isInsideRadius3D(npc, 1000))
					{
						player.sendMessage("You must go closer to Jay.");
					}
					enterInstance(player, npc, TEMPLATE_ID);
				}
				break;
			}
			
			case "TELEPORT":
			{
				player.teleToLocation(TeleportWhereType.TOWN, null);
				return null;
			}
			//////////////// BOSS PROCELLA WIND ACTION /////////////
			case "SPAWN_MINION_PROCELLA":
			{
				final Instance world = npc.getInstanceWorld();
				if ((world != null) && (npc.getId() == KING_PROCELLA))
				{
					world.setParameter("minion1", addSpawn(PROCELLA_GUARDIAN_1, 212663, 179421, -15486, 31011, true, 0, true, npc.getInstanceId()));
					world.setParameter("minion2", addSpawn(PROCELLA_GUARDIAN_1, 213258, 179822, -15486, 12001, true, 0, true, npc.getInstanceId()));
					world.setParameter("minion3", addSpawn(PROCELLA_GUARDIAN_1, 212558, 179974, -15486, 12311, true, 0, true, npc.getInstanceId()));
					startQuestTimer("HIDE_PROCELLA", 1000, world.getNpc(KING_PROCELLA), player);
				}
				break;
			}
			case "SPAWN_STORM_PROCELLA":
			{
				final Instance world = npc.getInstanceWorld();
				if ((world != null) && (world.getParameters().getInt("stormCount", 0) < STORM_MAX_COUNT))
				{
					world.getNpc(KING_PROCELLA).doCast(HURRICANE_SUMMON.getSkill());
					final Npc procellaStorm = addSpawn(PROCELLA_STORM, world.getNpc(KING_PROCELLA).getX() + getRandom(-500, 500), world.getNpc(KING_PROCELLA).getY() + getRandom(-500, 500), world.getNpc(KING_PROCELLA).getZ(), 31011, true, 0, true, npc.getInstanceId());
					procellaStorm.setRandomWalking(true);
					world.getParameters().increaseInt("stormCount", 1);
					startQuestTimer("SPAWN_STORM_PROCELLA", 30000, world.getNpc(KING_PROCELLA), player);
					startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC_PROCELLA", 100, procellaStorm, player);
				}
				break;
			}
			case "HIDE_PROCELLA":
			{
				final Instance world = npc.getInstanceWorld();
				if (world.getAliveNpcCount(PROCELLA_GUARDIAN_1) >= 1)
				{
					world.getNpc(KING_PROCELLA).setInvisible(true);
				}
				else if (world.getAliveNpcCount(PROCELLA_GUARDIAN_1) == 0)
				{
					world.getNpc(KING_PROCELLA).setInvisible(false);
					startQuestTimer("SPAWN_MINION_PROCELLA", 90000 + getRandom(-15000, 11000), world.getNpc(KING_PROCELLA), player);
				}
				break;
			}
			case "CHECK_CHAR_INSIDE_RADIUS_NPC_PROCELLA":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					final Player plr = world.getPlayers().stream().findAny().orElse(null);
					if ((plr != null) && (plr.isInsideRadius3D(npc, 200)))
					{
						npc.abortAttack();
						npc.abortCast();
						npc.setTarget(plr);
						if (plr.getAffectedSkillLevel(HURRICANE_BOLT) == 1)
						{
							npc.abortCast();
							startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC_PROCELLA", 100, npc, player);
						}
						else
						{
							if (SkillCaster.checkUseConditions(npc, HURRICANE_BOLT_LV_1.getSkill()))
							{
								npc.doCast(HURRICANE_BOLT_LV_1.getSkill());
							}
						}
						startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC_PROCELLA", 100, npc, player);
					}
					else
					{
						startQuestTimer("CHECK_CHAR_INSIDE_RADIUS_NPC_PROCELLA", 100, npc, player);
					}
				}
				break;
			}
			//////////////// BOSS PETRAM EARTH ACTION /////////////
			case "SPAWN_MINION":
			{
				final Instance world = npc.getInstanceWorld();
				if (world.getAliveNpcCount(PETRAM_FRAGMENT) == 0)
				{
					npc.doCast(EARTH_ENERGY.getSkill());
					
					if (!world.getParameters().getBoolean("spawnedMinions", false))
					{
						world.getParameters().set("spawnedMinions", true);
						
						final int stage = world.getParameters().getInt("stage", 0);
						world.getParameters().set("stage", stage + 1);
						
						world.setParameter("minionpetram1", addSpawn(npc, PETRAM_FRAGMENT, 221543, 191530, -15486, 1131, false, -1, true, npc.getInstanceId()));
						world.setParameter("minionpetram2", addSpawn(npc, PETRAM_FRAGMENT, 222069, 192019, -15486, 49364, false, -1, true, npc.getInstanceId()));
						world.setParameter("minionpetram3", addSpawn(npc, PETRAM_FRAGMENT, 222595, 191479, -15486, 34013, false, -1, true, npc.getInstanceId()));
						world.setParameter("minionpetram4", addSpawn(npc, PETRAM_FRAGMENT, 222077, 191017, -15486, 16383, false, -1, true, npc.getInstanceId()));
						
						npc.setInvul(true);
						npc.broadcastSay(ChatType.NPC_SHOUT, "HaHahaha, fighters lets kill them. Now Im invul!!!");
					}
					
					startQuestTimer("SUPPORT_PETRAM", 3000, npc, player);
				}
				break;
			}
			case "UNSPAWN_MINION":
			{
				final Instance world = npc.getInstanceWorld();
				if (world.getAliveNpcCount(PETRAM_FRAGMENT) == 0)
				{
					world.getParameters().set("spawnedMinions", false);
					
					npc.setInvul(false);
					npc.broadcastSay(ChatType.NPC_SHOUT, "Nooooo... Nooooo...");
				}
				break;
			}
			case "SUPPORT_PETRAM":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					final Npc m1 = world.getParameters().getObject("minionpetram1", Npc.class);
					final Npc m2 = world.getParameters().getObject("minionpetram2", Npc.class);
					final Npc m3 = world.getParameters().getObject("minionpetram3", Npc.class);
					final Npc m4 = world.getParameters().getObject("minionpetram4", Npc.class);
					if (!m1.isDead())
					{
						m1.setTarget(world.getNpc(KING_PETRAM));
						m1.doCast(TEST.getSkill());
					}
					if (!m2.isDead())
					{
						m2.setTarget(world.getNpc(KING_PETRAM));
						m2.doCast(TEST.getSkill());
					}
					if (!m3.isDead())
					{
						m3.setTarget(world.getNpc(KING_PETRAM));
						m3.doCast(TEST.getSkill());
					}
					if (!m4.isDead())
					{
						m4.setTarget(world.getNpc(KING_PETRAM));
						m4.doCast(TEST.getSkill());
					}
					
					startQuestTimer("SUPPORT_PETRAM", 10100, npc, player);
				}
				break;
			}
			case "EARTH_FURY":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					npc.doCast(EARTH_FURY.getSkill());
				}
				break;
			}
			//////////////// BOSS IGNIS ACTION /////////////
			case "CAST_FIRE_RAGE_1":
			{
				if (SkillCaster.checkUseConditions(npc, FIRE_RAG_2.getSkill()))
				{
					npc.doCast(FIRE_RAG_2.getSkill());
				}
				break;
			}
			case "CAST_FIRE_RAGE_2":
			{
				if (SkillCaster.checkUseConditions(npc, FIRE_RAG_4.getSkill()))
				{
					npc.doCast(FIRE_RAG_4.getSkill());
				}
				break;
			}
			case "CAST_FIRE_RAGE_3":
			{
				if (SkillCaster.checkUseConditions(npc, FIRE_RAG_6.getSkill()))
				{
					npc.doCast(FIRE_RAG_6.getSkill());
				}
				break;
			}
			case "CAST_FIRE_RAGE_4":
			{
				if (SkillCaster.checkUseConditions(npc, FIRE_RAG_8.getSkill()))
				{
					npc.doCast(FIRE_RAG_8.getSkill());
				}
				break;
			}
			//////////////// BOSS NEBULA ACTION /////////////
			case "SPAWN_WATER_SLIME":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					final Player plr = world.getPlayers().stream().findAny().get();
					startQuestTimer("CAST_AQUA_RAGE", 30000 + getRandom(-15000, 15000), npc, plr);
					if (npc.getId() == KING_NEBULA)
					{
						npc.doCast(AQUA_SUMMON.getSkill());
						for (int i = 0; i < getRandom(4, 6); i++)
						{
							addSpawn(npc, WATER_SLIME, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), true, -1, true, npc.getInstanceId());
							startQuestTimer("SPAWN_WATER_SLIME", 80000, npc, null);
						}
					}
				}
				break;
			}
			case "PLAYER_PARA":
			{
				if (player.getAffectedSkillLevel(AQUA_RAGE) == 5)
				{
					player.getEffectList().startAbnormalVisualEffect(AbnormalVisualEffect.FROZEN_PILLAR);
					player.setImmobilized(true);
					startQuestTimer("PLAYER_UNPARA", 5000, npc, player);
				}
				break;
			}
			case "PLAYER_UNPARA":
			{
				player.getEffectList().stopSkillEffects(SkillFinishType.REMOVED, AQUA_RAGE_5.getSkill());
				player.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.FROZEN_PILLAR);
				player.setImmobilized(false);
				break;
			}
			case "CAST_AQUA_RAGE":
			{
				startQuestTimer("CAST_AQUA_RAGE", 5000, npc, player);
				if ((player.isInsideRadius3D(npc, 1000)))
				{
					if (player.getAffectedSkillLevel(AQUA_RAGE) == 1)
					{
						if (SkillCaster.checkUseConditions(npc, AQUA_RAGE_2.getSkill()))
						{
							npc.doCast(AQUA_RAGE_2.getSkill());
						}
					}
					else if (player.getAffectedSkillLevel(AQUA_RAGE) == 2)
					{
						if (SkillCaster.checkUseConditions(npc, AQUA_RAGE_3.getSkill()))
						{
							npc.doCast(AQUA_RAGE_3.getSkill());
						}
					}
					else if (player.getAffectedSkillLevel(AQUA_RAGE) == 3)
					{
						if (SkillCaster.checkUseConditions(npc, AQUA_RAGE_4.getSkill()))
						{
							npc.doCast(AQUA_RAGE_4.getSkill());
						}
					}
					else if (player.getAffectedSkillLevel(AQUA_RAGE) == 4)
					{
						if (SkillCaster.checkUseConditions(npc, AQUA_RAGE_5.getSkill()))
						{
							npc.doCast(AQUA_RAGE_5.getSkill());
							startQuestTimer("PLAYER_PARA", 100, npc, player);
						}
					}
					else if (player.getAffectedSkillLevel(AQUA_RAGE) == 5)
					{
						npc.abortCast();
					}
					else
					{
						if (SkillCaster.checkUseConditions(npc, AQUA_RAGE_1.getSkill()))
						{
							npc.doCast(AQUA_RAGE_1.getSkill());
						}
					}
				}
				break;
			}
			case "check_status":
			{
				final Instance world = player.getInstanceWorld();
				if (!isInInstance(world))
				{
					return null;
				}
				
				switch (world.getStatus())
				{
					case 0:
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							world.setStatus(1);
							world.despawnGroup("sealstone");
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 1:
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							world.setStatus(2);
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_1, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 2:
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							world.setStatus(3);
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_2, true));
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_3, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 3:
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							world.setStatus(4);
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_4, true));
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_5, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 4:
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							world.setStatus(5);
							player.sendPacket(new ExShowScreenMessage(2, -1, 2, 0, 0, 0, 0, true, 8000, false, null, PORTAL_MSG.get(3), null));
							addSpawn(KING_PROCELLA, 212862, 179828, -15489, 48103, false, 0, false, world.getId());
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
							
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 5: // Dummy stage pause, u need defeat boss to continue.
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 6:
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							world.setStatus(7);
							world.spawnGroup("ruipwave_1");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_1, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 7:
					{
						if (world.getAliveNpcCount(RUIP) <= 3)
						{
							world.setStatus(8);
							world.spawnGroup("NormalMobs");
							world.spawnGroup("ruipwave_1");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_2, true));
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_3, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 8:
					{
						if (world.getAliveNpcCount(RUIP) <= 3)
						{
							world.setStatus(9);
							world.spawnGroup("NormalMobs");
							world.spawnGroup("ruipwave_2");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_4, true));
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_5, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 9:
					{
						if (world.getAliveNpcCount(RUIP) <= 3)
						{
							world.setStatus(10);
							player.sendPacket(new ExShowScreenMessage(2, -1, 2, 0, 0, 0, 0, true, 8000, false, null, PORTAL_MSG.get(1), null));
							addSpawn(KING_PETRAM, 222063, 191514, -15486, 50142, false, 0, false, world.getId());
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 10: // Dummy stage pause, u need defeat boss to continue.
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 11:
					{
						if (world.getAliveNpcCount(RUIP) <= 1)
						{
							world.setStatus(12);
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_1, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 12:
					{
						if (world.getAliveNpcCount(RUIP) <= 1)
						{
							world.setStatus(13);
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_2, true));
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_3, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 13:
					{
						if (world.getAliveNpcCount(RUIP) <= 1)
						{
							world.setStatus(14);
							world.spawnGroup("ruipwave_2");
							world.spawnGroup("NormalMobs");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_4, true));
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_5, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 14:
					{
						if (world.getAliveNpcCount(RUIP) <= 1)
						{
							world.setStatus(15);
							player.sendPacket(new ExShowScreenMessage(2, -1, 2, 0, 0, 0, 0, true, 8000, false, null, PORTAL_MSG.get(4), null));
							addSpawn(KING_IGNIS, 202350, 169121, -15484, 48103, false, 0, false, world.getId());
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 15: // Dummy stage pause, u need defeat boss for continue.
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 16:
					{
						if (world.getAliveNpcCount(RUIP) <= 3)
						{
							world.setStatus(17);
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_1, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 17:
					{
						if (world.getAliveNpcCount(RUIP) <= 3)
						{
							world.setStatus(18);
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_2, true));
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_3, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 18:
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							world.setStatus(19);
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_4, true));
							player.broadcastPacket(new OnEventTrigger(KILL_INDICATOR_TRIGGER_5, true));
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 19: // need defeat boss for finish.
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						
						{
							world.setStatus(20);
							world.spawnGroup("ruipwave_1");
							world.spawnGroup("NormalMobs");
							player.sendPacket(new ExShowScreenMessage(2, -1, 2, 0, 0, 0, 0, true, 8000, false, null, PORTAL_MSG.get(2), null));
							addSpawn(KING_NEBULA, 222127, 169057, -15486, 48730, false, 0, false, world.getId());
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
					case 20:
					{
						if (world.getAliveNpcCount(RUIP) == 0)
						{
							world.getParameters().set("DwellingOfSpiritsFinished", true);
							world.setStatus(21);
						}
						startQuestTimer("check_status", 1000, null, player);
						break;
					}
				}
				
				return null;
			}
		}
		
		return super.onAdvEvent(event, npc, player);
		
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		startQuestTimer("SPAWN_WATER_SLIME", 40000, npc, null);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAttack(Npc npc, Player player, int damage, boolean isSummon, Skill skill)
	{
		final Instance world = npc.getInstanceWorld();
		if (world == null)
		{
			return null;
		}
		
		switch (npc.getId())
		{
			case KING_PROCELLA:
			{
				if (npc.getCurrentHp() < (npc.getMaxHp() * 0.90))
				{
					startQuestTimer("SPAWN_MINION_PROCELLA", 80000 + getRandom(-15000, 15000), npc, player);
					startQuestTimer("SPAWN_STORM_PROCELLA", 4000, npc, player);
					world.setParameter("stormCount", 0);
				}
				break;
			}
			case KING_PETRAM:
			{
				if (npc.getCurrentHp() < (npc.getMaxHp() * 0.80))
				{
					startQuestTimer("EARTH_FURY", 1000, npc, player);
					if ((world.getParameters().getInt("stage", 0) == 0) || (npc.getCurrentHp() < (npc.getMaxHp() * 0.50)))
					{
						startQuestTimer("SPAWN_MINION", 1000, npc, player);
					}
				}
				break;
			}
			case KING_IGNIS:
			{
				if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.90)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.70)))
				{
					startQuestTimer("CAST_FIRE_RAGE_1", 1000, npc, null);
				}
				else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.70)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.50)))
				{
					startQuestTimer("CAST_FIRE_RAGE_2", 1000, npc, null);
				}
				else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.50)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.40)))
				{
					startQuestTimer("CAST_FIRE_RAGE_3", 1000, npc, null);
				}
				else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.40)) && (npc.getCurrentHp() > (npc.getMaxHp() * 0.25)))
				{
					startQuestTimer("CAST_FIRE_RAGE_4", 1000, npc, null);
				}
				break;
			}
		}
		
		return null;
	}
	
	@Override
	public String onCreatureSee(Npc npc, Creature creature)
	{
		if (creature.isPlayer())
		{
			final Instance world = creature.getInstanceWorld();
			if (world.getStatus() >= 5)
			{
				enableZoneWind();
				creature.getActingPlayer().sendPacket(new OnEventTrigger(WIND_FIRST_TRIGGER_1, true));
				creature.getActingPlayer().sendPacket(new OnEventTrigger(WIND_FIRST_TRIGGER_2, true));
			}
			else if (world.getStatus() >= 10)
			{
				enableZoneEarth();
				creature.getActingPlayer().sendPacket(new OnEventTrigger(EARTH_FIRST_TRIGGER_1, true));
				creature.getActingPlayer().sendPacket(new OnEventTrigger(EARTH_FIRST_TRIGGER_2, true));
			}
			else if (world.getStatus() >= 15)
			{
				enableZoneFire();
				creature.getActingPlayer().sendPacket(new OnEventTrigger(FIRE_FIRST_TRIGGER_1, true));
				creature.getActingPlayer().sendPacket(new OnEventTrigger(FIRE_FIRST_TRIGGER_2, true));
			}
			else if (world.getStatus() >= 20)
			{
				enableZoneWater();
				creature.getActingPlayer().sendPacket(new OnEventTrigger(WATER_FIRST_TRIGGER_1, true));
				creature.getActingPlayer().sendPacket(new OnEventTrigger(WATER_FIRST_TRIGGER_2, true));
			}
			else if (world.getStatus() >= 21)
			{
				world.despawnGroup("ruipwave_1");
				world.despawnGroup("ruipwave_2");
				world.despawnGroup("NormalMobs");
				world.finishInstance();
			}
		}
		
		return super.onCreatureSee(npc, creature);
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Instance instance = npc.getInstanceWorld();
		
		switch (npc.getId())
		{
			case KING_PETRAM:
			{
				final Npc portal = addSpawn(PORTAL_EVENT_EARTH, 222065, 192767, -15488, 16384, false, 0, false, instance.getId());
				portal.setDisplayEffect(1); // set the portal visible
				player.getVariables().set("DSIGNIS", 1);
				setActive(true);
				enableZoneEarthExit();
				cancelQuestTimer("SPAWN_MINION", npc, player);
				instance.setStatus(12);
				final Instance world = npc.getInstanceWorld();
				for (Npc spawn : world.getNpcs(PETRAM_FRAGMENT))
				{
					spawn.deleteMe();
				}
				break;
			}
			case KING_NEBULA:
			{
				final Npc portal = addSpawn(PORTAL_EVENT_WATER, 222127, 170488, -15488, 16384, false, 0, false, instance.getId());
				portal.setDisplayEffect(1);
				player.getVariables().set("DSIGNIS", 1);
				player.sendPacket(new ExSendUIEvent(player, false, false, 0, 0, NpcStringId.TIME_LEFT));
				setActive(true);
				enableZoneWindExit();
				instance.setStatus(21);
				instance.spawnGroup("animael");
				break;
			}
			case KING_IGNIS:
			{
				final Npc portal = addSpawn(PORTAL_EVENT_FIRE, 202349, 170533, -15488, 16384, false, 0, false, instance.getId());
				portal.setDisplayEffect(1);
				player.getVariables().set("DSIGNIS", 1);
				setActive(true);
				enableZoneFireExit();
				instance.setStatus(17);
				break;
			}
			case KING_PROCELLA:
			{
				final Npc portal = addSpawn(PORTAL_EVENT_WIND, 212863, 181090, -15487, 16384, false, 0, false, instance.getId());
				portal.setDisplayEffect(1);
				player.getVariables().set("DSIGNIS", 1);
				setActive(true);
				enableZoneWaterExit();
				instance.setStatus(7);
				final Instance world = npc.getInstanceWorld();
				for (Npc spawn : world.getNpcs(PROCELLA_GUARDIAN_1))
				{
					spawn.deleteMe();
				}
				break;
			}
			case PETRAM_FRAGMENT:
			{
				startQuestTimer("UNSPAWN_MINION", 1000, instance.getNpc(KING_PETRAM), player);
				break;
			}
			case PROCELLA_GUARDIAN_1:
			{
				
				startQuestTimer("HIDE_PROCELLA", 1000, instance.getNpc(KING_PROCELLA), player);
				break;
			}
			case WATER_SLIME:
			{
				if (getRandomBoolean())
				{
					switch (player.getAffectedSkillLevel(AQUA_RAGE))
					{
						case 1:
						{
							player.stopSkillEffects(AQUA_RAGE_1.getSkill());
							break;
						}
						case 2:
						{
							player.stopSkillEffects(AQUA_RAGE_2.getSkill());
							final Skill skill = SkillData.getInstance().getSkill(AQUA_RAGE, 1);
							skill.applyEffects(player, player);
							break;
						}
						case 3:
						{
							player.stopSkillEffects(AQUA_RAGE_3.getSkill());
							final Skill skill = SkillData.getInstance().getSkill(AQUA_RAGE, 2);
							skill.applyEffects(player, player);
							break;
						}
						case 4:
						{
							player.stopSkillEffects(AQUA_RAGE_4.getSkill());
							final Skill skill = SkillData.getInstance().getSkill(AQUA_RAGE, 3);
							skill.applyEffects(player, player);
							break;
						}
					}
				}
				break;
			}
		}
		
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (npc.getId() == ANIMAEL)
		{
			return "34176.htm";
		}
		
		if (!player.getInstanceWorld().getParameters().getBoolean("PlayerEnter", false))
		{
			player.getInstanceWorld().setParameter("PlayerEnter", true);
			player.getInstanceWorld().setDuration(30);
			startEvent(npc, player);
		}
		
		return super.onFirstTalk(npc, player);
	}
	
	// PORTALS
	private void enableZoneWind()
	{
		final ZoneType zone = ZoneManager.getInstance().getZoneByName("dwelling_portal_wind");
		if (zone != null)
		{
			zone.setEnabled(true);
		}
	}
	
	private void enableZoneEarth()
	{
		final ZoneType zone = ZoneManager.getInstance().getZoneByName("dwelling_portal_earth");
		if (zone != null)
		{
			zone.setEnabled(true);
		}
	}
	
	private void enableZoneFire()
	{
		final ZoneType zone = ZoneManager.getInstance().getZoneByName("dwelling_portal_fire");
		if (zone != null)
		{
			zone.setEnabled(true);
		}
	}
	
	private void enableZoneWater()
	{
		final ZoneType zone = ZoneManager.getInstance().getZoneByName("dwelling_portal_water");
		if (zone != null)
		{
			zone.setEnabled(true);
		}
	}
	
	private void enableZoneWindExit()
	{
		final ZoneType zone = ZoneManager.getInstance().getZoneByName("return_portal_procella");
		if (zone != null)
		{
			zone.setEnabled(true);
		}
	}
	
	private void enableZoneEarthExit()
	{
		final ZoneType zone = ZoneManager.getInstance().getZoneByName("return_portal_petram");
		if (zone != null)
		{
			zone.setEnabled(true);
		}
	}
	
	private void enableZoneFireExit()
	{
		final ZoneType zone = ZoneManager.getInstance().getZoneByName("return_portal_ignis");
		if (zone != null)
		{
			zone.setEnabled(true);
		}
	}
	
	private void enableZoneWaterExit()
	{
		final ZoneType zone = ZoneManager.getInstance().getZoneByName("return_portal_nebula");
		if (zone != null)
		{
			zone.setEnabled(true);
		}
	}
	
	private void startEvent(Npc npc, Player player)
	{
		if (!player.getInstanceWorld().getParameters().getBoolean("DwellingOfSpiritsFinished", false))
		{
			player.sendPacket(new ExSendUIEvent(player, false, false, 1800, 2, NpcStringId.TIME_LEFT));
			player.sendPacket(new ExShowScreenMessage(2, -1, 2, 0, 0, 0, 0, true, 8000, false, null, PORTAL_MSG.get(5), null));
			startQuestTimer("check_status", 1000, null, player);
		}
	}
	
	public static void main(String[] args)
	{
		new DwellingOfSpirits();
	}
}
