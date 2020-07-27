package parsleyj.simplerules.examples.vacuumcleaner;

import parsleyj.simplerules.forward.FCKnowledgeBase;
import parsleyj.simplerules.forward.SimpleForwardChaining;
import parsleyj.simplerules.terms.Atom;
import parsleyj.simplerules.terms.Relation;
import parsleyj.simplerules.terms.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static parsleyj.simplerules.KBBuilders.*;

public class VacuumReasoningEngine extends Thread{
    private final AtomicReference<VacuumAgent> agentInterface = new AtomicReference<>();
    private FCKnowledgeBase currentKB = kb().build();
    private long perceptIDCounter = 0;

    private final BlockingQueue<VacuumPerceptType> perceptQueue = new ArrayBlockingQueue<>(10, true);

    public void setup(VacuumAgent agentInterface){
        // keeps a reference to the agent, in order to send actions
        this.agentInterface.set(agentInterface);

        // builds a very simple knowledge base with the rules that define this agent's reactions
        Atom<VacuumAction> suckAction = new Atom<>(VacuumAction.SUCK);
        Atom<VacuumAction> moveAction = new Atom<>(VacuumAction.MOVE);
        Atom<VacuumPerceptType> cleanPercept = new Atom<>(VacuumPerceptType.CLEAN);
        Atom<VacuumPerceptType> dirtyPercept = new Atom<>(VacuumPerceptType.DIRTY);

        List<RuleBuilder> ruleBuilders = new ArrayList<>();
        ruleBuilders.add(rule()
                .withPremise(
                        relation("percept", var("PERC_ID"), cleanPercept)
                )
                .withHead(relation("action", var("PERC_ID"), moveAction))
        );
        ruleBuilders.add(rule()
                .withPremise(
                        relation("percept", var("PERC_ID"), dirtyPercept)
                )
                .withHead(relation("action", var("PERC_ID"), suckAction))
        );

        // adds an action to both rules; when an action is determined to be performed by the reasoning engine,
        //  it is enqueued in the agent's actuators queue.
        ruleBuilders.forEach(ruleBuilder -> ruleBuilder.withAction(unifiedHead->{
           if(unifiedHead instanceof Relation){
               Relation action = (Relation) unifiedHead;
               if(action.length() == 3 && action.getName().equals("action")) {
                   List<Term> terms = action.toJavaList();
                   // getting the action type
                   Term term1 = terms.get(1);
                   Term term2 = terms.get(2);
                   Atom<Long> percID = (Atom<Long>) term1;
                   Atom<VacuumAction> actionType = (Atom<VacuumAction>) term2;
                   System.out.println("Performing action "+actionType+" reacting to percept with ID "+percID+"...");
                   VacuumReasoningEngine.this.agentInterface.get().enqueueAction(actionType.getWrappedValue());
               }
           }
        }));

        // note that the KB object will contain within its facts a logbook with all the data perceived and all the
        // actions performed by the agent.
        currentKB = kb()
                .withRules(ruleBuilders.stream().map(RuleBuilder::build).collect(Collectors.toList()))
                .build();
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                VacuumPerceptType perceptType = perceptQueue.take();
                Relation percept = relation("percept", new Atom<>(perceptIDCounter++), new Atom<>(perceptType));
                System.out.println("Perceived: "+percept);
                currentKB.addFact(percept);
                currentKB = SimpleForwardChaining.getToFixedPoint(currentKB).getUpdatedKB();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPercept(VacuumPerceptType perceptType){
        try {
            perceptQueue.offer(perceptType, 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
