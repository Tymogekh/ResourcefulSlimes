package io.github.tymogekh.resourcefulslimes.datagen;

import io.github.tymogekh.resourcefulslimes.entity.ResourceSlime;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import org.jetbrains.annotations.NotNull;

public class SlimeVariantGenerator implements RegistrySetBuilder.RegistryBootstrap<ResourceSlime.Variant> {
    public SlimeVariantGenerator() {}

    @Override
    public void run(@NotNull BootstrapContext<ResourceSlime.Variant> bootstrapContext) {
        bootstrapContext.register(ResourceSlime.Variant.EMPTY, new ResourceSlime.Variant("empty", -1));
        bootstrapContext.register(ResourceSlime.Variant.COBBLESTONE, new ResourceSlime.Variant("cobblestone", 0x888788));
        bootstrapContext.register(ResourceSlime.Variant.IRON, new ResourceSlime.Variant("iron", 0xd8d8d8));
        bootstrapContext.register(ResourceSlime.Variant.GOLD, new ResourceSlime.Variant("gold", 0xf6ea20));
        bootstrapContext.register(ResourceSlime.Variant.COPPER, new ResourceSlime.Variant("copper", 0xe17c52));
        bootstrapContext.register(ResourceSlime.Variant.NETHERITE, new ResourceSlime.Variant("netherite", 0x624740));
        bootstrapContext.register(ResourceSlime.Variant.LAPIS, new ResourceSlime.Variant("lapis", 0x425ec4));
        bootstrapContext.register(ResourceSlime.Variant.REDSTONE, new ResourceSlime.Variant("redstone", 0xa31803));
        bootstrapContext.register(ResourceSlime.Variant.EMERALD, new ResourceSlime.Variant("emerald", 0x45dc5e));
        bootstrapContext.register(ResourceSlime.Variant.DIAMOND, new ResourceSlime.Variant("diamond", 0x68ecd8));
        bootstrapContext.register(ResourceSlime.Variant.QUARTZ, new ResourceSlime.Variant("quartz", 0xe4dfd6));
        bootstrapContext.register(ResourceSlime.Variant.COAL, new ResourceSlime.Variant("coal", 0x2e2e2e));
        bootstrapContext.register(ResourceSlime.Variant.AMETHYST, new ResourceSlime.Variant("amethyst", 0x8d6bcd));
    }
}
