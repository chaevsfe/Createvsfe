package com.hlysine.create_connected.datagen.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SimpleCCTrigger extends CriterionTriggerBase<SimpleCCTrigger.Instance> {

    public SimpleCCTrigger(String id) {
        super(id);
    }

    public void trigger(ServerPlayer player) {
        super.trigger(player, null);
    }

    public SimpleCCTrigger.Instance instance() {
        return new SimpleCCTrigger.Instance(getId());
    }

    @Override
    public Codec<Instance> codec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("_dummy").forGetter((i) -> Optional.ofNullable(""))
        ).apply(instance, (str) -> new Instance(getId())));
    }

    public static class Instance extends CriterionTriggerBase.Instance {

        public Instance(ResourceLocation idIn) {
            super(idIn, ContextAwarePredicate.create(new LootItemCondition[0]));
        }

        @Override
        protected boolean test(@Nullable List<Supplier<Object>> suppliers) {
            return true;
        }
    }
}
