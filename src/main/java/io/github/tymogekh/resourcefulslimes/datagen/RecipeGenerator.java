package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.init.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.Sieving;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.SlimeCreation;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
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

        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.COBBLESTONE), 400, 1.0F).requiresVariantOnSlimeBall(ResourceSlime.Variant.COBBLESTONE).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.IRON_INGOT), 400, 0.5F).requiresVariantOnSlimeBall(ResourceSlime.Variant.IRON).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.GOLD_INGOT), 400, 0.5F).requiresVariantOnSlimeBall(ResourceSlime.Variant.GOLD).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.COPPER_INGOT), 400, 0.5F).requiresVariantOnSlimeBall(ResourceSlime.Variant.COPPER).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.NETHERITE_SCRAP), 400, 0.1F).requiresVariantOnSlimeBall(ResourceSlime.Variant.NETHERITE).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.LAPIS_LAZULI), 400, 0.5F).requiresVariantOnSlimeBall(ResourceSlime.Variant.LAPIS).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.REDSTONE), 400, 0.5F).requiresVariantOnSlimeBall(ResourceSlime.Variant.REDSTONE).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.EMERALD), 400, 0.5F).requiresVariantOnSlimeBall(ResourceSlime.Variant.EMERALD).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.DIAMOND), 400, 0.25F).requiresVariantOnSlimeBall(ResourceSlime.Variant.DIAMOND).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.QUARTZ), 400, 1.0F).requiresVariantOnSlimeBall(ResourceSlime.Variant.QUARTZ).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.COAL), 400, 1.0F).requiresVariantOnSlimeBall(ResourceSlime.Variant.COAL).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);
        SlimeSievingRecipeBuilder.builder(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), new ItemStackTemplate(Items.AMETHYST_SHARD), 400, 0.5F).requiresVariantOnSlimeBall(ResourceSlime.Variant.AMETHYST).unlockedBy("has_resource_slime_ball", has(ItemInit.RESOURCE_SLIME_BALL.get())).save(this.output);

        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.COBBLESTONE).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.COBBLESTONE, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.IRON).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.IRON_INGOT, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.GOLD).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.GOLD_INGOT, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.COPPER).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.COPPER_INGOT, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.NETHERITE).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.NETHERITE_SCRAP, 64)).requires(Items.GOLD_INGOT, 32).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.LAPIS).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.LAPIS_BLOCK, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.REDSTONE).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.REDSTONE_BLOCK, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.EMERALD).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.EMERALD, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.DIAMOND).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.DIAMOND, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.QUARTZ).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.QUARTZ, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.COAL).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.COAL, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
        SlimeCreationRecipeBuilder.forVariant(this.registries.lookupOrThrow(ResourceSlime.Variant.REGISTRY_KEY), ResourceSlime.Variant.AMETHYST).requires(SizedIngredient.of(Items.SLIME_BLOCK, 1)).requires(SizedIngredient.of(Items.AMETHYST_SHARD, 64)).unlockedBy("has_slime_block", has(Items.SLIME_BLOCK)).save(this.output);
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

        private HolderGetter<ResourceSlime.Variant> variantHolderGetter;
        private Holder.Reference<ResourceSlime.Variant> variant;
        private int ticks;
        private float chance;
        private ItemStackTemplate result;
        private String group;
        private final RecipeUnlockAdvancementBuilder recipeUnlock = new RecipeUnlockAdvancementBuilder();

        private SlimeSievingRecipeBuilder() {}

        public static SlimeSievingRecipeBuilder builder(HolderGetter<ResourceSlime.Variant> variantHolderGetter, ItemStackTemplate result, int ticks, float outputChance) {
            SlimeSievingRecipeBuilder builder = new SlimeSievingRecipeBuilder();
            builder.variantHolderGetter = variantHolderGetter;
            builder.result = result;
            builder.ticks = ticks;
            builder.chance = outputChance;
            return builder;
        }

        public SlimeSievingRecipeBuilder requiresVariantOnSlimeBall(ResourceKey<ResourceSlime.Variant> variantKey) {
            this.variant = this.variantHolderGetter.getOrThrow(variantKey);
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
            Sieving recipe = new Sieving(this.variant, this.ticks, this.chance, this.result);
            recipeOutput.accept(key, recipe, this.recipeUnlock.build(recipeOutput, key, ""));
        }
    }

    public static class SlimeCreationRecipeBuilder implements RecipeBuilder {
        private Holder.Reference<ResourceSlime.Variant> resultVariant;
        private String group;
        private final List<SizedIngredient> ingredients = new ArrayList<>(3);
        private final RecipeUnlockAdvancementBuilder recipeUnlock = new RecipeUnlockAdvancementBuilder();

        private SlimeCreationRecipeBuilder() {}

        public static SlimeCreationRecipeBuilder forVariant(HolderGetter<ResourceSlime.Variant> variantHolderGetter, ResourceKey<ResourceSlime.Variant> variantKey) {
            SlimeCreationRecipeBuilder builder = new SlimeCreationRecipeBuilder();
            builder.resultVariant = variantHolderGetter.getOrThrow(variantKey);
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
            return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, this.resultVariant.value().name() + "_slime"));
        }

        @Override
        public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceKey<Recipe<?>> resourceKey) {
            SlimeCreation slimeCreation = new SlimeCreation(this.ingredients, this.resultVariant);
            recipeOutput.accept(resourceKey, slimeCreation, this.recipeUnlock.build(recipeOutput, resourceKey, ""));
        }
    }
}
