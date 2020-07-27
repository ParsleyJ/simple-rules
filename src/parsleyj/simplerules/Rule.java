package parsleyj.simplerules;

import parsleyj.simplerules.utils.Uniquer;
import parsleyj.simplerules.terms.Term;
import parsleyj.simplerules.terms.Type;
import parsleyj.simplerules.unify.Substitution;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A rule is a structure composed of sequence of conjuncts representing the premises of the rule, an "head" of the rule,
 * and an optional action (java code) to be executed when the premises are satisfied.
 */
public class Rule {

    private final List<Term> premises;
    private final Term head;
    private final Consumer<Term> whenFiredAction;

    /**
     * Creates a new rule with specified premises, head and no action.
     */
    public Rule(List<Term> premises, Term head) {
        this.premises = premises;
        this.head = head;
        this.whenFiredAction = null;
    }

    /**
     * Creates a new rule with specified premises, head and action.
     */
    public Rule(List<Term> premises, Term head, Consumer<Term> whenFiredAction) {
        this.premises = premises;
        this.head = head;
        this.whenFiredAction = whenFiredAction;
    }

    /**
     * Applies the specified substitution to all the terms in the premises and head of the rule.
     * The result is a new rule, no changes are done in place.
     * @param subs the substitutions to be applied
     * @return the new rule
     */
    public Rule applySubstitution(Substitution subs) {
        return new Rule(
                getPremises().stream()
                        .map(a -> a.applySubstitution(subs))
                        .collect(Collectors.toList()),
                getHead().applySubstitution(subs),
                whenFiredAction
        );
    }

    /**
     * Renames all the variables to new generated names. All the variables with the same name within the same rule are
     * renamed to the same new name.
     * @param uniquer the id generator used to generate the new names.
     * @return a copy of this rule with variables renamed.
     */
    public Rule standardizeApart(Uniquer<String> uniquer) {
        HashMap<String, String> namesMap = new HashMap<>();
        HashMap<String, Type> typesMap = new HashMap<>();

        for (Term term : getPremises()) {
            term.createUniqueVarNames(uniquer, namesMap);
            term.populateVarTypes(typesMap);
        }
        getHead().createUniqueVarNames(uniquer, namesMap);
        getHead().populateVarTypes(typesMap);

        return applySubstitution(Substitution.varNameSubstitution(namesMap, typesMap));
    }


    public List<Term> getPremises() {
        return premises;
    }

    public Term getHead() {
        return head;
    }

    /**
     * Returns true if this rule has defined an action.
     */
    public boolean hasAction(){
        return whenFiredAction != null;
    }

    /**
     * Executes the action. The term q is the head after the unification of the premises.
     * @param q the head of the rule, after the substitutions resulting from the unification of the premises are
     *          applied to it
     */
    public void executeAction(Term q){
        if (whenFiredAction != null) {
            whenFiredAction.accept(q);
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(head);
        sb.append(" :- ");
        for (int i = 0; i < premises.size(); i++) {
            if(i!=0){
                sb.append(", ");
            }
            sb.append(premises.get(i));
            //uncomment to print directories of premises
            //sb.append("[").append(premises.get(i).directory()).append("]");
        }

        if(hasAction()){
            sb.append(" (*with action)");
        }

        return sb.toString();
    }
}
