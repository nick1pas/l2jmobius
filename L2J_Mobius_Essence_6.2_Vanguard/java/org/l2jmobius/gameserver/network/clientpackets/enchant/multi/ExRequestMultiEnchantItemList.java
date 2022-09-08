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
package org.l2jmobius.gameserver.network.clientpackets.enchant.multi;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.PacketReader;
import org.l2jmobius.commons.util.Rnd;
import org.l2jmobius.gameserver.data.xml.EnchantItemData;
import org.l2jmobius.gameserver.model.World;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.request.EnchantItemRequest;
import org.l2jmobius.gameserver.model.holders.ItemHolder;
import org.l2jmobius.gameserver.model.item.enchant.EnchantResultType;
import org.l2jmobius.gameserver.model.item.enchant.EnchantScroll;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.GameClient;
import org.l2jmobius.gameserver.network.SystemMessageId;
import org.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import org.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import org.l2jmobius.gameserver.network.serverpackets.enchant.EnchantResult;
import org.l2jmobius.gameserver.network.serverpackets.enchant.multi.ExResultMultiEnchantItemList;
import org.l2jmobius.gameserver.network.serverpackets.enchant.multi.ExResultSetMultiEnchantItemList;
import org.l2jmobius.gameserver.network.serverpackets.enchant.single.ChangedEnchantTargetItemProbabilityList;
import org.l2jmobius.gameserver.util.Util;

/**
 * @author Index
 */
public class ExRequestMultiEnchantItemList implements IClientIncomingPacket
{
	private int _useLateAnnounce;
	private int _slotId;
	private final Map<Integer, Integer> _itemObjectId = new HashMap<>();
	private final Map<Integer, String> _result = new HashMap<>();
	private final Map<Integer, int[]> _successEnchant = new HashMap<>();
	private final Map<Integer, Integer> _failureEnchant = new HashMap<>();
	
	/**
	 * @code slot_id @code item_holder
	 */
	private final Map<Integer, ItemHolder> _failureReward = new HashMap<>();
	
	protected static final Logger LOGGER_ENCHANT = Logger.getLogger("enchant.items");
	
	@Override
	public boolean read(GameClient client, PacketReader packet)
	{
		_useLateAnnounce = packet.readC();
		_slotId = packet.readD();
		for (int i = 1; packet.getReadableBytes() != 0; i++)
		{
			_itemObjectId.put(i, packet.readD());
		}
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
		
		final EnchantItemRequest request = player.getRequest(EnchantItemRequest.class);
		if (request == null)
		{
			return;
		}
		
		if ((request.getEnchantingScroll() == null) || request.isProcessing())
		{
			return;
		}
		
		final Item scroll = request.getEnchantingScroll();
		if (scroll.getCount() < _slotId)
		{
			player.removeRequest(request.getClass());
			player.sendPacket(new ExResultSetMultiEnchantItemList(player, 1));
			Logger.getLogger("MultiEnchant - player " + player.getObjectId() + " " + player.getName() + " trying enchant items, when scrolls count less than items;");
		}
		final EnchantScroll scrollTemplate = EnchantItemData.getInstance().getEnchantScroll(scroll);
		if (scrollTemplate == null)
		{
			return;
		}
		
		final int[] slots = new int[_slotId];
		for (int i = 1; i <= _slotId; i++)
		{
			if (!request.checkMultiEnchantingItemsByObjectId(_itemObjectId.get(i)))
			{
				player.removeRequest(request.getClass());
				return;
			}
			slots[i - 1] = getMultiEnchantingSlotByObjectId(request, _itemObjectId.get(i));
		}
		
		_itemObjectId.clear();
		request.setProcessing(true);
		
		for (int slotCounter = 0; slotCounter < slots.length; slotCounter++)
		{
			final int i = slots[slotCounter];
			if ((i == -1) || (request.getMultiEnchantingItemsBySlot(i) == -1))
			{
				player.sendPacket(new ExResultMultiEnchantItemList(player, true));
				player.removeRequest(request.getClass());
				return;
			}
			
			final Item enchantItem = player.getInventory().getItemByObjectId(request.getMultiEnchantingItemsBySlot(i));
			if (scrollTemplate.getMaxEnchantLevel() < enchantItem.getEnchantLevel())
			{
				Logger.getLogger("MultiEnchant - player " + player.getObjectId() + " " + player.getName() + " trying over-enchant item " + enchantItem.getItemName() + " " + enchantItem.getObjectId());
				player.removeRequest(request.getClass());
				return;
			}
			
			if (player.getInventory().destroyItemByItemId("Enchant", scroll.getId(), 1, player, enchantItem) == null)
			{
				player.removeRequest(request.getClass());
				return;
			}
			
			final InventoryUpdate iu = new InventoryUpdate();
			synchronized (enchantItem)
			{
				if ((enchantItem.getOwnerId() != player.getObjectId()) || !enchantItem.isEnchantable())
				{
					player.sendPacket(SystemMessageId.AUGMENTATION_REQUIREMENTS_ARE_NOT_FULFILLED);
					player.removeRequest(request.getClass());
					player.sendPacket(new ExResultMultiEnchantItemList(player, true));
					return;
				}
				
				final EnchantResultType resultType = scrollTemplate.calculateSuccess(player, enchantItem, null);
				switch (resultType)
				{
					case ERROR:
					{
						player.sendPacket(SystemMessageId.AUGMENTATION_REQUIREMENTS_ARE_NOT_FULFILLED);
						player.removeRequest(request.getClass());
						_result.put(slots[i - 1], "ERROR");
						break;
					}
					case SUCCESS:
					{
						if (scrollTemplate.isCursed())
						{
							// Blessed enchant: Enchant value down by 1.
							client.sendPacket(SystemMessageId.THE_ENCHANT_VALUE_IS_DECREASED_BY_1);
							enchantItem.setEnchantLevel(enchantItem.getEnchantLevel() - 1);
						}
						// Increase enchant level only if scroll's base template has chance, some armors can success over +20 but they shouldn't have increased.
						else if (scrollTemplate.getChance(player, enchantItem) > 0)
						{
							enchantItem.setEnchantLevel(enchantItem.getEnchantLevel() + Math.min(Rnd.get(scrollTemplate.getRandomEnchantMin(), scrollTemplate.getRandomEnchantMax()), scrollTemplate.getMaxEnchantLevel()));
							enchantItem.updateDatabase();
						}
						_result.put(i, "SUCCESS");
						if (Config.LOG_ITEM_ENCHANTS)
						{
							if (enchantItem.getEnchantLevel() > 0)
							{
								LOGGER_ENCHANT.info("Success, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantItem.getEnchantLevel() + " " + enchantItem.getName() + "(" + enchantItem.getCount() + ") [" + enchantItem.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
							}
							else
							{
								LOGGER_ENCHANT.info("Success, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", " + enchantItem.getName() + "(" + enchantItem.getCount() + ") [" + enchantItem.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
							}
						}
						break;
					}
					case FAILURE:
					{
						if (scrollTemplate.isSafe())
						{
							// Safe enchant: Remain old value.
							player.sendPacket(SystemMessageId.ENCHANT_FAILED_THE_ENCHANT_SKILL_FOR_THE_CORRESPONDING_ITEM_WILL_BE_EXACTLY_RETAINED);
							player.sendPacket(new EnchantResult(EnchantResult.SAFE_FAIL, enchantItem));
							if (Config.LOG_ITEM_ENCHANTS)
							{
								if (enchantItem.getEnchantLevel() > 0)
								{
									LOGGER_ENCHANT.info("Safe Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantItem.getEnchantLevel() + " " + enchantItem.getName() + "(" + enchantItem.getCount() + ") [" + enchantItem.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
								}
								else
								{
									LOGGER_ENCHANT.info("Safe Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", " + enchantItem.getName() + "(" + enchantItem.getCount() + ") [" + enchantItem.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
								}
							}
						}
						if (scrollTemplate.isBlessed() || scrollTemplate.isBlessedDown() || scrollTemplate.isCursed())
						{
							// Blessed enchant: Enchant value down by 1.
							if (scrollTemplate.isBlessedDown() || scrollTemplate.isCursed())
							{
								client.sendPacket(SystemMessageId.THE_ENCHANT_VALUE_IS_DECREASED_BY_1);
								enchantItem.setEnchantLevel(enchantItem.getEnchantLevel() - 1);
							}
							else // Blessed enchant: Clear enchant value.
							{
								player.sendPacket(SystemMessageId.THE_BLESSED_ENCHANT_FAILED_THE_ENCHANT_VALUE_OF_THE_ITEM_BECAME_0);
								enchantItem.setEnchantLevel(0);
							}
							_result.put(i, "BLESSED_FAIL");
							enchantItem.updateDatabase();
							if (Config.LOG_ITEM_ENCHANTS)
							{
								if (enchantItem.getEnchantLevel() > 0)
								{
									LOGGER_ENCHANT.info("Blessed Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantItem.getEnchantLevel() + " " + enchantItem.getName() + "(" + enchantItem.getCount() + ") [" + enchantItem.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
								}
								else
								{
									LOGGER_ENCHANT.info("Blessed Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", " + enchantItem.getName() + "(" + enchantItem.getCount() + ") [" + enchantItem.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
								}
							}
						}
						else
						{
							// Enchant failed, destroy item.
							if (player.getInventory().destroyItem("Enchant", enchantItem, player, null) == null)
							{
								// Unable to destroy item, cheater?
								Util.handleIllegalPlayerAction(player, "Unable to delete item on enchant failure from " + player + ", possible cheater !", Config.DEFAULT_PUNISH);
								player.removeRequest(request.getClass());
								_result.put(i, "ERROR");
								if (Config.LOG_ITEM_ENCHANTS)
								{
									if (enchantItem.getEnchantLevel() > 0)
									{
										LOGGER_ENCHANT.info("Unable to destroy, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantItem.getEnchantLevel() + " " + enchantItem.getName() + "(" + enchantItem.getCount() + ") [" + enchantItem.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
									}
									else
									{
										LOGGER_ENCHANT.info("Unable to destroy, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", " + enchantItem.getName() + "(" + enchantItem.getCount() + ") [" + enchantItem.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
									}
								}
								return;
							}
							
							World.getInstance().removeObject(enchantItem);
							
							int count = 0;
							if (enchantItem.getTemplate().isCrystallizable())
							{
								count = Math.max(0, enchantItem.getCrystalCount() - ((enchantItem.getTemplate().getCrystalCount() + 1) / 2));
							}
							
							Item crystals = null;
							final int crystalId = enchantItem.getTemplate().getCrystalItemId();
							if (count > 0)
							{
								crystals = player.getInventory().addItem("Enchant", crystalId, count, player, enchantItem);
								final SystemMessage sm = new SystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1_X_S2);
								sm.addItemName(crystals);
								sm.addLong(count);
								player.sendPacket(sm);
								ItemHolder itemHolder = new ItemHolder(crystalId, count);
								_failureReward.put(_failureReward.size() + 1, itemHolder);
							}
							
							if (crystals != null)
							{
								iu.addItem(crystals);
							}
							
							if ((crystalId == 0) || (count == 0))
							{
								ItemHolder itemHolder = new ItemHolder(0, 0);
								_failureReward.put(_failureReward.size() + 1, itemHolder);
								_result.put(i, "NO_CRYSTAL");
							}
							else
							{
								ItemHolder itemHolder = new ItemHolder(0, 0);
								_failureReward.put(_failureReward.size() + 1, itemHolder);
								_result.put(i, "FAIL");
							}
							
							if (Config.LOG_ITEM_ENCHANTS)
							{
								if (enchantItem.getEnchantLevel() > 0)
								{
									LOGGER_ENCHANT.info("Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantItem.getEnchantLevel() + " " + enchantItem.getName() + "(" + enchantItem.getCount() + ") [" + enchantItem.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
								}
								else
								{
									LOGGER_ENCHANT.info("Fail, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", " + enchantItem.getName() + "(" + enchantItem.getCount() + ") [" + enchantItem.getObjectId() + "], " + scroll.getName() + "(" + scroll.getCount() + ") [" + scroll.getObjectId() + "]");
								}
							}
						}
						break;
					}
				}
			}
		}
		
		for (int slotCounter = 0; slotCounter < slots.length; slotCounter++)
		{
			final int i = slots[slotCounter];
			if (_result.get(i).equals("SUCCESS"))
			{
				int[] intArray = new int[2];
				intArray[0] = request.getMultiEnchantingItemsBySlot(i);
				intArray[1] = player.getInventory().getItemByObjectId(request.getMultiEnchantingItemsBySlot(i)).getEnchantLevel();
				_successEnchant.put(i, intArray);
			}
			else if (_result.get(i).equals("NO_CRYSTAL") || _result.get(i).equals("FAIL"))
			{
				_failureEnchant.put(i, request.getMultiEnchantingItemsBySlot(i));
				request.changeMultiEnchantingItemsBySlot(i, 0);
			}
			else
			{
				player.sendPacket(new ExResultMultiEnchantItemList(player, true));
				return;
			}
		}
		
		for (int i : _failureReward.keySet())
		{
			request.addMultiEnchantFailItems(_failureReward.get(i));
		}
		request.setProcessing(false);
		
		player.sendItemList();
		player.broadcastUserInfo();
		player.sendPacket(new ChangedEnchantTargetItemProbabilityList(player, true));
		
		if (_useLateAnnounce == 1)
		{
			request.setMultiSuccessEnchantList(_successEnchant);
			request.setMultiFailureEnchantList(_failureEnchant);
		}
		
		player.sendPacket(new ExResultMultiEnchantItemList(player, _successEnchant, _failureEnchant));
	}
	
	public int getMultiEnchantingSlotByObjectId(EnchantItemRequest request, int objectId)
	{
		int slotId = -1;
		for (int i = 1; i <= request.getMultiEnchantingItemsCount(); i++)
		{
			if ((request.getMultiEnchantingItemsCount() == 0) || (objectId == 0))
			{
				return slotId;
			}
			else if (request.getMultiEnchantingItemsBySlot(i) == objectId)
			{
				return i;
			}
		}
		return slotId;
	}
	
}