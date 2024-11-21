package io.github.tymogekh.resourcefulslimes.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> GROW_CHANCE_DECREASE;
    public static final ModConfigSpec.ConfigValue<Integer> ITEM_DROP_CHANCE_DECREASE;
    public static final ModConfigSpec.ConfigValue<Integer> NUTRITION_CAP;
    public static final ModConfigSpec.ConfigValue<Integer> FOOD_CONSUMPTION;
    public static final ModConfigSpec.ConfigValue<Integer> MAX_NUTRITION_STORAGE;

    static {
        BUILDER.push("Resource Slime Settings");
        GROW_CHANCE_DECREASE = BUILDER
                .comment("The higher this value, the smaller the chance for resource slimes to grow every tick.")
                .worldRestart()
                .defineInRange("grow_chance_decrease", 100000, 0, 1000000000);
        ITEM_DROP_CHANCE_DECREASE = BUILDER
                .comment("The higher this value, the smaller the chance for a resource slime to produce a resource every tick.")
                .worldRestart()
                .defineInRange("item_drop_chance_decrease", 10000, 0, 1000000000);
        NUTRITION_CAP = BUILDER
                .comment("Indicates maximum value resource slime's nutrition can achieve")
                .worldRestart()
                .defineInRange("nutrition_cap", 100, 0, 1000000000);
        FOOD_CONSUMPTION = BUILDER
                .comment("Indicates how much nutrition will a resource slime looses after producing a resource.")
                .worldRestart()
                .defineInRange("food_consumption", 10, 0, 1000000000);
        BUILDER.pop();
        BUILDER.push("Slime Feeder Settings");
        MAX_NUTRITION_STORAGE = BUILDER
                .comment("This value represents maximum nutrition capacity for a slime feeder.")
                .worldRestart()
                .defineInRange("nutrition_storage", 1000, 0, 1000000000);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
