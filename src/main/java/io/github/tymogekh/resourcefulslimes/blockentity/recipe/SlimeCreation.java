package io.github.tymogekh.resourcefulslimes.blockentity.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tymogekh.resourcefulslimes.init.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SlimeCreation implements Recipe<SlimeCreation.@NotNull SlimeCreationInput> {
    public static final MapCodec<SlimeCreation> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            SizedIngredient.NESTED_CODEC.listOf().fieldOf("ingredients").forGetter(SlimeCreation::getIngredients),
            RegistryFileCodec.create(ResourceSlime.Variant.REGISTRY_KEY, ResourceSlime.Variant.CODEC).fieldOf("output_variant").forGetter(SlimeCreation::getOutputVariantHolder)
    ).apply(inst, SlimeCreation::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SlimeCreation> STREAM_CODEC = StreamCodec.composite(
            SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), SlimeCreation::getIngredients, ResourceSlime.Variant.STREAM_CODEC, SlimeCreation::getOutputVariantHolder, SlimeCreation::new
    );
    private PlacementInfo placementInfo;
    private final List<SizedIngredient> ingredients;
    private final Holder<ResourceSlime.Variant> outputVariant;

    public SlimeCreation(List<SizedIngredient> ingredients, Holder<ResourceSlime.Variant> outputVariant) {
        this.ingredients = ingredients;
        this.outputVariant = outputVariant;
    }

    @Override
    public boolean matches(SlimeCreationInput slimeCreationInput, @NotNull Level level) {
        ArrayList<ItemStack> list = new ArrayList<>(slimeCreationInput.items());
        list.removeIf(ItemStack::isEmpty);
        for (SizedIngredient ingredient : this.ingredients) {
            if (list.isEmpty()) {
                return false;
            }
            if (!list.removeIf(ingredient::test)) {
                return false;
            }
        }
        return list.isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(SlimeCreationInput slimeCreationInput) {
        for (ItemStack stack : slimeCreationInput.items()) {
            for (SizedIngredient ingredient : this.ingredients) {
                if (ingredient.test(stack)) {
                    stack.shrink(ingredient.count());
                }
            }
        }
        return ItemStack.EMPTY;
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
    public @NotNull RecipeSerializer<? extends @NotNull Recipe<@NotNull SlimeCreationInput>> getSerializer() {
        return ResourcefulSlimes.SLIME_CREATION_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends @NotNull Recipe<@NotNull SlimeCreationInput>> getType() {
        return ResourcefulSlimes.SLIME_CREATION_RECIPE.get();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.ingredients.stream().map(SizedIngredient::ingredient).toList());
        }
        return this.placementInfo;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return ResourcefulSlimes.SLIME_CREATION_CATEGORY.get();
    }

    @Override
    public @NotNull List<RecipeDisplay> display() {
        CompoundTag tag = new CompoundTag();
        tag.store("variant", ResourceSlime.Variant.CODEC, this.outputVariant.value());
        ItemStackTemplate resultDisplay = new ItemStackTemplate(ItemInit.RESOURCE_SLIME_BUCKET.get());
        resultDisplay.apply(DataComponentPatch.builder().set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(tag)).build());
        return List.of(new SlimeCreationDisplay(
                this.ingredients.stream().map(ingredient -> ingredient.ingredient().display()).toList(),
                new SlotDisplay.ItemStackSlotDisplay(resultDisplay),
                new SlotDisplay.ItemSlotDisplay(ItemInit.SLIME_LAB_ITEM.get())
        ));
    }

    public List<SizedIngredient> getIngredients() {
        return this.ingredients;
    }

    public ResourceSlime.Variant getOutputVariant() {
        return this.outputVariant.value();
    }

    public Holder<ResourceSlime.Variant> getOutputVariantHolder() {
        return this.outputVariant;
    }

    public record SlimeCreationDisplay(List<SlotDisplay> input, SlotDisplay resultVariant, SlotDisplay craftingStation) implements RecipeDisplay {
        public static final MapCodec<SlimeCreationDisplay> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                SlotDisplay.CODEC.listOf().fieldOf("input").forGetter(SlimeCreationDisplay::input),
                SlotDisplay.CODEC.fieldOf("result_variant").forGetter(SlimeCreationDisplay::result),
                SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SlimeCreationDisplay::craftingStation)
        ).apply(inst, SlimeCreationDisplay::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SlimeCreationDisplay> STREAM_CODEC = StreamCodec.composite(
                SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), SlimeCreationDisplay::input, SlotDisplay.STREAM_CODEC, SlimeCreationDisplay::result, SlotDisplay.STREAM_CODEC, SlimeCreationDisplay::craftingStation, SlimeCreationDisplay::new
        );

        @Override
        public @NotNull SlotDisplay craftingStation() {
            return this.craftingStation;
        }

        @Override
        public @NotNull SlotDisplay result() {
            return this.resultVariant;
        }

        @Override
        public @NotNull Type<? extends @NotNull RecipeDisplay> type() {
            return ResourcefulSlimes.SLIME_CREATION_DISPLAY.get();
        }
    }

    public record SlimeCreationInput(NonNullList<ItemStack> items) implements RecipeInput {

        @Override
        public @NotNull ItemStack getItem(int i) {
            return items.get(i);
        }

        @Override
        public int size() {
            return items.size();
        }
    }
}
