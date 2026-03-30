package com.mrh0.createaddition.event;

import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import com.mrh0.createaddition.debug.CADebugger;
import com.mrh0.createaddition.energy.network.EnergyNetworkManager;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.network.ObservePacket;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import com.mrh0.createaddition.commands.CCApiCommand;

public class GameEvents {

    public static void register() {
        // World tick - energy network
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            EnergyNetworkManager.tickWorld(world);
        });

        // Server tick - portable energy
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            PortableEnergyManager.tick();
        });

        // World load - create energy network manager
        ServerWorldEvents.LOAD.register((server, world) -> {
            new EnergyNetworkManager(world);
        });

        // World unload - remove energy network
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            EnergyNetworkManager.instances.remove(world);
        });

        // Right-click block - straw on blaze burner
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            try {
                if (world.isClientSide()) return InteractionResult.PASS;
                var pos = hitResult.getBlockPos();
                var stack = player.getItemInHand(hand);
                BlockState state = world.getBlockState(pos);
                if (stack.getItem() == CAItems.STRAW.get() && world.getBlockEntity(pos) instanceof BlazeBurnerBlockEntity) {
                    if (state.is(AllBlocks.BLAZE_BURNER.get())) {
                        BlockState newState = CABlocks.LIQUID_BLAZE_BURNER.getDefaultState()
                                .setValue(LiquidBlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING)
                                .setValue(LiquidBlazeBurnerBlock.FACING, state.getValue(BlazeBurnerBlock.FACING));
                        world.setBlockAndUpdate(pos, newState);
                        if (!player.isCreative())
                            stack.shrink(1);
                        return InteractionResult.SUCCESS;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return InteractionResult.PASS;
        });

        // Command registration
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CCApiCommand.register(dispatcher);
        });
    }

    public static void registerClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ObservePacket.tick();
            CADebugger.tick();
        });
    }
}
