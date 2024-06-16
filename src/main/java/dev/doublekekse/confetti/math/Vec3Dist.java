package dev.doublekekse.confetti.math;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public record Vec3Dist(Vec3 mean, Vec3 stdDev) {
    private static final RandomSource random = RandomSource.create();

    public Vec3Dist(Vec3 center, double stdDev) {
        this(center, new Vec3(stdDev, stdDev, stdDev));
    }


    public Vec3 random() {
        return new Vec3(randomX(), randomY(), randomZ());
    }

    public double randomX() {
        return mean.x + random.nextGaussian() * stdDev.x;
    }


    public double randomY() {
        return mean.y + random.nextGaussian() * stdDev.y;
    }


    public double randomZ() {
        return mean.z + random.nextGaussian() * stdDev.z;
    }

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVec3(mean);
        friendlyByteBuf.writeVec3(stdDev);
    }

    public static Vec3Dist read(FriendlyByteBuf friendlyByteBuf) {
        return new Vec3Dist(friendlyByteBuf.readVec3(), friendlyByteBuf.readVec3());
    }
}
