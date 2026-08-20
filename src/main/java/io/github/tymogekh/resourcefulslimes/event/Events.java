package io.github.tymogekh.resourcefulslimes.event;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeFeederScreen;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeLabScreen;
import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeSieveScreen;
import io.github.tymogekh.resourcefulslimes.compat.jei.SlimesJEIPlugin;
import io.github.tymogekh.resourcefulslimes.datagen.*;
import io.github.tymogekh.resourcefulslimes.entity.gui.ResourceSlimeScreen;
import io.github.tymogekh.resourcefulslimes.entity.particle.ItemColoredParticle;
import io.github.tymogekh.resourcefulslimes.entity.renderer.ResourceSlimeRenderer;
import io.github.tymogekh.resourcefulslimes.init.BlockEntityInit;
import io.github.tymogekh.resourcefulslimes.init.MenuInit;
import io.github.tymogekh.resourcefulslimes.item.ResourceSlimeBucket;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = ResourcefulSlimes.MOD_ID)
public class Events {

    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuInit.SLIME_FEEDER_MENU.get(), SlimeFeederScreen::new);
        event.register(MenuInit.SLIME_SIEVE_MENU.get(), SlimeSieveScreen::new);
        event.register(MenuInit.RESOURCE_SLIME_MENU.get(), ResourceSlimeScreen::new);
        event.register(MenuInit.SLIME_LAB_MENU.get(), SlimeLabScreen::new);
    }

    @SubscribeEvent
    private static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        event.addProvider(new ModelGenerator(output));
        event.addProvider(new LangGeneration(output, "en_us"));
        event.addProvider(new ItemTagGeneration(output, event.getLookupProvider()));
        event.addProvider(new LootTableProvider(output, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(LootTableGenerator::new, LootContextParamSets.ENTITY)), event.getLookupProvider()));
        event.addProvider(new RecipeGenerator.Runner(output, event.getLookupProvider()));
    }

    @SubscribeEvent
    private static void layerDefinition(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ResourcefulSlimes.RESOURCE_SLIME.get(), ResourceSlimeRenderer::new);
    }

    @SubscribeEvent
    private static void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(ResourcefulSlimes.RESOURCE_SLIME.get(), Monster.createMonsterAttributes().build());
    }

    @SubscribeEvent
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, BlockEntityInit.SLIME_FEEDER_ENTITY.get(), (blockEntity, _) -> blockEntity.getHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, BlockEntityInit.SLIME_SIEVE_ENTITY.get(), (blockEntity, _) -> blockEntity.getHandler());
        event.registerBlockEntity(Capabilities.Item.BLOCK, BlockEntityInit.SLIME_LAB_BLOCK_ENTITY.get(), (blockEntity, _) -> blockEntity.getHandler());
    }

    @SubscribeEvent
    private static void itemTint(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "resource_slime_bucket"), ResourceSlimeBucket.VariantTint.MAP_CODEC);
    }

    @SubscribeEvent
    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ResourcefulSlimes.ITEM_COLORED_PARTICLE_TYPE.get(), new ItemColoredParticle.Provider());
    }

    @SubscribeEvent
    private static void onDataPackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(ResourcefulSlimes.SIEVING_RECIPE.get());
        event.sendRecipes(ResourcefulSlimes.SLIME_CREATION_RECIPE.get());
    }

    @SubscribeEvent
    private static void onRecipesReceived(RecipesReceivedEvent event) {
        SlimesJEIPlugin.SIEVING_RECIPES.clear();
        SlimesJEIPlugin.SIEVING_RECIPES.addAll(event.getRecipeMap().byType(ResourcefulSlimes.SIEVING_RECIPE.get()).stream().map(RecipeHolder::value).toList());
        SlimesJEIPlugin.SLIME_CREATION_RECIPES.clear();
        SlimesJEIPlugin.SLIME_CREATION_RECIPES.addAll(event.getRecipeMap().byType(ResourcefulSlimes.SLIME_CREATION_RECIPE.get()).stream().map(RecipeHolder::value).toList());
    }

    @SubscribeEvent
    private static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        SlimesJEIPlugin.SIEVING_RECIPES.clear();
        SlimesJEIPlugin.SLIME_CREATION_RECIPES.clear();
    }
}
