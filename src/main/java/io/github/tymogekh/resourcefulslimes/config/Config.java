package io.github.tymogekh.resourcefulslimes.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> GROW_CHANCE_DECREASE;

    static {
        BUILDER.push("Resourceful Slimes Config");
        GROW_CHANCE_DECREASE = BUILDER
                .comment("The higher this value, the smaller the chance for resource slimes to grow every tick.")
                .worldRestart()
                .defineInRange("grow_chance_decrease", 100000, 0, 1000000000);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
