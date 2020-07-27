package parsleyj.simplerules.examples.vacuumcleaner;

import java.util.Random;

/**
 * A RandomSleeper is an object that provides the ability to the current thread
 * to sleep for an unspecified amount of time (within boundaries).
 */
public class RandomSleeper {
    private final Random random;
    private final int minMillis;
    private final int maxMillis;

    /**
     * Creates a RandomSleeper instance with the specified Random object and time
     * boundaries.
     *
     * @param random    the random object used to generate the random amount of time
     * @param minMillis the lower (inclusive) time bound.
     * @param maxMillis the upper (exclusive) time bound.
     */
    public RandomSleeper(Random random, int minMillis, int maxMillis) {
        this.random = random;
        this.minMillis = minMillis;
        this.maxMillis = maxMillis;
    }

    /**
     * Creates a RandomSleeper with a new {@link Random} instance and
     * the specified time boundaries.
     *
     * @param minMillis the lower (inclusive) time bound.
     * @param maxMillis the upper (exclusive) time bound.
     */
    public RandomSleeper(int minMillis, int maxMillis) {
        this(new Random(), minMillis, maxMillis);
    }

    /**
     * Creates a RandomSleeper with a new {@link Random} instance, the
     * specified upper time bound, and 100ms as the lower time bound.
     *
     * @param maxMillis the upper (exclusive) time bound.
     */
    public RandomSleeper(int maxMillis) {
        this(new Random(), 100, maxMillis);
    }

    /**
     * Sleeps for a random amount of time, as specified by the time boundaries.
     *
     * @throws InterruptedException if the current thread is interrupted while sleeping
     */
    public void randomSleep() throws InterruptedException {
        int t = random.nextInt(maxMillis - minMillis) + minMillis;
        System.out.println("going to sleep for " + t + "ms...");
        Thread.sleep(t);
    }

    /**
     * Gets the {@link Random} object used to generate random time intervals
     *
     * @return the random object
     */
    public Random getRandom() {
        return random;
    }

    public int getMinMillis() {
        return minMillis;
    }

    public int getMaxMillis() {
        return maxMillis;
    }
}
