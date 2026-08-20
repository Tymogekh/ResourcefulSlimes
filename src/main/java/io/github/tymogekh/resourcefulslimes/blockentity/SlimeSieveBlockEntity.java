package io.github.tymogekh.resourcefulslimes.blockentity;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeSieveMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.Sieving;
import io.github.tymogekh.resourcefulslimes.blockentity.slot.MultiHandlerWithCheck;
import io.github.tymogekh.resourcefulslimes.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class SlimeSieveBlockEntity extends BaseContainerBlockEntity {

    private NonNullList<ItemStack> items;
    private final MultiHandlerWithCheck handler;
    private int sievingProgress = 0;
    private int cooldown = 0;

    public SlimeSieveBlockEntity(BlockPos p_155077_, BlockState p_155078_) {
        super(BlockEntityInit.SLIME_SIEVE_ENTITY.get(), p_155077_, p_155078_);
        this.items = NonNullList.withSize(2, ItemStack.EMPTY);
        this.handler = new MultiHandlerWithCheck(this.items, stack -> this.level != null && !this.level.isClientSide() &&
                ((ServerLevel) this.level).recipeAccess().recipeMap().byType(ResourcefulSlimes.SIEVING_RECIPE.get()).stream().anyMatch(rec -> rec.value().getIngredient().test(stack.toStack())));
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput valueInput) {
        super.loadAdditional(valueInput);
        this.sievingProgress = valueInput.getIntOr("SievingProgress", 0);
        this.cooldown = valueInput.getIntOr("Cooldown", 0);
        ContainerHelper.loadAllItems(valueInput, this.items);
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput valueOutput) {
        super.saveAdditional(valueOutput);
        valueOutput.putInt("SievingProgress", this.sievingProgress);
        valueOutput.putInt("Cooldown", this.cooldown);
        ContainerHelper.saveAllItems(valueOutput, this.items);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("SievingProgress", this.sievingProgress);
        return tag;
    }

    @Override
    public @Nullable Packet<@NotNull ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T t) {
        SlimeSieveBlockEntity slimeSieve = (SlimeSieveBlockEntity) t;
        if (slimeSieve.cooldown <= 0) {
            ItemStack ingredient = slimeSieve.items.getFirst();
            ItemStack result = slimeSieve.items.get(1);
            Optional<RecipeHolder<@NotNull Sieving>> optional = Optional.empty();
            if (slimeSieve.level != null && !slimeSieve.level.isClientSide()) {
                optional = ((ServerLevel) slimeSieve.level).recipeAccess().getRecipeFor(ResourcefulSlimes.SIEVING_RECIPE.get(), new Sieving.SievingRecipeInput(ingredient), Objects.requireNonNull(slimeSieve.getLevel()));
            }
            if (optional.isPresent()) {
                Sieving recipe = optional.get().value();
                ItemStack expectedResult = recipe.getResult().create().copy();
                if (!ingredient.isEmpty() && result.getCount() < 64 && result.isEmpty() || result.is(expectedResult.getItem())) {
                    if (slimeSieve.getSievingProgress() < recipe.getTicks()) {
                        slimeSieve.sievingProgress += 1;
                        if (slimeSieve.getSievingProgress() % 10 == 0) {
                            level.playSound(null, pos, SoundEvents.SLIME_BLOCK_STEP, SoundSource.BLOCKS, 0.5F, 1.0F);
                        }
                    } else {
                        slimeSieve.sievingProgress = 0;
                        if (result.isEmpty()) {
                            result = expectedResult.copy();
                        } else {
                            result.setCount(result.getCount() + 1);
                        }
                        ingredient.shrink(1);
                        if (recipe.getChance() >= Math.random()) {
                            slimeSieve.items.set(1, result);
                        }
                        level.playSound(null, pos, SoundEvents.SLIME_BLOCK_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
            } else {
                slimeSieve.sievingProgress = 0;
                slimeSieve.cooldown = 20;
            }
        } else {
            slimeSieve.cooldown--;
        }
        slimeSieve.setChanged();
        level.sendBlockUpdated(pos, state, state, 0);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container." + ResourcefulSlimes.MOD_ID + ".slimeSieve");
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

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return slot == 0 && this.level != null && !this.level.isClientSide() && ((ServerLevel) this.level).recipeAccess().recipeMap().byType(ResourcefulSlimes.SIEVING_RECIPE.get()).stream().anyMatch(rec -> rec.value().getIngredient().test(stack)) && super.canPlaceItem(slot, stack);
    }

    public int getSievingProgress() {
        return this.sievingProgress;
    }

    public MultiHandlerWithCheck getHandler() {
        return this.handler;
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
