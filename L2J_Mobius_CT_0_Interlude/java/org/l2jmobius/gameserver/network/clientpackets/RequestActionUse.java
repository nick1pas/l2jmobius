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

import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.ai.CtrlEvent;
import org.l2jmobius.gameserver.ai.CtrlIntention;
import org.l2jmobius.gameserver.ai.NextAction;
import org.l2jmobius.gameserver.ai.SummonAI;
import org.l2jmobius.gameserver.data.xml.PetDataTable;
import org.l2jmobius.gameserver.data.xml.PetSkillData;
import org.l2jmobius.gameserver.data.xml.SkillData;
import org.l2jmobius.gameserver.enums.ChatType;
import org.l2jmobius.gameserver.enums.MountType;
import org.l2jmobius.gameserver.enums.PrivateStoreType;
import org.l2jmobius.gameserver.model.WorldObject;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.actor.instance.BabyPet;
import org.l2jmobius.gameserver.model.actor.instance.Pet;
import org.l2jmobius.gameserver.model.actor.instance.SiegeFlag;
import org.l2jmobius.gameserver.model.actor.instance.StaticObject;
import org.l2jmobius.gameserver.model.effects.EffectType;
import org.l2jmobius.gameserver.model.holders.SkillHolder;
import org.l2jmobius.gameserver.model.skill.Skill;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.NpcStringId;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.serverpackets.ActionFailed;
import org.l2jmobius.gameserver.network.serverpackets.ChairSit;
import org.l2jmobius.gameserver.network.serverpackets.NpcSay;
import org.l2jmobius.gameserver.network.serverpackets.RecipeShopManageList;
import org.l2jmobius.gameserver.network.serverpackets.SocialAction;

/**
 * This class manages the action use request packet.
 * @author Zoey76
 */
public class RequestActionUse implements IClientIncomingPacket
{
	private static final int SIN_EATER_ID = 12564;
	private static final int SWITCH_STANCE_ID = 6054;
	private static final NpcStringId[] NPC_STRINGS =
	{
		NpcStringId.USING_A_SPECIAL_SKILL_HERE_COULD_TRIGGER_A_BLOODBATH,
		NpcStringId.HEY_WHAT_DO_YOU_EXPECT_OF_ME,
		NpcStringId.UGGGGGH_PUSH_IT_S_NOT_COMING_OUT,
		NpcStringId.AH_I_MISSED_THE_MARK
	};
	
	private int _actionId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	private GameClient _client;
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_actionId = packet.readD();
		_ctrlPressed = (packet.readD() == 1);
		_shiftPressed = (packet.readC() == 1);
		_client = client;
		return true;
	}
	
	@Override
	public void run(GameClient client)
	{
		final Player player = client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		// Don't do anything if player is dead or confused
		if ((player.isFakeDeath() && (_actionId != 0)) || player.isDead() || player.isOutOfControl())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Summon summon = player.getSummon();
		final WorldObject target = player.getTarget();
		switch (_actionId)
		{
			case 0: // Sit/Stand
			{
				if (player.isSitting() || !player.isMoving() || player.isFakeDeath())
				{
					useSit(player, target);
				}
				else
				{
					// Sit when arrive using next action.
					// Creating next action class.
					final NextAction nextAction = new NextAction(CtrlEvent.EVT_ARRIVED, CtrlIntention.AI_INTENTION_MOVE_TO, () -> useSit(player, target));
					// Binding next action to AI.
					player.getAI().setNextAction(nextAction);
				}
				break;
			}
			case 1: // Walk/Run
			{
				if (player.isRunning())
				{
					player.setWalking();
				}
				else
				{
					player.setRunning();
				}
				break;
			}
			case 10: // Private Store - Sell
			{
				player.tryOpenPrivateSellStore(false);
				break;
			}
			case 15: // Change Movement Mode (Pets)
			{
				if (validateSummon(summon, true))
				{
					((SummonAI) summon.getAI()).notifyFollowStatusChange();
				}
				break;
			}
			case 16: // Attack (Pets)
			{
				if (validateSummon(summon, true) && summon.canAttack(_ctrlPressed))
				{
					summon.doSummonAttack(target);
				}
				break;
			}
			case 17: // Stop (Pets)
			{
				if (validateSummon(summon, true))
				{
					summon.cancelAction();
				}
				break;
			}
			case 19: // Unsummon Pet
			{
				if (!validateSummon(summon, true))
				{
					break;
				}
				if (summon.isDead())
				{
					player.sendPacket(SystemMessageId.DEAD_PETS_CANNOT_BE_RETURNED_TO_THEIR_SUMMONING_ITEM);
					break;
				}
				if (summon.isAttackingNow() || summon.isInCombat() || summon.isMovementDisabled())
				{
					player.sendPacket(SystemMessageId.A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE);
					break;
				}
				if (summon.isHungry())
				{
					if (summon.isPet() && !((Pet) summon).getPetData().getFood().isEmpty())
					{
						player.sendPacket(SystemMessageId.YOU_MAY_NOT_RESTORE_A_HUNGRY_PET);
					}
					else
					{
						player.sendPacket(SystemMessageId.THE_HUNTING_HELPER_PET_CANNOT_BE_RETURNED_BECAUSE_THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_IT_LEAVES);
					}
					break;
				}
				summon.unSummon(player);
				break;
			}
			case 21: // Change Movement Mode (Servitors)
			{
				if (validateSummon(summon, false))
				{
					((SummonAI) summon.getAI()).notifyFollowStatusChange();
				}
				break;
			}
			case 22: // Attack (Servitors)
			{
				if (validateSummon(summon, false) && summon.canAttack(_ctrlPressed))
				{
					summon.doSummonAttack(target);
				}
				break;
			}
			case 23: // Stop (Servitors)
			{
				if (validateSummon(summon, false))
				{
					summon.cancelAction();
				}
				break;
			}
			case 28: // Private Store - Buy
			{
				player.tryOpenPrivateBuyStore();
				break;
			}
			case 32: // Wild Hog Cannon - Wild Cannon
			{
				useSkill("DDMagic", false);
				break;
			}
			case 36: // Soulless - Toxic Smoke
			{
				useSkill("RangeDebuff", false);
				break;
			}
			case 37: // Dwarven Manufacture
			{
				if (player.isAlikeDead() || player.isSellingBuffs())
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				if (player.getPrivateStoreType() != PrivateStoreType.NONE)
				{
					player.setPrivateStoreType(PrivateStoreType.NONE);
					player.broadcastUserInfo();
				}
				if (player.isSitting())
				{
					player.standUp();
				}
				player.sendPacket(new RecipeShopManageList(player, true));
				break;
			}
			case 38: // Mount/Dismount
			{
				player.mountPlayer(summon);
				break;
			}
			case 39: // Soulless - Parasite Burst
			{
				useSkill("RangeDD", false);
				break;
			}
			case 41: // Wild Hog Cannon - Attack
			{
				if (validateSummon(summon, false))
				{
					if ((target != null) && (target.isDoor() || (target instanceof SiegeFlag)))
					{
						useSkill(4230, false);
					}
					else
					{
						player.sendPacket(SystemMessageId.INVALID_TARGET);
					}
				}
				break;
			}
			case 42: // Kai the Cat - Self Damage Shield
			{
				useSkill("HealMagic", false);
				break;
			}
			case 43: // Merrow the Unicorn - Hydro Screw
			{
				useSkill("DDMagic", false);
				break;
			}
			case 44: // Big Boom - Boom Attack
			{
				useSkill("DDMagic", false);
				break;
			}
			case 45: // Boxer the Unicorn - Master Recharge
			{
				useSkill("HealMagic", player, false);
				break;
			}
			case 46: // Mew the Cat - Mega Storm Strike
			{
				useSkill("DDMagic", false);
				break;
			}
			case 47: // Silhouette - Steal Blood
			{
				useSkill("DDMagic", false);
				break;
			}
			case 48: // Mechanic Golem - Mech. Cannon
			{
				useSkill("DDMagic", false);
				break;
			}
			case 51: // General Manufacture
			{
				// Player shouldn't be able to set stores if he/she is alike dead (dead or fake death)
				if (player.isAlikeDead())
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				if (player.getPrivateStoreType() != PrivateStoreType.NONE)
				{
					player.setPrivateStoreType(PrivateStoreType.NONE);
					player.broadcastUserInfo();
				}
				if (player.isSitting())
				{
					player.standUp();
				}
				player.sendPacket(new RecipeShopManageList(player, false));
				break;
			}
			case 52: // Unsummon Servitor
			{
				if (validateSummon(summon, false))
				{
					if (summon.isAttackingNow() || summon.isInCombat())
					{
						player.sendPacket(SystemMessageId.A_SERVITOR_WHOM_IS_ENGAGED_IN_BATTLE_CANNOT_BE_DE_ACTIVATED);
						break;
					}
					summon.unSummon(player);
				}
				break;
			}
			case 53: // Move to target (Servitors)
			{
				if (validateSummon(summon, false) && (target != null) && (summon != target) && !summon.isMovementDisabled())
				{
					summon.setFollowStatus(false);
					summon.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, target.getLocation());
				}
				break;
			}
			case 54: // Move to target (Pets)
			{
				if (validateSummon(summon, true) && (target != null) && (summon != target) && !summon.isMovementDisabled())
				{
					summon.setFollowStatus(false);
					summon.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, target.getLocation());
				}
				break;
			}
			case 1000: // Siege Golem - Siege Hammer
			{
				if ((target != null) && target.isDoor())
				{
					useSkill(4079, false);
				}
				break;
			}
			case 1001: // Sin Eater - Ultimate Bombastic Buster
			{
				if (validateSummon(summon, true) && (summon.getId() == SIN_EATER_ID))
				{
					summon.broadcastPacket(new NpcSay(summon.getObjectId(), ChatType.NPC_GENERAL, summon.getId(), NPC_STRINGS[Rnd.get(NPC_STRINGS.length)]));
				}
				break;
			}
			case 1003: // Wind Hatchling/Strider - Wild Stun
			{
				useSkill("PhysicalSpecial", true);
				break;
			}
			case 1004: // Wind Hatchling/Strider - Wild Defense
			{
				useSkill("Buff", player, true);
				break;
			}
			case 1005: // Star Hatchling/Strider - Bright Burst
			{
				useSkill("DDMagic", true);
				break;
			}
			case 1006: // Star Hatchling/Strider - Bright Heal
			{
				useSkill("Heal", player, true);
				break;
			}
			case 1007: // Feline Queen - Blessing of Queen
			{
				useSkill("Buff1", player, false);
				break;
			}
			case 1008: // Feline Queen - Gift of Queen
			{
				useSkill("Buff2", player, false);
				break;
			}
			case 1009: // Feline Queen - Cure of Queen
			{
				useSkill("DDMagic", false);
				break;
			}
			case 1010: // Unicorn Seraphim - Blessing of Seraphim
			{
				useSkill("Buff1", player, false);
				break;
			}
			case 1011: // Unicorn Seraphim - Gift of Seraphim
			{
				useSkill("Buff2", player, false);
				break;
			}
			case 1012: // Unicorn Seraphim - Cure of Seraphim
			{
				useSkill("DDMagic", false);
				break;
			}
			case 1013: // Nightshade - Curse of Shade
			{
				useSkill("DeBuff1", false);
				break;
			}
			case 1014: // Nightshade - Mass Curse of Shade
			{
				useSkill("DeBuff2", false);
				break;
			}
			case 1015: // Nightshade - Shade Sacrifice
			{
				useSkill("Heal", false);
				break;
			}
			case 1016: // Cursed Man - Cursed Blow
			{
				useSkill("PhysicalSpecial1", false);
				break;
			}
			case 1017: // Cursed Man - Cursed Strike
			{
				useSkill("PhysicalSpecial2", false);
				break;
			}
			case 1031: // Feline King - Slash
			{
				useSkill("PhysicalSpecial1", false);
				break;
			}
			case 1032: // Feline King - Spinning Slash
			{
				useSkill("PhysicalSpecial2", false);
				break;
			}
			case 1033: // Feline King - Hold of King
			{
				useSkill("PhysicalSpecial3", false);
				break;
			}
			case 1034: // Magnus the Unicorn - Whiplash
			{
				useSkill("PhysicalSpecial1", false);
				break;
			}
			case 1035: // Magnus the Unicorn - Tridal Wave
			{
				useSkill("PhysicalSpecial2", false);
				break;
			}
			case 1036: // Spectral Lord - Corpse Kaboom
			{
				useSkill("PhysicalSpecial1", false);
				break;
			}
			case 1037: // Spectral Lord - Dicing Death
			{
				useSkill("PhysicalSpecial2", false);
				break;
			}
			case 1038: // Spectral Lord - Dark Curse
			{
				useSkill("PhysicalSpecial3", false);
				break;
			}
			case 1039: // Swoop Cannon - Cannon Fodder
			{
				useSkill(5110, false);
				break;
			}
			case 1040: // Swoop Cannon - Big Bang
			{
				useSkill(5111, false);
				break;
			}
			// Social Packets
			case 12: // Greeting
			{
				tryBroadcastSocial(2);
				break;
			}
			case 13: // Victory
			{
				tryBroadcastSocial(3);
				break;
			}
			case 14: // Advance
			{
				tryBroadcastSocial(4);
				break;
			}
			case 24: // Yes
			{
				tryBroadcastSocial(6);
				break;
			}
			case 25: // No
			{
				tryBroadcastSocial(5);
				break;
			}
			case 26: // Bow
			{
				tryBroadcastSocial(7);
				break;
			}
			case 29: // Unaware
			{
				tryBroadcastSocial(8);
				break;
			}
			case 30: // Social Waiting
			{
				tryBroadcastSocial(9);
				break;
			}
			case 31: // Laugh
			{
				tryBroadcastSocial(10);
				break;
			}
			case 33: // Applaud
			{
				tryBroadcastSocial(11);
				break;
			}
			case 34: // Dance
			{
				tryBroadcastSocial(12);
				break;
			}
			case 35: // Sorrow
			{
				tryBroadcastSocial(13);
				break;
			}
			case 62: // Charm
			{
				tryBroadcastSocial(14);
				break;
			}
			case 66: // Shyness
			{
				tryBroadcastSocial(15);
				break;
			}
		}
	}
	
	/**
	 * Use the sit action.
	 * @param player the player trying to sit
	 * @param target the target to sit, throne, bench or chair
	 * @return {@code true} if the player can sit, {@code false} otherwise
	 */
	protected boolean useSit(Player player, WorldObject target)
	{
		if (player.getMountType() != MountType.NONE)
		{
			return false;
		}
		
		if (!player.isSitting() && (target instanceof StaticObject) && (((StaticObject) target).getType() == 1) && player.isInsideRadius2D(target, StaticObject.INTERACTION_DISTANCE))
		{
			final ChairSit cs = new ChairSit(player, target.getId());
			_client.sendPacket(cs);
			player.sitDown();
			player.broadcastPacket(cs);
			return true;
		}
		
		if (player.isFakeDeath())
		{
			player.stopEffects(EffectType.FAKE_DEATH);
		}
		else if (player.isSitting())
		{
			player.standUp();
		}
		else
		{
			player.sitDown();
		}
		return true;
	}
	
	/**
	 * Cast a skill for active summon.<br>
	 * Target is specified as a parameter but can be overwrited or ignored depending on skill type.
	 * @param skillId the skill Id to be casted by the summon
	 * @param target the target to cast the skill on, overwritten or ignored depending on skill type
	 * @param pet if {@code true} it'll validate a pet, if {@code false} it will validate a servitor
	 */
	private void useSkill(int skillId, WorldObject target, boolean pet)
	{
		final Player player = _client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Summon summon = player.getSummon();
		if (!validateSummon(summon, pet))
		{
			return;
		}
		
		if (!canControl(summon))
		{
			return;
		}
		
		int level = 0;
		if (summon.isPet())
		{
			level = PetDataTable.getInstance().getPetData(summon.getId()).getAvailableLevel(skillId, summon.getLevel());
		}
		else
		{
			level = PetSkillData.getInstance().getAvailableLevel(summon, skillId);
		}
		
		if (level > 0)
		{
			summon.setTarget(target);
			summon.useMagic(SkillData.getInstance().getSkill(skillId, level), _ctrlPressed, _shiftPressed);
		}
		
		if (skillId == SWITCH_STANCE_ID)
		{
			summon.switchMode();
		}
	}
	
	private void useSkill(String skillName, WorldObject target, boolean pet)
	{
		final Player player = _client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		final Summon summon = player.getSummon();
		if (!validateSummon(summon, pet))
		{
			return;
		}
		
		if (!canControl(summon))
		{
			return;
		}
		
		if ((summon instanceof BabyPet) && !((BabyPet) summon).isInSupportMode())
		{
			_client.sendPacket(SystemMessageId.A_PET_ON_AUXILIARY_MODE_CANNOT_USE_SKILLS);
			return;
		}
		
		final SkillHolder skillHolder = summon.getTemplate().getParameters().getSkillHolder(skillName);
		if (skillHolder == null)
		{
			return;
		}
		
		final Skill skill = skillHolder.getSkill();
		if (skill != null)
		{
			summon.setTarget(target);
			summon.useMagic(skill, _ctrlPressed, _shiftPressed);
			if (skill.getId() == SWITCH_STANCE_ID)
			{
				summon.switchMode();
			}
		}
	}
	
	private boolean canControl(Summon summon)
	{
		if ((summon instanceof BabyPet) && !((BabyPet) summon).isInSupportMode())
		{
			_client.sendPacket(SystemMessageId.A_PET_ON_AUXILIARY_MODE_CANNOT_USE_SKILLS);
			return false;
		}
		
		if (summon.isPet() && ((summon.getLevel() - _client.getPlayer().getLevel()) > 20))
		{
			_client.sendPacket(SystemMessageId.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Cast a skill for active summon.<br>
	 * Target is retrieved from owner's target, then validated by overloaded method useSkill(int, Creature).
	 * @param skillId the skill Id to use
	 * @param pet if {@code true} it'll validate a pet, if {@code false} it will validate a servitor
	 */
	private void useSkill(int skillId, boolean pet)
	{
		final Player player = _client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		useSkill(skillId, player.getTarget(), pet);
	}
	
	/**
	 * Cast a skill for active summon.<br>
	 * Target is retrieved from owner's target, then validated by overloaded method useSkill(int, Creature).
	 * @param skillName the skill name to use
	 * @param pet if {@code true} it'll validate a pet, if {@code false} it will validate a servitor
	 */
	private void useSkill(String skillName, boolean pet)
	{
		final Player player = _client.getPlayer();
		if (player == null)
		{
			return;
		}
		
		useSkill(skillName, player.getTarget(), pet);
	}
	
	/**
	 * Validates the given summon and sends a system message to the master.
	 * @param summon the summon to validate
	 * @param checkPet if {@code true} it'll validate a pet, if {@code false} it will validate a servitor
	 * @return {@code true} if the summon is not null and whether is a pet or a servitor depending on {@code checkPet} value, {@code false} otherwise
	 */
	private boolean validateSummon(Summon summon, boolean checkPet)
	{
		if ((summon != null) && ((checkPet && summon.isPet()) || summon.isServitor()))
		{
			if (summon.isPet() && ((Pet) summon).isUncontrollable())
			{
				_client.sendPacket(SystemMessageId.ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_CAN_VIEW_THE_SIEGE_WAR_STATUS_WINDOW_DURING_A_SIEGE_WAR);
				return false;
			}
			if (summon.isBetrayed())
			{
				_client.sendPacket(SystemMessageId.YOUR_PET_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
				return false;
			}
			return true;
		}
		
		if (checkPet)
		{
			_client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_PET);
		}
		else
		{
			_client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR);
		}
		return false;
	}
	
	/**
	 * Try to broadcast SocialAction packet.
	 * @param id the social action Id to broadcast
	 */
	private void tryBroadcastSocial(int id)
	{
		final Player player = _client.getPlayer();
		if (player == null)
		{
			return;
		}
		if (player.isFishing())
		{
			_client.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_3);
			return;
		}
		
		if (player.canMakeSocialAction())
		{
			player.broadcastPacket(new SocialAction(player.getObjectId(), id));
		}
	}
}
