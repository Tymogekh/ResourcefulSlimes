package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.init.ItemInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ItemTagGeneration extends ItemTagsProvider {


    public ItemTagGeneration(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ResourcefulSlimes.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(Tags.Items.SLIME_BALLS)
                .add(ItemInit.RESOURCE_SLIME_BALL.getKey());

        tag(Tags.Items.BUCKETS_ENTITY_DRY)
                .add(ItemInit.RESOURCE_SLIME_BUCKET.getKey());
    }
}
