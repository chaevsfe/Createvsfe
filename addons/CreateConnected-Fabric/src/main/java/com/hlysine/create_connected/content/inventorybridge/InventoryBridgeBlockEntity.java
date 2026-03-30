package com.hlysine.create_connected.content.inventorybridge;

import com.hlysine.create_connected.content.inventoryaccessport.WrappedItemHandler;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.SidedFilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.utility.BlockFace;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import static com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlock.ATTACHED_NEGATIVE;
import static com.hlysine.create_connected.content.inventorybridge.InventoryBridgeBlock.ATTACHED_POSITIVE;

public class InventoryBridgeBlockEntity extends SmartBlockEntity implements SidedStorageBlockEntity {
    protected Storage<ItemVariant> itemCapability;
    private InvManipulationBehaviour negativeInventory;
    private InvManipulationBehaviour positiveInventory;

    SidedFilteringBehaviour filters;
    public FilteringBehaviour negativeFilter;
    public FilteringBehaviour positiveFilter;

    private boolean powered;

    public InventoryBridgeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

        itemCapability = null;
        powered = false;
    }

    @Override
    public void initialize() {
        super.initialize();
        updateConnectedInventory();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing1 =
                (w, p, s) -> new BlockFace(p, InventoryBridgeBlock.getNegativeTarget(s));
        CapManipulationBehaviourBase.InterfaceProvider towardBlockFacing2 =
                (w, p, s) -> new BlockFace(p, InventoryBridgeBlock.getPositiveTarget(s));
        behaviours.add(negativeInventory = new InvManipulationBehaviour(this, towardBlockFacing1));
        behaviours.add(positiveInventory = new InvManipulationBehaviour(this, towardBlockFacing2));
        behaviours.add(filters = new SidedFilteringBehaviour(
                this,
                new InventoryBridgeFilterSlot(),
                (facing, filter) -> {
                    if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
                        negativeFilter = filter;
                    } else {
                        positiveFilter = filter;
                    }
                    return filter;
                },
                facing -> facing.getAxis() == getBlockState().getValue(InventoryBridgeBlock.AXIS)
        ));
    }

    public boolean isAttachedNegative() {
        return !powered && negativeInventory.hasInventory() && !(negativeInventory.getInventory() instanceof WrappedStorage);
    }

    public boolean isAttachedPositive() {
        return !powered && positiveInventory.hasInventory() && !(positiveInventory.getInventory() instanceof WrappedStorage);
    }

    public void updateConnectedInventory() {
        negativeInventory.lazyTick();
        positiveInventory.lazyTick();
        boolean previouslyPowered = powered;
        powered = level.hasNeighborSignal(worldPosition);
        if (powered != previouslyPowered) {
            notifyUpdate();
        }
        boolean attachedNegative = isAttachedNegative();
        boolean attachedPositive = isAttachedPositive();
        if (attachedNegative != getBlockState().getValue(ATTACHED_NEGATIVE) || attachedPositive != getBlockState().getValue(ATTACHED_POSITIVE)) {
            BlockState state = getBlockState()
                    .setValue(ATTACHED_NEGATIVE, attachedNegative)
                    .setValue(ATTACHED_POSITIVE, attachedPositive);
            level.setBlockAndUpdate(worldPosition, state);
        }
    }

    @Override
    protected void read(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        powered = tag.getBoolean("Powered");
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putBoolean("Powered", powered);
    }

    private Storage<ItemVariant> getNegativeStorage() {
        if (powered) return null;
        Storage<ItemVariant> storage = negativeInventory.getInventory();
        if (storage instanceof WrappedStorage) return null;
        return storage;
    }

    private Storage<ItemVariant> getPositiveStorage() {
        if (powered) return null;
        Storage<ItemVariant> storage = positiveInventory.getInventory();
        if (storage instanceof WrappedStorage) return null;
        return storage;
    }

    private void refreshCapability() {
        itemCapability = new InventoryBridgeStorage();
    }

    @Override
    public Storage<ItemVariant> getItemStorage(Direction side) {
        if (itemCapability == null)
            refreshCapability();
        return itemCapability;
    }

    /**
     * Marker interface for wrapped storages (to prevent recursion).
     */
    interface WrappedStorage {
    }

    private class InventoryBridgeStorage implements Storage<ItemVariant>, WrappedStorage {

        private final ThreadLocal<Boolean> recursionGuard = ThreadLocal.withInitial(() -> false);

        private <T> T preventRecursion(Supplier<T> value, T defaultValue) {
            if (recursionGuard.get()) return defaultValue;
            recursionGuard.set(true);
            T result = value.get();
            recursionGuard.set(false);
            return result;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            return preventRecursion(() -> {
                ItemStack stack = resource.toStack((int) Math.min(maxAmount, Integer.MAX_VALUE));
                Storage<ItemVariant> s1 = getNegativeStorage();
                Storage<ItemVariant> s2 = getPositiveStorage();
                boolean negative = negativeFilter.test(stack);
                boolean positive = positiveFilter.test(stack);

                if (s1 == null && s2 == null) return 0L;

                if (s1 == null) {
                    if (!positive) return 0L;
                    if (negative && !negativeFilter.getFilter().isEmpty() && positiveFilter.getFilter().isEmpty())
                        return 0L;
                    return s2.insert(resource, maxAmount, transaction);
                }

                if (s2 == null) {
                    if (!negative) return 0L;
                    if (positive && !positiveFilter.getFilter().isEmpty() && negativeFilter.getFilter().isEmpty())
                        return 0L;
                    return s1.insert(resource, maxAmount, transaction);
                }

                if (!negative && !positive) return 0L;
                boolean negativeFilterEmpty = negativeFilter.getFilter().isEmpty();
                boolean positiveFilterEmpty = positiveFilter.getFilter().isEmpty();

                long inserted = 0;
                if (negative && (!positive || !positiveFilterEmpty || negativeFilterEmpty))
                    inserted += s1.insert(resource, maxAmount - inserted, transaction);
                if (positive && (!negative || !negativeFilterEmpty || positiveFilterEmpty) && inserted < maxAmount)
                    inserted += s2.insert(resource, maxAmount - inserted, transaction);
                return inserted;
            }, 0L);
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notBlankNotNegative(resource, maxAmount);
            return preventRecursion(() -> {
                Storage<ItemVariant> s1 = getNegativeStorage();
                Storage<ItemVariant> s2 = getPositiveStorage();
                if (s1 == null && s2 == null) return 0L;

                long extracted = 0;
                if (s1 != null) extracted += s1.extract(resource, maxAmount - extracted, transaction);
                if (s2 != null && extracted < maxAmount) extracted += s2.extract(resource, maxAmount - extracted, transaction);
                return extracted;
            }, 0L);
        }

        @Override
        public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
            return preventRecursion(() -> {
                Storage<ItemVariant> s1 = getNegativeStorage();
                Storage<ItemVariant> s2 = getPositiveStorage();
                if (s1 == null && s2 == null) return java.util.Collections.emptyIterator();
                if (s1 == null) return s2.iterator();
                if (s2 == null) return s1.iterator();
                return new Iterator<>() {
                    private final Iterator<StorageView<ItemVariant>> it1 = s1.iterator();
                    private final Iterator<StorageView<ItemVariant>> it2 = s2.iterator();
                    private boolean first = true;

                    @Override
                    public boolean hasNext() {
                        if (first && it1.hasNext()) return true;
                        first = false;
                        return it2.hasNext();
                    }

                    @Override
                    public StorageView<ItemVariant> next() {
                        if (first && it1.hasNext()) return it1.next();
                        first = false;
                        return it2.next();
                    }
                };
            }, java.util.Collections.emptyIterator());
        }
    }
}
