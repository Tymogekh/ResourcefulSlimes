package io.github.tymogekh.resourcefulslimes.blockentity;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.block.SlimeLabBlock;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeLabMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.SlimeCreation;
import io.github.tymogekh.resourcefulslimes.blockentity.slot.MultiHandlerWithCheck;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntitySpawnReason;
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
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class SlimeLabBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items;
    private final MultiHandlerWithCheck handler;
    private ResourceSlime.@Nullable Variant resourceSlimeVariant;
    private int cooldown = 0;

    public SlimeLabBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ResourcefulSlimes.SLIME_LAB_BLOCK_ENTITY.get(), worldPosition, blockState);
        this.items = NonNullList.withSize(3, ItemStack.EMPTY);
        this.handler = new MultiHandlerWithCheck(this.items, itemResource -> this.level != null && !this.level.isClientSide() &&
                ((ServerLevel) this.level).recipeAccess().recipeMap().byType(ResourcefulSlimes.SLIME_CREATION_RECIPE.get()).stream().anyMatch(recipe -> recipe.value().getIngredients().stream().anyMatch(ingredient -> itemResource.is(ingredient.ingredient().getValues()))));
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.cooldown = input.getIntOr("Cooldown", 0);
        input.getInt("Variant").ifPresent(id -> this.resourceSlimeVariant = ResourceSlime.Variant.byId(id));
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Cooldown", this.cooldown);
        if (this.resourceSlimeVariant != null) {
            output.putInt("Variant", this.resourceSlimeVariant.getId());
        }
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        if (this.resourceSlimeVariant != null) {
            tag.putInt("Variant", this.resourceSlimeVariant.getId());
        } else {
            tag.remove("Variant");
        }
        return tag;
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container." + ResourcefulSlimes.MOD_ID + ".slimeLab");
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
        return new SlimeLabMenu(i, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public @Nullable Packet<@NotNull ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ValueInput valueInput) {
        super.onDataPacket(net, valueInput);
        Optional<Integer> id = valueInput.getInt("Variant");
        if (id.isPresent()) {
            this.setResourceSlimeVariant(ResourceSlime.Variant.byId(id.get()));
        } else {
            this.setResourceSlimeVariant(null);
        }
    }

    @Override
    public void preRemoveSideEffects(@NotNull BlockPos pos, @NotNull BlockState state) {
        Level level = this.getLevel();
        if (level != null) {
            Containers.dropContents(level, pos, this);
            level.invalidateCapabilities(pos);
        }
        super.preRemoveSideEffects(pos, state);
    }

    public ResourceSlime.@Nullable Variant getResourceSlimeVariant() {
        return this.resourceSlimeVariant;
    }

    public void setResourceSlimeVariant(ResourceSlime.@Nullable Variant resourceSlimeVariant) {
        this.resourceSlimeVariant = resourceSlimeVariant;
    }

    public MultiHandlerWithCheck getHandler() {
        return this.handler;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T t) {
        if (!level.isClientSide()) {
            SlimeLabBlockEntity blockEntity = (SlimeLabBlockEntity) t;
            Optional<RecipeHolder<@NotNull SlimeCreation>> recipe = Optional.empty();
            if (blockEntity.cooldown <= 0) {
                recipe = ((ServerLevel) level).recipeAccess().getRecipeFor(ResourcefulSlimes.SLIME_CREATION_RECIPE.get(), new SlimeCreation.SlimeCreationInput(blockEntity.getItems()), level);
                if (recipe.isPresent()) {
                    blockEntity.setResourceSlimeVariant(recipe.get().value().getOutputVariant());
                    blockEntity.cooldown = 20;
                } else {
                    blockEntity.setResourceSlimeVariant(null);
                }
                blockEntity.setChanged();
                level.sendBlockUpdated(pos, state, state, SlimeLabBlock.UPDATE_ALL);
            } else {
                blockEntity.cooldown--;
            }
            if (blockEntity.getResourceSlimeVariant() != null && level.hasNeighborSignal(pos) && recipe.isPresent()) {
                recipe.get().value().assemble(new SlimeCreation.SlimeCreationInput(blockEntity.getItems()));
                ResourceSlime slime = ResourcefulSlimes.RESOURCE_SLIME.get().create(level, EntitySpawnReason.TRIGGERED);
                if (slime != null) {
                    BlockPos spawnPos = pos.relative(state.getValue(SlimeLabBlock.FACING), 1);
                    slime.setVariant(blockEntity.getResourceSlimeVariant());
                    slime.setSize(1, true);
                    slime.setPos(spawnPos.getX() + 0.5F, spawnPos.getY() + 0.5F, spawnPos.getZ() + 0.5F);
                    level.addFreshEntity(slime);
                    blockEntity.setResourceSlimeVariant(null);
                    blockEntity.cooldown = 20;
                    blockEntity.setChanged();
                    level.sendBlockUpdated(pos, state, state, SlimeLabBlock.UPDATE_ALL);
                }
            }
        }
    }
}
