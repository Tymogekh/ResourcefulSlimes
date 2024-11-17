package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ItemTagGeneration extends ItemTagsProvider {


    public ItemTagGeneration(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CompletableFuture.completedFuture(TagLookup.empty()), ResourcefulSlimes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
            if(variant.isModded()){
                this.tag(variant.getResourceTag()).add(variant.getDropItem());
            }
        }
    }
}
