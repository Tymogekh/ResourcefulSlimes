package io.github.tymogekh.resourcefulslimes.blockentity.slot;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import java.util.function.Predicate;

public class MultiHandlerWithCheck extends ItemStacksResourceHandler {
    protected final Predicate<ItemResource> predicate;

    public MultiHandlerWithCheck(NonNullList<ItemStack> stacks, Predicate<ItemResource> predicate) {
        super(stacks);
        this.predicate = predicate;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return super.isValid(index, resource) && this.predicate.test(resource);
    }
}
