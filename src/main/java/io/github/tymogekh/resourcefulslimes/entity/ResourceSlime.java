package io.github.tymogekh.resourcefulslimes.entity;

import io.github.tymogekh.resourcefulslimes.ResourcefulSlimes;
import io.github.tymogekh.resourcefulslimes.config.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
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

public class ResourceSlime extends Slime implements Bucketable {
    private static final EntityDataAccessor<Byte> RESOURCE = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> SATURATION = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(ResourceSlime.class, EntityDataSerializers.BOOLEAN);
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
        builder.define(FROM_BUCKET, false);
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Resource", this.getVariant().getId());
        compound.putInt("Saturation", this.saturation);
        compound.putBoolean("FromBucket", this.fromBucket());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.saturation = compound.getInt("Saturation");
        this.setVariant(ResourceSlime.Variant.byId(compound.getInt("Resource")));
        this.setFromBucket(compound.getBoolean("FromBucket"));
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
        if(this.getSize() < 4 && this.random.nextInt(Config.GROW_CHANCE_DECREASE.get()) == 0){
            this.setSize(this.getSize()+1, true);
        } else if(this.saturation >= Config.FOOD_CONSUMPTION.get() && this.random.nextInt(Config.ITEM_DROP_CHANCE_DECREASE.get()) == 0){
            this.saturation -= Config.FOOD_CONSUMPTION.get();
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, 1.5F);
            this.spawnAtLocation(this.getVariant().getDropItem());
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
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        FoodProperties foodProperties = stack.getFoodProperties(player);
        if(foodProperties != null && this.saturation < Config.NUTRITION_CAP.get()) {
            stack.consume(1, player);
            this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.5F);
            if (this.saturation + foodProperties.nutrition() <= Config.NUTRITION_CAP.get()) {
                this.saturation += foodProperties.nutrition();
            } else {
                this.saturation = Config.NUTRITION_CAP.get();
            }
            return InteractionResult.SUCCESS;
        } else if(stack.is(Items.BUCKET) && this.getSize() == 1) {
            this.slimePickup(player, hand);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
        if(spawnType.equals(MobSpawnType.SPAWN_EGG)) {
            ArrayList<Variant> presentValues = presentValues();
            this.setVariant(presentValues.get(this.random.nextInt(presentValues.size()-1)));
        } else if(spawnType.equals(MobSpawnType.BUCKET)){
            this.setSize(1, false);
            return spawnGroupData;
        } else if(spawnType.equals(MobSpawnType.NATURAL)){
                Holder<Biome> holder = level.getBiome(this.blockPosition());
                ArrayList<Variant> variants = new ArrayList<>();
                for(Variant variant : presentValues()){
                    if(holder.is(variant.getSpawnBiomeTag())){
                        variants.add(variant);
                    }
                }
                if(variants.size() == 1){
                    this.setVariant(variants.getFirst());
                } else {
                    this.setVariant(spawnTie(variants.getFirst(), variants.getLast()));
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

    private static Variant spawnTie(Variant variant1, Variant variant2){
        if((int)(Math.random()*2) == 0){
            return variant1;
        } else {
            return variant2;
        }
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

    private void slimePickup(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.getItem() == Items.BUCKET && this.isAlive()) {
            this.playSound(this.getPickupSound(), 1.0F, 1.0F);
            ItemStack itemstack1 = this.getBucketItemStack();
            this.saveToBucketTag(itemstack1);
            ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, player, itemstack1, false);
            player.setItemInHand(hand, itemstack2);
            this.discard();
        }
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

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean b) {
        this.entityData.set(FROM_BUCKET, b);
    }

    @Override
    public void saveToBucketTag(@NotNull ItemStack itemStack) {
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, itemStack, compoundTag -> {
            compoundTag.putByte("Variant", this.getVariant().getId());
            compoundTag.putInt("Saturation", this.saturation);
        });
    }

    @Override
    public void loadFromBucketTag(@NotNull CompoundTag compoundTag) {
        this.setVariant(Variant.byId(compoundTag.getByte("Variant")));
    }

    @Override
    public @NotNull ItemStack getBucketItemStack() {
        return new ItemStack(ResourcefulSlimes.RESOURCE_SLIME_BUCKET);
    }

    @Override
    public @NotNull SoundEvent getPickupSound() {
        return SoundEvents.SLIME_JUMP_SMALL;
    }


    public enum Variant implements StringRepresentable {
        IRON((byte) 0, Tags.Items.INGOTS_IRON, "iron", 0xd8d8d8, Items.IRON_INGOT, Tags.Biomes.IS_MOUNTAIN,  true),
        GOLD((byte) 1, Tags.Items.INGOTS_GOLD, "gold", 0xf6ea20, Items.GOLD_INGOT, Tags.Biomes.IS_TAIGA, true),
        COPPER((byte) 2, Tags.Items.INGOTS_COPPER, "copper", 0xe17c52, Items.COPPER_INGOT, Tags.Biomes.IS_PLAINS, true),
        NETHERITE((byte) 3, Tags.Items.ORES_NETHERITE_SCRAP, "netherite_scrap", 0x624740, Items.NETHERITE_SCRAP, Tags.Biomes.IS_DRY_NETHER,  true),
        LAPIS((byte) 4, Tags.Items.GEMS_LAPIS, "lapis_lazuli", 0x425ec4, Items.LAPIS_LAZULI, Tags.Biomes.IS_SNOWY, true),
        REDSTONE((byte) 5, Tags.Items.DUSTS_REDSTONE, "redstone", 0xa31803, Items.REDSTONE, Tags.Biomes.IS_BADLANDS, true),
        EMERALD((byte) 6, Tags.Items.GEMS_EMERALD, "emerald", 0x45dc5e, Items.EMERALD, Tags.Biomes.IS_JUNGLE, true),
        DIAMOND((byte) 7, Tags.Items.GEMS_DIAMOND, "diamond", 0x68ecd8, Items.DIAMOND, Tags.Biomes.IS_ICY,true),
        QUARTZ((byte) 8, Tags.Items.GEMS_QUARTZ, "quartz", 0xe4dfd6, Items.QUARTZ, Tags.Biomes.IS_WET_NETHER, true),
        COAL((byte) 9, Tags.Items.ORES_COAL, "coal", 0x2e2e2e, Items.COAL, Tags.Biomes.IS_DESERT,true),
        AMETHYST((byte) 10, Tags.Items.GEMS_AMETHYST, "amethyst", 0x8d6bcd, Items.AMETHYST_SHARD, Tags.Biomes.IS_CAVE, true),
        NICKEL((byte) 11, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/nickel")), "nickel", 0xbabc94, ResourcefulSlimes.NICKEL_INGOT.get(), Tags.Biomes.IS_DESERT,false),
        SILVER((byte) 12, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/silver")), "silver", 0x7ec3c3, ResourcefulSlimes.SILVER_INGOT.get(), Tags.Biomes.IS_ICY,false),
        LEAD((byte) 13, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/lead")), "lead", 0x4f8bb1, ResourcefulSlimes.LEAD_INGOT.get(), Tags.Biomes.IS_SNOWY,false),
        ZINC((byte) 14, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/zinc")), "zinc", 0xbfcece, ResourcefulSlimes.ZINC_INGOT.get(), Tags.Biomes.IS_PLAINS, false),
        URANIUM((byte) 15, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/uranium")), "uranium", 0xbbba63, ResourcefulSlimes.URANIUM_INGOT.get(), Tags.Biomes.IS_JUNGLE,false),
        TIN((byte) 16, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/tin")), "tin", 0x85c3ca, ResourcefulSlimes.TIN_INGOT.get(), Tags.Biomes.IS_MOUNTAIN, false),
        ALUMINIUM((byte) 17, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/aluminium")), "aluminium", 0xadadad, ResourcefulSlimes.ALUMINIUM_INGOT.get(), Tags.Biomes.IS_TAIGA, false),
        OSMIUM((byte) 18, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:ingots/osmium")), "osmium", 0x9ec6c7, ResourcefulSlimes.OSMIUM_INGOT.get(), Tags.Biomes.IS_BADLANDS,false),
        CERTUS_QUARTZ((byte) 19, TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse("c:gems/certus_quartz")), "certus_quartz", 0x9df6ff, ResourcefulSlimes.CERTUS_QUARTZ.get(), Tags.Biomes.IS_CAVE, false);


        private static final IntFunction<ResourceSlime.Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        private final TagKey<Item> resourceTag;
        private final byte id;
        private final String name;
        private final int color;
        private final Item dropItem;
        private final boolean isVanilla;
        private final TagKey<Biome> spawnsIn;

        Variant(byte id, TagKey<Item> resource_tag, String name, int color, Item drop, TagKey<Biome> spawnsIn, boolean is_vanilla){
            this.resourceTag = resource_tag;
            this.id = id;
            this.name = name;
            this.color = color;
            this.dropItem = drop;
            this.spawnsIn = spawnsIn;
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

        public TagKey<Biome> getSpawnBiomeTag(){
            return this.spawnsIn;
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
