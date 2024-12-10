package io.github.tymogekh.resourcefulslimes;

import io.github.tymogekh.resourcefulslimes.block.SlimeFeederBlock;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeFeederBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeFeederMenu;
import io.github.tymogekh.resourcefulslimes.config.Config;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import io.github.tymogekh.resourcefulslimes.item.ResourceSlimeBucket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


@Mod(ResourcefulSlimes.MOD_ID)
public class ResourcefulSlimes {

    public static final String MOD_ID = "resourcefulslimes";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ResourceSlime>> RESOURCE_SLIME = ENTITY_TYPES.register("resource_slime",
            () -> EntityType.Builder.of(ResourceSlime::new, MobCategory.CREATURE).sized(0.52F, 0.52F).eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F).clientTrackingRange(10).build(MOD_ID + ":resource_slime"));

    public static final DeferredHolder<Item, DeferredSpawnEggItem> RANDOM_RESOURCE_SLIME_SPAWN_EGG = ITEMS.register("random_resource_slime_spawn_egg",
            () -> new DeferredSpawnEggItem(RESOURCE_SLIME, -1, -1,
                    new Item.Properties()));

    public static final DeferredHolder<Block, SlimeFeederBlock> SLIME_FEEDER_BLOCK = BLOCKS.register("slime_feeder", () -> new SlimeFeederBlock(
            BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.2F).explosionResistance(0.6F)));
    public static final DeferredHolder<Item, BlockItem> SLIME_FEEDER_ITEM = ITEMS.register("slime_feeder", () -> new BlockItem(SLIME_FEEDER_BLOCK.get(),
            new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SlimeFeederBlockEntity>> SLIME_FEEDER_ENTITY = BLOCK_ENTITY_TYPES.register("slime_feeder", () -> BlockEntityType.Builder.of(SlimeFeederBlockEntity::new, SLIME_FEEDER_BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<SlimeFeederMenu>> SLIME_FEEDER_MENU = MENUS.register("slime_feeder", () -> IMenuTypeExtension.create(SlimeFeederMenu::new));

    public static final DeferredHolder<Item, Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ALUMINIUM_INGOT = ITEMS.register("aluminium_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> URANIUM_INGOT = ITEMS.register("uranium_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> ZINC_INGOT = ITEMS.register("zinc_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> NICKEL_INGOT = ITEMS.register("nickel_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> OSMIUM_INGOT = ITEMS.register("osmium_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> LEAD_INGOT = ITEMS.register("lead_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> SILVER_INGOT = ITEMS.register("silver_ingot", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> CERTUS_QUARTZ = ITEMS.register("certus_quartz", () -> new Item(new Item.Properties()));

    public static final DeferredHolder<Item, ResourceSlimeBucket> RESOURCE_SLIME_BUCKET = ITEMS.register("resource_slime_bucket", ResourceSlimeBucket::new);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group." + MOD_ID + ".tab"))
            .icon(() -> RANDOM_RESOURCE_SLIME_SPAWN_EGG.get().getDefaultInstance())
            .displayItems(((itemDisplayParameters, output) -> {
                output.accept(RANDOM_RESOURCE_SLIME_SPAWN_EGG.get());
                output.accept(RESOURCE_SLIME_BUCKET.get());
                output.accept(SLIME_FEEDER_ITEM.get());
                for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
                    if(variant.isModded()) {
                        output.accept(variant.getDropItem());
                    }
                }
            })).build());

    public ResourcefulSlimes(IEventBus bus, ModContainer container){
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + "-common.toml");
        BLOCKS.register(bus);
        MENUS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
        ENTITY_TYPES.register(bus);
        ITEMS.register(bus);
        CREATIVE_TABS.register(bus);
    }
}
