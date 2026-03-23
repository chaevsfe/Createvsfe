package com.jozufozu.flywheel.util.box;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

/**
 * Compat stub for old Flywheel 0.6.x GridAlignedBB.
 * Grid-aligned bounding box for light volume calculations.
 */
public class GridAlignedBB implements ImmutableBox {
	private int minX, minY, minZ, maxX, maxY, maxZ;

	public GridAlignedBB() {
	}

	public GridAlignedBB(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	public static GridAlignedBB from(BlockPos a, BlockPos b) {
		return new GridAlignedBB(
			Math.min(a.getX(), b.getX()),
			Math.min(a.getY(), b.getY()),
			Math.min(a.getZ(), b.getZ()),
			Math.max(a.getX(), b.getX()) + 1,
			Math.max(a.getY(), b.getY()) + 1,
			Math.max(a.getZ(), b.getZ()) + 1
		);
	}

	public static GridAlignedBB from(BlockPos pos) {
		return new GridAlignedBB(pos.getX(), pos.getY(), pos.getZ(),
			pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
	}

	public static GridAlignedBB from(AABB aabb) {
		return new GridAlignedBB(
			(int) Math.floor(aabb.minX), (int) Math.floor(aabb.minY), (int) Math.floor(aabb.minZ),
			(int) Math.ceil(aabb.maxX), (int) Math.ceil(aabb.maxY), (int) Math.ceil(aabb.maxZ));
	}

	public void translate(BlockPos pos) {
		minX += pos.getX();
		minY += pos.getY();
		minZ += pos.getZ();
		maxX += pos.getX();
		maxY += pos.getY();
		maxZ += pos.getZ();
	}

	public void setMinY(int y) {
		minY = y;
	}

	public void grow(int amount) {
		minX -= amount;
		minY -= amount;
		minZ -= amount;
		maxX += amount;
		maxY += amount;
		maxZ += amount;
	}

	public void grow(int x, int y, int z) {
		minX -= x;
		minY -= y;
		minZ -= z;
		maxX += x;
		maxY += y;
		maxZ += z;
	}

	public void fixMinMax() {
		int tmpX = Math.min(minX, maxX);
		int tmpY = Math.min(minY, maxY);
		int tmpZ = Math.min(minZ, maxZ);
		maxX = Math.max(minX, maxX);
		maxY = Math.max(minY, maxY);
		maxZ = Math.max(minZ, maxZ);
		minX = tmpX;
		minY = tmpY;
		minZ = tmpZ;
	}

	public GridAlignedBB assign(BlockPos a, BlockPos b) {
		this.minX = Math.min(a.getX(), b.getX());
		this.minY = Math.min(a.getY(), b.getY());
		this.minZ = Math.min(a.getZ(), b.getZ());
		this.maxX = Math.max(a.getX(), b.getX()) + 1;
		this.maxY = Math.max(a.getY(), b.getY()) + 1;
		this.maxZ = Math.max(a.getZ(), b.getZ()) + 1;
		return this;
	}

	public int sizeX() { return maxX - minX; }
	public int sizeY() { return maxY - minY; }
	public int sizeZ() { return maxZ - minZ; }

	public boolean sameAs(ImmutableBox other, int margin) {
		return Math.abs(minX - other.getMinX()) <= margin
			&& Math.abs(minY - other.getMinY()) <= margin
			&& Math.abs(minZ - other.getMinZ()) <= margin
			&& Math.abs(maxX - other.getMaxX()) <= margin
			&& Math.abs(maxY - other.getMaxY()) <= margin
			&& Math.abs(maxZ - other.getMaxZ()) <= margin;
	}

	public GridAlignedBB assign(ImmutableBox other) {
		this.minX = other.getMinX();
		this.minY = other.getMinY();
		this.minZ = other.getMinZ();
		this.maxX = other.getMaxX();
		this.maxY = other.getMaxY();
		this.maxZ = other.getMaxZ();
		return this;
	}

	public void grow(double amount) {
		grow((int) Math.ceil(amount));
	}

	public void translate(int x, int y, int z) {
		minX += x;
		minY += y;
		minZ += z;
		maxX += x;
		maxY += y;
		maxZ += z;
	}

	public boolean intersects(ImmutableBox other) {
		return this.minX < other.getMaxX() && this.maxX > other.getMinX()
			&& this.minY < other.getMaxY() && this.maxY > other.getMinY()
			&& this.minZ < other.getMaxZ() && this.maxZ > other.getMinZ();
	}

	@Override
	public int getMinX() { return minX; }
	@Override
	public int getMinY() { return minY; }
	@Override
	public int getMinZ() { return minZ; }
	@Override
	public int getMaxX() { return maxX; }
	@Override
	public int getMaxY() { return maxY; }
	@Override
	public int getMaxZ() { return maxZ; }
}
