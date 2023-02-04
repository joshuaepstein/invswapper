package uk.joshuaepstein.invswapper.container.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.Nullable;

public class ArmorSlot extends ReadOnlySlot {
	private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[] {
			InventoryMenu.EMPTY_ARMOR_SLOT_HELMET,
			InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
			InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
			InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS
	};
	private final EquipmentSlot slotType;

	public ArmorSlot(Container inventory, EquipmentSlot slotType, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
		this.slotType = slotType;
	}

	@Nullable
	@Override
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		if (this.slotType.getType() != EquipmentSlot.Type.ARMOR)
			return null;
		return Pair.of(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[this.slotType.getIndex()]);
	}
}
