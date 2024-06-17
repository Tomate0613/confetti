package dev.doublekekse.confetti.particle;

import java.util.function.Supplier;

public record ConfettiOptions(
    Supplier<float[]> colorSupplier,
    int maxLifetime,
    int maxLifetimeOnFloor,
    float terminalVelocity,
    float gravity,
    double maxRotationalSpeed,
    float randomSpeed
) {
    public static final class Builder {
        Supplier<float[]> colorSupplier = () -> new float[]{(float) Math.random(), (float) Math.random(), (float) Math.random()};
        int maxLifetime = 2500;
        int maxLifetimeOnFloor = 350;
        float terminalVelocity = .24f;
        float gravity = -0.04f;
        double maxRotationalSpeed = .5;
        float randomSpeed = 0.04f;

        /**
         * @param colorSupplier Three floats from 0 to 1
         */
        public Builder setColorSupplier(Supplier<float[]> colorSupplier) {
            this.colorSupplier = colorSupplier;
            return this;
        }

        public Builder setMaxLifetime(int maxLifetime) {
            this.maxLifetime = maxLifetime;
            return this;
        }

        public Builder setMaxLifetimeOnFloor(int maxLifetimeOnFloor) {
            this.maxLifetimeOnFloor = maxLifetimeOnFloor;
            return this;
        }

        public Builder setTerminalVelocity(float terminalVelocity) {
            this.terminalVelocity = terminalVelocity;
            return this;
        }

        public Builder setGravity(float gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setMaxRotationalSpeed(double maxRotationalSpeed) {
            this.maxRotationalSpeed = maxRotationalSpeed;
            return this;
        }

        public Builder setRandomSpeed(float randomSpeed) {
            this.randomSpeed = randomSpeed;
            return this;
        }

        public ConfettiOptions build() {
            assert colorSupplier != null;
            assert maxLifetime > 0;
            assert maxLifetimeOnFloor >= 0;
            assert maxRotationalSpeed >= 0;
            assert randomSpeed >= 0;

            return new ConfettiOptions(colorSupplier, maxLifetime, maxLifetimeOnFloor, terminalVelocity, gravity, maxRotationalSpeed, randomSpeed);
        }
    }
}
