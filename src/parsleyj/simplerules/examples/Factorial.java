package parsleyj.simplerules.examples;

import parsleyj.simplerules.NativeFacts;
import parsleyj.simplerules.forward.FCKnowledgeBase;
import parsleyj.simplerules.forward.FCResult;
import parsleyj.simplerules.forward.SimpleForwardChaining;

import static parsleyj.simplerules.KBBuilders.*;


/**
 * Computes all the factorial values, starting from 0!, up to the chosen goal (10!).
 * Note that this example uses a set of integer representations and operations based on Java's integer numbers and
 * arithmetic; for this reason, soundness is not guaranteed in case of overflow.
 */
public class Factorial {

    public static void main(String[] argv){
        // building the knowledge base
        FCKnowledgeBase factorialKB = kb()
                // add all the native integer facts, used to perform comparisons and arithmetical operations
                .withFacts(NativeFacts.nativeIntegerFacts())
                // fact for base case: factorial of 0 is 1.
                .withFact(rel("fact").withTerms(atom(0), atom(1)).build())
                .withRule(
                        // adds the rule for the inductive step
                        rule().withPremises(
                                // if there is a fact(M, F) in the kb
                                rel("fact").withTerms(var("M"), var("F")).build(),
                                // and M < 10 (just a termination condition for this example)
                                invokeNative("INT_LIB", "<", var("M"), atom(10)),
                                // compute N = M+1
                                invokeNative("INT_LIB", "+", var("M"), atom(1), var("N")),
                                // compute RESULT = F*N
                                invokeNative("INT_LIB", "*", var("F"), var("N"), var("RESULT"))
                        ).withHead(
                                // then it can be said that fact(N, RESULT) is true and it can be added to the kb
                                rel("fact").withTerms(var("N"), var("RESULT")).build()
                        ).build()
                ).build();

        System.out.println("Factorial (10!) example!");
        System.out.println("Initial KB:");
        System.out.println();
        System.out.println(factorialKB);
        // computes all the computable facts given the input kb.
        FCResult result = SimpleForwardChaining.getToFixedPoint(factorialKB);
        System.out.println();
        System.out.println("########################################");
        System.out.println();
        System.out.println("After "+result.getIterationsDone()+" iterations, this is the resulting knowledge base:");
        // the updated kb is the result object.
        System.out.println(result.getUpdatedKB());
    }
}
