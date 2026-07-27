package io.github.tymogekh.resourcefulslimes.blockentity.gui;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlimeLabScreen extends AbstractContainerScreen<@NotNull SlimeLabMenu> implements MenuAccess<@NotNull SlimeLabMenu> {
    public SlimeLabScreen(SlimeLabMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.width = 176;
        this.height = 166;
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        extractTransparentBackground(graphics);
        graphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/gui/slime_lab.png"),
                this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        LivingEntity displayEntity = getDisplayEntity(this.menu.getLevel(), this.menu.getResourceSlimeVariant());
        if (displayEntity != null) {
            InventoryScreen.extractEntityInInventoryFollowsMouse(graphics, this.leftPos + 90, this.topPos + 20, this.leftPos + 162, this.topPos + 72, 40, 0.15F, mouseX, mouseY, displayEntity);
        }
        extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public @NotNull SlimeLabMenu getMenu() {
        return this.menu;
    }

    public static @Nullable LivingEntity getDisplayEntity(Level level, ResourceSlime.Variant variant) {
        if (variant == null) {
            return null;
        }
        ResourceSlime resourceSlime = ResourcefulSlimes.RESOURCE_SLIME.get().create(level, EntitySpawnReason.SPAWNER);
        if (resourceSlime != null) {
            resourceSlime.setSize(1, true);
            resourceSlime.setVariant(variant);
            resourceSlime.setId(-1);
        }
        return resourceSlime;
    }
}
