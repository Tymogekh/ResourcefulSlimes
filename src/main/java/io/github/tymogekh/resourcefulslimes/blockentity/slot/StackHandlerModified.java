package io.github.tymogekh.resourcefulslimes.blockentity.slot;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class StackHandlerModified extends ItemStackHandler {

    public StackHandlerModified(NonNullList<ItemStack> stacks){
        super(stacks);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.get(DataComponents.FOOD) != null;
    }
}
