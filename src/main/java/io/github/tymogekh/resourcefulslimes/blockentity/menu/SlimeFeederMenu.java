package io.github.tymogekh.resourcefulslimes.blockentity.menu;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeFeederBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.slot.SlimeFeederSlot;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlimeFeederMenu extends AbstractContainerMenu {

    private final ContainerLevelAccess access;

    public SlimeFeederMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(ResourcefulSlimes.SLIME_FEEDER_MENU.get(), containerId, inventory, ContainerLevelAccess.NULL, inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public SlimeFeederMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, ContainerLevelAccess access, BlockEntity blockEntity) {
        super(menuType, containerId);
        this.access = access;
        SlimeFeederBlockEntity blockEntity1 = (SlimeFeederBlockEntity) blockEntity;
        for(int column = 0; column < 3; column++){
            for(int row = 0; row < 9; row++){
                addSlot(new Slot(inventory, 9 + column + row * 9, 8 + column*18, 84 + row*18));
            }
        }
        for(int column = 0; column < 9; column++){
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
        addSlot(new SlimeFeederSlot(blockEntity1.items, 37, 62, 32));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        Slot slot = this.getSlot(i);
        ItemStack stack = ItemStack.EMPTY;
        ItemStack stack1 = slot.getItem();
        if(this.moveItemStackTo(stack1, 36, 37, false)){
            stack = stack1;
        }
        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(access, player, ResourcefulSlimes.SLIME_FEEDER_BLOCK.get());
    }


}
