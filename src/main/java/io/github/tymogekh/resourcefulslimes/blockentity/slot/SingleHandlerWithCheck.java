package io.github.tymogekh.resourcefulslimes.blockentity.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStackResourceHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SingleHandlerWithCheck extends ItemStackResourceHandler {
    private ItemStack stack;
    private final Predicate<ItemResource> predicate;

    public SingleHandlerWithCheck(ItemStack stack, Predicate<ItemResource> predicate) {
        super();
        this.stack = stack;
        this.predicate = predicate;
    }


    @Override
    protected @NotNull ItemStack getStack() {
        return this.stack;
    }

    @Override
    protected void setStack(@NotNull ItemStack itemStack) {
        this.stack = itemStack;
    }

    @Override
    protected boolean isValid(@NotNull ItemResource resource) {
        return super.isValid(resource) && predicate.test(resource);
    }
}
