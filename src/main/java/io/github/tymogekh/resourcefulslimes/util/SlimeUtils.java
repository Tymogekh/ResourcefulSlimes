package io.github.tymogekh.resourcefulslimes.util;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import joptsimple.internal.Strings;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Locale;

public final class SlimeUtils {
    public static String capitalizeFirst(String lowerCaseString) {
        return lowerCaseString.substring(0, 1).toUpperCase(Locale.ENGLISH) + lowerCaseString.substring(1);
    }

    public static String capitalizeAll(String lowerCaseString, String separator) {
        String[] words = lowerCaseString.split(separator);
        for (int index = 0; index < words.length; index++) {
            String word = words[index];
            words[index] = capitalizeFirst(word);
        }
        return Strings.join(words, " ");
    }

    public static ResourceKey<ResourceSlime.Variant> createVariantResourceKey(String name) {
        return ResourceKey.create(ResourceSlime.Variant.REGISTRY_KEY, Identifier.fromNamespaceAndPath(ResourcefulSlimes.MOD_ID, name));
    }
}
