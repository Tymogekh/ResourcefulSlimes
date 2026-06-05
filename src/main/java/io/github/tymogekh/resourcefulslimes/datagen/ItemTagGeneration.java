package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
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
        for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
            this.tag(Tags.Items.SLIME_BALLS).add(variant.getDropItem());
            if(variant.isModded()){
                this.tag(variant.getResourceTag()).add(variant.getIngotOrGem());
            }
        }
    }
}
