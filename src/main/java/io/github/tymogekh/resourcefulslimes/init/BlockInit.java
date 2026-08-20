package io.github.tymogekh.resourcefulslimes.init;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.block.SlimeFeederBlock;
import io.github.tymogekh.resourcefulslimes.block.SlimeLabBlock;
import io.github.tymogekh.resourcefulslimes.block.SlimeSieveBlock;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public final class BlockInit {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.Blocks.createBlocks(ResourcefulSlimes.MOD_ID);

    public static final DeferredBlock<@NotNull SlimeFeederBlock> SLIME_FEEDER_BLOCK = BLOCKS.registerBlock("slime_feeder", props -> new SlimeFeederBlock(props.sound(SoundType.STONE).strength(1.2F).explosionResistance(0.6F)));
    public static final DeferredBlock<@NotNull SlimeSieveBlock> SLIME_SIEVE_BLOCK = BLOCKS.registerBlock("slime_sieve", props -> new SlimeSieveBlock(props.sound(SoundType.STONE).strength(1.2F).explosionResistance(0.6F)));
    public static final DeferredBlock<@NotNull SlimeLabBlock> SLIME_LAB_BLOCK = BLOCKS.registerBlock("slime_lab", props -> new SlimeLabBlock(props.requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER)));
}
