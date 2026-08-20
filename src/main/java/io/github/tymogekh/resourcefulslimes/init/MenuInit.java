package io.github.tymogekh.resourcefulslimes.init;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeFeederMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeLabMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeSieveMenu;
import io.github.tymogekh.resourcefulslimes.entity.gui.ResourceSlimeMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public final class MenuInit {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, ResourcefulSlimes.MOD_ID);

    public static final DeferredHolder<MenuType<?>, @NotNull MenuType<@NotNull ResourceSlimeMenu>> RESOURCE_SLIME_MENU = MENUS.register("resource_slime_menu", () -> IMenuTypeExtension.create(ResourceSlimeMenu::new));
    public static final DeferredHolder<MenuType<?>, @NotNull MenuType<@NotNull SlimeFeederMenu>> SLIME_FEEDER_MENU = MENUS.register("slime_feeder", () -> IMenuTypeExtension.create(SlimeFeederMenu::new));
    public static final DeferredHolder<MenuType<?>, @NotNull MenuType<@NotNull SlimeSieveMenu>> SLIME_SIEVE_MENU = MENUS.register("slime_sieve", () -> IMenuTypeExtension.create(SlimeSieveMenu::new));
    public static final DeferredHolder<MenuType<?>, @NotNull MenuType<@NotNull SlimeLabMenu>> SLIME_LAB_MENU = MENUS.register("slime_lab", () -> IMenuTypeExtension.create(SlimeLabMenu::new));

}
