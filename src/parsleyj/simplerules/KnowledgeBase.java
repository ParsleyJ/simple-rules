package parsleyj.simplerules;

import parsleyj.simplerules.forward.FCKnowledgeBase;
import parsleyj.simplerules.terms.Term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Object containing a generic knowledge base of facts and rules.
 *
 * TODO generalize?
 */
public class KnowledgeBase {
    /**
     * The facts in this knowledge base
     */
    protected final List<Term> allFacts = new ArrayList<>();
    /**
     * The rules in this knowledge base
     */
    protected final List<Rule> rules = new ArrayList<>();

    /**
     * Converts all the facts as rules with no bodies, and returns the list of all the rules (the already defined ones
     * and the converted ones).
     */
    public List<Rule> getEverythingAsRule(){//TODO fix this
        List<Rule> result = new ArrayList<>();
        getAllFacts().forEach(f -> result.add(new Rule(Collections.emptyList(), f)));
        result.addAll(rules);
        return result;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<Term> getAllFacts() {
        return allFacts;
    }

    /**
     * Force-adds a fact to this knowledge base
     * @param fact the fact to be added
     */
    public void addFact(Term fact){
        allFacts.add(fact);
    }

    /**
     * Force-adds some facts to this knowledge base
     * @param facts the facts to be added
     */
    public void addFacts(List<Term> facts){
        allFacts.addAll(facts);
    }

    /**
     * Creates a new knowledge base with all the contents of this one.
     * @return the copy of this knowledge base
     */
    public KnowledgeBase copy(){
        KnowledgeBase kb = new FCKnowledgeBase();
        allFacts.forEach(kb::addFact);
        kb.rules.addAll(this.rules);
        return kb;
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Rules(").append(rules.size()).append("):\n");
        for (Rule rule : rules) {
            sb.append(rule.toString()).append(".\n");
        }
        sb.append("\nFacts(").append(allFacts.size()).append("):\n");
        for (Term fact : allFacts) {
            sb.append(fact.toString()).append("\n");//.append(".          [").append(fact.directory()).append("]\n");
        }
        sb.append("\n\n");
        return sb.toString();
    }
}
