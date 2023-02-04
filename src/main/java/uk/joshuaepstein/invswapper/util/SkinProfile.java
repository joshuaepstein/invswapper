package uk.joshuaepstein.invswapper.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Team Iskallia - Vault Hunters 1.18.2
 */
public class SkinProfile {
  public static final ExecutorService SERVICE = Executors.newFixedThreadPool(4);

  private final AtomicReference<Boolean> slim = new AtomicReference<>(false);
  public AtomicReference<GameProfile> gameProfile = new AtomicReference<>();
  public AtomicReference<PlayerInfo> playerInfo = new AtomicReference<>();
  private String latestNickname;

  public boolean isEmpty() {
    return (getLatestNickname() == null);
  }

  public String getLatestNickname() {
    return latestNickname;
  }

  public void updateSkin(@Nullable String name) {
    if (name == null || name.isEmpty()) {
      this.latestNickname = null;
      this.gameProfile.set(null);
      this.playerInfo.set(null);
      this.slim.set(false);
      return;
    }
    if (name.equals(this.latestNickname))
      return;
    this.latestNickname = name;
    if (FMLEnvironment.dist.isClient())
      SERVICE.submit(() -> {
            this.gameProfile.set(new GameProfile(null, name));
            SkullBlockEntity.updateGameprofile(this.gameProfile.get(), (newProfile) -> {
              this.gameProfile.set(newProfile);
              ClientboundPlayerInfoPacket.PlayerUpdate data = new ClientboundPlayerInfoPacket.PlayerUpdate(this.gameProfile.get(), 0, null, null);
                this.playerInfo.set(new PlayerInfo(data));
                this.slim.set(isSlim(newProfile));
            });
          });
  }

  private static boolean isSlim(GameProfile gameProfile) {
    if (!gameProfile.isComplete())
      return false;
    SkinManager skinManager = Minecraft.getInstance().getSkinManager();
    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> skinCache = skinManager.getInsecureSkinInformation(gameProfile);
    if (skinCache.containsKey(MinecraftProfileTexture.Type.SKIN)) {
      MinecraftProfileTexture texture = skinCache.get(MinecraftProfileTexture.Type.SKIN);
      String s = texture.getMetadata("model");
      return (s != null && !s.equals("default"));
    }
    return false;
  }
  
  public boolean isSlim() {
    return this.slim.get();
  }
  
  @OnlyIn(Dist.CLIENT)
  public ResourceLocation getLocationSkin() {
    if (this.playerInfo == null || this.playerInfo.get() == null)
      return DefaultPlayerSkin.getDefaultSkin();
    try {
      return this.playerInfo.get().getSkinLocation();
    } catch (Exception e) {
      e.printStackTrace();
      return DefaultPlayerSkin.getDefaultSkin();
    }
  }
}