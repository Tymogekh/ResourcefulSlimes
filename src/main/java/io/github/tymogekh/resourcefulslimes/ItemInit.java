package io.github.tymogekh.resourcefulslimes;

import io.github.tymogekh.resourcefulslimes.item.ResourceSlimeBucket;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ItemInit {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.Items.createItems(ResourcefulSlimes.MOD_ID);

    public static final DeferredItem<@NotNull BlockItem> SLIME_FEEDER_ITEM = ITEMS.registerItem("slime_feeder", props -> new BlockItem(ResourcefulSlimes.SLIME_FEEDER_BLOCK.get(), props));
    public static final DeferredItem<@NotNull BlockItem> SLIME_SIEVE_ITEM = ITEMS.registerItem("slime_sieve", props -> new BlockItem(ResourcefulSlimes.SLIME_SIEVE_BLOCK.get(), props));
    public static final DeferredItem<@NotNull BlockItem> SLIME_LAB_ITEM = ITEMS.registerItem("slime_lab", props -> new BlockItem(ResourcefulSlimes.SLIME_LAB_BLOCK.get(), props));

    public static final DeferredItem<@NotNull SpawnEggItem> RANDOM_RESOURCE_SLIME_SPAWN_EGG = ITEMS.registerItem("random_resource_slime_spawn_egg",
            props -> new SpawnEggItem(props.spawnEgg(ResourcefulSlimes.RESOURCE_SLIME.get())));

    public static final DeferredItem<@NotNull ResourceSlimeBucket> RESOURCE_SLIME_BUCKET = ITEMS.registerItem("resource_slime_bucket", ResourceSlimeBucket::new);
    public static final DeferredItem<@NotNull Item> SLIMEPEDIA = ITEMS.registerItem("slimepedia", props -> new Item(props.stacksTo(1)));

    public static final DeferredItem<@NotNull Item> TIN_INGOT = ITEMS.registerItem("tin_ingot", Item::new);
    public static final DeferredItem<@NotNull Item> ALUMINIUM_INGOT = ITEMS.registerItem("aluminium_ingot", Item::new);
    public static final DeferredItem<@NotNull Item> URANIUM_INGOT = ITEMS.registerItem("uranium_ingot", Item::new);
    public static final DeferredItem<@NotNull Item> ZINC_INGOT = ITEMS.registerItem("zinc_ingot", Item::new);
    public static final DeferredItem<@NotNull Item> NICKEL_INGOT = ITEMS.registerItem("nickel_ingot", Item::new);
    public static final DeferredItem<@NotNull Item> OSMIUM_INGOT = ITEMS.registerItem("osmium_ingot", Item::new);
    public static final DeferredItem<@NotNull Item> LEAD_INGOT = ITEMS.registerItem("lead_ingot", Item::new);
    public static final DeferredItem<@NotNull Item> SILVER_INGOT = ITEMS.registerItem("silver_ingot", Item::new);
    public static final DeferredItem<@NotNull Item> CERTUS_QUARTZ = ITEMS.registerItem("certus_quartz", Item::new);

    public static final DeferredItem<@NotNull Item> RESOURCE_SLIME_BALL = ITEMS.registerItem("resource_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> COBBLESTONE_SLIME_BALL = ITEMS.registerItem("cobblestone_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> IRON_SLIME_BALL = ITEMS.registerItem("iron_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> GOLD_SLIME_BALL = ITEMS.registerItem("gold_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> COPPER_SLIME_BALL = ITEMS.registerItem("copper_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> NETHERITE_SLIME_BALL = ITEMS.registerItem("netherite_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> LAPIS_SLIME_BALL = ITEMS.registerItem("lapis_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> REDSTONE_SLIME_BALL = ITEMS.registerItem("redstone_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> EMERALD_SLIME_BALL = ITEMS.registerItem("emerald_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> DIAMOND_SLIME_BALL = ITEMS.registerItem("diamond_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> QUARTZ_SLIME_BALL = ITEMS.registerItem("quartz_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> COAL_SLIME_BALL = ITEMS.registerItem("coal_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> AMETHYST_SLIME_BALL = ITEMS.registerItem("amethyst_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> TIN_SLIME_BALL = ITEMS.registerItem("tin_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> ALUMINIUM_SLIME_BALL = ITEMS.registerItem("aluminium_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> URANIUM_SLIME_BALL = ITEMS.registerItem("uranium_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> ZINC_SLIME_BALL = ITEMS.registerItem("zinc_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> NICKEL_SLIME_BALL = ITEMS.registerItem("nickel_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> OSMIUM_SLIME_BALL = ITEMS.registerItem("osmium_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> LEAD_SLIME_BALL = ITEMS.registerItem("lead_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> SILVER_SLIME_BALL = ITEMS.registerItem("silver_slime_ball", Item::new);
    public static final DeferredItem<@NotNull Item> CERTUS_QUARTZ_SLIME_BALL = ITEMS.registerItem("certus_slime_ball", Item::new);
}
