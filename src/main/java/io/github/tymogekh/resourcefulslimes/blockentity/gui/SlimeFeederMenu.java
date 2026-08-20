package io.github.tymogekh.resourcefulslimes.blockentity.gui;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeFeederBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.slot.ModifiedSlot;
import io.github.tymogekh.resourcefulslimes.init.BlockInit;
import io.github.tymogekh.resourcefulslimes.init.MenuInit;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class SlimeFeederMenu extends AbstractContainerMenu {

    private final SlimeFeederBlockEntity blockEntity;

    public SlimeFeederMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public SlimeFeederMenu(int containerId, Inventory inventory, BlockEntity blockEntity) {
        super(MenuInit.SLIME_FEEDER_MENU.get(), containerId);
        this.blockEntity = (SlimeFeederBlockEntity) blockEntity;
        addSlot(new ModifiedSlot(this.blockEntity, 0, 62, 31, stack -> stack.get(DataComponents.FOOD) != null));
        for(int column = 0; column < 3; column++){
            for(int row = 0; row < 9; row++){
                addSlot(new Slot(inventory, 9 + row + column * 9, 8 + row * 18, 84 + column * 18));
            }
        }
        for(int hotbarColumn = 0; hotbarColumn < 9; hotbarColumn++){
            addSlot(new Slot(inventory, hotbarColumn, 8 + hotbarColumn * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        Slot slot = this.getSlot(i);
        ItemStack stack = ItemStack.EMPTY;
        ItemStack stack1 = slot.getItem();
        if(slot.hasItem()){
            stack = stack1;
            if(i != 0){
                if(stack1.get(DataComponents.FOOD) == null || !this.moveItemStackTo(stack, 0, 1, false)){
                    return ItemStack.EMPTY;
                }
            } else if(!this.moveItemStackTo(stack, 1, 37, false)){
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
            slot.setChanged();
        }
        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.NULL, player, BlockInit.SLIME_FEEDER_BLOCK.get());
    }

    public int getNutrition(){
        return this.blockEntity.getNutrition();
    }
}
