package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.init.BlockInit;
import io.github.tymogekh.resourcefulslimes.init.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class LangGeneration extends LanguageProvider {

    public LangGeneration(PackOutput output, String locale) {
        super(output, ResourcefulSlimes.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add(ResourcefulSlimes.RESOURCE_SLIME.get(), "Resource Slime");
        add(ItemInit.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get(), "Random Resource Slime Spawn Egg");
        add(ItemInit.RESOURCE_SLIME_BUCKET.get(), "Resource Slime Bucket");
        add(ItemInit.SLIMEPEDIA.get(), "Slimepedia");
        add(ItemInit.RESOURCE_SLIME_BALL.get(), "Resource Slime Ball");
        add(BlockInit.SLIME_FEEDER_BLOCK.get(), "Slime Feeder");
        add(ItemInit.SLIME_FEEDER_ITEM.get(), "Slime Feeder");
        add(BlockInit.SLIME_SIEVE_BLOCK.get(), "Slime Sieve");
        add(ItemInit.SLIME_SIEVE_ITEM.get(), "Slime Sieve");
        add(BlockInit.SLIME_LAB_BLOCK.get(), "Slime Lab");
        add(ItemInit.SLIME_LAB_ITEM.get(), "Slime Lab");
        add("item_group.resourcefulslimes.tab", "Resourceful Slimes");
        add("container." + ResourcefulSlimes.MOD_ID + ".slimeFeeder", "Slime Feeder");
        add("container." + ResourcefulSlimes.MOD_ID + ".slimeSieve", "Slime Sieve");
        add("container." + ResourcefulSlimes.MOD_ID + ".slimeLab", "Slime Lab");
        add("gui." + ResourcefulSlimes.MOD_ID + ".resourceSlime", "Resource Slime");
        add("recipe." + ResourcefulSlimes.MOD_ID + ".sieving", "Sieving");
        add("recipe." + ResourcefulSlimes.MOD_ID + ".slime_creation", "Slime Creation");
    }
}
