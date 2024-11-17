package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SlimePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Stream;


public class LootTableGenerator extends EntityLootSubProvider {

    public LootTableGenerator(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        this.add(ResourcefulSlimes.RESOURCE_SLIME.get(), LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        LootItem.lootTableItem(Items.SLIME_BALL)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(-2.0F, 1.0F)))
                                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))
                                                .when(this.killedByFrog().invert())
                                                .when(
                                                        LootItemEntityPropertyCondition.hasProperties(
                                                                LootContext.EntityTarget.THIS,
                                                                EntityPredicate.Builder.entity().subPredicate(SlimePredicate.sized(MinMaxBounds.Ints.atLeast(2)))
                                                        )
                                                )
                                )
                )
        );
    }

    @Override
    protected @NotNull Stream<EntityType<?>> getKnownEntityTypes() {
        EntityType<?>[] entityTypes = {ResourcefulSlimes.RESOURCE_SLIME.get()};
        return Arrays.stream(entityTypes);
    }
}
