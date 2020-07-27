package parsleyj.simplerules.terms;

import parsleyj.simplerules.utils.Uniquer;
import parsleyj.simplerules.unify.Substitution;

import java.util.HashMap;
import java.util.Map;

/**
 * A Variable is a special kind of term that can be substituted by other terms during the unification process.
 * The unification process itself is the way to find a valid substitution of variables to make the term satisfy certain
 * properties (i.e. to be equal or "just a renaming" of another term).
 */
public class Variable implements Term {
    /**
     * The name of the variable
     */
    private final String name;

    /**
     * The type of the variable
     */
    private final Type type;


    /**
     * Creates a variable with the specified name and type {@code ANY}.
     *
     * @param name name of the variable
     */
    public Variable(String name) {
        this(Type.ANY, name);
    }

    /**
     * Creates a variable with specified type and name.
     *
     * @param type the type of the variable
     * @param name the name of the variable
     */
    public Variable(Type type, String name) {
        this.type = type;
        this.name = name;
    }



    @Override
    public boolean eq(Term y) {
        return false;
    }


    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean justARenaming(Term term2) {
        return term2 instanceof Variable;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public void populateVarTypes(Map<String, Type> typesMap) {
        if (!typesMap.containsKey(name)) {
            typesMap.put(name, type);
        }
    }

    @Override
    public void createUniqueVarNames(Uniquer<String> uniquer, HashMap<String, String> namesMap) {
        if (!namesMap.containsKey(this.name)) {
            namesMap.put(this.name, uniquer.next());
        }
    }


    @Override
    public Term applySubstitution(Substitution subs) {
        return subs.getOrDefault(getName(), this);
    }
}
