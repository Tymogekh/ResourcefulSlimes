package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.ItemInit;
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
        basicItem(ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "item/resource_slime_ball"))
                .texture("layer0", "resourcefulslimes:item/resource_slime_ball");
        basicItem(ItemInit.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get());
        basicItem(ItemInit.SLIMEPEDIA.get());
        withExistingParent(ItemInit.RESOURCE_SLIME_BUCKET.getRegisteredName(), mcLoc("item/generated"))
                .texture("layer0", mcLoc("item/bucket"))
                .texture("layer1", ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "item/resource_slime_bucket"));
        for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
            withExistingParent(variant.getDropItem().toString(), ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "item/resource_slime_ball"));
            if(variant.isModded() && variant.getIngotOrGem().toString().contains("ingot")) {
                withExistingParent(variant.getIngotOrGem().toString(), ResourceLocation.withDefaultNamespace("item/iron_ingot"));
            }
        }

        withExistingParent(ItemInit.CERTUS_QUARTZ.getRegisteredName(), ResourceLocation.withDefaultNamespace("item/quartz"));
        simpleBlockItem(ResourcefulSlimes.SLIME_FEEDER_BLOCK.get());
        simpleBlockItem(ResourcefulSlimes.SLIME_SIEVE_BLOCK.get());
    }
}
