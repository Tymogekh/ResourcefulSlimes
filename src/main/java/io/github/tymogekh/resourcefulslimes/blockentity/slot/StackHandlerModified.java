package io.github.tymogekh.resourcefulslimes.blockentity.slot;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class StackHandlerModified extends ItemStackHandler {

    private final Function<ItemStack, Boolean> func;

    public StackHandlerModified(NonNullList<ItemStack> stacks, Function<ItemStack, Boolean> function){
        super(stacks);
        this.func = function;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return slot == 0 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return slot == 0 && this.func.apply(stack);
    }
}
