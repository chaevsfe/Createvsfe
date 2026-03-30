package com.mrh0.createaddition.energy.fabric;

/**
 * Stub interface replacing net.minecraftforge.energy.IEnergyStorage.
 * On Fabric, energy is handled through Team Reborn Energy or custom systems.
 */
public interface IEnergyStorage {
    int receiveEnergy(int maxReceive, boolean simulate);
    int extractEnergy(int maxExtract, boolean simulate);
    int getEnergyStored();
    int getMaxEnergyStored();
    boolean canExtract();
    boolean canReceive();
}
