package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.SlimeSievingRecipe;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider {

    private final RecipeOutput output;

    public RecipeGenerator(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.output = output;
    }

    @Override
    protected void buildRecipes() {
        for (ResourceSlime.Variant variant : ResourceSlime.Variant.values()) {
            new SlimeSievingRecipeBuilder(Ingredient.of(variant.getDropItem()), variant.getIngotOrGem())
                    .unlockedBy("has_resource_slime_ball", this.has(Tags.Items.SLIME_BALLS))
                    .save(this.output);
        }
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput recipeOutput) {
            return new RecipeGenerator(provider, recipeOutput);
        }

        @Override
        public @NotNull String getName() {
            return ResourcefulSlimes.MOD_ID + "_recipe_provider";
        }
    }

    public static class SlimeSievingRecipeBuilder implements RecipeBuilder {

        private final Ingredient ingredient;
        private final ItemLike result;
        private String group;
        private final Map<String, Criterion<?>> criterion = new LinkedHashMap<>();

        public SlimeSievingRecipeBuilder(Ingredient ingredient, ItemLike result) {
            this.ingredient = ingredient;
            this.result = result;
        }

        @Override
        public @NotNull RecipeBuilder unlockedBy(@NotNull String s, @NotNull Criterion<?> criterion) {
            this.criterion.put(s, criterion);
            return this;
        }

        @Override
        public @NotNull RecipeBuilder group(@Nullable String s) {
            if (this.group == null) {
                this.group = s;
            }
            return this;
        }

        @Override
        public @NotNull Item getResult() {
            return this.result.asItem();
        }

        @Override
        public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> key) {
            Advancement.Builder advancement = recipeOutput.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(key))
                    .rewards(AdvancementRewards.Builder.recipe(key))
                    .requirements(AdvancementRequirements.Strategy.OR);
            SlimeSievingRecipe recipe = new SlimeSievingRecipe(this.ingredient, new ItemStack(this.result));
            recipeOutput.accept(key, recipe, advancement.build(key.location().withPrefix("recipes/")));
        }
    }
}
