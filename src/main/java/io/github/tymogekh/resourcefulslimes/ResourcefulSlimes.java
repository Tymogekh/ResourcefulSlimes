package io.github.tymogekh.resourcefulslimes;

import com.mojang.serialization.MapCodec;
import io.github.tymogekh.resourcefulslimes.block.SlimeFeederBlock;
import io.github.tymogekh.resourcefulslimes.block.SlimeSieveBlock;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeFeederBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeSieveBlockEntity;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeFeederMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeSieveMenu;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.SlimeSievingRecipe;
import io.github.tymogekh.resourcefulslimes.blockentity.recipe.SlimeSievingSerializer;
import io.github.tymogekh.resourcefulslimes.config.Config;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import io.github.tymogekh.resourcefulslimes.entity.gui.ResourceSlimeMenu;
import io.github.tymogekh.resourcefulslimes.entity.particle.ItemColoredParticleOption;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;


@Mod(ResourcefulSlimes.MOD_ID)
public class ResourcefulSlimes {

    public static final String MOD_ID = "resourcefulslimes";


    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister<RecipeBookCategory> RECIPE_BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, MOD_ID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);
    public static final DeferredRegister<RecipeDisplay.Type<?>> RECIPE_DISPLAYS = DeferredRegister.create(Registries.RECIPE_DISPLAY, MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ResourceSlime>> RESOURCE_SLIME = ENTITY_TYPES.register("resource_slime",
            () -> EntityType.Builder.of(ResourceSlime::new, MobCategory.CREATURE).sized(0.52F, 0.52F).eyeHeight(0.325F)
                    .spawnDimensionsScale(4.0F).clientTrackingRange(10).build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, "resource_slime"))));

    public static final DeferredHolder<MenuType<?>, MenuType<ResourceSlimeMenu>> RESOURCE_SLIME_MENU = MENUS.register("resource_slime_menu", () -> IMenuTypeExtension.create(ResourceSlimeMenu::new));

    public static final DeferredHolder<Block, SlimeFeederBlock> SLIME_FEEDER_BLOCK = BLOCKS.register("slime_feeder", () -> new SlimeFeederBlock(
            BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.2F).explosionResistance(0.6F).setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "slime_feeder")))));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SlimeFeederBlockEntity>> SLIME_FEEDER_ENTITY = BLOCK_ENTITY_TYPES.register("slime_feeder", () -> new BlockEntityType<>(SlimeFeederBlockEntity::new, SLIME_FEEDER_BLOCK.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<SlimeFeederMenu>> SLIME_FEEDER_MENU = MENUS.register("slime_feeder", () -> IMenuTypeExtension.create(SlimeFeederMenu::new));

    public static final DeferredHolder<Block, SlimeSieveBlock> SLIME_SIEVE_BLOCK = BLOCKS.register("slime_sieve", () -> new SlimeSieveBlock(
            BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.2F).explosionResistance(0.6F).setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, "slime_sieve")))));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SlimeSieveBlockEntity>> SLIME_SIEVE_ENTITY = BLOCK_ENTITY_TYPES.register("slime_sieve", () -> new BlockEntityType<>(SlimeSieveBlockEntity::new, SLIME_SIEVE_BLOCK.get()));
    public static final DeferredHolder<MenuType<?>, MenuType<SlimeSieveMenu>> SLIME_SIEVE_MENU = MENUS.register("slime_sieve", () -> IMenuTypeExtension.create(SlimeSieveMenu::new));

    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> SLIME_SIEVE_CATEGORY = RECIPE_BOOK_CATEGORIES.register("slime_sieve_misc", RecipeBookCategory::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<SlimeSievingRecipe>> SLIME_SIEVE_RECIPE = RECIPE_TYPES.register("slime_sieve_recipe", registry -> new RecipeType<>() {
        @Override
        public String toString() {
            return registry.toString();
        }
    });
    public static final DeferredHolder<RecipeSerializer<?>, SlimeSievingSerializer> SLIME_SIEVING_SERIALIZER = RECIPE_SERIALIZERS.register("slime_sieve_recipe", SlimeSievingSerializer::new);
    public static final DeferredHolder<RecipeDisplay.Type<?>, RecipeDisplay.Type<SlimeSievingRecipe.SlimeSievingRecipeDisplay>> SLIME_SIEVING_RECIPE_DISPLAY = RECIPE_DISPLAYS.register("slime_sieve_recipe",
            () -> new RecipeDisplay.Type<>(SlimeSievingRecipe.SlimeSievingRecipeDisplay.CODEC, SlimeSievingRecipe.SlimeSievingRecipeDisplay.STREAM_CODEC));

    public static final DeferredHolder<ParticleType<?>, ParticleType<ItemColoredParticleOption>> ITEM_COLORED_PARTICLE_TYPE = PARTICLE_TYPES.register("item_colored", () -> new ParticleType<>(false) {
        @Override
        public @NotNull MapCodec<ItemColoredParticleOption> codec() {
            return ItemColoredParticleOption.codec();
        }

        @Override
        public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ItemColoredParticleOption> streamCodec() {
            return ItemColoredParticleOption.streamCodec();
        }
    });

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group." + MOD_ID + ".tab"))
            .icon(() -> ItemInit.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get().getDefaultInstance())
            .displayItems(((itemDisplayParameters, output) -> {
                output.accept(ItemInit.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get());
                output.accept(ItemInit.SLIME_FEEDER_ITEM.get());
                output.accept(ItemInit.SLIME_SIEVE_ITEM.get());
                output.accept(ItemInit.SLIMEPEDIA.get());
                for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
                    CompoundTag tag = new CompoundTag();
                    tag.putByte("Variant", variant.getId());
                    ItemStack stack = new ItemStack(ItemInit.RESOURCE_SLIME_BUCKET);
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
