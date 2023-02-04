package uk.joshuaepstein.invswapper.client.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;

public class FontHelper {

    public static void drawStringWithBorder(PoseStack matrixStack, String text, float x, float y, int color, int borderColor) {
        Minecraft minecraft = Minecraft.getInstance();

        minecraft.font.draw(matrixStack, text, x - 1, y, borderColor);
        minecraft.font.draw(matrixStack, text, x + 1, y, borderColor);
        minecraft.font.draw(matrixStack, text, x, y - 1, borderColor);
        minecraft.font.draw(matrixStack, text, x, y + 1, borderColor);
        minecraft.font.draw(matrixStack, text, x, y, color);
    }

    public static void drawCenteredString(PoseStack matrixStack, String text, float x, float y, int color){
        Minecraft mc = Minecraft.getInstance();

        mc.font.draw(matrixStack, text, x - mc.font.width(text) / 2, y, color);
    }

}
