package com.simibubi.create.content.logistics.factoryBoard;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction;

public class FactoryPanelConnection {
	public static final Codec<FactoryPanelConnection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		FactoryPanelPosition.CODEC.fieldOf("position").forGetter(i -> i.from),
		Codec.INT.fieldOf("amount").forGetter(i -> i.amount),
		Codec.INT.fieldOf("arrow_bending").forGetter(i -> i.arrowBendMode)
	).apply(instance, FactoryPanelConnection::new));

	public FactoryPanelPosition from;
	public int amount;
	public List<Direction> path;
	public int arrowBendMode;
	public boolean success;

	public WeakReference<Object> cachedSource;

	public FactoryPanelConnection(FactoryPanelPosition from, int amount) {
		this(from, amount, -1);
	}

	public FactoryPanelConnection(FactoryPanelPosition from, int amount, int arrowBendMode) {
		this.from = from;
		this.amount = amount;
		this.arrowBendMode = arrowBendMode;
		path = new ArrayList<>();
		success = true;
		cachedSource = new WeakReference<>(null);
	}
}
