package uk.joshuaepstein.invswapper.integration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.CuriosHelper;
import top.theillusivec4.curios.common.CuriosRegistry;
import top.theillusivec4.curios.common.network.NetworkHandler;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncStack;
import top.theillusivec4.curios.common.slottype.SlotType;
import top.theillusivec4.curios.server.SlotHelper;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class IntegrationCurios {
	public static Collection<CompoundTag> getSerializedCuriosItemStacks(Player player) {
		return player.getCapability(CuriosCapability.INVENTORY).map(inv -> {
			ArrayList<CompoundTag> stacks = new ArrayList<>();
			for (ICurioStacksHandler handle : inv.getCurios().values()) {
				IDynamicStackHandler stackHandler = handle.getStacks();
				for (int index = 0; index < stackHandler.getSlots(); ++index) {
					ItemStack stack = stackHandler.getStackInSlot(index);
					if (stack.isEmpty()) continue;
					stacks.add(stack.serializeNBT());
				}
			}
			return stacks;
		}).orElse(new ArrayList<>());
	}

	public static Map<String, List<ItemStack>> getCuriosItemStacks(LivingEntity entity) {
		return entity.getCapability(CuriosCapability.INVENTORY).map(inv -> {
			Map<String, List<ItemStack>> contents = new HashMap<>();
			inv.getCurios().forEach((key, handler) -> {
				IDynamicStackHandler stackHandler = handler.getStacks();
				for (int index = 0; index < stackHandler.getSlots(); ++index) {
					(contents.computeIfAbsent(key, str -> new ArrayList<>())).add(stackHandler.getStackInSlot(index));
				}
			});
			return contents;
		}).orElse(Collections.emptyMap());
	}

	public static void clearCurios(LivingEntity entity) {
		CuriosApi.getCuriosHelper().getCuriosHandler(entity).ifPresent(handler -> handler.getCurios().values().forEach(stacksHandler -> {
			IDynamicStackHandler stackHandler = stacksHandler.getStacks();
			IDynamicStackHandler cosmeticStackHandler = stacksHandler.getCosmeticStacks();
			String id = stacksHandler.getIdentifier();
			for (int i = 0; i < stackHandler.getSlots(); ++i) {
				UUID uuid = UUID.nameUUIDFromBytes((id + i).getBytes());
				NonNullList<Boolean> renderStates = stacksHandler.getRenders();
				SlotContext slotContext = new SlotContext(id, entity, i, false, renderStates.size() > i && (Boolean) renderStates.get(i));
				ItemStack stack = stackHandler.getStackInSlot(i);
				Multimap<Attribute, AttributeModifier> map = CuriosApi.getCuriosHelper().getAttributeModifiers(slotContext, uuid, stack);
				Multimap<String, AttributeModifier> slots = HashMultimap.create();
				Set<CuriosHelper.SlotAttributeWrapper> toRemove = new HashSet<>();
				for (Attribute attribute : map.keySet()) {
					if (!(attribute instanceof CuriosHelper.SlotAttributeWrapper wrapper)) continue;
					slots.putAll(wrapper.identifier, map.get(attribute));
					toRemove.add(wrapper);
				}
				for (Attribute attribute : toRemove) {
					map.removeAll(attribute);
				}
				entity.getAttributes().removeAttributeModifiers(map);
				handler.removeSlotModifiers(slots);
				CuriosApi.getCuriosHelper().getCurio(stack).ifPresent(curio -> curio.onUnequip(slotContext, stack));
				stackHandler.setStackInSlot(i, ItemStack.EMPTY);
				NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SPacketSyncStack(entity.getId(), id, i, ItemStack.EMPTY, SPacketSyncStack.HandlerType.EQUIPMENT, new CompoundTag()));
				cosmeticStackHandler.setStackInSlot(i, ItemStack.EMPTY);
				NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SPacketSyncStack(entity.getId(), id, i, ItemStack.EMPTY, SPacketSyncStack.HandlerType.COSMETIC, new CompoundTag()));
			}
		}));
	}

	public static CompoundTag getMappedSerializedCuriosItemStacks(Player player, BiPredicate<Player, ItemStack> stackFilter, boolean removeSnapshotItems) {
		return player.getCapability(CuriosCapability.INVENTORY).map(inv -> {
			CompoundTag tag = new CompoundTag();
			inv.getCurios().forEach((key, handle) -> {
				CompoundTag keyMap = new CompoundTag();
				IDynamicStackHandler stackHandler = handle.getStacks();
				for (int slot = 0; slot < stackHandler.getSlots(); ++slot) {
					ItemStack stack = stackHandler.getStackInSlot(slot);
					if (!stackFilter.test(player, stack) || stack.isEmpty()) continue;
					ItemStack stackCopy = stack.copy();
					keyMap.put(String.valueOf(slot), stackCopy.serializeNBT());
					if (!removeSnapshotItems) continue;
					stackHandler.setStackInSlot(slot, ItemStack.EMPTY);
				}
				tag.put(key, (Tag)keyMap);
			});
			return tag;
		}).orElse(new CompoundTag());
	}


	public static List<ItemStack> applyMappedSerializedCuriosItemStacks(Player player, CompoundTag tag, boolean replaceExisting) {
		return player.getCapability(CuriosCapability.INVENTORY).map(inv -> {
			ArrayList<ItemStack> filledItems = new ArrayList<>();
			for (String handlerKey : tag.getAllKeys()) {
				inv.getStacksHandler(handlerKey).ifPresent(arg_0 -> {
					IDynamicStackHandler stackHandler = arg_0.getStacks();
					CompoundTag handlerKeyMap = tag.getCompound(handlerKey);
					for (String strSlot : handlerKeyMap.getAllKeys()) {
						int slot;
						try {
							slot = Integer.parseInt(strSlot);
						} catch (NumberFormatException exc) {
							continue;
						}
						if (slot < 0 || slot >= stackHandler.getSlots()) continue;
						ItemStack stack = ItemStack.of(handlerKeyMap.getCompound(strSlot));
						if (replaceExisting || stackHandler.getStackInSlot(slot).isEmpty()) {
							stackHandler.setStackInSlot(slot, stack);
							continue;
						}
						filledItems.add(stack);
					}
				});
			}
			return filledItems;
		}).orElse(new ArrayList<>());
	}

	public static Map<String, ItemStack> getCuriosItemStacksFromTag(CompoundTag tag) {
		Map<String, ItemStack> contents = new HashMap<>();
		if (CuriosApi.getSlotHelper() == null) {
			tag.getAllKeys().forEach((key) -> {
				CompoundTag itemTag = tag.getCompound(key).getCompound("0");
				if (itemTag.isEmpty()) {
					contents.put(key, ItemStack.EMPTY);
					return;
				}
				contents.put(key, ItemStack.of(itemTag));
			});
			return contents;
		};
		CuriosApi.getSlotHelper().getSlotTypes().forEach((slot) -> {
			String identifier = slot.getIdentifier();
			if (tag.contains(identifier)) {
				CompoundTag slotTag = tag.getCompound(identifier);
				int slotIndex = slotTag.getAllKeys().stream().filter(k -> k.matches("\\d+")).findFirst().map(Integer::parseInt).orElse(0);
				contents.put(identifier, ItemStack.of(slotTag.getCompound(String.valueOf(slotIndex))));
			} else {
				contents.put(identifier, ItemStack.EMPTY);
			}
		});
		return contents;
	}
}
