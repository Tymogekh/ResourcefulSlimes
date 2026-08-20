package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.init.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.Sieving;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.SlimeCreation;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RecipeGenerator extends RecipeProvider {

    private final RecipeOutput output;
    private final HolderLookup.Provider registries;

    public RecipeGenerator(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.output = output;
        this.registries = registries;
    }

    @Override
    protected void buildRecipes() {
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemInit.SLIME_FEEDER_ITEM.get()).define('P', ItemTags.PLANKS).define('I', Tags.Items.INGOTS_IRON).define('C', Tags.Items.COBBLESTONES).pattern("PPP").pattern("I I").pattern("CCC").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemInit.SLIME_SIEVE_ITEM.get()).define('P', ItemTags.PLANKS).define('I', Tags.Items.INGOTS_IRON).define('C', Tags.Items.COBBLESTONES).define('B', Items.IRON_BARS).pattern("PBP").pattern("I I").pattern("CCC").unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON)).save(this.output);
        ShapedRecipeBuilder.shaped(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemInit.SLIME_LAB_ITEM.get()).define('C', Tags.Items.INGOTS_COPPER).define('F', Items.FURNACE).define('D', Items.DISPENSER).pattern("CCC").pattern("CFD").pattern("CCC").unlockedBy("has_copper", has(Tags.Items.INGOTS_COPPER)).save(this.output);

        ShapelessRecipeBuilder.shapeless(this.registries.lookupOrThrow(Registries.ITEM), RecipeCategory.MISC, ItemInit.SLIMEPEDIA.get()).requires(Items.BOOK).requires(Tags.Items.SLIME_BALLS).unlockedBy("has_slime_ball", has(Tags.Items.SLIME_BALLS)).save(this.output);

        for (ResourceSlime.Variant variant : ResourceSlime.Variant.values()) {
            SlimeSievingRecipeBuilder.builder(new ItemStackTemplate(variant.getIngotOrGem()), 400, SlimeSievingRecipeBuilder.getChanceForVariant(variant)).requires(Ingredient.of(variant.getDropItem()))
                    .unlockedBy("has_" + variant.getSerializedName() + "_slime_ball", this.has(variant.getDropItem())).save(this.output);
            if (!variant.isModded()) {
                if (variant != ResourceSlime.Variant.COBBLESTONE && variant != ResourceSlime.Variant.NETHERITE && variant != ResourceSlime.Variant.LAPIS) {
                    boolean flag = BuiltInRegistries.ITEM.getKey(variant.getIngotOrGem()).getPath().contains("_ingot");
                    ItemLike additionalItem = flag ? ItemInit.COBBLESTONE_SLIME_BALL.get() : ItemInit.LAPIS_SLIME_BALL.get();
                    SlimeCreationRecipeBuilder.forVariant(variant).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(variant.getIngotOrGem(), 64)).requires(additionalItem, 32).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
                }
            } else {
                HolderSet<Item> item = this.registries.getOrThrow(variant.getResourceTag());
                SlimeCreationRecipeBuilder.forVariant(variant).requires(Items.SLIME_BLOCK, 1).requires(new SizedIngredient(Ingredient.of(item), 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
            }
        }
        SlimeCreationRecipeBuilder.forVariant(ResourceSlime.Variant.NETHERITE).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.NETHERITE_SCRAP, 64)).requires(ItemInit.GOLD_SLIME_BALL.get(), 32).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(ResourceSlime.Variant.COBBLESTONE).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.COBBLESTONE, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(ResourceSlime.Variant.LAPIS).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.LAPIS_BLOCK, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
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

        private Ingredient ingredient;
        private int ticks;
        private float chance;
        private ItemStackTemplate result;
        private String group;
        private final RecipeUnlockAdvancementBuilder recipeUnlock = new RecipeUnlockAdvancementBuilder();

        private SlimeSievingRecipeBuilder() {}

        public static SlimeSievingRecipeBuilder builder(ItemStackTemplate result, int ticks, float outputChance) {
            SlimeSievingRecipeBuilder builder = new SlimeSievingRecipeBuilder();
            builder.result = result;
            builder.ticks = ticks;
            builder.chance = outputChance;
            return builder;
        }

        public SlimeSievingRecipeBuilder requires(Ingredient ingredient) {
            this.ingredient = ingredient;
            return this;
        }

        @Override
        public @NotNull RecipeBuilder unlockedBy(@NotNull String s, @NotNull Criterion<?> criterion) {
            this.recipeUnlock.unlockedBy(s, criterion);
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
        public @NotNull ResourceKey<Recipe<?>> defaultId() {
            return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, RecipeBuilder.getDefaultRecipeId(this.result).identifier().getPath() + "_from_sieving"));
        }

        public ItemLike getResult() {
            return this.result.item().value();
        }

        @Override
        public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> key) {
            Sieving recipe = new Sieving(this.ingredient, this.ticks, this.chance, this.result);
            recipeOutput.accept(key, recipe, this.recipeUnlock.build(recipeOutput, key, ""));
        }

        public static float getChanceForVariant(ResourceSlime.Variant variant) {
            if (variant == ResourceSlime.Variant.COAL || variant == ResourceSlime.Variant.COBBLESTONE
            || variant == ResourceSlime.Variant.QUARTZ) {
                return 1.0F;
            } else if (variant == ResourceSlime.Variant.DIAMOND || variant == ResourceSlime.Variant.NETHERITE) {
                return 0.25F;
            }
            return 0.5F;
        }
    }

    public static class SlimeCreationRecipeBuilder implements RecipeBuilder {
        private ResourceSlime.Variant resultVariant;
        private String group;
        private final List<SizedIngredient> ingredients = new ArrayList<>(3);
        private final RecipeUnlockAdvancementBuilder recipeUnlock = new RecipeUnlockAdvancementBuilder();

        private SlimeCreationRecipeBuilder() {}

        public static SlimeCreationRecipeBuilder forVariant(ResourceSlime.Variant variant) {
            SlimeCreationRecipeBuilder builder = new SlimeCreationRecipeBuilder();
            builder.resultVariant = variant;
            return builder;
        }

        public SlimeCreationRecipeBuilder requires(ItemLike item, int count) {
            this.ingredients.add(SizedIngredient.of(item, count));
            return this;
        }

        public SlimeCreationRecipeBuilder requires(SizedIngredient ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }

        @Override
        public @NotNull RecipeBuilder unlockedBy(@NotNull String s, @NotNull Criterion<?> criterion) {
            this.recipeUnlock.unlockedBy(s, criterion);
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
        public @NotNull ResourceKey<Recipe<?>> defaultId() {
            return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, this.resultVariant.getSerializedName() + "_slime"));
        }

        @Override
        public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
            SlimeCreation slimeCreation = new SlimeCreation(this.ingredients, this.resultVariant);
            recipeOutput.accept(resourceKey, slimeCreation, this.recipeUnlock.build(recipeOutput, resourceKey, ""));
        }
    }
}
