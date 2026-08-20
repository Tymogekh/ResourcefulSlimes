package io.github.tymogekh.resourcefulslimes.compat.jei;

import io.github.tymogekh.resourcefulslimes.init.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.SlimeCreation;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class SlimeCreationCategory implements IRecipeCategory<SlimeCreation> {
    private final IDrawable icon;
    private final IDrawable redstoneSign;

    public SlimeCreationCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemLike(ItemInit.SLIME_LAB_ITEM.get());
        this.redstoneSign = helper.createDrawableItemLike(Items.REDSTONE_TORCH);
    }

    @Override
    public @NotNull IRecipeType<SlimeCreation> getRecipeType() {
        return SlimesJEIPlugin.SLIME_CREATION_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("recipe." + ResourcefulSlimes.MOD_ID + ".slime_creation");
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 166;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(@NotNull SlimeCreation recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "textures/gui/slime_lab_no_inv.png"),
                0, 0, 0, 0, 176, 83, 256, 256);
    }

    @Override
    public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, @NotNull SlimeCreation recipe, @NotNull IFocusGroup focuses) {
        IRecipeCategory.super.createRecipeExtras(builder, recipe, focuses);
        builder.addDrawable(this.redstoneSign, 0, 0);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull SlimeCreation recipe, @NotNull IFocusGroup focuses) {
        for (int slot = 0; slot < recipe.getIngredients().size(); slot++) {
            SizedIngredient ingredient = recipe.getIngredients().get(slot);
            if (!ingredient.ingredient().isEmpty()) {
                builder.addInputSlot(19 + slot * 18, 27).add(new ItemStack(ingredient.ingredient().getValues().get(0), ingredient.count()));
            }
        }
        CompoundTag tag = new CompoundTag();
        tag.putInt("Variant", recipe.getOutputVariant().getId());
        ItemStack itemStack = new ItemStack(ItemInit.RESOURCE_SLIME_BUCKET.get());
        itemStack.set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(tag));
        builder.addOutputSlot(117, 36).add(itemStack);
    }
}
