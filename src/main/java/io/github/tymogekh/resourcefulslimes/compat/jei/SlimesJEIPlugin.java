package io.github.tymogekh.resourcefulslimes.compat.jei;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeLabScreen;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeSieveScreen;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.Sieving;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.SlimeCreation;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class SlimesJEIPlugin implements IModPlugin {

    public static final List<Sieving> SIEVING_RECIPES = new ArrayList<>();
    public static final IRecipeType<@NotNull Sieving> SIEVING_TYPE = IRecipeType.create(Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "sieving"), Sieving.class);

    public static final List<SlimeCreation> SLIME_CREATION_RECIPES = new ArrayList<>();
    public static final IRecipeType<SlimeCreation> SLIME_CREATION_TYPE = IRecipeType.create(Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "slime_creation"), SlimeCreation.class);

    @Override
    public @NotNull Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new SievingCategory(guiHelper));
        registration.addRecipeCategories(new SlimeCreationCategory(guiHelper));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        registration.addRecipes(SIEVING_TYPE, SIEVING_RECIPES);
        registration.addRecipes(SLIME_CREATION_TYPE, SLIME_CREATION_RECIPES);
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(SlimeSieveScreen.class, 81, 34, 21, 17, SIEVING_TYPE);
        registration.addRecipeClickArea(SlimeLabScreen.class, 72, 34, 16, 20, SLIME_CREATION_TYPE);
    }
}
