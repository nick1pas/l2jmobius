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
package instances.TrainingZone;

import java.util.Arrays;

import org.l2jmobius.gameserver.data.xml.TimedHuntingZoneData;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.instancemanager.InstanceManager;
import org.l2jmobius.gameserver.model.Location;
import org.l2jmobius.gameserver.model.StatSet;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Creature;
import org.l2jmobius.gameserver.model.actor.Npc;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.holders.TimedHuntingZoneHolder;
import org.l2jmobius.gameserver.model.instancezone.Instance;
import org.l2jmobius.gameserver.model.skill.SkillCaster;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.serverpackets.ExSendUIEvent;
import org.l2jmobius.gameserver.network.serverpackets.huntingzones.TimedHuntingZoneExit;

import instances.AbstractInstance;

/**
 * @author Serenitty
 * @URL https://www.youtube.com/watch?v=SuRXhj79-rI
 */
public class TrainingZone extends AbstractInstance
{
	// NPCs
	private static final int GROWN = 34307;
	private static final int TIND = 34308;
	private static final int TOKA = 34305;
	private static final int ERI = 34306;
	private static final int ARBANA = 34309;
	// BOSSES
	private static final int BOOJUDU = 25952; // Local area boss
	private static final int PETRON = 25953; // Local area boss
	private static final int KERION = 25954; // Local area boss
	private static final int TUKHAH = 25955; // Local area boss
	// TOI
	private static final int CHEL = 25963; // Insolence Boss
	private static final int RILVA = 25961; // Insolence Boss
	private static final int RYUN = 25962; // Insolence Boss
	// Skill
	private static final SkillHolder SAYHA_BUFF = new SkillHolder(48489, 1); //
	// Attack buff
	private static final SkillHolder MENTOR_PATK = new SkillHolder(48490, 1); // P atk +1000 buff
	private static final SkillHolder MENTOR_MATK = new SkillHolder(48491, 1); // M atk +1000 buff
	private static final SkillHolder MENTOR_ATK_SPEED = new SkillHolder(48492, 1); // ATK Spd +24%
	private static final SkillHolder MENTOR_CST_SPEED = new SkillHolder(48493, 1); // Casting Spd +24%
	private static final SkillHolder MENTOR_PSKILL_CRIT = new SkillHolder(48494, 1); // P skill critical rate +11%
	private static final SkillHolder MENTOR_MSKILL_CRIT = new SkillHolder(48495, 1); // M skill critical Damage +21%
	private static final SkillHolder MENTOR_ALL_CRITICAL_DAMAGE = new SkillHolder(48496, 1); // critical damage +500
	private static final SkillHolder MENTOR_SS_DAMAGE = new SkillHolder(48497, 1); // Soulshots Spirit shots damage
	private static final SkillHolder MENTOR_SKILL_POWER = new SkillHolder(48498, 1); // skill power +10%
	private static final SkillHolder MENTOR_ALL_PMCRITICAL_DAMAGE = new SkillHolder(48499, 1); // All critical damage 15%
	private static final SkillHolder MENTOR_ABSORBS_DAMAGE_MP = new SkillHolder(48500, 1); // Absorbs 3% inflicted damage as mp
	private static final SkillHolder MENTOR_ABSORBS_DAMAGE_HP = new SkillHolder(48501, 1); // Absorbs 8% inflicted damage as hp
	// Defense buff
	private static final SkillHolder MENTOR_PDEF = new SkillHolder(48502, 1); // P Def +1000 buff
	private static final SkillHolder MENTOR_MDEF = new SkillHolder(48503, 1); // M Def +1000 buff
	private static final SkillHolder MENTOR_MAXHP_MP = new SkillHolder(48504, 1); // Max hp mp +33%
	private static final SkillHolder MENTOR_SPEED = new SkillHolder(48505, 1); // Speed +12
	private static final SkillHolder MENTOR_RECEIVED_CRIT_DAMAGE = new SkillHolder(48506, 1); // P critical damage -15%
	private static final SkillHolder MENTOR_RECEIVED_CRIT_DAMAGE2 = new SkillHolder(48507, 1); // P critical damage -300
	private static final SkillHolder MENTOR_RATE_CRITICAL_RECEIVED = new SkillHolder(48508, 1); // All received critical rate -15%
	private static final SkillHolder MENTOR_RECEIVED_PVE = new SkillHolder(48509, 1); // Received pve critical damage -10%
	private static final SkillHolder MENTOR_HP_POTION_RECOVERY = new SkillHolder(48510, 1); // HP potion recovery potions effect +100
	// Event Buff: example Defeat the qeeen etc.
	protected static final int[] NO_DELETE_BUFFS =
	{
		48200,
		48233,
		48235,
		48236,
		48483,
	};
	private static final int[] LOCAL_MOBS =
	{
		22152,
		22153,
		22154,
		22155,
		22242,
		22245,
		22243,
		22276,
		22135,
		22139,
		22135,
		22141,
		22145,
		22144,
		22143,
		22141,
	};
	private static final int[] TOI_MOBS =
	{
		21990,
		21989,
		20812,
		21991,
		21994,
		21995,
		21996,
		22000,
		22001,
		22002,
		22003,
		22025,
		22026,
		22027,
		22010,
		22011,
		22012,
		22013,
		22016,
		22017,
		22021,
		22022,
		22024,
		22026,
		22028,
		22029,
		22030,
		22032,
		22033,
		22035,
		22039,
		22037,
		22038,
	};
	private static final Location LOCAL_AREA = new Location(-56255, 13537, -3336);
	private static final Location WAITING_AREA = new Location(-49550, 17189, -3016);
	private static final Location INSOLENCE_TOWER = new Location(-52849, 5272, -240);
	
	private static boolean _teleported;
	private static boolean _summonReady;
	
	private static final int TEMPLATE_ID = 224;
	
	public TrainingZone()
	{
		super(TEMPLATE_ID);
		addFirstTalkId(GROWN, TIND, TOKA, ERI, ARBANA);
		addTalkId(GROWN, TIND, TOKA, ERI, ARBANA);
		addAttackId(LOCAL_MOBS);
		addAttackId(TOI_MOBS);
		addCreatureSeeId(GROWN, TIND);
		addInstanceEnterId(TEMPLATE_ID);
		addInstanceLeaveId(TEMPLATE_ID);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.startsWith("ENTER"))
		{
			final int zoneId = Integer.parseInt(event.split(" ")[1]);
			final TimedHuntingZoneHolder huntingZone = TimedHuntingZoneData.getInstance().getHuntingZone(zoneId);
			if (huntingZone == null)
			{
				return null;
			}
			
			if (huntingZone.isSoloInstance())
			{
				enterInstance(player, npc, huntingZone.getInstanceId());
			}
			else
			{
				Instance world = null;
				for (Instance instance : InstanceManager.getInstance().getInstances())
				{
					if (instance.getTemplateId() == huntingZone.getInstanceId())
					{
						world = instance;
						break;
					}
				}
				if (world == null)
				{
					world = InstanceManager.getInstance().createInstance(huntingZone.getInstanceId(), player);
				}
				
				player.teleToLocation(huntingZone.getEnterLocation(), world);
			}
		}
		switch (event)
		{
			case "34307.htm": // Gronw
			{
				break;
			}
			case "GivePatk":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_PATK.getSkill());
				}
				break;
			}
			case "GiveMatk":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_MATK.getSkill());
				}
				break;
			}
			case "GiveAtkSpeed":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_ATK_SPEED.getSkill());
				}
				break;
			}
			case "GiveCastingSpd":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_CST_SPEED.getSkill());
				}
				break;
			}
			case "GivePskillcriticalrate":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_PSKILL_CRIT.getSkill());
				}
				break;
			}
			case "GivePskillcriticalDamage":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_MSKILL_CRIT.getSkill());
				}
				break;
			}
			case "GivePskillcriticalDamage+500":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_ALL_CRITICAL_DAMAGE.getSkill());
				}
				break;
			}
			case "GiveSshotsdamage":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_SS_DAMAGE.getSkill());
				}
				break;
			}
			case "GivePskillPower":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_SKILL_POWER.getSkill());
				}
				break;
			}
			case "GiveAllcriticaldamage":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_ALL_PMCRITICAL_DAMAGE.getSkill());
				}
				break;
			}
			case "GiveAbsorbs3inflictedMp":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_ABSORBS_DAMAGE_MP.getSkill());
				}
				break;
			}
			case "GiveAbsorbs8inflictedHp":
			{
				if ((npc.getId() == TOKA) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_ABSORBS_DAMAGE_HP.getSkill());
				}
				break;
			}
			case "GivePdef":
			{
				if ((npc.getId() == ERI) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_PDEF.getSkill());
				}
				break;
			}
			case "GiveMdef":
			{
				if ((npc.getId() == ERI) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_MDEF.getSkill());
				}
				break;
			}
			case "GiveMxHpMp":
			{
				if ((npc.getId() == ERI) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_MAXHP_MP.getSkill());
				}
				break;
			}
			case "GiveSpeed":
			{
				if ((npc.getId() == ERI) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_SPEED.getSkill());
				}
				break;
			}
			case "GiveReceivedCritDamage":
			{
				if ((npc.getId() == ERI) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_RECEIVED_CRIT_DAMAGE.getSkill());
				}
				break;
			}
			case "GiveReceivedCritDamage2":
			{
				if ((npc.getId() == ERI) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_RECEIVED_CRIT_DAMAGE2.getSkill());
				}
				break;
			}
			case "GiveRateCriticalRcv":
			{
				if ((npc.getId() == ERI) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_RATE_CRITICAL_RECEIVED.getSkill());
				}
				break;
			}
			case "GiveReceivedPve":
			{
				if ((npc.getId() == ERI) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_RECEIVED_PVE.getSkill());
				}
				break;
			}
			case "GiveHpPotionRevcovery":
			{
				if ((npc.getId() == ERI) && npc.isInInstance())
				{
					SkillCaster.triggerCast(npc, player, MENTOR_HP_POTION_RECOVERY.getSkill());
				}
				break;
			}
			case "npc_talk":
			{
				final Instance world = npc.getInstanceWorld();
				if (world != null)
				{
					npc.broadcastSay(ChatType.NPC_SHOUT, "I can Summon mobs for the training");
					startQuestTimer(event, 11500, npc, player);
				}
				break;
			}
			
			case "NPC_BUFFSUPPORT":
			{
				if (npc.getId() == TOKA)
				{
					return npc.getId() + "-tokaBuff.html";
				}
				if (npc.getId() == ERI)
				{
					return npc.getId() + "-eriBuff.html";
				}
				break;
			}
			case "INSOLENCE_TOWER":
			{
				if (npc.getId() == GROWN)
				{
					return npc.getId() + "-tower.html";
				}
				break;
			}
			case "BOSS_LOCAL_SPAWN":
			{
				final Instance world = npc.getInstanceWorld();
				if (npc.getVariables().getInt("TRAINIG_AREA_TELEPORT") == 1)
				{
					world.spawnGroup("tookhack");
				}
				break;
			}
			case "DEBUFF":
			{
				final Instance world = npc.getInstanceWorld();
				World.getInstance().forEachVisibleObjectInRange(npc, Player.class, 800, closeby ->
				{
					if (closeby.isPlayer() && (!world.getParameters().getBoolean("SayhaActive", false)))
					{
						world.getParameters().set("SayhaActive", true);
						npc.setTarget(closeby);
						npc.doCast(SAYHA_BUFF.getSkill());
					}
				});
				break;
			}
			// Last 10 Min < 600000 Boss spawn
			case "SpawnBossClockList":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if ((areaTeleport == 1) && (world.getRemainingTime() < 600000))
				{
					addSpawn(BOOJUDU, -56776, 12532, -3384, 33897, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 2) && (world.getRemainingTime() < 600000))
				{
					addSpawn(PETRON, -56776, 12532, -3384, 33897, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 3) && (world.getRemainingTime() < 600000))
				{
					addSpawn(KERION, -56776, 12532, -3384, 33897, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 4) && (world.getRemainingTime() < 600000))
				{
					addSpawn(TUKHAH, -56776, 12532, -3384, 33897, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 5) && (world.getRemainingTime() < 600000))
				{
					addSpawn(RILVA, -51725, 6149, -245, 54160, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 6) && (world.getRemainingTime() < 600000))
				{
					addSpawn(RILVA, -51725, 6149, -245, 54160, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 7) && (world.getRemainingTime() < 600000))
				{
					addSpawn(RILVA, -51725, 6149, -245, 54160, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 8) && (world.getRemainingTime() < 600000))
				{
					addSpawn(RILVA, -51725, 6149, -245, 54160, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 9) && (world.getRemainingTime() < 600000))
				{
					addSpawn(RYUN, -51725, 6149, -245, 54160, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 10) && (world.getRemainingTime() < 600000))
				{
					addSpawn(RYUN, -51725, 6149, -245, 54160, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 11) && (world.getRemainingTime() < 600000))
				{
					addSpawn(RYUN, -51725, 6149, -245, 54160, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport == 12) && (world.getRemainingTime() < 600000))
				{
					addSpawn(RYUN, -51725, 6149, -245, 54160, false, 0, true, player.getInstanceId());
				}
				if ((areaTeleport >= 13) && (world.getRemainingTime() < 600000))
				{
					addSpawn(CHEL, -51725, 6149, -245, 54160, false, 0, true, player.getInstanceId());
				}
				break;
			}
			case "LIZARMENDSELECT":
			{
				final Instance world = npc.getInstanceWorld();
				
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
					
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					world.spawnGroup("PlainsoftheLizardmen");
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 1);
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "SELMAHUMSELECT":
			{
				final Instance world = npc.getInstanceWorld();
				
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					world.spawnGroup("SelMahumBase");
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 2);
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "ORCBARRACKSELECT":
			{
				final Instance world = npc.getInstanceWorld();
				
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					world.spawnGroup("OrcBarrackskerrs");
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 3);
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "ORCBARRACKTUREKSELECT":
			{
				final Instance world = npc.getInstanceWorld();
				
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					world.spawnGroup("OrcBarrackstureks");
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 4);
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT":
			{
				final Instance world = npc.getInstanceWorld();
				
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 5);
					world.spawnGroup("TowerOfinsolence1");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT2":
			{
				final Instance world = npc.getInstanceWorld();
				
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 6);
					world.spawnGroup("TowerOfinsolence2");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT3":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 7);
					world.spawnGroup("TowerOfinsolence3");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT4":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 8);
					world.spawnGroup("TowerOfinsolence4");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT5":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 9);
					world.spawnGroup("TowerOfinsolence5");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT6":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 10);
					world.spawnGroup("TowerOfinsolence6");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT7":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 11);
					world.spawnGroup("TowerOfinsolence7");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT8":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 12);
					world.spawnGroup("TowerOfinsolence8");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT9":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 13);
					world.spawnGroup("TowerOfinsolence9");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT10":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
					
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 14);
					world.spawnGroup("TowerOfinsolence10");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT11":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
					
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 15);
					world.spawnGroup("TowerOfinsolence11");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "TOWEROFINSOLENCESELECT12":
			{
				final Instance world = npc.getInstanceWorld();
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_summonReady = false;
					world.getNpcs().stream().filter(WorldObject::isAttackable).forEach(Npc::deleteMe);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
					return npc.getId() + "-removeselect.html";
				}
				else if (areaTeleport == 0)
				{
					_summonReady = true;
					npcVars.set("TRAINIG_AREA_TELEPORT", areaTeleport + 16);
					world.spawnGroup("TowerOfinsolence12");
					return npc.getId() + "-Selected.html";
				}
				break;
			}
			case "LOCALTELEPORT":
			{
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport <= 4)
				{
					player.teleToLocation(LOCAL_AREA);
					
					_teleported = true;
				}
				if (areaTeleport >= 5)
				{
					player.teleToLocation(INSOLENCE_TOWER);
					_teleported = true;
				}
				break;
			}
			case "LOCALTELEPORTRETURN":
			{
				final StatSet npcVars = player.getVariables();
				final int areaTeleport = npcVars.getInt("TRAINIG_AREA_TELEPORT", 0);
				if (areaTeleport >= 1)
				{
					_teleported = false;
					player.teleToLocation(WAITING_AREA);
					npcVars.remove("TRAINIG_AREA_TELEPORT");
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
			final Npc grown = world.getNpc(GROWN);
			startQuestTimer("npc_talk", 2000, grown, null);
			if (!world.getParameters().getBoolean("SayhaActive", false))
			{
				startQuestTimer("DEBUFF", 500, grown, null, true);
			}
		}
		return super.onCreatureSee(npc, creature);
	}
	
	@Override
	public void onInstanceEnter(Player player, Instance instance)
	{
		startEvent(player);
		player.getInstanceWorld().setDuration(60);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (_teleported && (npc.getId() == TIND))
		{
			return npc.getId() + "-waitingzone.html";
		}
		if (_summonReady && (npc.getId() == TIND))
		{
			return npc.getId() + "-teleport.html";
		}
		if (npc.getId() == TIND)
		{
			return npc.getId() + "-NoSummonStatus.html";
		}
		if (npc.getId() == TOKA)
		{
			return npc.getId() + "-toka.html";
		}
		if (npc.getId() == ERI)
		{
			return npc.getId() + "-eri.html";
		}
		return super.onFirstTalk(npc, player);
	}
	
	@Override
	public String onAttack(Npc npc, Player attacker, int damage, boolean isSummon)
	{
		final Instance world = npc.getInstanceWorld();
		
		if (!world.getParameters().getBoolean("spawnedBoss", false))
		{
			startQuestTimer("SpawnBossClockList", 1000, npc, attacker);
			world.getParameters().set("spawnedBoss", true);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public void onInstanceLeave(Player player, Instance instance)
	{
		player.sendPacket(new ExSendUIEvent(player, true, false, 3600, 0, NpcStringId.TIME_LEFT));
		player.sendPacket(TimedHuntingZoneExit.STATIC_PACKET);
		removeBuffs(player);
		final StatSet npcVars = player.getVariables();
		npcVars.remove("TRAINIG_AREA_TELEPORT");
		instance.finishInstance();
	}
	
	private void startEvent(Player player)
	{
		final Instance instance = player.getInstanceWorld();
		player.sendPacket(new ExSendUIEvent(player, false, false, Math.min(3600, (int) (instance.getRemainingTime() / 1000)), 0, NpcStringId.TIME_LEFT));
	}
	
	private void removeBuffs(Creature ch)
	{
		ch.getEffectList().stopEffects(info -> (info != null) && !info.getSkill().isStayAfterDeath() && (Arrays.binarySearch(NO_DELETE_BUFFS, info.getSkill().getId()) < 0), true, true);
	}
	
	public static void main(String[] args)
	{
		new TrainingZone();
	}
}
