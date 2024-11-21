package io.github.tymogekh.resourcefulslimes.datagen;

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
        add(ResourcefulSlimes.RANDOM_RESOURCE_SLIME_SPAWN_EGG.get(), "Random Resource Slime Spawn Egg");
        add(ResourcefulSlimes.RESOURCE_SLIME_BUCKET.get(), "Resource Slime Bucket");
        add(ResourcefulSlimes.SLIME_FEEDER_BLOCK.get(), "Slime Feeder");
        add("item_group.resourcefulslimes.tab", "Resourceful Slimes");
        for(ResourceSlime.Variant variant : ResourceSlime.Variant.values()){
            if(variant.isModded()) {
                add(variant.getDropItem(), capitalizeAll(variant.getDropItem().toString().replaceFirst(ResourcefulSlimes.MOD_ID + ":", "")));
            }
        }
    }

    private static String capitalizeAll(String string){
        List<String> words = Arrays.stream(string.split("_")).map(StringUtils::capitalize).toList();
        return String.join(" ", words);
    }
}
