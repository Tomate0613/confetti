package dev.doublekekse.confetti.math;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public record Vec3Dist(Vec3 mean, Vec3 stdDev) {
    private static final RandomSource random = RandomSource.create();

    public static final StreamCodec<ByteBuf, Vec3Dist> STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, Vec3Dist::mean, Vec3.STREAM_CODEC, Vec3Dist::stdDev, Vec3Dist::new);

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
}
