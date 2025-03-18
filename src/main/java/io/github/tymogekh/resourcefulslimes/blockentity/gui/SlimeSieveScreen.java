package io.github.tymogekh.resourcefulslimes.blockentity.gui;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SlimeSieveScreen extends AbstractContainerScreen<SlimeSieveMenu> implements MenuAccess<SlimeSieveMenu> {

    public SlimeSieveScreen(SlimeSieveMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float v, int i, int i1) {
        renderTransparentBackground(guiGraphics);
        guiGraphics.blit(RenderType::guiTextured, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/gui/slime_sieve.png"),
                this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, float v) {
        super.render(guiGraphics, x, y, v);
        int scaled = (int) (this.getMenu().getSievingProgress() * 0.105F);
        guiGraphics.blitSprite(RenderType::guiTextured, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "container/slime_sieve/sieving_progress"),
                21, 17, 0, 0, this.leftPos + 81, this.topPos + 34, scaled, 17);
        renderTooltip(guiGraphics, x, y);
    }

    @Override
    public @NotNull SlimeSieveMenu getMenu() {
        return this.menu;
    }
}
