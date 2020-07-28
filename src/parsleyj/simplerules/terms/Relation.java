package parsleyj.simplerules.terms;


public interface Relation extends Struct {
    String RELATION_DIR = "RELATION";




    /**
     * Returns the name of a relation in the Prolog's "predicate style" (name/arity) format.
     *
     * @param name  name of the relation
     * @param arity arity of the relation
     * @return the name of a relation in the Prolog's "predicate style" (name/arity) format.
     */
    static String getPredicateStyleName(String name, int arity) {
        return name + "/" + arity;
    }

    /**
     * Returns the name of this relation in the Prolog's "predicate style" (name/arity) format.
     *
     * @return the name of this relation in the Prolog's "predicate style" (name/arity) format.
     */
    default String getPredicateStyleName() {
        return getPredicateStyleName(getName(), length() - 1);
    }

    @Override
    default boolean eq(Term y) {
        if (y instanceof Relation) {
            Relation relation = (Relation) y;
            if (!this.getName().equals(relation.getName())) {
                return false;
            }
        }
        return Struct.super.eq(y);
    }

    String getName();
}
