package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.init.BlockInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BlockTagsGenerator extends BlockTagsProvider {
    public BlockTagsGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ResourcefulSlimes.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(BlockInit.SLIME_FEEDER_BLOCK.get())
                .add(BlockInit.SLIME_SIEVE_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(BlockInit.SLIME_FEEDER_BLOCK.get())
                .add(BlockInit.SLIME_SIEVE_BLOCK.get())
                .add(BlockInit.SLIME_LAB_BLOCK.get());
    }
}
