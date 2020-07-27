package parsleyj.simplerules.unify;

import parsleyj.simplerules.terms.Term;
import parsleyj.simplerules.terms.Type;
import parsleyj.simplerules.terms.Variable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Support data structure containing variable substitutions for the unification process and the automatic renaming
 * process.
 */
public class Substitution {
    private final Map<String, Term> bindings = new HashMap<>();
    private final Set<String> strongBindings = new HashSet<>();

    public Substitution() {
    }

    /**
     * Creates a new substitution, where each variable is substituted with an other one with specified type and name.
     *
     * @param namesMap the map old var name -> new var name for each variable
     * @param types    the map old var name -> new var type for each variable
     * @return the new Substitution object
     */
    public static Substitution varNameSubstitution(Map<String, String> namesMap, Map<String, Type> types) {
        Substitution substitution = new Substitution();
        namesMap.forEach((k, v) -> substitution.put(k, new Variable(types.get(k), v)));
        return substitution;
    }

    /**
     * Puts a binding between a variable and a term.
     *
     * @param name the name of the variable to which the term is bound.
     * @param what the term to be bound to the variable.
     */
    public void put(String name, Term what) {
        if (bindings.containsKey(name) && strongBindings.contains(name)) {
            return;
        }
        bindings.put(name, what);
        strongBindings.add(name);
    }


    /**
     * Checks whether this substitution object contains a binding for the variable with specified name.
     *
     * @param name the name of the variable
     * @return true if this substitution object contains a binding for the variable with specified name.
     */
    public boolean contains(String name) {
        return bindings.containsKey(name);
    }

    /**
     * Returns the term that would be substituted to the variable with specified name, when this substitution is
     * applied, or null if there is no such binding entry in this substitution.
     *
     * @param name the name of the variable
     * @return the term that would be substituted to the variable with specified name, when this substitution is
     * applied, or null if there is no such binding entry in this substitution.
     */
    public Term get(String name) {
        return bindings.get(name);
    }

    /**
     * Returns the term that would be substituted to the variable with specified name, when this substitution is
     * applied, or the default provided value if there is no such binding entry in this substitution.
     *
     * @param name the name of the variable
     * @return the term that would be substituted to the variable with specified name, when this substitution is
     * applied, or the default provided value if there is no such binding entry in this substitution.
     */
    public Term getOrDefault(String name, Term def) {
        return bindings.getOrDefault(name, def);
    }

    /**
     * Creates a copy of this substitution object, with the same bindings
     *
     * @return the substitution copy
     */
    public Substitution copy() {
        Substitution s = new Substitution();
        bindings.forEach(s.bindings::put);
        s.strongBindings.addAll(strongBindings);
        return s;
    }

    @Override
    public String toString() {
        return bindings.toString();
    }

}
