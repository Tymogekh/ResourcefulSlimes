package io.github.tymogekh.resourcefulslimes.blockentity.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tymogekh.resourcefulslimes.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SlimeSievingRecipe implements Recipe<SlimeSievingRecipe.SlimeSievingRecipeInput> {

    private final Ingredient ingredient;
    private final ItemStack result;
    private PlacementInfo info;

    public SlimeSievingRecipe(Ingredient ingredient, ItemStack result) {
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<SlimeSievingRecipeInput>> getSerializer() {
        return ResourcefulSlimes.SLIME_SIEVING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<SlimeSievingRecipeInput>> getType() {
        return ResourcefulSlimes.SLIME_SIEVE_RECIPE.get();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (this.info == null) {
            List<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(this.ingredient);
            this.info = PlacementInfo.create(ingredients);
        }
        return this.info;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return ResourcefulSlimes.SLIME_SIEVE_CATEGORY.get();
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public boolean matches(@NotNull SlimeSievingRecipeInput slimeSievingRecipeInput, @NotNull Level level) {
        return this.ingredient.test(slimeSievingRecipeInput.stack());
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SlimeSievingRecipeInput slimeSievingRecipeInput, HolderLookup.@NotNull Provider provider) {
        return this.result.copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull List<RecipeDisplay> display() {
        return List.of(new SlimeSievingRecipeDisplay(
                this.ingredient.display(),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                new SlotDisplay.ItemSlotDisplay(ItemInit.SLIME_SIEVE_ITEM)
        ));
    }

    public record SlimeSievingRecipeDisplay(SlotDisplay input, SlotDisplay result, SlotDisplay station) implements RecipeDisplay {

        public static final MapCodec<SlimeSievingRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SlotDisplay.CODEC.fieldOf("ingredient").forGetter(SlimeSievingRecipeDisplay::input),
                SlotDisplay.CODEC.fieldOf("result").forGetter(SlimeSievingRecipeDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SlimeSievingRecipeDisplay::craftingStation)
        ).apply(inst, SlimeSievingRecipeDisplay::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SlimeSievingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC, SlimeSievingRecipeDisplay::input,
                SlotDisplay.STREAM_CODEC, SlimeSievingRecipeDisplay::result,
                SlotDisplay.STREAM_CODEC, SlimeSievingRecipeDisplay::craftingStation,
                SlimeSievingRecipeDisplay::new
        );

        @Override
        public @NotNull SlotDisplay craftingStation() {
            return this.station;
        }

        @Override
        public @NotNull SlotDisplay result() {
            return this.result;
        }

        @Override
        public @NotNull Type<? extends RecipeDisplay> type() {
            return ResourcefulSlimes.SLIME_SIEVING_RECIPE_DISPLAY.get();
        }
    }

    public record SlimeSievingRecipeInput(ItemStack stack) implements RecipeInput {

        @Override
        public @NotNull ItemStack getItem(int i) {
            return this.stack;
        }

        @Override
        public int size() {
            return 1;
        }
    }
}
