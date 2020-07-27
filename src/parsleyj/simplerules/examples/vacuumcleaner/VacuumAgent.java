package parsleyj.simplerules.examples.vacuumcleaner;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Extremely simple demonstration of agent application which uses the forward chaining system and simple actions to
 * create a very simple reactive agent with reasoning support.
 */
class VacuumAgent extends Thread {
    private final BlockingQueue<VacuumAction> actionqueue = new ArrayBlockingQueue<>(1, true);
    private final AtomicReference<VacuumPerceptType> currentCellState = new AtomicReference<>(VacuumPerceptType.DIRTY);
    private final VacuumReasoningEngine engine = new VacuumReasoningEngine();
    private final Random randomGenerator = new Random();
    private final RandomSleeper sleeper = new RandomSleeper(randomGenerator, 1_000, 10_000);

    @Override
    public void run() {
        engine.setup(this);
        engine.start();
        //noinspection InfiniteLoopStatement
        while (true){
            // percepts the current cell dirtiness via the sensors
            VacuumPerceptType percept = currentCellState.get();
            // sends the percept to the reasoning engine, which reasons about it
            engine.addPercept(percept);
            try {
                // takes the next action to be performed in the queue
                VacuumAction nextAction = actionqueue.take();
                // depending on the type of the next action (MOVE/SUCK):
                switch (nextAction){
                    case MOVE: { // takes some time to move to the next cell, which can be either dirty or clean
                        sleeper.randomSleep();
                        if (randomGenerator.nextBoolean()) {
                            currentCellState.set(VacuumPerceptType.DIRTY);
                        } else {
                            currentCellState.set(VacuumPerceptType.CLEAN);
                        }
                    }break;
                    case SUCK:{ // takes some time to clean this cell
                        sleeper.randomSleep();
                        currentCellState.set(VacuumPerceptType.CLEAN);
                    }break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized void enqueueAction(VacuumAction wrappedValue) {
        try {
            actionqueue.offer(wrappedValue, 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] argv){
        new VacuumAgent().start();
    }
}
