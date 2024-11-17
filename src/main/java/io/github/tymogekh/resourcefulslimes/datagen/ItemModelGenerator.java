package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider {

    public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ResourcefulSlimes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ResourcefulSlimes.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get());
        for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
            if(variant.isModded() && variant.getDropItem().toString().contains("ingot")) {
                withExistingParent(variant.getDropItem().toString(), ResourceLocation.withDefaultNamespace("item/iron_ingot"));
            }
        }
        withExistingParent(ResourcefulSlimes.CERTUS_QUARTZ.getRegisteredName(), ResourceLocation.withDefaultNamespace("item/quartz"));
    }
}
