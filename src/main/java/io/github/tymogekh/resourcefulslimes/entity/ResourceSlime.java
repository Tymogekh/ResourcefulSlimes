package io.github.tymogekh.resourcefulslimes.entity;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.config.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;

public class ResourceSlime extends Slime{
    private static final EntityDataAccessor<Byte> RESOURCE = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> SATURATION = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.INT);
    private ParticleOptions particle;
    private int saturation = 0;

    public ResourceSlime(EntityType<? extends Slime> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RESOURCE, (byte) 0);
        builder.define(SATURATION, 0);
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Resource", this.getVariant().getId());
        compound.putInt("Saturation", this.saturation);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.saturation = compound.getInt("Saturation");
        this.setVariant(ResourceSlime.Variant.byId(compound.getInt("Resource")));
    }


    public void setVariant(Variant variant){
        this.entityData.set(RESOURCE, variant.getId());
    }

    public ResourceSlime.Variant getVariant(){
        return ResourceSlime.Variant.byId(this.entityData.get(RESOURCE));
    }

    @Override
    public void tick() {
        super.tick();
        int chance = this.random.nextInt(Config.GROW_CHANCE_DECREASE.get());
        if(this.getSize() <= 4 && chance == 0){
            this.setSize(this.getSize()+1, true);
        }
    }

    @Override
    protected @NotNull ParticleOptions getParticleType() {
        if(this.particle == null) {
            this.particle = new ItemParticleOption(ParticleTypes.ITEM, this.getVariant().getDropItem().getDefaultInstance());
        }
        return this.particle;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if(spawnType.equals(MobSpawnType.SPAWN_EGG)) {
            ArrayList<Variant> presentValues = presentValues();
            this.setVariant(presentValues.get(this.random.nextInt(presentValues.size()-1)));
        } else if(spawnType.equals(MobSpawnType.NATURAL)){
            Holder<Biome> holder = level.getBiome(this.blockPosition());
            if(holder.is(Tags.Biomes.IS_MOUNTAIN)){
                this.setVariant(Variant.IRON);
            } else if(holder.is(Tags.Biomes.IS_BADLANDS)) {
                this.setVariant(Variant.REDSTONE);
            } else if(holder.is(Tags.Biomes.IS_ICY)){
                this.setVariant(Variant.LAPIS);
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    public static ArrayList<Variant> presentValues(){
        ArrayList<Variant> list = new ArrayList<>();
        for(Variant variant : ResourceSlime.Variant.values()){
            Optional<HolderSet.Named<Item>> optional = BuiltInRegistries.ITEM.getTag(variant.getResourceTag());
            if((optional.isPresent() && optional.get().size() > 1) || !variant.isModded()){
                list.add(variant);
            }
        }
        return list;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        int i = this.getSize();
        Variant variant = this.getVariant();
        if (!this.level().isClientSide && i > 1 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = this.getDimensions(this.getPose()).width();
            float f1 = f / 2.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);
            ArrayList<Mob> children = new ArrayList<>();

            for(int l = 0; l < k; ++l) {
                float f2 = ((float)(l % 2) - 0.5F) * f1;
                float f3 = ((float)(l / 2) - 0.5F) * f1;
                ResourceSlime slime = (ResourceSlime) this.getType().create(this.level());
                if (slime != null) {
                    if (this.isPersistenceRequired()) {
                        slime.setPersistenceRequired();
                    }
                    slime.setCustomName(component);
                    slime.setNoAi(flag);
                    slime.setInvulnerable(this.isInvulnerable());
                    slime.setSize(j, true);
                    slime.setVariant(variant);
                    slime.moveTo(this.getX() + (double)f2, this.getY() + 0.5, this.getZ() + (double)f3, this.random.nextFloat() * 360.0F, 0.0F);
                    children.add(slime);
                }
            }

            if (!EventHooks.onMobSplit(this, children).isCanceled()) {
                Level var10001 = this.level();
                Objects.requireNonNull(var10001);
                children.forEach(var10001::addFreshEntity);
            }
        }
        if (reason == RemovalReason.KILLED || reason == RemovalReason.DISCARDED) {
            this.triggerOnDeathMobEffects(reason);
        }
        this.setRemoved(reason);
        this.brain.clearMemories();
    }

    @Override
    public void checkDespawn() {
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    protected boolean isDealsDamage() {
        return false;
    }


    public enum Variant implements StringRepresentable {
        IRON((byte) 0, Tags.Items.INGOTS_IRON, "iron", 0xd8d8d8, Items.IRON_INGOT, true),
        GOLD((byte) 1, Tags.Items.INGOTS_GOLD, "gold", 0xf6ea20, Items.GOLD_INGOT, true),
        COPPER((byte) 2, Tags.Items.INGOTS_COPPER, "copper", 0xe17c52, Items.COPPER_INGOT, true),
        NETHERITE((byte) 3, Tags.Items.ORES_NETHERITE_SCRAP, "netherite_scrap", 0x624740, Items.NETHERITE_SCRAP, true),
        LAPIS((byte) 4, Tags.Items.GEMS_LAPIS, "lapis_lazuli", 0x425ec4, Items.LAPIS_LAZULI, true),
        REDSTONE((byte) 5, Tags.Items.DUSTS_REDSTONE, "redstone", 0xa31803, Items.REDSTONE, true),
        EMERALD((byte) 6, Tags.Items.GEMS_EMERALD, "emerald", 0x45dc5e, Items.EMERALD, true),
        DIAMOND((byte) 7, Tags.Items.GEMS_DIAMOND, "diamond", 0x68ecd8, Items.DIAMOND, true),
        QUARTZ((byte) 8, Tags.Items.GEMS_QUARTZ, "quartz", 0xe4dfd6, Items.QUARTZ, true),
        COAL((byte) 9, Tags.Items.ORES_COAL, "coal", 0x2e2e2e, Items.COAL, true),
        AMETHYST((byte) 10, Tags.Items.GEMS_AMETHYST, "amethyst", 0x8d6bcd, Items.AMETHYST_SHARD, true),
        NICKEL((byte) 11, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/nickel")), "nickel", 0xbabc94, ResourcefulSlimes.NICKEL_INGOT.get(), false),
        SILVER((byte) 12, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/silver")), "silver", 0x7ec3c3, ResourcefulSlimes.SILVER_INGOT.get(), false),
        LEAD((byte) 13, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/lead")), "lead", 0x4f8bb1, ResourcefulSlimes.LEAD_INGOT.get(), false),
        ZINC((byte) 14, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/zinc")), "zinc", 0xbfcece, ResourcefulSlimes.ZINC_INGOT.get(), false),
        URANIUM((byte) 15, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/uranium")), "uranium", 0xbbba63, ResourcefulSlimes.URANIUM_INGOT.get(), false),
        TIN((byte) 16, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/tin")), "tin", 0x85c3ca, ResourcefulSlimes.TIN_INGOT.get(), false),
        ALUMINIUM((byte) 17, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/aluminium")), "aluminium", 0xadadad, ResourcefulSlimes.ALUMINIUM_INGOT.get(), false),
        OSMIUM((byte) 18, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/osmium")), "osmium", 0x9ec6c7, ResourcefulSlimes.OSMIUM_INGOT.get(), false),
        CERTUS_QUARTZ((byte) 19, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:gems/certus_quartz")), "certus_quartz", 0xbef9ff, ResourcefulSlimes.CERTUS_QUARTZ.get(), false);


        private static final IntFunction<ResourceSlime.Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        private final TagKey<Item> resourceTag;
        private final byte id;
        private final String name;
        private final int color;
        private final Item dropItem;
        private final boolean isVanilla;

        Variant(byte id, TagKey<Item> resource_tag, String name, int color, Item drop, boolean is_vanilla){
            this.resourceTag = resource_tag;
            this.id = id;
            this.name = name;
            this.color = color;
            this.dropItem = drop;
            this.isVanilla = is_vanilla;
        }

        public static Variant byId(int id){
            return BY_ID.apply(id);
        }

        public byte getId(){
            return this.id;
        }
        public TagKey<Item> getResourceTag(){
            return this.resourceTag;
        }

        public int getColor(){
            return this.color;
        }

        public Item getDropItem(){
            return this.dropItem;
        }

        public boolean isModded(){
            return !this.isVanilla;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

}
