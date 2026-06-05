package io.github.tymogekh.resourcefulslimes.entity.gui;

import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;


public class ResourceSlimeScreen extends BookViewScreen implements MenuAccess<@NotNull ResourceSlimeMenu> {

    private final ResourceSlimeMenu menu;
    
    public ResourceSlimeScreen(ResourceSlimeMenu menu, Inventory playerInventory, Component title) {
        super();
        this.menu = menu;
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        ResourceSlime slime = this.getMenu().getSlime();
        int marginLeft = this.width / 2 - 50;
        InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, marginLeft, this.height / 2 - 100, this.width / 2 + 50, this.height / 2 - 35, 17, 0.25F, mouseX, mouseY, slime);
        graphics.text(this.getFont(), slime.getVariant().getDisplayName().getString(), marginLeft, this.height / 2 - 20, 0xFF4a4a4a, false);
        graphics.text(this.getFont(), "Saturation: " + slime.getEntityData().get(ResourceSlime.SATURATION), marginLeft, this.height / 2 - 10, 0xFF4a4a4a, false);
        graphics.text(this.getFont(), "Growth: " + slime.getEntityData().get(ResourceSlime.GROWTH), marginLeft, this.height / 2, 0xFF4a4a4a, false);
        graphics.text(this.getFont(), "Splitting: " + slime.getEntityData().get(ResourceSlime.SPLITTING), marginLeft, this.height / 2 + 10, 0xFF4a4a4a, false);
        graphics.text(this.getFont(), "Hunger Red.: " + slime.getEntityData().get(ResourceSlime.HUNGER_REDUCTION), marginLeft, this.height / 2 + 20, 0xFF4a4a4a, false);
        graphics.text(this.getFont(), "Productiveness: " + slime.getEntityData().get(ResourceSlime.PRODUCTIVENESS), marginLeft, this.height / 2 + 30, 0xFF4a4a4a, false);
    }

    @Override
    public @NotNull ResourceSlimeMenu getMenu() {
        return this.menu;
    }
}
