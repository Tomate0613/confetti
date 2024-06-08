package dev.doublekekse.confetti.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ConfettiParticle {
    @Environment(value = EnvType.CLIENT)
    public static class Provider
        implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet spriteSet) {
            this.sprite = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double dX, double dY, double dZ) {
            ConfettiPieceParticle overlayParticle = new ConfettiPieceParticle(clientLevel, x, y, z, dX, dY, dZ);
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

        static final int MAX_LIFETIME = 2500;
        static final int MAX_LIFETIME_ON_FLOOR = 500;
        static final float TERMINAL_VELOCITY = .24f;

        ConfettiPieceParticle(ClientLevel clientLevel, double x, double y, double z, double dX, double dY, double dZ) {
            super(clientLevel, x, y, z);

            this.lifetime = MAX_LIFETIME;
            this.gravity = -0.04F;

            this.xd = dX * .1;
            this.yd = dY * .1;
            this.zd = dZ * .1;

            this.rotation = new Quaternionf();
            this.oldRotation = this.rotation;
            this.rotation.rotateXYZ((float) (Math.random() * Math.PI), (float) (Math.random() * Math.PI), (float) (Math.random() * Math.PI));
            this.rotationAxis = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()).normalize();
            this.rotationSpeed = (float) (Math.random() * .5);

            this.setColor((float) Math.random(), (float) Math.random(), (float) Math.random());
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

            if (wasStoppedByCollision && lifetime > MAX_LIFETIME_ON_FLOOR) {
                lifetime = MAX_LIFETIME_ON_FLOOR;
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

            if (-this.yd > TERMINAL_VELOCITY) {
                this.yd = -TERMINAL_VELOCITY;
            }

            float horizontalSpeed = 0.04f;

            this.yd += (Math.random() - .5) * horizontalSpeed;

            this.xd -= this.xd * 0.075;
            this.xd += (Math.random() - .5) * horizontalSpeed;

            this.zd -= this.zd * 0.075;
            this.zd += (Math.random() - .5) * horizontalSpeed;

            this.move(this.xd, this.yd, this.zd);

            Quaternionf deltaRotation = new Quaternionf().rotateAxis(rotationSpeed, rotationAxis.x, rotationAxis.y, rotationAxis.z);
            rotation.mul(deltaRotation);
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
            return ((lifetime / (double) MAX_LIFETIME_ON_FLOOR)) * .02 + rotationAxis.x * .005;
        }

        void offsetY(double offset) {
            this.yo += offset;
            this.y += offset;
        }

        protected void checkLifetime() {
            if (this.lifetime-- <= 0) {
                this.remove();
            }
        }

        @Override
        public float getQuadSize(float f) {
            return .1f;
        }
    }
}
