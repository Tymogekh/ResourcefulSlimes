package io.github.tymogekh.resourcefulslimes;

import io.github.tymogekh.resourcefulslimes.blockentity.gui.SlimeFeederScreen;
import io.github.tymogekh.resourcefulslimes.datagen.ItemModelGenerator;
import io.github.tymogekh.resourcefulslimes.datagen.ItemTagGeneration;
import io.github.tymogekh.resourcefulslimes.datagen.LangGeneration;
import io.github.tymogekh.resourcefulslimes.datagen.LootTableGenerator;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import io.github.tymogekh.resourcefulslimes.entity.gui.ResourceSlimeScreen;
import io.github.tymogekh.resourcefulslimes.entity.renderer.ResourceSlimeRenderer;
import io.github.tymogekh.resourcefulslimes.item.ResourceSlimeBucket;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = ResourcefulSlimes.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Events {

    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ResourcefulSlimes.SLIME_FEEDER_MENU.get(), SlimeFeederScreen::new);
        event.register(ResourcefulSlimes.RESOURCE_SLIME_MENU.get(), ResourceSlimeScreen::new);
    }

    @SubscribeEvent
    private static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        generator.addProvider(true, new ItemModelGenerator(output, fileHelper));
        generator.addProvider(true, new LangGeneration(output, "en_us"));
    }


    @SubscribeEvent
    private static void gatherServerData(GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        generator.addProvider(true, new ItemTagGeneration(output, event.getLookupProvider(), event.getExistingFileHelper()));
        generator.addProvider(true, new LootTableProvider(output, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(LootTableGenerator::new, LootContextParamSets.ENTITY)), event.getLookupProvider()));
    }

    @SubscribeEvent
    private static void registerSpawnRules(RegisterSpawnPlacementsEvent event) {
        event.register(ResourcefulSlimes.RESOURCE_SLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ResourceSlime::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
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
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ResourcefulSlimes.SLIME_FEEDER_ENTITY.get(), (blockEntity, side) -> blockEntity.getHandler());
    }

    @SubscribeEvent
    private static void itemTint(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(ResourceLocation.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, "resource_slime_bucket"), ResourceSlimeBucket.VariantTint.MAP_CODEC);
    }
}
