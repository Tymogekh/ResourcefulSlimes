package io.github.tymogekh.resourcefulslimes.entity.gui;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceSlimeMenu extends AbstractContainerMenu {

    private final ResourceSlime slime;

    public ResourceSlimeMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf){
        this(ResourcefulSlimes.RESOURCE_SLIME_MENU.get(), containerId, (ResourceSlime) inventory.player.level().getEntity(buf.readInt()));
    }

    public ResourceSlimeMenu(@Nullable MenuType<?> menuType, int containerId, ResourceSlime slime) {
        super(menuType, containerId);
        this.slime = slime;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.slime.isAlive() && player.canInteractWithEntity(this.slime, 4.0);
    }

    public ResourceSlime getSlime() {
        return this.slime;
    }
}
