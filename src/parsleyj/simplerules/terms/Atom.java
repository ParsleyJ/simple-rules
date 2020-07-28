package parsleyj.simplerules.terms;

import parsleyj.simplerules.utils.Uniquer;

import java.util.HashMap;
import java.util.Map;

/**
 * An atom is a ground term which represents a single piece of unsplittable data.
 * It does not contain variables.
 *
 * @param <T> the type of the wrapped data
 */
public class Atom<T> implements Term {

    private final T lit;
    private final Class<?> clazz;
    private final JavaType type;

    /**
     * Creates an Atom wrapping the value {@code lit}. The type is the {@link JavaType} extracted by using .getClass on
     * the value.
     *
     * @param lit the value to be wrapped
     */
    public Atom(T lit) {
        this.lit = lit;
        this.clazz = lit.getClass();
        this.type = new JavaType(clazz);
    }

    /**
     * Creates an Atom wrapping the value {@code lit}. The type is the {@link JavaType} obtained by the {@code clazz}
     * argument.
     *
     * @param lit   the value to be wrapped
     * @param clazz the type used to create the {@link JavaType} used as type of this Atom term.
     */
    public Atom(T lit, Class<?> clazz) {
        this.lit = lit;
        this.clazz = clazz;
        this.type = new JavaType(clazz);
    }

    /**
     * Returns the java class of the type of this atom.
     */
    public Class<?> getType() {
        return clazz;
    }


    /**
     * Returns the wrapped value of this Atom
     */
    public T getWrappedValue() {
        return lit;
    }


    @Override
    public void createUniqueVarNames(Uniquer<String> uniquer, HashMap<String, String> namesMap) {
        //No variables, do nothing
    }

    @Override
    public void populateVarTypes(Map<String, Type> typesMap) {
        //No variables, do nothing
    }


    /**
     * Returns true only if y is an instance of Atom and the wrapped values are equal.
     * @param y the term to which this term will be checked against.
     * @return true only if y is an instance of Atom and the wrapped values are equal.
     */
    @Override
    public boolean eq(Term y) {
        if (y instanceof Atom) {
            return lit.equals(((Atom) y).lit);
        }

        return false;
    }


    @Override
    public Type type() {
        return type;
    }



    @Override
    public String toString() {
        return "«" + lit.toString() + "»";
    }


}
