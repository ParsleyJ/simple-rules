package parsleyj.simplerules.terms;

import parsleyj.simplerules.utils.Uniquer;
import parsleyj.simplerules.unify.Substitution;

import java.util.*;

/**
 * A "Term" is the most generic type of data handled by a logic reasoning engine.
 */
public interface Term  {

    //todo immutability
    //todo lists
    //todo maps
    //todo annotations

    /**
     * Global indexing directory
     */
    String GLOBAL_DIR = "GLOBAL";

    /**
     * Returns the indexing directory path of the fact represented by this term.
     *
     * @return the directory path
     */
    default List<String> directoryPath() {
        return new ArrayList<>(Collections.singleton(GLOBAL_DIR));
    }

    /**
     * Populates the provided namesMap of old-name/new-name associations, in order to standardize this term.
     *
     * @param uniquer  the unique string generator used to create the variable names
     * @param namesMap the associations between old and new names. This method reads and updates this map.
     */
    void createUniqueVarNames(Uniquer<String> uniquer, HashMap<String, String> namesMap);

    /**
     * Method used to apply a variable {@link Substitution} to this Term.
     *
     * @param subs the substitutions to be applied
     * @return a Term in which all variables affected by the Substitution are correctly substituted.
     */
    default Term applySubstitution(Substitution subs) {
        return this;
    }


    /**
     * returns true if this and y are both completely instantiated (they do not contain variables), and are equal.
     *
     * @param y the term to which this term will be checked against.
     * @return true if this and y are both completely instantiated (they do not contain variables), and are equal.
     */
    boolean eq(Term y);

    /**
     * returns true if this term and term2 have similar structure and if for each variable in this structure, there
     * is a corresponding variable in term2.
     *
     * @param term2 the term to which this term will be checked against.
     * @return true if this term and term2 have similar structure and if to each variable in this structure, there
     * is a variable in the same position in term2, false otherwise.
     */
    default boolean justARenaming(Term term2) {
        return eq(term2);
    }


    /**
     * Returns the type - from the point of view of the reasoning engine - of this term
     *
     * @return the type of this term
     */
    Type type();


    /**
     * Populates a map with names/types pairs of the variables in this term.
     *
     * @param typesMap the names/types map of the variables in this term.
     */
    void populateVarTypes(Map<String, Type> typesMap);

}
