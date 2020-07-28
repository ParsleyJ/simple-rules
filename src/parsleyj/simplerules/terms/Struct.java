package parsleyj.simplerules.terms;

import parsleyj.simplerules.utils.Uniquer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A Struct is a term made of a sequence of sub-terms.
 */
public interface Struct extends Term {
    String STRUCT_DIR = "STRUCT";

    @Override
    default List<String> directoryPath() {
        return mutListAppend(Term.super.directoryPath(), STRUCT_DIR);
    }

    /**
     * Populates a {@link List} with the terms of this struct.
     * @return the populated Java List
     */
    List<Term> toJavaList();

    /**
     * Returns the first term in the struct
     */
    Term first();

    /**
     * Returns a struct made of all the terms in this struct, except the first one.
     */
    Struct rest();

    /**
     * Returns true if this struct has no terms.
     */
    boolean empty();


    default int length() {
        return toJavaList().size();
    }


    @Override
    default boolean eq(Term y) {
        if (y instanceof Struct) {
            if (this.empty()) {
                return ((Struct) y).empty();
            }
            return this.first().eq(((Struct) y).first())
                    && this.rest().eq(((Struct) y).rest());
        }
        return false;
    }




    @Override
    default void createUniqueVarNames(Uniquer<String> uniquer, HashMap<String, String> namesMap) {
        for (Term term : toJavaList()) {
            term.createUniqueVarNames(uniquer, namesMap);
        }
    }

    @Override
    default void populateVarTypes(Map<String, Type> typesMap) {
        for (Term term : toJavaList()) {
            term.populateVarTypes(typesMap);
        }
    }



    @Override
    default boolean justARenaming(Term term2) {
        if (term2 instanceof Struct) {
            Struct struct2 = (Struct) term2;
            if (this.length() == 0) {
                return struct2.length() == 0;
            }
            if (this.length() == struct2.length()) {
                return this.first().justARenaming(struct2.first())
                        && this.rest().justARenaming(struct2.rest());
            } else {
                return false;
            }
        }
        return Term.super.justARenaming(term2);
    }


    static List<String> mutListAppend(List<String> original, String element) {
        original.add(element);
        return original;
    }
}
