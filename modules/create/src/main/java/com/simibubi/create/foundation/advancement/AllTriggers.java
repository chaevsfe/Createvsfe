package com.simibubi.create.foundation.advancement;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.advancements.CriteriaTriggers;

public class AllTriggers {

	private static final List<CriterionTriggerBase<?>> triggers = new LinkedList<>();
	private static boolean registered = false;

	public static SimpleCreateTrigger addSimple(String id) {
		return add(new SimpleCreateTrigger(id));
	}

	private static <T extends CriterionTriggerBase<?>> T add(T instance) {
		triggers.add(instance);
		return instance;
	}

	public static void register() {
		if (registered) return;
		registered = true;
		triggers.forEach(ctp -> {
			try {
				CriteriaTriggers.register(ctp.getId().getPath(), ctp);
			} catch (Exception e) {
				// Skip duplicate or invalid trigger registration from addons
			}
		});
	}

}
