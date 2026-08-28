package io.github.tymogekh.resourcefulslimes.init;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import io.github.tymogekh.resourcefulslimes.item.ResourceSlimeBucket;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public final class ItemInit {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.Items.createItems(ResourcefulSlimes.MOD_ID);

    public static final DeferredItem<@NotNull BlockItem> SLIME_FEEDER_ITEM = ITEMS.registerItem("slime_feeder", props -> new BlockItem(BlockInit.SLIME_FEEDER_BLOCK.get(), props));
    public static final DeferredItem<@NotNull BlockItem> SLIME_SIEVE_ITEM = ITEMS.registerItem("slime_sieve", props -> new BlockItem(BlockInit.SLIME_SIEVE_BLOCK.get(), props));
    public static final DeferredItem<@NotNull BlockItem> SLIME_LAB_ITEM = ITEMS.registerItem("slime_lab", props -> new BlockItem(BlockInit.SLIME_LAB_BLOCK.get(), props));

    public static final DeferredItem<@NotNull SpawnEggItem> RANDOM_RESOURCE_SLIME_SPAWN_EGG = ITEMS.registerItem("random_resource_slime_spawn_egg",
            props -> new SpawnEggItem(props.spawnEgg(ResourcefulSlimes.RESOURCE_SLIME.get())));

    public static final DeferredItem<@NotNull ResourceSlimeBucket> RESOURCE_SLIME_BUCKET = ITEMS.registerItem("resource_slime_bucket", ResourceSlimeBucket::new);
    public static final DeferredItem<@NotNull Item> SLIMEPEDIA = ITEMS.registerItem("slimepedia", props -> new Item(props.stacksTo(1)));

    public static final DeferredItem<@NotNull Item> RESOURCE_SLIME_BALL = ITEMS.registerItem("resource_slime_ball", props -> new Item(props.component(ResourcefulSlimes.RESOURCE_SLIME_VARIANT.get(), new ResourceSlime.Variant("empty", -1))));
}
