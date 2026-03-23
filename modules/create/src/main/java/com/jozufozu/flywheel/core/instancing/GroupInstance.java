package com.jozufozu.flywheel.core.instancing;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import com.jozufozu.flywheel.api.Instancer;

/**
 * Compat stub for old Flywheel 0.6.x GroupInstance.
 */
public class GroupInstance<D> extends AbstractList<D> {
	private final List<D> backing = new ArrayList<>();
	private final Instancer<D> instancer;

	public GroupInstance(Instancer<D> instancer) {
		this.instancer = instancer;
	}

	public GroupInstance(Instancer<D> instancer, int size) {
		this.instancer = instancer;
		for (int i = 0; i < size; i++) {
			D instance = instancer.createInstance();
			if (instance != null) {
				backing.add(instance);
			}
		}
	}

	@Override
	public D get(int index) {
		return backing.get(index);
	}

	@Override
	public int size() {
		return backing.size();
	}

	public void clear() {
		backing.clear();
	}

	public void resize(int newSize) {
		while (backing.size() < newSize) {
			D instance = instancer.createInstance();
			if (instance != null) {
				backing.add(instance);
			}
		}
		while (backing.size() > newSize) {
			backing.remove(backing.size() - 1);
		}
	}
}
