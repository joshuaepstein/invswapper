package uk.joshuaepstein.invswapper.block;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import uk.joshuaepstein.invswapper.block.entity.InvArmorStandBE;
import uk.joshuaepstein.invswapper.init.ModBlocks;

import java.util.List;
import java.util.Objects;

public class InvArmorStand extends Block implements EntityBlock {
	public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public InvArmorStand() {
		this(Properties.of(Material.STONE, MaterialColor.STONE).strength(1.0F, 3600000.0F));
	}

	protected InvArmorStand(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof InvArmorStandBE invArmorStandBE) {
			if (invArmorStandBE.getOwner().equals(player.getUUID())) {
				InteractionResult result = invArmorStandBE.use(state, level, pos, player, hand, hitResult);
				if (result == null) {
					return super.use(state, level, pos, player, hand, hitResult);
				} else {
					return result;
				}
			} else {
				return super.use(state, level, pos, player, hand, hitResult);
			}
		}
		return super.use(state, level, pos, player, hand, hitResult);
	}

	@Override
	public RenderShape getRenderShape(BlockState p_60550_) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState p_60584_) {
		return PushReaction.BLOCK;
	}

	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_) {
		return List.of();
	}

	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
		return SHAPE;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return ModBlocks.INV_ARMOR_STAND_BE.create(p_153215_, p_153216_);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos pos = context.getClickedPos();
		Level level = context.getLevel();
		if (pos.getY() < level.getMaxBuildHeight() - 2 && level.getBlockState(pos.above(1)).canBeReplaced(context))
			return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
		return null;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState State, @Nullable LivingEntity placer, ItemStack stack) {
		if (placer instanceof ServerPlayer serverPlayer) {
			if (level.getBlockEntity(pos) instanceof InvArmorStandBE invArmorStandBE) {
				if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
					if (!stack.getTagElement("BlockEntityTag").contains("Owner")) {
						invArmorStandBE.setOwner(serverPlayer);
					}
					invArmorStandBE.skin.updateSkin(serverPlayer.getScoreboardName());
				}	else {
					invArmorStandBE.setOwner(serverPlayer);
					invArmorStandBE.skin.updateSkin(serverPlayer.getScoreboardName());
				}
			}
		}
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide) {
			BlockEntity tileEntity = level.getBlockEntity(pos);
			ItemStack itemStack = new ItemStack(this);
			if (tileEntity instanceof InvArmorStandBE invArmorStandBE) {
				CompoundTag beTag = new CompoundTag();
				CompoundTag compoundTag = invArmorStandBE.saveWithoutMetadata();
				beTag.put("BlockEntityTag", compoundTag);
				itemStack.setTag(beTag);
			}
			Block.popResource(level, pos, itemStack);
		}
		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
		super.createBlockStateDefinition(p_49915_.add(FACING));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter getter, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(itemStack, getter, tooltip, flag);
		CompoundTag blockEntity = itemStack.getOrCreateTag();

		if (blockEntity.contains("BlockEntityTag")) {
			CompoundTag blockEntityTag = blockEntity.getCompound("BlockEntityTag");
			if (blockEntityTag.contains("Owner")) {
				Level level = Minecraft.getInstance().level;
				if (level == null) return;
				level.players().forEach(player -> {
					if (Objects.equals(player.getUUID(), blockEntityTag.getUUID("Owner")))
						tooltip.add(new TextComponent("Owned by ").append(new TextComponent(player.getName().getString()).withStyle(style -> style.withColor(3705077))));
				});
			}
			if (blockEntityTag.contains("Inventory")) {
				ListTag inventory = blockEntityTag.getList("Inventory", 10);
				Inventory inv = new Inventory(null);
				inv.load(inventory);
				if (!inv.isEmpty()) {
					tooltip.add(new TextComponent("Contains Player Inventory").withStyle(ChatFormatting.GREEN));

					tooltip.add(TextComponent.EMPTY);
					if (Screen.hasShiftDown()) {
						for (int i = 3; i >= 0; i--) {
							ItemStack stack = inv.getItem(39 - (3-i));
							if (!stack.isEmpty()) {
								String name = switch (i) {
									case 0 -> "Boots";
									case 1 -> "Leggings";
									case 2 -> "Chestplate";
									case 3 -> "Helmet";
									default -> "Unknown";
								};
								tooltip.add((new TextComponent(name).withStyle(ChatFormatting.GRAY)).append(": ").append(new TextComponent(stack.getHoverName().getString()).withStyle(style -> style.withColor(ChatFormatting.WHITE))));
							} else {
								String name = switch (i) {
									case 0 -> "Boots";
									case 1 -> "Leggings";
									case 2 -> "Chestplate";
									case 3 -> "Helmet";
									default -> "Unknown";
								};
								tooltip.add(new TextComponent(name).withStyle(ChatFormatting.GRAY).append(": ").append(new TextComponent("Empty").withStyle(style -> style.withColor(ChatFormatting.WHITE))));
							}
						}
						tooltip.add(new TextComponent("+ More").withStyle(ChatFormatting.GRAY));
					} else {
						tooltip.add(new TextComponent("Hold <" + ChatFormatting.WHITE + ChatFormatting.BOLD + "SHIFT" + ChatFormatting.RESET + "> for more info").withStyle(ChatFormatting.GRAY));
					}
				}
			}
		}
	}
}
