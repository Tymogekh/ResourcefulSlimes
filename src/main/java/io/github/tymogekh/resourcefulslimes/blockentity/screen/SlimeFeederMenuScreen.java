package io.github.tymogekh.resourcefulslimes.blockentity.screen;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.menu.SlimeFeederMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public class SlimeFeederMenuScreen extends AbstractContainerScreen<SlimeFeederMenu> {

    public SlimeFeederMenuScreen(AbstractContainerMenu menu, Inventory playerInventory, Component title) {
        super((SlimeFeederMenu) menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float v, int i, int i1) {
        renderTransparentBackground(guiGraphics);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/gui/slime_feeder.png"), 0, 0, this.width, this.height, 0, 0, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
