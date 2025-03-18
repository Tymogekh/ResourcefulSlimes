package io.github.tymogekh.resourcefulslimes.blockentity.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ModifiedSlot extends Slot {

    private final Function<ItemStack, Boolean> func;

    public ModifiedSlot(Container container, int index, int xPosition, int yPosition, Function<ItemStack, Boolean> function) {
        super(container, index, xPosition, yPosition);
        this.func = function;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return this.func.apply(stack);
    }
}
