package dev.doublekekse.confetti.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.doublekekse.confetti.math.Vec3Dist;
import dev.doublekekse.confetti.packet.ExtendedParticlePacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ExtendedParticleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var base = literal("extended_particle").requires(source -> source.hasPermission(2));

        var particle = argument("name", ParticleArgument.particle());

        var pos = argument("pos", Vec3Argument.vec3());
        var posDist = argument("posDist", Vec3Argument.vec3(false));

        var vel = argument("vel", Vec3Argument.vec3(false));
        var velDist = argument("velDist", Vec3Argument.vec3(false));

        var count = argument("count", IntegerArgumentType.integer(1));
        var viewers = argument("viewers", EntityArgument.players());

        dispatcher.register(
            base.then(
                particle.then(pos.then(posDist.then(vel.then(velDist.then(count.executes(ctx -> handle(ParticleArgument.getParticle(ctx, "name"), getDist(ctx, "pos"), getDist(ctx, "vel"), IntegerArgumentType.getInteger(ctx, "count"), false, ctx.getSource().getLevel().players()))
                    .then(literal("normal").executes(ctx -> handle(ParticleArgument.getParticle(ctx, "name"), getDist(ctx, "pos"), getDist(ctx, "vel"), IntegerArgumentType.getInteger(ctx, "count"), false, ctx.getSource().getLevel().players()))
                        .then(viewers.executes(ctx -> handle(ParticleArgument.getParticle(ctx, "name"), getDist(ctx, "pos"), getDist(ctx, "vel"), IntegerArgumentType.getInteger(ctx, "count"), false, EntityArgument.getPlayers(ctx, "viewers")))))
                    .then(literal("force").executes(ctx -> handle(ParticleArgument.getParticle(ctx, "name"), getDist(ctx, "pos"), getDist(ctx, "vel"), IntegerArgumentType.getInteger(ctx, "count"), true, ctx.getSource().getLevel().players()))
                        .then(viewers.executes(ctx -> handle(ParticleArgument.getParticle(ctx, "name"), getDist(ctx, "pos"), getDist(ctx, "vel"), IntegerArgumentType.getInteger(ctx, "count"), true, EntityArgument.getPlayers(ctx, "viewers"))))))
                ))))
            )
        );
    }

    private static int handle(ParticleOptions particle, Vec3Dist pos, Vec3Dist vel, int count, boolean force, Collection<ServerPlayer> viewers) {
        var packet = new ExtendedParticlePacket(pos, vel, count, force, particle);
        var packetByteBuf = PacketByteBufs.create();
        packet.write(packetByteBuf);
        viewers.forEach(viewer -> ServerPlayNetworking.send(viewer, ExtendedParticlePacket.TYPE, packetByteBuf));

        return 1;
    }

    private static Vec3Dist getDist(CommandContext<CommandSourceStack> ctx, String arg) {
        var mean = Vec3Argument.getVec3(ctx, arg);
        var dist = Vec3Argument.getVec3(ctx, arg + "Dist");

        return new Vec3Dist(mean, dist);
    }
}
