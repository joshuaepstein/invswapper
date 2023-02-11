package uk.joshuaepstein.invswapper.block.entity;

import com.google.common.base.Predicates;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.CuriosHelper;
import uk.joshuaepstein.invswapper.InvSwapMod;
import uk.joshuaepstein.invswapper.container.StatueContainer;
import uk.joshuaepstein.invswapper.container.slot.ReadonlyItemStackSlot;
import uk.joshuaepstein.invswapper.init.ModBlocks;
import uk.joshuaepstein.invswapper.init.ModItems;
import uk.joshuaepstein.invswapper.integration.IntegrationCurios;

import java.util.*;
import java.util.function.BiPredicate;

import static uk.joshuaepstein.invswapper.integration.IntegrationCurios.getCuriosItemStacksFromTag;

public class InvArmorStandBE extends SkinnableTileEntity {
	private UUID owner;
	private Inventory inventory = new Inventory(null);
	private CompoundTag curiosSlots = new CompoundTag();
	private ItemStack standItem;
	private BlockState stand = Blocks.SMOOTH_STONE_SLAB.defaultBlockState();
	private boolean isSmall = true;

	public InvArmorStandBE(BlockPos pos, BlockState state) {
		super(ModBlocks.INV_ARMOR_STAND_BE, pos, state);
		this.standItem = new ItemStack(Blocks.SMOOTH_STONE_SLAB);
		this.owner = null;
	}

	public CompoundTag getCuriosSlots() {
		return curiosSlots;
	}

	public InvArmorStandBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public BlockState getStand() {
		return stand;
	}

	protected void updateSkin() {}

	public UUID getOwner() {return owner;}
	public void setOwner(Player owner) {
		this.owner = owner.getUUID();
		this.skin.updateSkin(owner.getScoreboardName());
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("PlayerNickname"))
			this.skin.updateSkin(tag.getString("PlayerNickname"));
		if (tag.contains("Owner"))
			this.owner = tag.getUUID("Owner");
		if (tag.contains("Inventory")) {
			ListTag invTag = tag.getList("Inventory", 10);
			this.inventory = new Inventory(null);
			this.inventory.load(invTag);
		} else {
			this.inventory = new Inventory(null);
		}
		if (tag.contains("Stand"))
			this.stand = NbtUtils.readBlockState(tag.getCompound("Stand"));
		if (tag.contains("IsSmall"))
			this.isSmall = tag.getBoolean("IsSmall");
		if (tag.contains("CuriosInv"))
			this.curiosSlots = tag.getCompound("CuriosInv");
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		String nickname = this.skin.getLatestNickname();
		if (nickname != null)
			tag.putString("PlayerNickname", nickname);
		if (this.owner != null)
			tag.putUUID("Owner", owner);
		Inventory inventory = this.inventory;
		ListTag invTag = new ListTag();
		inventory.save(invTag);
		tag.put("Inventory", invTag);
		if (this.stand.getBlock() != Blocks.SMOOTH_STONE_SLAB)
			tag.put("Stand", NbtUtils.writeBlockState(this.stand));
		tag.putBoolean("IsSmall", this.isSmall);
		tag.put("CuriosInv", this.curiosSlots);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (this.owner != null && !this.owner.equals(player.getUUID()) && !player.isCreative())
			return null;
		ItemStack heldItem = player.getMainHandItem();
		if (heldItem.getItem() == ModItems.SIZE_KEY) {
			this.isSmall = !this.isSmall;
			level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.PLAYERS, 0.6F, 0.7F);
			sendUpdates();
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		if (player.isShiftKeyDown()) {
			if (level.isClientSide) return InteractionResult.SUCCESS;
			NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
				@Override
				public @NotNull Component getDisplayName() {
					return new TranslatableComponent("container.statue");
				}

				@Override
				public @NotNull AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
					return new StatueContainer(id, level, pos, player.getInventory());
				}
			}, buffer -> buffer.writeBlockPos(pos));
			return InteractionResult.SUCCESS;
		} else {
			Item item = heldItem.getItem();
			if (item instanceof BlockItem bi && bi.getBlock() instanceof SlabBlock) {
				if (bi.getBlock().defaultBlockState() == this.stand) return InteractionResult.sidedSuccess(level.isClientSide);
				ItemStack s = heldItem.copy();
				s.setCount(1);
				this.setStand(s, bi);
				level.playSound(null, pos, bi.getBlock().defaultBlockState().getSoundType().getPlaceSound(), SoundSource.PLAYERS, 0.6F, 1.2F);
				return InteractionResult.sidedSuccess(level.isClientSide);
			} else {
				int xplevels = player.experienceLevel;
				if (xplevels < 1) {
					player.displayClientMessage(new TranslatableComponent("message.statue.notenoughxp").withStyle(ChatFormatting.RED), true);
					return InteractionResult.FAIL;
				}
				player.experienceLevel -= 1;
				ListTag invTag = new ListTag();
				ListTag playerTag = new ListTag();
				if (InvSwapMod.isCuriosLoaded) {
					CompoundTag currentCurios = curiosSlots.copy();
					curiosSlots = IntegrationCurios.getMappedSerializedCuriosItemStacks(player, (player1, stack1) -> true, true);
					IntegrationCurios.applyMappedSerializedCuriosItemStacks(player, currentCurios, true);
				}
				this.inventory.save(invTag);
				player.getInventory().save(playerTag);

				player.getInventory().clearContent();
				player.getInventory().load(invTag);
				player.getInventory().setChanged();

				this.inventory.clearContent();
				this.inventory.load(playerTag);
				this.inventory.setChanged();

				for (int i = 0; i < 4; i++) {
					ItemStack stack = player.getInventory().getArmor(i);
					if(stack.isEmpty()) continue;
					if (stack.getItem() instanceof ArmorItem) {
						level.playSound(null, pos, stack.getEquipSound(), SoundSource.PLAYERS, 0.6F, 1.2F);
					}
				}

				level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6F, 1.2F);

				return InteractionResult.SUCCESS;
			}
		}
	}

	public void setStand(ItemStack stack, BlockItem blockItem) {
		this.standItem = stack;
		this.stand = blockItem.getBlock().defaultBlockState();
		sendUpdates();
	}

	public boolean getSmall() {
		return this.isSmall;
	}

	public void setSmall(boolean isSmall) {
		this.isSmall = isSmall;
		sendUpdates();
	}

}