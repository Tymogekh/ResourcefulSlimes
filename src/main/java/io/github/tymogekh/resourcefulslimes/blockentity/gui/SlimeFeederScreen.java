package io.github.tymogekh.resourcefulslimes.blockentity.gui;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.config.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SlimeFeederScreen extends AbstractContainerScreen<SlimeFeederMenu> implements MenuAccess<SlimeFeederMenu> {

    public SlimeFeederScreen(AbstractContainerMenu menu, Inventory playerInventory, Component title) {
        super((SlimeFeederMenu) menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float v, int i, int i1) {
        renderTransparentBackground(guiGraphics);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/gui/slime_feeder.png"), this.leftPos, this.topPos, 0, 0 ,this.imageWidth, this.imageHeight);
        int scaled = (int) (this.getMenu().getNutrition() / (float) Config.MAX_NUTRITION_STORAGE.get() * 49);
        guiGraphics.blitSprite(ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "container/slime_feeder/nutrition"), this.leftPos + 97, this.topPos + 64 - scaled, 18, scaled);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public @NotNull SlimeFeederMenu getMenu() {
        return this.menu;
    }
}
