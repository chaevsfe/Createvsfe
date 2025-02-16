package io.github.fabricators_of_create.porting_lib_ufo;

import io.github.fabricators_of_create.porting_lib_ufo.util.NetworkHooks;
import io.github.fabricators_of_create.porting_lib_ufo.util.ServerLifecycleHooks;
import io.github.fabricators_of_create.porting_lib_ufo.util.TierSortingRegistry;
import io.github.fabricators_of_create.porting_lib_ufo.util.TrueCondition;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class PortingLibUtility implements ModInitializer {
	@Override
	public void onInitialize() {
		
		ServerLifecycleHooks.init();
		TrueCondition.init();
	}
}
