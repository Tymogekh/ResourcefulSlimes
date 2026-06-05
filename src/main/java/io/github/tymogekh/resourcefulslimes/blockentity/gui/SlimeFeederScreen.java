package io.github.tymogekh.resourcefulslimes.blockentity.gui;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.config.Config;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class SlimeFeederScreen extends AbstractContainerScreen<@NotNull SlimeFeederMenu> implements MenuAccess<@NotNull SlimeFeederMenu> {

    public SlimeFeederScreen(SlimeFeederMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.width = 176;
        this.height = 166;
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractTransparentBackground(graphics);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/gui/slime_feeder.png"),
                this.leftPos, this.topPos, 0, 0 ,this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        int scaled = (int) (this.getMenu().getNutrition() / (float) Config.MAX_NUTRITION_STORAGE.get() * 49);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "container/slime_feeder/nutrition"),
                18, 51, 0, 0, this.leftPos + 97, this.topPos + 64 - scaled, 18, scaled);
        if (mouseX > this.leftPos + 97 && mouseX < this.leftPos + 115 && mouseY > this.topPos + 15 && mouseY < this.topPos + 64) {
            graphics.tooltip(this.getFont(), List.of(ClientTooltipComponent.create(FormattedCharSequence.forward(String.valueOf(this.getMenu().getNutrition()), Style.EMPTY))),
                    mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, ItemStack.EMPTY.get(DataComponents.TOOLTIP_STYLE));
        }
        extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public @NotNull SlimeFeederMenu getMenu() {
        return this.menu;
    }
}
