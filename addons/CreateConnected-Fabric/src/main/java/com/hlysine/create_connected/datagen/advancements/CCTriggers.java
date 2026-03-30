package com.hlysine.create_connected.datagen.advancements;

import net.minecraft.advancements.CriteriaTriggers;

import java.util.LinkedList;
import java.util.List;

public class CCTriggers {

    private static final List<CriterionTriggerBase<?>> triggers = new LinkedList<>();

    public static SimpleCCTrigger addSimple(String id) {
        return add(new SimpleCCTrigger(id));
    }

    private static <T extends CriterionTriggerBase<?>> T add(T instance) {
        triggers.add(instance);
        return instance;
    }

    public static void register() {
        triggers.forEach(ctp -> CriteriaTriggers.register(ctp.getId().getPath(), ctp));
    }
}
