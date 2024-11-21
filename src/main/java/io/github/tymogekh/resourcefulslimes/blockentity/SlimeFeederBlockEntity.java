package io.github.tymogekh.resourcefulslimes.blockentity;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.menu.SlimeFeederMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlimeFeederBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler items = new ItemStackHandler(1);
    public int nutrition = 0;

    public SlimeFeederBlockEntity(BlockPos pos, BlockState blockState) {
        super(ResourcefulSlimes.SLIME_FEEDER_ENTITY.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Nutrition", this.nutrition);
        tag.put("Inventory", this.items.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.nutrition = tag.getInt("Nutrition");
        if(tag.contains("Inventory")){
            this.items.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.slimeFeeder");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
        return new SlimeFeederMenu(ResourcefulSlimes.SLIME_FEEDER_MENU.get(), i, inventory, ContainerLevelAccess.NULL, this);
    }
}
