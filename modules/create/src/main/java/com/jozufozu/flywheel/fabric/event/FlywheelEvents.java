package com.jozufozu.flywheel.fabric.event;

import java.util.function.Consumer;

import com.jozufozu.flywheel.event.BeginFrameEvent;
import com.jozufozu.flywheel.event.GatherContextEvent;
import com.jozufozu.flywheel.event.RenderLayerEvent;
import com.jozufozu.flywheel.event.ReloadRenderersEvent;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Compat stub for old Flywheel 0.6.x FlywheelEvents.
 * Provides no-op events that can be registered against but never fire.
 */
public class FlywheelEvents {
	public static final Event<Consumer<GatherContextEvent>> GATHER_CONTEXT =
		EventFactory.createArrayBacked(Consumer.class, listeners -> (event) -> {
			for (Consumer<GatherContextEvent> listener : listeners) {
				listener.accept(event);
			}
		});

	public static final Event<Consumer<BeginFrameEvent>> BEGIN_FRAME =
		EventFactory.createArrayBacked(Consumer.class, listeners -> (event) -> {
			for (Consumer<BeginFrameEvent> listener : listeners) {
				listener.accept(event);
			}
		});

	public static final Event<Consumer<RenderLayerEvent>> RENDER_LAYER =
		EventFactory.createArrayBacked(Consumer.class, listeners -> (event) -> {
			for (Consumer<RenderLayerEvent> listener : listeners) {
				listener.accept(event);
			}
		});

	public static final Event<Consumer<ReloadRenderersEvent>> RELOAD_RENDERERS =
		EventFactory.createArrayBacked(Consumer.class, listeners -> (event) -> {
			for (Consumer<ReloadRenderersEvent> listener : listeners) {
				listener.accept(event);
			}
		});
}
