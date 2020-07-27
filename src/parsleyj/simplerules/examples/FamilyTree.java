package parsleyj.simplerules.examples;

import parsleyj.simplerules.NativeFacts;
import parsleyj.simplerules.forward.FCKnowledgeBase;
import parsleyj.simplerules.forward.FCResult;
import parsleyj.simplerules.forward.SimpleForwardChaining;
import parsleyj.simplerules.terms.Atom;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static parsleyj.simplerules.KBBuilders.*;

public class FamilyTree {

    public static void main(String[] argv){
        FCKnowledgeBase initialKB = createFamilyKnowledgeBase();
        System.out.println("Initial Knowledge Base: ");
        System.out.println();
        System.out.println(initialKB);

        FCResult result = SimpleForwardChaining.getToFixedPoint(initialKB);
        System.out.println();
        System.out.println("########################################");
        System.out.println();
        System.out.println("After "+result.getIterationsDone()+" iterations, this is the resulting knowledge base:");

        System.out.println(result.getUpdatedKB());
    }

    /**
     * Creates a FCKnowledgeBase for the family tree example
     * @return the knowledge base
     */
    public static FCKnowledgeBase createFamilyKnowledgeBase() {
        // first, declares all the atoms
        // men
        Atom<String> jack = atom("jack");
        Atom<String> oliver = atom("oliver");
        Atom<String> ali = atom("ali");
        Atom<String> james = atom("james");
        Atom<String> simon = atom("simon");
        Atom<String> harry = atom("harry");
        // women
        Atom<String> helen = atom("helen");
        Atom<String> sophie = atom("sophie");
        Atom<String> jess = atom("jess");
        Atom<String> lily = atom("lily");

        // returns a new knowledge base (using a FCKnowledgeBaseBuilder):
        return kb()

                // adds some native facts, used for equality/inequality comparisons
                .withFacts(NativeFacts.nativeCommonFacts())

                // adds a male(«p») fact for each p that is a male
                .withFacts(Stream.of(jack, oliver, ali, james, simon, harry)
                        .map(x -> rel("male").withTerm(x).build())
                        .collect(Collectors.toList()))

                // adds a female(«p») fact for each p that is a female
                .withFacts(Stream.of(helen, sophie, jess, lily)
                        .map(x -> rel("female").withTerm(x).build())
                        .collect(Collectors.toList()))

                // adds all the facts regarding parent relationships
                .withFact(rel("parent_of").withTerms(jack, jess).build())
                .withFact(rel("parent_of").withTerms(jack, lily).build())
                .withFact(rel("parent_of").withTerms(helen, jess).build())
                .withFact(rel("parent_of").withTerms(helen, lily).build())
                .withFact(rel("parent_of").withTerms(oliver, james).build())
                .withFact(rel("parent_of").withTerms(sophie, james).build())
                .withFact(rel("parent_of").withTerms(jess, simon).build())
                .withFact(rel("parent_of").withTerms(ali, simon).build())
                .withFact(rel("parent_of").withTerms(lily, harry).build())
                .withFact(rel("parent_of").withTerms(james, harry).build())

                // adding the rules:

                // X is father of Y if X is parent of Y AND X is male.
                .withRule(rule().withHead(
                        rel("father_of").withTerms(var("X"), var("Y")).build()
                ).withPremises(
                        rel("male").withTerm(var("X")).build(),
                        rel("parent_of").withTerms(var("X"), var("Y")).build()
                ).build())

                // X is mather of Y if X is parent of Y AND X is male.
                .withRule(rule().withHead(
                        rel("mother_of").withTerms(var("X"), var("Y")).build()
                ).withPremises(
                        rel("female").withTerm(var("X")).build(),
                        rel("parent_of").withTerms(var("X"), var("Y")).build()
                ).build())

                // X is grandfather of Y if there is some Z for which X is father of Z AND Z is parent of Y.
                .withRule(rule().withHead(
                        rel("grandfather_of").withTerms(var("X"), var("Y")).build()
                ).withPremises(
                        rel("father_of").withTerms(var("X"), var("Z")).build(),
                        rel("parent_of").withTerms(var("Z"), var("Y")).build()
                ).build())

                // X is grandmother of Y if there is some Z for which X is mother of Z AND Z is parent of Y.
                .withRule(rule().withHead(
                        rel("grandmother_of").withTerms(var("X"), var("Y")).build()
                ).withPremises(
                        rel("mother_of").withTerms(var("X"), var("Z")).build(),
                        rel("parent_of").withTerms(var("Z"), var("Y")).build())
                .build())

                // X is sister of Y if:
                //      - X is female
                //      - there is some F for which
                //          - F is parent of X
                //          - F is parent of Y
                //      - X is not the same as Y
                .withRule(rule().withHead(
                        rel("sister_of").withTerms(var("X"), var("Y")).build()
                ).withPremises(
                        rel("female").withTerms(var("X")).build(),
                        rel("parent_of").withTerms(var("F"), var("X")).build(),
                        rel("parent_of").withTerms(var("F"), var("Y")).build(),
                        invokeNative("COMMON_LIB", "!=", var("X"), var("Y"))
                ).build())

                // X is brother of Y if:
                //      - X is male
                //      - there is some F for which
                //          - F is parent of X
                //          - F is parent of Y
                //      - X is not the same as Y
                .withRule(rule().withHead(
                        rel("brother_of").withTerms(var("X"), var("Y")).build()
                ).withPremises(
                        rel("parent_of").withTerms(var("F"), var("X")).build(),
                        rel("male").withTerms(var("X")).build(),
                        rel("parent_of").withTerms(var("F"), var("Y")).build(),
                        invokeNative("COMMON_LIB", "!=", var("X"), var("Y"))
                ).build())

                // X is ancestor of Y if (base case) X is parent of Y.
                .withRule(rule().withHead(
                        rel("ancestor_of").withTerms(var("X"), var("Y")).build()
                ).withPremises(
                        rel("parent_of").withTerms(var("X"), var("Y")).build()
                ).build())

                // X is ancestor of Y if (inductive step):
                //      - there is some Z for which
                //          - X is parent of Z
                //          - Z is ancestor of Y
                .withRule(rule().withHead(
                        rel("ancestor_of").withTerms(var("X"), var("Y")).build()
                ).withPremises(
                        rel("parent_of").withTerms(var("X"), var("Z")).build(),
                        rel("ancestor_of").withTerms(var("Z"), var("Y")).build()
                ).build())

                .build();
    }


}
