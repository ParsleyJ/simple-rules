package parsleyj.simplerules.terms;

import parsleyj.simplerules.unify.Substitution;

import java.util.ArrayList;
import java.util.List;


/**
 * Relation standard implementation.
 */
public class RelationImpl extends StructImpl implements Relation {


    @Override
    public List<String> directoryPath() {
        return Struct.mutListAppend(Struct.mutListAppend(super.directoryPath(), RelationImpl.RELATION_DIR), getPredicateStyleName());
    }

    /**
     * The name of the relation.
     */
    private final String name;

    /**
     * Creates a relation with specified type and name
     */
    public RelationImpl(Type type, String name) {
        this(type, name, new ArrayList<>());
    }

    /**
     * Creates a relation with specified type, name and list of sub-terms
     */
    public RelationImpl(Type type, String name, List<Term> terms) {
        super(type, prependNameToListOfTerms(name, terms));
        this.name = name;
    }

    private static List<Term> prependNameToListOfTerms(String name, List<Term> terms) {
        List<Term> l = new ArrayList<>();
        l.add(new Atom<>(name));
        l.addAll(terms);
        return l;
    }

    @Override
    public String getName() {
        return name;
    }




    @Override
    public RelationImpl applySubstitution(Substitution subs) {
        List<Term> newTerms = new ArrayList<>();
        List<Term> terms = toJavaList();
        for (Term term : terms.subList(1, terms.size())) {
            newTerms.add(term.applySubstitution(subs));
        }
        return new RelationImpl(type(), name, newTerms);
    }


    @Override
    public boolean justARenaming(Term term2) {
        if (term2 instanceof RelationImpl) {
            RelationImpl r2 = (RelationImpl) term2;
            if (!this.name.equals(r2.name)) {
                return false;
            }
            return rest().justARenaming(r2.rest());
        }
        return super.justARenaming(term2);
    }


    @Override
    public String toString() {
        Struct rest = rest();
        return getName() + rest;
    }
}
