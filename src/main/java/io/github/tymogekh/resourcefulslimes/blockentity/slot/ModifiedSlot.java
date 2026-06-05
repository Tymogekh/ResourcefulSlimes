package io.github.tymogekh.resourcefulslimes.blockentity.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public class ModifiedSlot extends Slot {

    private final Predicate<ItemStack> func;

    public ModifiedSlot(BlockEntity blockEntity, int index, int xPosition, int yPosition, Predicate<ItemStack> function) {
        super((Container) blockEntity, index, xPosition, yPosition);
        this.func = function;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return this.func.test(stack);
    }
}
