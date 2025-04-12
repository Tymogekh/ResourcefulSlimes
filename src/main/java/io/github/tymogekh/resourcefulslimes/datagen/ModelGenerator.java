package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.block.SlimeFeederBlock;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import io.github.tymogekh.resourcefulslimes.item.ResourceSlimeBucket;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.data.PackOutput;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class ModelGenerator extends ModelProvider {

    public ModelGenerator(PackOutput output) {
        super(output, ResourcefulSlimes.MOD_ID);
    }

    @Override
    protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ResourcefulSlimes.SLIME_FEEDER_BLOCK.get())
                        .with(PropertyDispatch.initial(SlimeFeederBlock.FILLED)
                                .select(false, BlockModelGenerators.plainVariant(TexturedModel.CUBE_TOP_BOTTOM
                                        .updateTexture(mapping -> mapping.put(TextureSlot.TOP, mcLocation("block/oak_planks"))
                                                .put(TextureSlot.SIDE, modLocation("block/slime_feeder"))
                                                .put(TextureSlot.BOTTOM, mcLocation("block/cobblestone")))
                                                .create(ResourcefulSlimes.SLIME_FEEDER_BLOCK.get(), blockModels.modelOutput)))
                                .select(true, BlockModelGenerators.plainVariant(TexturedModel.CUBE_TOP_BOTTOM
                                        .updateTexture(mapping -> mapping.put(TextureSlot.TOP, mcLocation("block/oak_planks"))
                                                .put(TextureSlot.SIDE, modLocation("block/slime_feeder_filled"))
                                                .put(TextureSlot.BOTTOM, mcLocation("block/cobblestone")))
                                                .createWithSuffix(ResourcefulSlimes.SLIME_FEEDER_BLOCK.get(), "_filled", blockModels.modelOutput)))));
        blockModels.createTrivialBlock(ResourcefulSlimes.SLIME_SIEVE_BLOCK.get(), TexturedModel.CUBE_TOP_BOTTOM.updateTexture(mapping ->
                mapping.put(TextureSlot.SIDE, modLocation("block/slime_sieve"))
                .put(TextureSlot.TOP, modLocation("block/slime_sieve_top"))
                .put(TextureSlot.BOTTOM, mcLocation("block/cobblestone"))));
        itemModels.itemModelOutput.accept(ItemInit.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get(), ItemModelUtils.plainModel(itemModels.createFlatItemModel(ItemInit.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM)));
        itemModels.itemModelOutput.accept(ItemInit.SLIMEPEDIA.get(), ItemModelUtils.plainModel(itemModels.createFlatItemModel(ItemInit.SLIMEPEDIA.get(), ModelTemplates.FLAT_ITEM)));
        itemModels.itemModelOutput.accept(ItemInit.RESOURCE_SLIME_BUCKET.get(), ItemModelUtils.tintedModel(itemModels.generateLayeredItem(ItemInit.RESOURCE_SLIME_BUCKET.get(), mcLocation("item/bucket"), modLocation("item/resource_slime_bucket")),
                ItemModelUtils.constantTint(-1), new ResourceSlimeBucket.VariantTint(-1)));
        itemModels.itemModelOutput.accept(ItemInit.RESOURCE_SLIME_BALL.get(), ItemModelUtils.plainModel(itemModels.createFlatItemModel(ItemInit.RESOURCE_SLIME_BALL.get(), ModelTemplates.FLAT_ITEM)));
        itemModels.itemModelOutput.accept(ItemInit.CERTUS_QUARTZ.get(), ItemModelUtils.tintedModel(itemModels.createFlatItemModel(ItemInit.CERTUS_QUARTZ.get(), Items.QUARTZ, ModelTemplates.FLAT_ITEM), ItemModelUtils.constantTint(ARGB.opaque(ResourceSlime.Variant.CERTUS_QUARTZ.getColor()))));
        for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
            itemModels.itemModelOutput.accept(variant.getDropItem(), ItemModelUtils.tintedModel(itemModels.createFlatItemModel(variant.getDropItem(), ItemInit.RESOURCE_SLIME_BALL.get(), ModelTemplates.FLAT_ITEM), ItemModelUtils.constantTint(ARGB.opaque(variant.getColor()))));
            if(variant.isModded() && variant.getIngotOrGem().toString().contains("ingot")) {
                itemModels.itemModelOutput.accept(variant.getIngotOrGem(), ItemModelUtils.tintedModel(mcLocation("item/iron_ingot"), ItemModelUtils.constantTint(ARGB.opaque(variant.getColor()))));
            }
        }
    }
}
