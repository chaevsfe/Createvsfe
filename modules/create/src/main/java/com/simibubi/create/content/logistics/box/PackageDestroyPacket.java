package com.simibubi.create.content.logistics.box;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.simibubi.create.foundation.utility.VecHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PackageDestroyPacket extends SimplePacketBase {

	private final Vec3 location;
	private final ItemStack box;

	public PackageDestroyPacket(Vec3 location, ItemStack box) {
		this.location = location;
		this.box = box;
	}

	public PackageDestroyPacket(RegistryFriendlyByteBuf buffer) {
		location = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		box = ItemStack.STREAM_CODEC.decode(buffer);
	}

	@Override
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeDouble(location.x);
		buffer.writeDouble(location.y);
		buffer.writeDouble(location.z);
		ItemStack.STREAM_CODEC.encode(buffer, box);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ClientLevel level = Minecraft.getInstance().level;
			if (level == null) return;
			for (int i = 0; i < 10; i++) {
				Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, level.getRandom(), .125f);
				Vec3 pos = location.add(motion.scale(4));
				level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, box), pos.x, pos.y,
					pos.z, motion.x, motion.y, motion.z);
			}
		});
		return true;
	}
}
