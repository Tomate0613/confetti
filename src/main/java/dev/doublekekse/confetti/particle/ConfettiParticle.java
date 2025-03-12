package dev.doublekekse.confetti.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.doublekekse.confetti.config.ConfettiConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ConfettiParticle {
    @Environment(value = EnvType.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;
        private final ConfettiOptions options;

        public Provider(SpriteSet spriteSet) {
            this(spriteSet, new ConfettiOptions.Builder().build());
        }

        private Provider(SpriteSet spriteSet, ConfettiOptions options) {
            this.sprite = spriteSet;
            this.options = options;
        }

        public static ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> customProvider(ConfettiOptions options) {
            return (FabricSpriteProvider spriteSet) -> new Provider(spriteSet, options);
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double dX, double dY, double dZ) {
            if (!ConfettiConfig.ENABLED) {
                return null;
            }

            ConfettiPieceParticle overlayParticle = new ConfettiPieceParticle(clientLevel, x, y, z, dX, dY, dZ, options);
            overlayParticle.pickSprite(this.sprite);
            return overlayParticle;
        }
    }

    @Environment(value = EnvType.CLIENT)
    public static class ConfettiPieceParticle extends TextureSheetParticle {
        Quaternionf rotation;
        Quaternionf oldRotation;
        Vector3f rotationAxis;
        float rotationSpeed;
        ConfettiOptions options;

        ConfettiPieceParticle(ClientLevel clientLevel, double x, double y, double z, double dX, double dY, double dZ, ConfettiOptions options) {
            super(clientLevel, x, y, z);

            this.lifetime = options.maxLifetime();
            this.gravity = options.gravity();

            this.xd = dX;
            this.yd = dY;
            this.zd = dZ;

            this.rotation = new Quaternionf();
            this.oldRotation = this.rotation;
            this.rotation.rotateXYZ((float) (Math.random() * Math.PI), (float) (Math.random() * Math.PI), (float) (Math.random() * Math.PI));
            this.rotationAxis = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()).normalize();
            this.rotationSpeed = (float) (Math.random() * options.maxRotationalSpeed());
            this.options = options;

            var color = this.options.colorSupplier().get();
            this.setColor(color[0], color[1], color[2]);
        }

        @Override
        public @NotNull ParticleRenderType getRenderType() {
            return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
        }

        Quaternionf getRotation(Camera camera, float tickPercentage) {
            if (stoppedByCollision) {
                Quaternionf quaternionf = new Quaternionf();
                quaternionf.rotationXYZ((float) -(Math.PI * .5), 0, 0);

                return faceTowardsCamera(quaternionf, camera);
            } else {
                return faceTowardsCamera(new Quaternionf(oldRotation).slerp(rotation, tickPercentage), camera);
            }

        }

        Quaternionf faceTowardsCamera(Quaternionf rotation, Camera camera) {
            Vector3f particleForward = new Vector3f(0, 0, -1).rotate(rotation);
            Vector3f cameraDelta = new Vector3f((float) x, (float) y, (float) z).sub(camera.getPosition().toVector3f());

            if (particleForward.dot(cameraDelta) < 0) {
                rotation.rotateY((float) Math.PI);
            }

            return rotation;
        }

        @Override
        public void tick() {
            var wasStoppedByCollision = this.stoppedByCollision;

            if (lifetime % 5 == 0) {
                this.stoppedByCollision = false;
            }

            if (wasStoppedByCollision && lifetime > options.maxLifetimeOnFloor()) {
                lifetime = options.maxLifetimeOnFloor();
            }

            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.oldRotation = new Quaternionf(rotation);

            checkLifetime();

            if (this.removed) {
                return;
            }

            this.yd += this.gravity;

            if (-this.yd > options.terminalVelocity()) {
                this.yd = -options.terminalVelocity();
            }

            this.yd -= this.yd * 0.075;
            this.yd += (Math.random() - .5) * options.randomSpeed();

            this.xd -= this.xd * 0.075;
            this.xd += (Math.random() - .5) * options.randomSpeed();

            this.zd -= this.zd * 0.075;
            this.zd += (Math.random() - .5) * options.randomSpeed();

            if (wasStoppedByCollision) {
                this.xd = 0;
                this.zd = 0;
            }

            collisions();

            this.move(this.xd, this.yd, this.zd);

            Quaternionf deltaRotation = new Quaternionf().rotateAxis(rotationSpeed, rotationAxis.x, rotationAxis.y, rotationAxis.z);
            rotation.mul(deltaRotation);
        }

        void collisions() {
            var player = Minecraft.getInstance().player;

            if (player != null) {
                var pos = player.position();
                var dir = player.getDeltaMovement().with(Direction.Axis.Y, 0);

                var dX = this.x - pos.x;
                var dY = this.y - pos.y;
                var dZ = this.z - pos.z;

                collision(dX, dY, dZ, 1, player.getBbHeight(), dir);
            }
        }

        void collision(double dX, double dY, double dZ, double radius, double height, Vec3 dir) {
            if (dX * dX + dZ * dZ < radius * radius && dY >= 0 && dY <= height) {
                double dist = Math.sqrt(dX * dX + dZ * dZ);

                if (dist > 0) {
                    double pushFactor = (radius - dist) / radius;
                    double normX = dX / dist;
                    double normZ = dZ / dist;

                    this.xd += normX * pushFactor * dir.length();
                    this.yd += (dir.length() * 1.3) * pushFactor;
                    this.zd += normZ * pushFactor * dir.length();
                }
            }
        }

        @Override
        public void render(VertexConsumer vertexConsumer, Camera camera, float tickPercentage) {
            var offset = getOffset();

            offsetY(offset);
            var rotation = getRotation(camera, tickPercentage);
            this.renderRotatedQuad(vertexConsumer, camera, rotation, tickPercentage);
            offsetY(-offset);
        }

        double getOffset() {
            return ((lifetime / (double) options.maxLifetimeOnFloor())) * .02 + rotationAxis.x * .0015;
        }

        void offsetY(double offset) {
            this.yo += offset;
            this.y += offset;
        }

        protected void checkLifetime() {
            if (!ConfettiConfig.ENABLED) {
                this.lifetime = 0;
            }

            if (this.lifetime-- <= 0) {
                this.remove();
            }
        }

        @Override
        public float getQuadSize(float f) {
            if (this.lifetime - f < 5) {
                return Mth.clampedLerp(.1f, 0f, ((5 - (lifetime - f)) / 5));
            }

            return .1f;
        }
    }
}
