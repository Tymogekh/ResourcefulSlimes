package io.github.tymogekh.resourcefulslimes.blockentity.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class SlimeSievingSerializer implements RecipeSerializer<SlimeSievingRecipe> {

    public static final MapCodec<SlimeSievingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(SlimeSievingRecipe::getIngredient),
            ItemStack.CODEC.fieldOf("result").forGetter(SlimeSievingRecipe::getResult)
    ).apply(inst, SlimeSievingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlimeSievingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, SlimeSievingRecipe::getIngredient,
            ItemStack.STREAM_CODEC, SlimeSievingRecipe::getResult,
            SlimeSievingRecipe::new);

    @Override
    public @NotNull MapCodec<SlimeSievingRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, SlimeSievingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
