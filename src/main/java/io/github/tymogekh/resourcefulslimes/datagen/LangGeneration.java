package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.ItemInit;
import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

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
        add(ResourcefulSlimes.SLIME_FEEDER_BLOCK.get(), "Slime Feeder");
        add(ItemInit.SLIME_FEEDER_ITEM.get(), "Slime Feeder");
        add(ResourcefulSlimes.SLIME_SIEVE_BLOCK.get(), "Slime Sieve");
        add(ItemInit.SLIME_SIEVE_ITEM.get(), "Slime Sieve");
        add(ResourcefulSlimes.SLIME_LAB_BLOCK.get(), "Slime Lab");
        add(ItemInit.SLIME_LAB_ITEM.get(), "Slime Lab");
        add("item_group.resourcefulslimes.tab", "Resourceful Slimes");
        add("container." + ResourcefulSlimes.MOD_ID + ".slimeFeeder", "Slime Feeder");
        add("container." + ResourcefulSlimes.MOD_ID + ".slimeSieve", "Slime Sieve");
        add("container." + ResourcefulSlimes.MOD_ID + ".slimeLab", "Slime Lab");
        add("gui." + ResourcefulSlimes.MOD_ID + ".resourceSlime", "Resource Slime");
        add("recipe." + ResourcefulSlimes.MOD_ID + ".sieving", "Sieving");
        add("recipe." + ResourcefulSlimes.MOD_ID + ".slime_creation", "Slime Creation");
        for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
            String displayName = variant.getDisplayName().getString();
            add(variant.getDisplayName().getString(), "Variant: " + capitalizeAll(displayName.replaceFirst("entity.resourcefulslimes.resource_slime.variant.", "")));
            add(variant.getDropItem(), capitalizeAll(variant.getDropItem().toString().replaceFirst(ResourcefulSlimes.MOD_ID + ":", "")));
            if(variant.isModded()) {
                add(variant.getIngotOrGem(), capitalizeAll(variant.getIngotOrGem().toString().replaceFirst(ResourcefulSlimes.MOD_ID + ":", "")));
            }
        }
    }

    private static String capitalizeAll(String string){
        List<String> words = Arrays.stream(string.split("_")).map(StringUtils::capitalize).toList();
        return String.join(" ", words);
    }
}
