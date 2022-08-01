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
package ai.bosses.Antharas;

import java.util.HashMap;
import java.util.Map;

import org.l2jmobius.Config;
import org.l2jmobius.commons.util.CommonUtil;
import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.enums.MountType;
import org.l2jmobius.gameserver.instancemanager.ZoneManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Attackable;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.model.zone.type.NoRestartZone;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2jmobius.gameserver.network.serverpackets.NpcInfo;
import org.l2jmobius.gameserver.network.serverpackets.PlaySound;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;
import org.l2jmobius.gameserver.network.serverpackets.SpecialCamera;

import instances.AbstractInstance;

/**
 * Antharas
 * @author Sero
 */
public class Antharas extends AbstractInstance
{
	// NPCs
	private static final int ANTHARAS = 29223; // Antharas
	private static final int CUBE = 31859; // Teleportation Cubic
	private static final int GUIDE = 34543;
	private static final Map<Integer, Location> INVISIBLE_NPC = new HashMap<>();
	static
	{
		INVISIBLE_NPC.put(29077, new Location(177229, 113298, -7735)); // antaras_clear_npc_1
		INVISIBLE_NPC.put(29078, new Location(176707, 113585, -7735)); // antaras_clear_npc_2
		INVISIBLE_NPC.put(29079, new Location(176385, 113889, -7735)); // antaras_clear_npc_3
		INVISIBLE_NPC.put(29080, new Location(176082, 114241, -7735)); // antaras_clear_npc_4
		INVISIBLE_NPC.put(29081, new Location(176066, 114802, -7735)); // antaras_clear_npc_5
		INVISIBLE_NPC.put(29082, new Location(176095, 115313, -7735)); // antaras_clear_npc_6
		INVISIBLE_NPC.put(29083, new Location(176425, 115829, -7735)); // antaras_clear_npc_7
		INVISIBLE_NPC.put(29084, new Location(176949, 116378, -7735)); // antaras_clear_npc_8
		INVISIBLE_NPC.put(29085, new Location(177655, 116402, -7735)); // antaras_clear_npc_9
		INVISIBLE_NPC.put(29086, new Location(178248, 116395, -7735)); // antaras_clear_npc_10
		INVISIBLE_NPC.put(29087, new Location(178706, 115998, -7735)); // antaras_clear_npc_11
		INVISIBLE_NPC.put(29088, new Location(179208, 115452, -7735)); // antaras_clear_npc_12
		INVISIBLE_NPC.put(29089, new Location(179191, 115079, -7735)); // antaras_clear_npc_13
		INVISIBLE_NPC.put(29090, new Location(179221, 114546, -7735)); // antaras_clear_npc_14
		INVISIBLE_NPC.put(29091, new Location(178916, 113925, -7735)); // antaras_clear_npc_15
		INVISIBLE_NPC.put(29092, new Location(178782, 113814, -7735)); // antaras_clear_npc_16
		INVISIBLE_NPC.put(29093, new Location(178419, 113417, -7735)); // antaras_clear_npc_17
		INVISIBLE_NPC.put(29094, new Location(177855, 113282, -7735)); // antaras_clear_npc_18
	}
	// Skill
	private static final SkillHolder ANTH_JUMP = new SkillHolder(4106, 1); // Antharas Stun
	private static final SkillHolder ANTH_TAIL = new SkillHolder(4107, 1); // Antharas Stun
	private static final SkillHolder ANTH_FEAR = new SkillHolder(4108, 1); // Antharas Terror
	private static final SkillHolder ANTH_DEBUFF = new SkillHolder(4109, 1); // Curse of Antharas
	private static final SkillHolder ANTH_MOUTH = new SkillHolder(4110, 2); // Breath Attack
	private static final SkillHolder ANTH_BREATH = new SkillHolder(4111, 1); // Antharas Fossilization
	private static final SkillHolder ANTH_NORM_ATTACK = new SkillHolder(4112, 1); // Ordinary Attack
	private static final SkillHolder ANTH_NORM_ATTACK_EX = new SkillHolder(4113, 1); // Animal doing ordinary attack
	private static final SkillHolder ANTH_REGEN_1 = new SkillHolder(4125, 1); // Antharas Regeneration
	private static final SkillHolder ANTH_REGEN_2 = new SkillHolder(4239, 1); // Antharas Regeneration
	private static final SkillHolder ANTH_REGEN_3 = new SkillHolder(4240, 1); // Antharas Regeneration
	private static final SkillHolder ANTH_REGEN_4 = new SkillHolder(4241, 1); // Antharas Regeneration
	private static final SkillHolder DISPEL_BOM = new SkillHolder(5042, 1); // NPC Dispel Bomb
	private static final SkillHolder ANTH_ANTI_STRIDER = new SkillHolder(4258, 1); // Hinder Strider
	private static final SkillHolder ANTH_FEAR_SHORT = new SkillHolder(5092, 1); // Antharas Terror
	private static final SkillHolder ANTH_METEOR = new SkillHolder(5093, 1); // Antharas Meteor
	// Zone
	private static final NoRestartZone ZONE = ZoneManager.getInstance().getZoneById(70050, NoRestartZone.class); // Antharas Nest zone
	// @formatter:on
	// Misc
	private static final int TEMPLATE_ID = 304;
	
	public Antharas()
	{
		super(TEMPLATE_ID);
		addTalkId(GUIDE, CUBE);
		addSpawnId(INVISIBLE_NPC.keySet());
		addSpawnId(ANTHARAS);
		addSpellFinishedId(ANTHARAS);
		addAttackId(ANTHARAS);
		addKillId(ANTHARAS);
		
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		switch (event)
		{
			case "SPAWN_ANTHARAS":
			{
				final Instance world = player.getInstanceWorld();
				final Npc antharas = addSpawn(ANTHARAS, 185708, 114298, -8221, 0, false, 0, false, world.getId());
				antharas.disableCoreAI(true);
				antharas.setRandomWalking(false);
				antharas.teleToLocation(181323, 114850, -7623, 32542);
				broadcastPacket(world, null, new PlaySound("BS02_A"));
				startQuestTimer("CAMERA_1", 23, null, player, false);
				broadcastPacket(world, new NpcInfo(antharas), null);
				world.setParameter("antharas", antharas);
				break;
			}
			case "CAMERA_1":
			{
				final Instance world = player.getInstanceWorld();
				final Npc antharas = world.getParameters().getObject("antharas", Npc.class);
				broadcastPacket(world, new SpecialCamera(antharas, 700, 13, -19, 0, 10000, 20000, 0, 0, 0, 0, 0), null);
				startQuestTimer("CAMERA_2", 3000, npc, player, false);
				break;
			}
			case "CAMERA_2":
			{
				final Instance world = player.getInstanceWorld();
				final Npc antharas = world.getParameters().getObject("antharas", Npc.class);
				broadcastPacket(world, new SpecialCamera(antharas, 700, 13, 0, 6000, 10000, 20000, 0, 0, 0, 0, 0), null);
				startQuestTimer("CAMERA_3", 10000, npc, player, false);
				break;
			}
			case "CAMERA_3":
			{
				final Instance world = player.getInstanceWorld();
				final Npc antharas = world.getParameters().getObject("antharas", Npc.class);
				broadcastPacket(world, new SpecialCamera(antharas, 3700, 0, -3, 0, 10000, 10000, 0, 0, 0, 0, 0), null);
				broadcastPacket(world, new SocialAction(antharas.getObjectId(), 1), null);
				startQuestTimer("CAMERA_4", 200, npc, player, false);
				startQuestTimer("SOCIAL", 5200, npc, player, false);
				break;
			}
			case "CAMERA_4":
			{
				final Instance world = player.getInstanceWorld();
				final Npc antharas = world.getParameters().getObject("antharas", Npc.class);
				broadcastPacket(world, new SpecialCamera(antharas, 1100, 0, -3, 22000, 10000, 30000, 0, 0, 0, 0, 0), null);
				startQuestTimer("CAMERA_5", 10800, npc, player, false);
				break;
			}
			case "CAMERA_5":
			{
				final Instance world = player.getInstanceWorld();
				final Npc antharas = world.getParameters().getObject("antharas", Npc.class);
				broadcastPacket(world, new SpecialCamera(antharas, 1100, 0, -3, 300, 10000, 7000, 0, 0, 0, 0, 0), null);
				startQuestTimer("START_MOVE", 1900, npc, player, false);
				break;
			}
			case "SOCIAL":
			{
				final Instance world = player.getInstanceWorld();
				final Npc antharas = world.getParameters().getObject("antharas", Npc.class);
				broadcastPacket(world, new SocialAction(antharas.getObjectId(), 2), null);
				break;
			}
			case "START_MOVE":
			{
				final Instance world = player.getInstanceWorld();
				final Npc antharas = world.getParameters().getObject("antharas", Npc.class);
				antharas.disableCoreAI(false);
				antharas.setRandomWalking(true);
				
				for (Player players : World.getInstance().getVisibleObjectsInRange(npc, Player.class, 4000))
				{
					if (players.isHero())
					{
						broadcastPacket(world, new ExShowScreenMessage(NpcStringId.S1_YOU_CANNOT_HOPE_TO_DEFEAT_ME_WITH_YOUR_MEAGER_STRENGTH, 2, 4000, players.getName()), null);
						break;
					}
				}
				
				antharas.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(179011, 114871, -7704));
				startQuestTimer("CHECK_ATTACK", 60000, antharas, player, false);
				break;
			}
			case "SET_REGEN":
			{
				if (npc != null)
				{
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.25))
					{
						if (!npc.isAffectedBySkill(ANTH_REGEN_4.getSkillId()))
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, ANTH_REGEN_4.getSkill(), npc);
						}
					}
					else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.5))
					{
						if (!npc.isAffectedBySkill(ANTH_REGEN_3.getSkillId()))
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, ANTH_REGEN_3.getSkill(), npc);
						}
					}
					else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.75))
					{
						if (!npc.isAffectedBySkill(ANTH_REGEN_2.getSkillId()))
						{
							npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, ANTH_REGEN_2.getSkill(), npc);
						}
					}
					else if (!npc.isAffectedBySkill(ANTH_REGEN_1.getSkillId()))
					{
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, ANTH_REGEN_1.getSkill(), npc);
					}
					startQuestTimer("SET_REGEN", 60000, npc, player, false);
				}
				break;
			}
			case "CHECK_ATTACK":
			{
				if (npc != null)
				{
					final int attacker1hate = npc.getInstanceWorld().getParameters().getInt("attacker1hate", 0);
					if (attacker1hate > 10)
					{
						npc.getInstanceWorld().getParameters().set("attacker1hate", attacker1hate - getRandom(10));
					}
					final int attacker2hate = npc.getInstanceWorld().getParameters().getInt("attacker2hate", 0);
					if (attacker2hate > 10)
					{
						npc.getInstanceWorld().getParameters().set("attacker2hate", attacker2hate - getRandom(10));
					}
					final int attacker3hate = npc.getInstanceWorld().getParameters().getInt("attacker3hate", 0);
					if (attacker3hate > 10)
					{
						npc.getInstanceWorld().getParameters().set("attacker3hate", attacker3hate - getRandom(10));
					}
					manageSkills(npc);
					startQuestTimer("CHECK_ATTACK", 60000, npc, player, false);
				}
				break;
			}
			
			case "TID_USED_FEAR":
			{
				if (npc != null)
				{
					final int sandStorm = npc.getInstanceWorld().getParameters().getInt("sandStorm", 0);
					if (sandStorm == 0)
					{
						npc.getInstanceWorld().getParameters().set("sandStorm", 1);
						npc.disableCoreAI(true);
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(177648, 114816, -7735));
						startQuestTimer("TID_FEAR_MOVE_TIMEOVER", 2000, npc, null);
						startQuestTimer("TID_FEAR_COOLTIME", 300000, npc, null);
					}
				}
				break;
			}
			case "TID_FEAR_COOLTIME":
			{
				npc.getInstanceWorld().getParameters().set("sandStorm", 0);
				break;
			}
			case "TID_FEAR_MOVE_TIMEOVER":
			{
				final int sandStorm = npc.getInstanceWorld().getParameters().getInt("sandStorm", 0);
				if ((sandStorm == 1) && (npc.getX() == 177648) && (npc.getY() == 114816))
				{
					npc.getInstanceWorld().getParameters().set("sandStorm", 2);
					npc.getInstanceWorld().getParameters().set("moveChance", 0);
					npc.disableCoreAI(false);
					INVISIBLE_NPC.entrySet().forEach(entry -> addSpawn(entry.getKey(), entry.getValue()));
				}
				else if (sandStorm == 1)
				{
					final int moveChance = npc.getInstanceWorld().getParameters().getInt("moveChance", 0);
					if (moveChance <= 3)
					{
						npc.getInstanceWorld().getParameters().set("moveChance", moveChance + 1);
						npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(177648, 114816, -7735));
						startQuestTimer("TID_FEAR_MOVE_TIMEOVER", 5000, npc, null);
					}
					else
					{
						npc.teleToLocation(177648, 114816, -7735, npc.getHeading());
						startQuestTimer("TID_FEAR_MOVE_TIMEOVER", 1000, npc, null);
					}
				}
				break;
			}
			case "MANAGE_SKILL":
			{
				manageSkills(npc);
				break;
			}
		}
		return null;
	}
	
	@Override
	public String onAggroRangeEnter(Npc npc, Player player, boolean isSummon)
	{
		npc.doCast(DISPEL_BOM.getSkill());
		npc.doDie(player);
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon, Skill skill)
	{
		if (npc.getId() == ANTHARAS)
		{
			if (!ZONE.isCharacterInZone(attacker))
			{
				LOGGER.warning(getClass().getSimpleName() + ": Player " + attacker.getName() + " attacked Antharas in invalid conditions!");
				attacker.teleToLocation(80464, 152294, -3534);
			}
			
			if ((attacker.getMountType() == MountType.STRIDER) && !attacker.isAffectedBySkill(ANTH_ANTI_STRIDER.getSkillId()) && SkillCaster.checkUseConditions(npc, ANTH_ANTI_STRIDER.getSkill()))
			{
				addSkillCastDesire(npc, attacker, ANTH_ANTI_STRIDER.getSkill(), 100);
			}
			
			if (skill == null)
			{
				refreshAiParams(attacker, damage * 1000);
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.25))
			{
				refreshAiParams(attacker, (damage / 3) * 100);
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.5))
			{
				refreshAiParams(attacker, damage * 20);
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.75))
			{
				refreshAiParams(attacker, damage * 10);
			}
			else
			{
				refreshAiParams(attacker, (damage / 3) * 20);
			}
			manageSkills(npc);
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(Npc npc, Player player, boolean isSummon)
	{
		final Instance world = player.getInstanceWorld();
		final Npc antharas = world.getParameters().getObject("antharas", Npc.class);
		if (ZONE.isCharacterInZone(player) && (npc.getId() == ANTHARAS))
		{
			notifyEvent("DESPAWN_MINIONS", null, null);
			ZONE.broadcastPacket(new SpecialCamera(antharas, 1200, 20, -10, 0, 10000, 13000, 0, 0, 0, 0, 0));
			ZONE.broadcastPacket(new PlaySound("BS01_D"));
			world.finishInstance();
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public void onMoveFinished(Npc npc)
	{
		npc.doCast(DISPEL_BOM.getSkill());
		npc.doDie(null);
	}
	
	@Override
	public String onSpawn(Npc npc)
	{
		if (npc.getId() == ANTHARAS)
		{
			((Attackable) npc).setCanReturnToSpawnPoint(false);
			npc.setRandomWalking(false);
			cancelQuestTimer("SET_REGEN", npc, null);
			startQuestTimer("SET_REGEN", 60000, npc, null);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSpellFinished(Npc npc, Player player, Skill skill)
	{
		if ((skill.getId() == ANTH_FEAR.getSkillId()) || (skill.getId() == ANTH_FEAR_SHORT.getSkillId()))
		{
			startQuestTimer("TID_USED_FEAR", 7000, npc, null);
		}
		startQuestTimer("MANAGE_SKILL", 1000, npc, null);
		return super.onSpellFinished(npc, player, skill);
	}
	
	private final void refreshAiParams(Player attacker, int damage)
	{
		final Instance world = attacker.getInstanceWorld();
		if (world == null)
		{
			return;
		}
		
		final Player attacker1 = world.getParameters().getObject("attacker1", Player.class);
		final Player attacker2 = world.getParameters().getObject("attacker2", Player.class);
		final Player attacker3 = world.getParameters().getObject("attacker3", Player.class);
		final int attacker1hate = world.getParameters().getInt("attacker1hate", 0);
		final int attacker2hate = world.getParameters().getInt("attacker2hate", 0);
		final int attacker3hate = world.getParameters().getInt("attacker3hate", 0);
		
		if ((attacker1 != null) && (attacker == attacker1))
		{
			if (attacker1hate < (damage + 1000))
			{
				world.getParameters().set("attacker1hate", damage + getRandom(3000));
			}
		}
		else if ((attacker2 != null) && (attacker == attacker2))
		{
			if (attacker2hate < (damage + 1000))
			{
				world.getParameters().set("attacker2hate", damage + getRandom(3000));
			}
		}
		else if ((attacker3 != null) && (attacker == attacker3))
		{
			if (attacker3hate < (damage + 1000))
			{
				world.getParameters().set("attacker3hate", damage + getRandom(3000));
			}
		}
		else
		{
			final int i1 = CommonUtil.min(attacker1hate, attacker2hate, attacker3hate);
			if (attacker1hate == i1)
			{
				world.getParameters().set("attacker1hate", damage + getRandom(3000));
				attacker.getInstanceWorld().getParameters().set("attacker1", attacker);
			}
			else if (attacker2hate == i1)
			{
				world.getParameters().set("attacker2hate", damage + getRandom(3000));
				attacker.getInstanceWorld().getParameters().set("attacker2", attacker);
			}
			else if (attacker3hate == i1)
			{
				world.getParameters().set("attacker3hate", damage + getRandom(3000));
				attacker.getInstanceWorld().getParameters().set("attacker3", attacker);
			}
		}
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (npc.getId() == GUIDE)
		{
			enterInstance(player, npc, TEMPLATE_ID);
			Instance world = player.getInstanceWorld();
			if ((world != null))
			{
				startQuestTimer("SPAWN_ANTHARAS", Config.ANTHARAS_WAIT_TIME * 60000, null, player);
			}
		}
		else // Teleport Cube
		{
			Instance world = player.getInstanceWorld();
			if (world != null)
			{
				teleportPlayerOut(player, world);
			}
		}
		return null;
	}
	
	void broadcastPacket(Instance world, IClientOutgoingPacket packet, PlaySound playSound)
	{
		for (Player player : world.getPlayers())
		{
			if ((player != null) && player.isOnline())
			{
				player.sendPacket(packet);
			}
		}
	}
	
	private void manageSkills(Npc npc)
	{
		if (npc.isCastingNow() || npc.isCoreAIDisabled() || !npc.isInCombat())
		{
			return;
		}
		
		final Instance world = npc.getInstanceWorld();
		if (world == null)
		{
			return;
		}
		
		int i1 = 0;
		int i2 = 0;
		Player c2 = null;
		final Player attacker1 = world.getParameters().getObject("attacker1", Player.class);
		int attacker1hate = world.getParameters().getInt("attacker1hate", 0);
		if ((attacker1 == null) || (npc.calculateDistance3D(attacker1) > 9000) || attacker1.isDead())
		{
			attacker1hate = 0;
			world.getParameters().set("attacker1hate", attacker1hate);
		}
		
		final Player attacker2 = world.getParameters().getObject("attacker2", Player.class);
		int attacker2hate = world.getParameters().getInt("attacker2hate", 0);
		if ((attacker2 == null) || (npc.calculateDistance3D(attacker2) > 9000) || attacker2.isDead())
		{
			attacker2hate = 0;
			world.getParameters().set("attacker2hate", attacker2hate);
		}
		
		final Player attacker3 = world.getParameters().getObject("attacker3", Player.class);
		int attacker3hate = world.getParameters().getInt("attacker3hate", 0);
		if ((attacker3 == null) || (npc.calculateDistance3D(attacker3) > 9000) || attacker3.isDead())
		{
			attacker3hate = 0;
			world.getParameters().set("attacker3hate", attacker3hate);
		}
		
		if (attacker1hate > attacker2hate)
		{
			i1 = 2;
			i2 = attacker1hate;
			c2 = attacker1;
		}
		else if (attacker2hate > 0)
		{
			i1 = 3;
			i2 = attacker2hate;
			c2 = attacker2;
		}
		
		if (attacker3hate > i2)
		{
			i1 = 4;
			i2 = attacker3hate;
			c2 = attacker3;
		}
		if (i2 > 0)
		{
			if (getRandom(100) < 70)
			{
				switch (i1)
				{
					case 2:
					{
						attacker1hate = 500;
						world.getParameters().set("attacker1hate", attacker1hate);
						break;
					}
					case 3:
					{
						attacker2hate = 500;
						world.getParameters().set("attacker1hate", attacker2hate);
						break;
					}
					case 4:
					{
						attacker3hate = 500;
						world.getParameters().set("attacker1hate", attacker3hate);
						break;
					}
				}
			}
			
			final double distanceC2 = npc.calculateDistance3D(c2);
			final double directionC2 = npc.calculateDirectionTo(c2);
			SkillHolder skillToCast = null;
			boolean castOnTarget = false;
			if (npc.getCurrentHp() < (npc.getMaxHp() * 0.25))
			{
				if (getRandom(100) < 30)
				{
					castOnTarget = true;
					skillToCast = ANTH_MOUTH;
				}
				else if ((getRandom(100) < 80) && (((distanceC2 < 1423) && (directionC2 < 188) && (directionC2 > 172)) || ((distanceC2 < 802) && (directionC2 < 194) && (directionC2 > 166))))
				{
					skillToCast = ANTH_TAIL;
				}
				else if ((getRandom(100) < 40) && (((distanceC2 < 850) && (directionC2 < 210) && (directionC2 > 150)) || ((distanceC2 < 425) && (directionC2 < 270) && (directionC2 > 90))))
				{
					skillToCast = ANTH_DEBUFF;
				}
				else if ((getRandom(100) < 10) && (distanceC2 < 1100))
				{
					skillToCast = ANTH_JUMP;
				}
				else if (getRandom(100) < 10)
				{
					castOnTarget = true;
					skillToCast = ANTH_METEOR;
				}
				else if (getRandom(100) < 6)
				{
					castOnTarget = true;
					skillToCast = ANTH_BREATH;
				}
				else if (getRandomBoolean())
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK_EX;
				}
				else if (getRandom(100) < 5)
				{
					castOnTarget = true;
					skillToCast = getRandomBoolean() ? ANTH_FEAR : ANTH_FEAR_SHORT;
				}
				else
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK;
				}
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.5))
			{
				if ((getRandom(100) < 80) && (((distanceC2 < 1423) && (directionC2 < 188) && (directionC2 > 172)) || ((distanceC2 < 802) && (directionC2 < 194) && (directionC2 > 166))))
				{
					skillToCast = ANTH_TAIL;
				}
				else if ((getRandom(100) < 40) && (((distanceC2 < 850) && (directionC2 < 210) && (directionC2 > 150)) || ((distanceC2 < 425) && (directionC2 < 270) && (directionC2 > 90))))
				{
					skillToCast = ANTH_DEBUFF;
				}
				else if ((getRandom(100) < 10) && (distanceC2 < 1100))
				{
					skillToCast = ANTH_JUMP;
				}
				else if (getRandom(100) < 7)
				{
					castOnTarget = true;
					skillToCast = ANTH_METEOR;
				}
				else if (getRandom(100) < 6)
				{
					castOnTarget = true;
					skillToCast = ANTH_BREATH;
				}
				else if (getRandomBoolean())
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK_EX;
				}
				else if (getRandom(100) < 5)
				{
					castOnTarget = true;
					skillToCast = getRandomBoolean() ? ANTH_FEAR : ANTH_FEAR_SHORT;
				}
				else
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK;
				}
			}
			else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.75))
			{
				if ((getRandom(100) < 80) && (((distanceC2 < 1423) && (directionC2 < 188) && (directionC2 > 172)) || ((distanceC2 < 802) && (directionC2 < 194) && (directionC2 > 166))))
				{
					skillToCast = ANTH_TAIL;
				}
				else if ((getRandom(100) < 10) && (distanceC2 < 1100))
				{
					skillToCast = ANTH_JUMP;
				}
				else if (getRandom(100) < 5)
				{
					castOnTarget = true;
					skillToCast = ANTH_METEOR;
				}
				else if (getRandom(100) < 6)
				{
					castOnTarget = true;
					skillToCast = ANTH_BREATH;
				}
				else if (getRandomBoolean())
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK_EX;
				}
				else if (getRandom(100) < 5)
				{
					castOnTarget = true;
					skillToCast = getRandomBoolean() ? ANTH_FEAR : ANTH_FEAR_SHORT;
				}
				else
				{
					castOnTarget = true;
					skillToCast = ANTH_NORM_ATTACK;
				}
			}
			else if ((getRandom(100) < 80) && (((distanceC2 < 1423) && (directionC2 < 188) && (directionC2 > 172)) || ((distanceC2 < 802) && (directionC2 < 194) && (directionC2 > 166))))
			{
				skillToCast = ANTH_TAIL;
			}
			else if (getRandom(100) < 3)
			{
				castOnTarget = true;
				skillToCast = ANTH_METEOR;
			}
			else if (getRandom(100) < 6)
			{
				castOnTarget = true;
				skillToCast = ANTH_BREATH;
			}
			else if (getRandomBoolean())
			{
				castOnTarget = true;
				skillToCast = ANTH_NORM_ATTACK_EX;
			}
			else if (getRandom(100) < 5)
			{
				castOnTarget = true;
				skillToCast = getRandomBoolean() ? ANTH_FEAR : ANTH_FEAR_SHORT;
			}
			else
			{
				castOnTarget = true;
				skillToCast = ANTH_NORM_ATTACK;
			}
			
			if ((skillToCast != null) && SkillCaster.checkUseConditions(npc, skillToCast.getSkill()))
			{
				if (castOnTarget)
				{
					addSkillCastDesire(npc, c2, skillToCast.getSkill(), 100);
				}
				else
				{
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skillToCast.getSkill(), npc);
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		new Antharas();
	}
}
