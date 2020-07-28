package parsleyj.simplerules.terms;

import parsleyj.simplerules.unify.UnificationResult;

public interface CustomUnifiable {
    /**
     * The unification operation for this term. It implements the semantics of unification for this term.
     *
     * @param theta theta value as input of unification, containing the partial variable bindings.
     * @param other the other term to be unified with this native term.
     * @return theta value after the unification. It may be a solution with eventual updated variable bindings, or a
     * failure value.
     */
    UnificationResult customUnify(UnificationResult theta, Term other);

}
