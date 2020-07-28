package parsleyj.simplerules.forward;

import parsleyj.simplerules.*;
import parsleyj.simplerules.terms.Struct;
import parsleyj.simplerules.terms.Term;
import parsleyj.simplerules.unify.SimpleUnify;
import parsleyj.simplerules.unify.Substitution;
import parsleyj.simplerules.unify.UnificationResult;
import parsleyj.simplerules.utils.Uniquer;

import java.util.*;

/**
 * Created on 03/12/2019.
 */
public class SimpleForwardChaining {


    public static FCResult getToFixedPoint(FCKnowledgeBase initialKB) {
        return forwardChainingAsk(initialKB, null);
    }


    public static FCResult forwardChainingAsk(FCKnowledgeBase initialKB, Struct query) {
        boolean stopAtQuery = query != null;
        FCKnowledgeBase kb = initialKB.copy();
        Uniquer<String> uniquer = new Uniquer<>(l -> "__gen_" + l);

        List<Term> newFacts = new ArrayList<>();
        int iterationCounter = 0;
        do {
            newFacts.clear();
            if (stopAtQuery) {
                //check for trivial queries first
                List<Term> queryFacts = kb.factsInDirectory(query.directoryPath());
                for (Term fact : queryFacts) {
                    UnificationResult unify = SimpleUnify.unify(fact, query);
                    if (!unify.isFailure()) {
                        return new FCResult(true, iterationCounter, unify.getSubstitution(), kb);
                    }
                }
            }

            List<Rule> rules = kb.getRules();
            for (Rule rule : rules) {
                Rule std = rule.standardizeApart(uniquer);
                FCInternalContinuation FCInternalContinuation = new FCInternalContinuation(kb, std.getPremises());

                while (FCInternalContinuation.hasNext()) {
                    List<Term> selectedFacts = FCInternalContinuation.next();



                    UnificationResult ur = SimpleUnify.conjunctUnify(UnificationResult.empty(), selectedFacts, std.getPremises());
                    if (!ur.isFailure()) {
                        Substitution subs = ur.getSubstitution();

                        Term q = std.getHead().applySubstitution(subs);

                        // if there is no fact in the kb and the new facts for which q is "just a renaming of"
                        //
                        if (kb.getAllFacts().stream().noneMatch(f -> f.justARenaming(q)) &&
                                newFacts.stream().noneMatch(f -> f.justARenaming(q))) {

                            newFacts.add(q);

                            rule.executeAction(q);

                            if (stopAtQuery) {
                                UnificationResult unify = SimpleUnify.unify(q, query);
                                if (!unify.isFailure()) {
                                    kb.addFacts(newFacts);
                                    return new FCResult(true, iterationCounter, unify.getSubstitution(), kb);
                                }
                            }
                        }
                    }

                }
            }
            kb.addFacts(newFacts);
            iterationCounter++;
        } while (!newFacts.isEmpty());

        return new FCResult(!stopAtQuery, iterationCounter, new Substitution(), kb);
    }

    private static class FCInternalContinuation implements Iterator<List<Term>> {
        private final int[] indexes;
        private final List<List<Term>> selectedFacts = new ArrayList<>();


        public FCInternalContinuation(FCKnowledgeBase kb, List<? extends Term> premises) {
            indexes = new int[premises.size()];
            for (Term premise : premises) {
                selectedFacts.add(kb.factsInDirectory(premise.directoryPath()));
            }
        }

        public boolean isEverythingMaxedOut() {
            if(selectedFacts.stream().anyMatch(List::isEmpty)){
                return true;
            }
            for (int i = 0; i < indexes.length; i++) {
                int premiseIndex = indexes[i];
                if (premiseIndex <= selectedFacts.get(i).size()-1) {
                    return false;
                }
            }
            return true;
        }


        @Override
        public boolean hasNext() {
            return !isEverythingMaxedOut();
        }

        private void inc(int[] vect, int viewSize) {
            if(viewSize==0){
                for (int i = 0; i < vect.length; i++) {
                    vect[i] = selectedFacts.get(i).size();
                }
                return;
            }
            vect[viewSize - 1]++;
            if (vect[viewSize - 1] == selectedFacts.get(viewSize-1).size()) {
                vect[viewSize - 1] = 0;
                inc(vect, viewSize - 1);
            }
        }

        @Override
        public List<Term> next() {
            int[] currentCounters = new int[indexes.length];
            System.arraycopy(indexes, 0, currentCounters, 0, indexes.length);
            inc(indexes, indexes.length);
            List<Term> result = new ArrayList<>();
            for (int i = 0; i < currentCounters.length; i++) {
                int currentCounter = currentCounters[i];
                result.add(selectedFacts.get(i).get(currentCounter));
            }
            return result;
        }
    }
}
