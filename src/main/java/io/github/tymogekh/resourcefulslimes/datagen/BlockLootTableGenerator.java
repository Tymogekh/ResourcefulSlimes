package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.init.BlockInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class BlockLootTableGenerator extends BlockLootSubProvider {
    public BlockLootTableGenerator(HolderLookup.Provider registries) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(BlockInit.SLIME_FEEDER_BLOCK.get());
        dropSelf(BlockInit.SLIME_SIEVE_BLOCK.get());
        dropSelf(BlockInit.SLIME_LAB_BLOCK.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return List.of(BlockInit.SLIME_FEEDER_BLOCK.get(), BlockInit.SLIME_SIEVE_BLOCK.get(), BlockInit.SLIME_LAB_BLOCK.get());
    }
}
