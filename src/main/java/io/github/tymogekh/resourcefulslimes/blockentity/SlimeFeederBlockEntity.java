package io.github.tymogekh.resourcefulslimes.blockentity;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.block.SlimeFeederBlock;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeFeederMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.slot.StackHandlerModified;
import io.github.tymogekh.resourcefulslimes.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


public class SlimeFeederBlockEntity extends BaseContainerBlockEntity {

    private NonNullList<ItemStack> items;
    private final StackHandlerModified handler;
    private int nutrition;

    public SlimeFeederBlockEntity(BlockPos pos, BlockState blockState) {
        super(ResourcefulSlimes.SLIME_FEEDER_ENTITY.get(), pos, blockState);
        this.items = NonNullList.withSize(1, ItemStack.EMPTY);
        this.handler = new StackHandlerModified(this.items, stack -> stack.get(DataComponents.FOOD) != null);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.getInt("Nutrition").isPresent()) {
            this.nutrition = tag.getInt("Nutrition").get();
        }
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Nutrition", this.nutrition);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Nutrition", this.nutrition);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.slimeFeeder");
    }

    @Override
    @NotNull
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory) {
        return new SlimeFeederMenu(i, inventory, this);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T t) {
        SlimeFeederBlockEntity slimeFeeder = (SlimeFeederBlockEntity) t;
        int nutrition = slimeFeeder.nutrition;
        boolean flag = nutrition == 0;
        ItemStack stack = slimeFeeder.items.getFirst();
        if(!stack.isEmpty() && nutrition < Config.MAX_NUTRITION_STORAGE.get() && stack.get(DataComponents.FOOD) != null){
            int foodNutrition = Objects.requireNonNull(stack.get(DataComponents.FOOD)).nutrition();
            stack.shrink(1);
            if(nutrition + foodNutrition < Config.MAX_NUTRITION_STORAGE.get()){
                slimeFeeder.nutrition += foodNutrition;
            } else {
                slimeFeeder.nutrition = Config.MAX_NUTRITION_STORAGE.get();
            }
            if(flag){
                SlimeFeederBlock.changeBlockState(level, state, pos, true);
            }
            slimeFeeder.setChanged();
            level.sendBlockUpdated(pos, state, state, 0);
        }
    }

    public ItemStackHandler getHandler(){
        return this.handler;
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return stack.get(DataComponents.FOOD) != null && super.canPlaceItem(slot, stack);
    }

    public int getNutrition(){
        return this.nutrition;
    }

    public void shrinkNutrition(int amount){
        this.nutrition -= amount;
    }

    public void setNutrition(int nutrition){
        this.nutrition = nutrition;
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos p_394577_, @NotNull BlockState p_394161_) {
        Level level = this.getLevel();
        if (level != null) {
            Containers.dropContents(level, p_394577_, this);
            level.invalidateCapabilities(p_394577_);
        }
        super.preRemoveSideEffects(p_394577_, p_394161_);
    }
}
