package uk.joshuaepstein.invswapper.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import uk.joshuaepstein.invswapper.util.SkinProfile;

import javax.annotation.Nullable;

/**
 * @author Team Iskallia - Vault Hunters 1.18.2
 */
public abstract class SkinnableTileEntity extends BlockEntity {
  public SkinProfile skin;
  
  public SkinnableTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
    super(tileEntityTypeIn, pos, state);
    this.skin = new SkinProfile();
  }
  
  public SkinProfile getSkin() {
    return this.skin;
  }
  
  public void sendUpdates() {
    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
    this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
    setChanged();
  }
  
  @Nullable
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }
  
  protected abstract void updateSkin();
}