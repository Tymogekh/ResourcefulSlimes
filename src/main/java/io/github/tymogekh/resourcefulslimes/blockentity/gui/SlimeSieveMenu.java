package io.github.tymogekh.resourcefulslimes.blockentity.gui;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeSieveBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.slot.ModifiedSlot;
import io.github.tymogekh.resourcefulslimes.init.BlockInit;
import io.github.tymogekh.resourcefulslimes.init.MenuInit;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class SlimeSieveMenu extends AbstractContainerMenu {

    private final SlimeSieveBlockEntity blockEntity;

    public SlimeSieveMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public SlimeSieveMenu(int containerId, Inventory inventory, BlockEntity blockEntity) {
        super(MenuInit.SLIME_SIEVE_MENU.get(), containerId);
        this.blockEntity = (SlimeSieveBlockEntity) blockEntity;
        Level level = inventory.player.level();
        addSlot(new ModifiedSlot(this.blockEntity, 0, 55, 35,
                stack -> !level.isClientSide() && ((ServerLevel) level).recipeAccess().recipeMap().byType(ResourcefulSlimes.SIEVING_RECIPE.get()).stream().anyMatch(rec -> rec.value().getIngredient().test(stack))));
        addSlot(new ModifiedSlot(this.blockEntity, 1, 116, 35, stack -> false));
        for(int column = 0; column < 3; column++){
            for(int row = 0; row < 9; row++){
                addSlot(new Slot(inventory, 9 + row + column * 9, 8 + row*18, 84 + column*18));
            }
        }
        for(int hotbarColumn = 0; hotbarColumn < 9; hotbarColumn++){
            addSlot(new Slot(inventory, hotbarColumn, 8 + hotbarColumn * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        Slot slot = this.getSlot(i);
        ItemStack stack = slot.getItem();
        if(slot.hasItem()){
            if(i > 1){
                if(!this.moveItemStackTo(stack, 0, 1, false)){
                    return ItemStack.EMPTY;
                }
            } else if(!this.moveItemStackTo(stack, 2, 38, false)){
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
            slot.setChanged();
        } else {
            stack = ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.NULL, player, BlockInit.SLIME_SIEVE_BLOCK.get());
    }

    public int getSievingProgress() {
        return this.blockEntity.getSievingProgress();
    }
}
