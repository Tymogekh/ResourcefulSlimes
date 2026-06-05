package io.github.tymogekh.resourcefulslimes.blockentity.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tymogekh.resourcefulslimes.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Sieving implements Recipe<Sieving.@NotNull SievingRecipeInput> {

    private final Ingredient ingredient;
    private final int ticks;
    private final float chance;
    private final ItemStackTemplate result;
    private PlacementInfo info;
    public static final MapCodec<Sieving> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(Sieving::getIngredient),
            ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter(Sieving::getTicks),
            ExtraCodecs.POSITIVE_FLOAT.fieldOf("chance").forGetter(Sieving::getChance),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(Sieving::getResult)
    ).apply(inst, Sieving::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Sieving> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, Sieving::getIngredient,
            ByteBufCodecs.INT, Sieving::getTicks,
            ByteBufCodecs.FLOAT, Sieving::getChance,
            ItemStackTemplate.STREAM_CODEC, Sieving::getResult,
            Sieving::new);

    public Sieving(Ingredient ingredient, int ticks, float chance, ItemStackTemplate result) {
        this.ingredient = ingredient;
        this.ticks = ticks;
        this.chance = chance;
        this.result = result;
    }

    @Override
    public @NotNull RecipeSerializer<? extends @NotNull Recipe<@NotNull SievingRecipeInput>> getSerializer() {
        return ResourcefulSlimes.SIEVING_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends @NotNull Recipe<@NotNull SievingRecipeInput>> getType() {
        return ResourcefulSlimes.SIEVING_RECIPE.get();
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
        return ResourcefulSlimes.SIEVING_CATEGORY.get();
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStackTemplate getResult() {
        return this.result;
    }

    public int getTicks() {return this.ticks;}

    public float getChance() {return this.chance;}

    @Override
    public boolean matches(@NotNull Sieving.SievingRecipeInput slimeSievingRecipeInput, @NotNull Level level) {
        return this.ingredient.test(slimeSievingRecipeInput.stack());
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SievingRecipeInput sievingRecipeInput) {
        return this.result.create().copy();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public @NotNull String group() {
        return "";
    }

    @Override
    public @NotNull List<RecipeDisplay> display() {
        return List.of(new SievingRecipeDisplay(
                this.ingredient.display(),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                new SlotDisplay.ItemSlotDisplay(ItemInit.SLIME_SIEVE_ITEM)
        ));
    }

    public record SievingRecipeDisplay(SlotDisplay input, SlotDisplay result, SlotDisplay station) implements RecipeDisplay {

        public static final MapCodec<SievingRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SlotDisplay.CODEC.fieldOf("ingredient").forGetter(SievingRecipeDisplay::input),
                SlotDisplay.CODEC.fieldOf("result").forGetter(SievingRecipeDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SievingRecipeDisplay::craftingStation)
        ).apply(inst, SievingRecipeDisplay::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SievingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC, SievingRecipeDisplay::input,
                SlotDisplay.STREAM_CODEC, SievingRecipeDisplay::result,
                SlotDisplay.STREAM_CODEC, SievingRecipeDisplay::craftingStation,
                SievingRecipeDisplay::new
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
        public @NotNull Type<? extends @NotNull RecipeDisplay> type() {
            return ResourcefulSlimes.SLIME_SIEVING_RECIPE_DISPLAY.get();
        }
    }

    public record SievingRecipeInput(ItemStack stack) implements RecipeInput {

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
