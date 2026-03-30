package com.mrh0.createaddition.blocks.rolling_mill;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import io.github.fabricators_of_create.porting_lib_ufo.transfer.item.ItemStackHandler;
import java.util.List;
public class RollingMillBlockEntity extends KineticBlockEntity {
    public ItemStackHandler inputInv;
    public ItemStackHandler outputInv;
    public int timer;
    public RollingMillBlockEntity(BlockEntityType<? extends RollingMillBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        inputInv = new ItemStackHandler(1);
        outputInv = new ItemStackHandler(9);
    }
    @Override public void addBehaviours(List<BlockEntityBehaviour> behaviours) { super.addBehaviours(behaviours); }
}
