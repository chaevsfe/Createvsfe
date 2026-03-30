package com.hlysine.create_connected.mixin.kineticbattery;

import com.hlysine.create_connected.CCBlocks;
import com.hlysine.create_connected.CCItems;
import com.simibubi.create.content.kinetics.deployer.DeployerHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DeployerHandler.class)
public class DeployerHandlerMixin {
    @Inject(
            method = "shouldActivate",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void activateForBattery(ItemStack held, Level world, BlockPos targetPos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (held.getItem() == CCBlocks.KINETIC_BATTERY.asItem() || held.getItem() == CCItems.CHARGED_KINETIC_BATTERY.get())
            if (world.getBlockState(targetPos).is(CCBlocks.KINETIC_BATTERY.get()))
                cir.setReturnValue(true);

    }
}
