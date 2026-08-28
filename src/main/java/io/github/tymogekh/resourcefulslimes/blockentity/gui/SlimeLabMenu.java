package io.github.tymogekh.resourcefulslimes.blockentity.gui;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeLabBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.slot.ModifiedSlot;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import io.github.tymogekh.resourcefulslimes.init.BlockInit;
import io.github.tymogekh.resourcefulslimes.init.MenuInit;
import net.minecraft.core.Holder;
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
import org.jetbrains.annotations.Nullable;

public class SlimeLabMenu extends AbstractContainerMenu {
    private final SlimeLabBlockEntity blockEntity;

    public SlimeLabMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf byteBuf) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(byteBuf.readBlockPos()));
    }

    public SlimeLabMenu(int containerId, Inventory inventory, BlockEntity blockEntity) {
        super(MenuInit.SLIME_LAB_MENU.get(), containerId);
        this.blockEntity = (SlimeLabBlockEntity) blockEntity;
        Level level = inventory.player.level();
        for (int slotIndex = 0; slotIndex < 3; slotIndex++) {
            addSlot(new ModifiedSlot(this.blockEntity, slotIndex, 19 + slotIndex * 18, 27, itemStack ->
                    !level.isClientSide() && ((ServerLevel) level).recipeAccess().recipeMap().byType(ResourcefulSlimes.SLIME_CREATION_RECIPE.get()).stream().anyMatch(recipe -> recipe.value().getIngredients().stream().anyMatch(ingredient -> itemStack.is(ingredient.ingredient().getValues())))));
        }
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
            if(i > 2){
                if(!this.moveItemStackTo(stack, 0, 3, false)){
                    return ItemStack.EMPTY;
                }
            } else if(!this.moveItemStackTo(stack, 3, 39, false)){
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
        return stillValid(ContainerLevelAccess.NULL, player, BlockInit.SLIME_LAB_BLOCK.get());
    }

    public @Nullable Holder<ResourceSlime.Variant> getResourceSlimeVariant() {
        return this.blockEntity.getResourceSlimeVariant();
    }

    public Level getLevel() {
        return this.blockEntity.getLevel();
    }
}
