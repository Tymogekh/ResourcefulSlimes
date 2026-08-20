package io.github.tymogekh.resourcefulslimes.init;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeFeederBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeLabBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeSieveBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public final class BlockEntityInit {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourcefulSlimes.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, @NotNull BlockEntityType<@NotNull SlimeFeederBlockEntity>> SLIME_FEEDER_ENTITY = BLOCK_ENTITY_TYPES.register("slime_feeder", () -> new BlockEntityType<>(SlimeFeederBlockEntity::new, BlockInit.SLIME_FEEDER_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, @NotNull BlockEntityType<@NotNull SlimeSieveBlockEntity>> SLIME_SIEVE_ENTITY = BLOCK_ENTITY_TYPES.register("slime_sieve", () -> new BlockEntityType<>(SlimeSieveBlockEntity::new, BlockInit.SLIME_SIEVE_BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, @NotNull BlockEntityType<@NotNull SlimeLabBlockEntity>> SLIME_LAB_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("slime_lab", () -> new BlockEntityType<>(SlimeLabBlockEntity::new, BlockInit.SLIME_LAB_BLOCK.get()));
}
