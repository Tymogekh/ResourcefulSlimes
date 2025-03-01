package io.github.tymogekh.resourcefulslimes.blockentity.slot;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class StackHandlerModified extends ItemStackHandler {

    private final Function<ItemStack, Boolean> FUNC;

    public StackHandlerModified(NonNullList<ItemStack> stacks, Function<ItemStack, Boolean> function){
        super(stacks);
        this.FUNC = function;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return slot == 0 && this.FUNC.apply(stack);
    }
}
