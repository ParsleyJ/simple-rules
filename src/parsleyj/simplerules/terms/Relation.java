package parsleyj.simplerules.terms;

import parsleyj.simplerules.unify.Substitution;

import java.util.ArrayList;
import java.util.List;


/**
 * A relation is a special kind of struct term. It is composed by a name and a set of sub-terms. It is usually written
 * using functional notation, e.g.: "relationName(term1, term2)"
 */
public class Relation extends StructImpl {
    private static final String RELATION_DIR = "RELATION";


    /**
     * The name of the relation.
     */
    private final String name;

    /**
     * Creates a relation with specified type and name
     */
    public Relation(Type type, String name) {
        this(type, name, new ArrayList<>());
    }

    /**
     * Creates a relation with specified type, name and list of sub-terms
     */
    public Relation(Type type, String name, List<Term> terms) {
        super(type, addName(name, terms));
        this.name = name;
    }

    private static List<Term> addName(String name, List<Term> terms) {
        List<Term> l = new ArrayList<>();
        l.add(new Atom<>(name));
        l.addAll(terms);
        return l;
    }

    /**
     * Returns the name of a relation in the Prolog's "predicate style" (name/arity) format.
     * @param name name of the relation
     * @param arity arity of the relation
     * @return the name of a relation in the Prolog's "predicate style" (name/arity) format.
     */
    public static String getPredicateStyleName(String name, int arity) {
        return name + "/" + arity;
    }

    /**
     * Returns the name of this relation in the Prolog's "predicate style" (name/arity) format.
     * @return the name of this relation in the Prolog's "predicate style" (name/arity) format.
     */
    public String getPredicateStyleName() {
        return getPredicateStyleName(name, length() - 1);
    }

    @Override
    public List<String> directoryPath() {
        return Struct.mutListAppend(Struct.mutListAppend(super.directoryPath(), RELATION_DIR), getPredicateStyleName());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        Struct rest = rest();
//        if (name.length() <= 3 && rest.toJavaList().size() == 2) {//TODO make this optional
//            return rest.toJavaList().get(0) + " " + name + " " + rest.toJavaList().get(1);
//        }
        return name + rest;
    }


    @Override
    public boolean eq(Term y) {
        if (y instanceof Relation) {
            Relation relation = (Relation) y;
            if (!this.name.equals(relation.name)) {
                return false;
            }
        }
        return super.eq(y);
    }

    @Override
    public Relation applySubstitution(Substitution subs) {
        List<Term> newTerms = new ArrayList<>();
        List<Term> terms = toJavaList();
        for (Term term : terms.subList(1, terms.size())) {
            newTerms.add(term.applySubstitution(subs));
        }
        return new Relation(type(), name, newTerms);
    }


    @Override
    public boolean justARenaming(Term term2) {
        if (term2 instanceof Relation) {
            Relation r2 = (Relation) term2;
            if (!this.name.equals(r2.name)) {
                return false;
            }
            return rest().justARenaming(r2.rest());
        }
        return super.justARenaming(term2);
    }


}
