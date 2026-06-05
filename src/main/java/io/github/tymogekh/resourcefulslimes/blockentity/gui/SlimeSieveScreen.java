package io.github.tymogekh.resourcefulslimes.blockentity.gui;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SlimeSieveScreen extends AbstractContainerScreen<@NotNull SlimeSieveMenu> implements MenuAccess<@NotNull SlimeSieveMenu> {

    public SlimeSieveScreen(SlimeSieveMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.width = 176;
        this.height = 166;
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractTransparentBackground(graphics);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/gui/slime_sieve.png"),
                this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        int scaled = (int) (this.getMenu().getSievingProgress() * 0.0525F);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "container/slime_sieve/sieving_progress"),
                21, 17, 0, 0, this.leftPos + 81, this.topPos + 34, scaled, 17);
        extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public @NotNull SlimeSieveMenu getMenu() {
        return this.menu;
    }
}
