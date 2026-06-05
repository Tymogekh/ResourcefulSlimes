package io.github.tymogekh.resourcefulslimes;

import io.github.tymogekh.resourcefulslimes.block.SlimeFeederBlock;
import io.github.tymogekh.resourcefulslimes.block.SlimeLabBlock;
import io.github.tymogekh.resourcefulslimes.block.SlimeSieveBlock;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeFeederBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeLabBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeSieveBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeFeederMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeLabMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeSieveMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.Sieving;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.SlimeCreation;
import io.github.tymogekh.resourcefulslimes.config.Config;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import io.github.tymogekh.resourcefulslimes.entity.gui.ResourceSlimeMenu;
import io.github.tymogekh.resourcefulslimes.entity.particle.ItemColoredParticleOption;
import io.github.tymogekh.resourcefulslimes.entity.particle.ItemColoredParticleType;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;


@Mod(ResourcefulSlimes.MOD_ID)
public class ResourcefulSlimes {

    public static final String MOD_ID = "resourcefulslimes";


    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.Entities.createEntities(MOD_ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.Blocks.createBlocks(MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredRegister<RecipeDisplay.Type<?>> RECIPE_DISPLAYS = DeferredRegister.create(Registries.RECIPE_DISPLAY, MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID);

    public static final DeferredHolder<EntityType<?>, @NotNull EntityType<@NotNull ResourceSlime>> RESOURCE_SLIME = ENTITY_TYPES.register("resource_slime",
            () -> EntityType.Builder.of(ResourceSlime::new, MobCategory.CREATURE).sized(0.52F, 0.52F).eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F).clientTrackingRange(10).build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "resource_slime"))));

    public static final DeferredHolder<MenuType<?>, @NotNull MenuType<@NotNull ResourceSlimeMenu>> RESOURCE_SLIME_MENU = MENUS.register("resource_slime_menu", () -> IMenuTypeExtension.create(ResourceSlimeMenu::new));

    public static final DeferredBlock<@NotNull SlimeFeederBlock> SLIME_FEEDER_BLOCK = BLOCKS.registerBlock("slime_feeder", props -> new SlimeFeederBlock(props.sound(SoundType.STONE).strength(1.2F).explosionResistance(0.6F)));
    public static final DeferredHolder<BlockEntityType<?>, @NotNull BlockEntityType<@NotNull SlimeFeederBlockEntity>> SLIME_FEEDER_ENTITY = BLOCK_ENTITY_TYPES.register("slime_feeder", () -> new BlockEntityType<>(SlimeFeederBlockEntity::new, SLIME_FEEDER_BLOCK.get()));
    public static final DeferredHolder<MenuType<?>, @NotNull MenuType<@NotNull SlimeFeederMenu>> SLIME_FEEDER_MENU = MENUS.register("slime_feeder", () -> IMenuTypeExtension.create(SlimeFeederMenu::new));

    public static final DeferredBlock<@NotNull SlimeSieveBlock> SLIME_SIEVE_BLOCK = BLOCKS.registerBlock("slime_sieve", props -> new SlimeSieveBlock(props.sound(SoundType.STONE).strength(1.2F).explosionResistance(0.6F)));
    public static final DeferredHolder<BlockEntityType<?>, @NotNull BlockEntityType<@NotNull SlimeSieveBlockEntity>> SLIME_SIEVE_ENTITY = BLOCK_ENTITY_TYPES.register("slime_sieve", () -> new BlockEntityType<>(SlimeSieveBlockEntity::new, SLIME_SIEVE_BLOCK.get()));
    public static final DeferredHolder<MenuType<?>, @NotNull MenuType<@NotNull SlimeSieveMenu>> SLIME_SIEVE_MENU = MENUS.register("slime_sieve", () -> IMenuTypeExtension.create(SlimeSieveMenu::new));

    public static final DeferredHolder<RecipeBookCategory, @NotNull RecipeBookCategory> SIEVING_CATEGORY = RECIPE_BOOK_CATEGORIES.register("sieving_misc", RecipeBookCategory::new);
    public static final DeferredHolder<RecipeType<?>, @NotNull RecipeType<@NotNull Sieving>> SIEVING_RECIPE = RECIPE_TYPES.register("sieving", _ -> RecipeType.simple(Identifier.fromNamespaceAndPath(MOD_ID, "sieving")));
    public static final DeferredHolder<RecipeSerializer<?>, @NotNull RecipeSerializer<@NotNull Sieving>> SIEVING_SERIALIZER = RECIPE_SERIALIZERS.register("sieving", () -> new RecipeSerializer<>(Sieving.CODEC, Sieving.STREAM_CODEC));
    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.@NotNull Type<Sieving.@NotNull SievingRecipeDisplay>> SLIME_SIEVING_RECIPE_DISPLAY = RECIPE_DISPLAYS.register("sieving",
            () -> new RecipeDisplay.Type<>(Sieving.SievingRecipeDisplay.CODEC, Sieving.SievingRecipeDisplay.STREAM_CODEC));

    public static final DeferredBlock<@NotNull SlimeLabBlock> SLIME_LAB_BLOCK = BLOCKS.registerBlock("slime_lab", props -> new SlimeLabBlock(props.requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER)));
    public static final DeferredHolder<BlockEntityType<?>, @NotNull BlockEntityType<@NotNull SlimeLabBlockEntity>> SLIME_LAB_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("slime_lab", () -> new BlockEntityType<>(SlimeLabBlockEntity::new, SLIME_LAB_BLOCK.get()));
    public static final DeferredHolder<MenuType<?>, @NotNull MenuType<@NotNull SlimeLabMenu>> SLIME_LAB_MENU = MENUS.register("slime_lab", () -> IMenuTypeExtension.create(SlimeLabMenu::new));

    public static final DeferredHolder<RecipeBookCategory, @NotNull RecipeBookCategory> SLIME_CREATION_CATEGORY = RECIPE_BOOK_CATEGORIES.register("slime_creation_misc", RecipeBookCategory::new);
    public static final DeferredHolder<RecipeType<?>, @NotNull RecipeType<@NotNull SlimeCreation>> SLIME_CREATION_RECIPE = RECIPE_TYPES.register("slime_creation", _ -> RecipeType.simple(Identifier.fromNamespaceAndPath(MOD_ID, "slime_creation")));
    public static final DeferredHolder<RecipeSerializer<?>, @NotNull RecipeSerializer<@NotNull SlimeCreation>> SLIME_CREATION_SERIALIZER = RECIPE_SERIALIZERS.register("slime_creation", () -> new RecipeSerializer<>(SlimeCreation.CODEC, SlimeCreation.STREAM_CODEC));
    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.@NotNull Type<SlimeCreation.@NotNull SlimeCreationDisplay>> SLIME_CREATION_DISPLAY = RECIPE_DISPLAYS.register("slime_creation",
            () -> new RecipeDisplay.Type<>(SlimeCreation.SlimeCreationDisplay.CODEC, SlimeCreation.SlimeCreationDisplay.STREAM_CODEC));

    public static final DeferredHolder<ParticleType<?>, @NotNull ParticleType<@NotNull ItemColoredParticleOption>> ITEM_COLORED_PARTICLE_TYPE = PARTICLE_TYPES.register("item_colored", () -> new ItemColoredParticleType(false));

    @SuppressWarnings("unused")
    public static final DeferredHolder<CreativeModeTab, @NotNull CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group." + MOD_ID + ".tab"))
            .icon(() -> ItemInit.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get().getDefaultInstance())
            .displayItems(((itemDisplayParameters, output) -> {
                output.accept(ItemInit.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get());
                output.accept(ItemInit.SLIME_FEEDER_ITEM.get());
                output.accept(ItemInit.SLIME_SIEVE_ITEM.get());
                output.accept(ItemInit.SLIME_LAB_ITEM.get());
                output.accept(ItemInit.SLIMEPEDIA.get());
                for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
                    CompoundTag tag = new CompoundTag();
                    tag.putInt("Variant", variant.getId());
                    ItemStack stack = new ItemStack(ItemInit.RESOURCE_SLIME_BUCKET.asItem());
                    stack.applyComponents(DataComponentMap.builder().set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(tag)).build());
                    output.accept(stack);
                }
                for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
                    output.accept(variant.getDropItem());
                }
                for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
                    if(variant.isModded()) {
                        output.accept(variant.getIngotOrGem());
                    }
                }
            })).build());

    public ResourcefulSlimes(IEventBus bus, ModContainer container){
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC, MOD_ID + "-common.toml");
        BLOCKS.register(bus);
        MENUS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
        PARTICLE_TYPES.register(bus);
        ENTITY_TYPES.register(bus);
        ItemInit.ITEMS.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        RECIPE_TYPES.register(bus);
        RECIPE_BOOK_CATEGORIES.register(bus);
        RECIPE_DISPLAYS.register(bus);
        CREATIVE_TABS.register(bus);
        NeoForge.EVENT_BUS.addListener(this::tooltipEvent);
    }

    private void tooltipEvent(ItemTooltipEvent event) {
        if (event.getItemStack().is(ItemInit.RESOURCE_SLIME_BUCKET) && event.getEntity() != null) {
            CustomData customData = event.getItemStack().get(DataComponents.BUCKET_ENTITY_DATA);
            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                if (tag.contains("Variant") && tag.getByte("Variant").isPresent()) {
                    event.getToolTip().addLast(ResourceSlime.Variant.byId(tag.getByte("Variant").get()).getDisplayName());
                }
            }
        }
    }
}
