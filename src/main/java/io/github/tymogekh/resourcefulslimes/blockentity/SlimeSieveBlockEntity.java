package io.github.tymogekh.resourcefulslimes.blockentity;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeSieveMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.slot.StackHandlerModified;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SlimeSieveBlockEntity extends BaseContainerBlockEntity {

    private NonNullList<ItemStack> items;
    private final StackHandlerModified handler;
    private short sievingProgress = 0;
    public static HashMap<Item, Item> CRAFTING_RESULT = new HashMap<>();

    public SlimeSieveBlockEntity(BlockPos p_155077_, BlockState p_155078_) {
        super(ResourcefulSlimes.SLIME_SIEVE_ENTITY.get(), p_155077_, p_155078_);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        this.handler = new StackHandlerModified(this.items, stack -> CRAFTING_RESULT.containsKey(stack.getItem()));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider p_338309_) {
        super.loadAdditional(tag, p_338309_);
        this.sievingProgress = tag.getShort("SievingProgress");
        ContainerHelper.loadAllItems(tag, this.items, p_338309_);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider p_324280_) {
        super.saveAdditional(tag, p_324280_);
        tag.putShort("SievingProgress", this.sievingProgress);
        ContainerHelper.saveAllItems(tag, this.items, p_324280_);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putShort("SievingProgress", this.sievingProgress);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T t) {
        SlimeSieveBlockEntity slimeSieve = (SlimeSieveBlockEntity) t;
        ItemStack ingredient = slimeSieve.items.getFirst();
        ItemStack result = slimeSieve.items.get(1);
        if (!ingredient.isEmpty() && result.getCount() < 64) {
            if (CRAFTING_RESULT.containsKey(ingredient.getItem()) && (result.isEmpty() || result.is(CRAFTING_RESULT.get(ingredient.getItem())))) {
                if (slimeSieve.getSievingProgress() < 200) {
                    slimeSieve.sievingProgress += 1;
                } else {
                    slimeSieve.sievingProgress = 0;
                    if (result.isEmpty()) {
                        result = new ItemStack(CRAFTING_RESULT.get(ingredient.getItem()));
                    } else {
                        result.setCount(result.getCount() + 1);
                    }
                    ingredient.shrink(1);
                    slimeSieve.items.set(1, result);
                }
            }
        } else {
            slimeSieve.sievingProgress = 0;
        }
        slimeSieve.setChanged();
        level.sendBlockUpdated(pos, state, state, 0);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.slimeSieve");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory) {
        return new SlimeSieveMenu(i, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    public StackHandlerModified getHandler() {
        return this.handler;
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return slot == 0 && CRAFTING_RESULT.containsKey(stack.getItem()) && super.canPlaceItem(slot, stack);
    }

    public int getSievingProgress() {
        return this.sievingProgress;
    }

    static {
        for (ResourceSlime.Variant variant : ResourceSlime.Variant.values()) {
            CRAFTING_RESULT.put(variant.getDropItem(), variant.getIngotOrGem());
        }
    }
}
