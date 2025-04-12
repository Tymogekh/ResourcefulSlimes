package io.github.tymogekh.resourcefulslimes;

import io.github.tymogekh.resourcefulslimes.item.ResourceSlimeBucket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ResourcefulSlimes.MOD_ID);

    public static final DeferredHolder<Item, BlockItem> SLIME_FEEDER_ITEM = ITEMS.register("slime_feeder", () -> new BlockItem(ResourcefulSlimes.SLIME_FEEDER_BLOCK.get(),
            new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "slime_feeder")))));
    public static final DeferredHolder<Item, BlockItem> SLIME_SIEVE_ITEM = ITEMS.register("slime_sieve", () -> new BlockItem(ResourcefulSlimes.SLIME_SIEVE_BLOCK.get(),
            new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "slime_sieve")))));

    public static final DeferredHolder<Item, SpawnEggItem> RANDOM_RESOURCE_SLIME_SPAWN_EGG = ITEMS.register("random_resource_slime_spawn_egg",
            () -> new SpawnEggItem(ResourcefulSlimes.RESOURCE_SLIME.get(),
                    new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "random_resource_slime_spawn_egg")))));

    public static final DeferredHolder<Item, ResourceSlimeBucket> RESOURCE_SLIME_BUCKET = ITEMS.register("resource_slime_bucket", ResourceSlimeBucket::new);
    public static final DeferredHolder<Item, Item> SLIMEPEDIA = ITEMS.register("slimepedia", () -> new Item(new Item.Properties()
            .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "slimepedia"))).stacksTo(1)));

    public static final DeferredHolder<Item, Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "tin_ingot")))));
    public static final DeferredHolder<Item, Item> ALUMINIUM_INGOT = ITEMS.register("aluminium_ingot", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "aluminium_ingot")))));
    public static final DeferredHolder<Item, Item> URANIUM_INGOT = ITEMS.register("uranium_ingot", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "uranium_ingot")))));
    public static final DeferredHolder<Item, Item> ZINC_INGOT = ITEMS.register("zinc_ingot", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "zinc_ingot")))));
    public static final DeferredHolder<Item, Item> NICKEL_INGOT = ITEMS.register("nickel_ingot", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "nickel_ingot")))));
    public static final DeferredHolder<Item, Item> OSMIUM_INGOT = ITEMS.register("osmium_ingot", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "osmium_ingot")))));
    public static final DeferredHolder<Item, Item> LEAD_INGOT = ITEMS.register("lead_ingot", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "lead_ingot")))));
    public static final DeferredHolder<Item, Item> SILVER_INGOT = ITEMS.register("silver_ingot", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "silver_ingot")))));
    public static final DeferredHolder<Item, Item> CERTUS_QUARTZ = ITEMS.register("certus_quartz", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "certus_quartz")))));

    public static final DeferredHolder<Item, Item> RESOURCE_SLIME_BALL = ITEMS.register("resource_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "resource_slime_ball")))));
    public static final DeferredHolder<Item, Item> COBBLESTONE_SLIME_BALL = ITEMS.register("cobblestone_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "cobblestone_slime_ball")))));
    public static final DeferredHolder<Item, Item> IRON_SLIME_BALL = ITEMS.register("iron_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "iron_slime_ball")))));
    public static final DeferredHolder<Item, Item> GOLD_SLIME_BALL = ITEMS.register("gold_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "gold_slime_ball")))));
    public static final DeferredHolder<Item, Item> COPPER_SLIME_BALL = ITEMS.register("copper_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "copper_slime_ball")))));
    public static final DeferredHolder<Item, Item> NETHERITE_SLIME_BALL = ITEMS.register("netherite_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "netherite_slime_ball")))));
    public static final DeferredHolder<Item, Item> LAPIS_SLIME_BALL = ITEMS.register("lapis_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "lapis_slime_ball")))));
    public static final DeferredHolder<Item, Item> REDSTONE_SLIME_BALL = ITEMS.register("redstone_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "redstone_slime_ball")))));
    public static final DeferredHolder<Item, Item> EMERALD_SLIME_BALL = ITEMS.register("emerald_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "emerald_slime_ball")))));
    public static final DeferredHolder<Item, Item> DIAMOND_SLIME_BALL = ITEMS.register("diamond_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "diamond_slime_ball")))));
    public static final DeferredHolder<Item, Item> QUARTZ_SLIME_BALL = ITEMS.register("quartz_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "quartz_slime_ball")))));
    public static final DeferredHolder<Item, Item> COAL_SLIME_BALL = ITEMS.register("coal_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "coal_slime_ball")))));
    public static final DeferredHolder<Item, Item> AMETHYST_SLIME_BALL = ITEMS.register("amethyst_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "amethyst_slime_ball")))));
    public static final DeferredHolder<Item, Item> TIN_SLIME_BALL = ITEMS.register("tin_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "tin_slime_ball")))));
    public static final DeferredHolder<Item, Item> ALUMINIUM_SLIME_BALL = ITEMS.register("aluminium_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "aluminium_slime_ball")))));
    public static final DeferredHolder<Item, Item> URANIUM_SLIME_BALL = ITEMS.register("uranium_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "uranium_slime_ball")))));
    public static final DeferredHolder<Item, Item> ZINC_SLIME_BALL = ITEMS.register("zinc_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "zinc_slime_ball")))));
    public static final DeferredHolder<Item, Item> NICKEL_SLIME_BALL = ITEMS.register("nickel_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "nickel_slime_ball")))));
    public static final DeferredHolder<Item, Item> OSMIUM_SLIME_BALL = ITEMS.register("osmium_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "osmium_slime_ball")))));
    public static final DeferredHolder<Item, Item> LEAD_SLIME_BALL = ITEMS.register("lead_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "lead_slime_ball")))));
    public static final DeferredHolder<Item, Item> SILVER_SLIME_BALL = ITEMS.register("silver_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "silver_slime_ball")))));
    public static final DeferredHolder<Item, Item> CERTUS_QUARTZ_SLIME_BALL = ITEMS.register("certus_slime_ball", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "certus_slime_ball")))));
}
