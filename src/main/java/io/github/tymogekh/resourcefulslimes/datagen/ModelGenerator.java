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
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.util.ARGB;
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
                                        .updateTexture(mapping -> mapping.put(TextureSlot.TOP, new Material(mcLocation("block/oak_planks")))
                                                .put(TextureSlot.SIDE, new Material(modLocation("block/slime_feeder")))
                                                .put(TextureSlot.BOTTOM, new Material(mcLocation("block/cobblestone"))))
                                        .create(ResourcefulSlimes.SLIME_FEEDER_BLOCK.get(), blockModels.modelOutput)))
                                .select(true, BlockModelGenerators.plainVariant(TexturedModel.CUBE_TOP_BOTTOM
                                        .updateTexture(mapping -> mapping.put(TextureSlot.TOP, new Material(mcLocation("block/oak_planks")))
                                                .put(TextureSlot.SIDE, new Material(modLocation("block/slime_feeder_filled")))
                                                .put(TextureSlot.BOTTOM, new Material(mcLocation("block/cobblestone"))))
                                        .createWithSuffix(ResourcefulSlimes.SLIME_FEEDER_BLOCK.get(), "_filled", blockModels.modelOutput)))));
        blockModels.createTrivialBlock(ResourcefulSlimes.SLIME_SIEVE_BLOCK.get(), TexturedModel.CUBE_TOP_BOTTOM.updateTexture(mapping ->
                mapping.put(TextureSlot.SIDE, new Material(modLocation("block/slime_sieve")))
                    .put(TextureSlot.TOP, new Material(modLocation("block/slime_sieve_top")))
                    .put(TextureSlot.BOTTOM, new Material(mcLocation("block/cobblestone")))));
        blockModels.createHorizontallyRotatedBlock(ResourcefulSlimes.SLIME_LAB_BLOCK.get(), TexturedModel.ORIENTABLE.updateTexture(mapping ->
                mapping.put(TextureSlot.FRONT, new Material(modLocation("block/slime_lab_front")))
                        .put(TextureSlot.SIDE, new Material(modLocation("block/slime_lab_side")))
                        .put(TextureSlot.TOP, new Material(mcLocation("block/copper_block")))
                        .put(TextureSlot.BOTTOM, new Material(mcLocation("block/copper_block")))));
        itemModels.itemModelOutput.accept(ItemInit.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get(), ItemModelUtils.plainModel(itemModels.createFlatItemModel(ItemInit.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM)));
        itemModels.itemModelOutput.accept(ItemInit.SLIMEPEDIA.get(), ItemModelUtils.plainModel(itemModels.createFlatItemModel(ItemInit.SLIMEPEDIA.get(), ModelTemplates.FLAT_ITEM)));
        itemModels.itemModelOutput.accept(ItemInit.RESOURCE_SLIME_BUCKET.get(), ItemModelUtils.tintedModel(itemModels.generateLayeredItem(ItemInit.RESOURCE_SLIME_BUCKET.get(), new Material(mcLocation("item/bucket")), new Material(modLocation("item/resource_slime_bucket"))),
                ItemModelUtils.constantTint(-1), new ResourceSlimeBucket.VariantTint(-1)));
        itemModels.itemModelOutput.accept(ItemInit.RESOURCE_SLIME_BALL.get(), ItemModelUtils.plainModel(itemModels.createFlatItemModel(ItemInit.RESOURCE_SLIME_BALL.get(), ModelTemplates.FLAT_ITEM)));
        for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
            itemModels.itemModelOutput.accept(variant.getDropItem(), ItemModelUtils.tintedModel(itemModels.createFlatItemModel(variant.getDropItem(), ItemInit.RESOURCE_SLIME_BALL.get(), ModelTemplates.FLAT_ITEM), ItemModelUtils.constantTint(ARGB.opaque(variant.getColor()))));
            if(variant.isModded()) {
                itemModels.itemModelOutput.accept(variant.getIngotOrGem(), ItemModelUtils.plainModel(itemModels.createFlatItemModel(variant.getIngotOrGem(), ModelTemplates.FLAT_ITEM)));
            }
        }
    }
}
