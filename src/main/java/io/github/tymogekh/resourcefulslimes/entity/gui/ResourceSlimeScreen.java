package io.github.tymogekh.resourcefulslimes.entity.gui;

import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ResourceSlimeScreen extends BookViewScreen implements MenuAccess<ResourceSlimeMenu> {

    private final ResourceSlimeMenu menu;

    public ResourceSlimeScreen(ResourceSlimeMenu menu, Inventory playerInventory, Component title) {
        super();
        this.menu = menu;
    }

    @Override
    public void render(@NotNull GuiGraphics p_283479_, int p_283661_, int p_281248_, float p_281886_) {
        super.render(p_283479_, p_283661_, p_281248_, p_281886_);
        ResourceSlime slime = this.getMenu().getSlime();
        int marginLeft = this.width / 2 - 50;
        InventoryScreen.renderEntityInInventoryFollowsMouse(p_283479_, marginLeft, this.height / 2 - 100, this.width / 2 + 50, this.height / 2 - 35, 17, 0.25F, p_283661_, p_281248_, slime);
        p_283479_.drawString(this.getFont(), slime.getVariant().getDisplayName().getString(), marginLeft, this.height / 2 - 20, 0x4a4a4a, false);
        p_283479_.drawString(this.getFont(), "Saturation: " + slime.getEntityData().get(ResourceSlime.SATURATION), marginLeft, this.height / 2 - 10, 0x4a4a4a, false);
        p_283479_.drawString(this.getFont(), "Growth: " + slime.getEntityData().get(ResourceSlime.GROWTH), marginLeft, this.height / 2, 0x4a4a4a, false);
        p_283479_.drawString(this.getFont(), "Splitting: " + slime.getEntityData().get(ResourceSlime.SPLITTING), marginLeft, this.height / 2 + 10, 0x4a4a4a, false);
        p_283479_.drawString(this.getFont(), "Hunger Red.: " + slime.getEntityData().get(ResourceSlime.HUNGER_REDUCTION), marginLeft, this.height / 2 + 20, 0x4a4a4a, false);
        p_283479_.drawString(this.getFont(), "Productiveness: " + slime.getEntityData().get(ResourceSlime.PRODUCTIVENESS), marginLeft, this.height / 2 + 30, 0x4a4a4a, false);
    }

    @Override
    public @NotNull ResourceSlimeMenu getMenu() {
        return this.menu;
    }
}
