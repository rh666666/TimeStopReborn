package com.adoleiiiiii.timestop.common;

import com.adoleiiiiii.timestop.TimeStopReborn;
import com.adoleiiiiii.timestop.render.entity.KnifeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 模组实体类型注册。
 */
public final class EntityRegister {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TimeStopReborn.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<KnifeEntity>> FLYING_SWORD =
            ENTITIES.register("flying_sword", () -> EntityType.Builder
                    .<KnifeEntity>of(KnifeEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(25)
                    .build(ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "flying_sword").toString()));

    private EntityRegister() {
    }
}
