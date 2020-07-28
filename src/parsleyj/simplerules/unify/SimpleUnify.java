package parsleyj.simplerules.unify;

import parsleyj.simplerules.terms.*;

import java.util.List;

/**
 * Class containing the simple recursive unification algorithm, with some tweaks to handle Native facts.
 */
public class SimpleUnify {

    /**
     * Simple recursive unification algorithm, with some tweaks to handle Native facts.
     *
     * @param theta the input theta value, containing all the previous binding information.
     * @param x     the first term to be unified
     * @param y     the second term to be unified
     * @return a new theta value, eventually containing all the new variable substitutions (bindings) to be performed
     * on {@code x} in order to make it "equal" or "just a renaming" of {@code y}. If there is no such substitution, a
     * failure value is returned.
     */
    public static UnificationResult unify(
            UnificationResult theta,
            Term x,
            Term y
    ) {
        if (theta.isFailure()) {
            return UnificationResult.FAILURE;
        } else if (x instanceof CustomUnifiable) {
            return ((CustomUnifiable) x).customUnify(theta, y);
        } else if (y instanceof CustomUnifiable) {
            return ((CustomUnifiable) y).customUnify(theta, x);
        } else if (x.eq(y)) {
            return theta;
        } else if (x instanceof Variable) {
            return unifyVar((Variable) x, y, theta);
        } else if (y instanceof Variable) {
            return unifyVar((Variable) y, x, theta);
        } else if (x instanceof Struct && y instanceof Struct) {
            Struct structX = ((Struct) x);
            Struct structY = ((Struct) y);
            if (structX.toJavaList().size() != structY.toJavaList().size()) {
                return UnificationResult.FAILURE;
            }

            return unify(
                    unify(
                            theta,
                            structX.first(),
                            structY.first()
                    ),
                    structX.rest(),
                    structY.rest()
            );
        } else {
            return UnificationResult.FAILURE;
        }

    }

    /**
     * Sub algorithm of {@code unify(...)} used to unify a variable with a term.
     *
     * @param var   the variable to be unified
     * @param t     the term to which the variable will be unified with
     * @param theta the input theta value containing all previously found substitutions.
     * @return the theta value eventually enriched with the substitution to be performed to this variable, or a failure
     * value in case of incompatibilities between the variable and the provided term.
     */
    public static UnificationResult unifyVar(
            Variable var,
            Term t,
            UnificationResult theta
    ) {

        if (theta.isFailure() || !var.type().compatible(t)) {
            return UnificationResult.FAILURE;
        }


        if (theta.getSubstitution().contains(var.getName())) {
            return unify(theta, theta.getSubstitution().get(var.getName()), t);
        }

        if (t instanceof Variable
                && theta.getSubstitution().contains(((Variable) t).getName())) {
            return unify(theta, var, theta.getSubstitution().get(((Variable) t).getName()));
        }


        UnificationResult copy = theta.copy();
        copy.getSubstitution().put(var.getName(), t);
        return copy;
    }

    /**
     * Performs a fresh new unification between the two provided terms. This is equivalent to a call to
     * {@code SimpleUnify.unify(UnificationResult.empty(), x, y)}.
     *
     * @param x the first term to be unified
     * @param y the second term to be unified
     * @return the resulting theta value
     */
    public static UnificationResult unify(Term x, Term y) {
        return SimpleUnify.unify(UnificationResult.empty(), x, y);
    }

    /**
     * Unification algorithm between two sequences of conjuncts. Note that the two list parameters are assumed
     * to be of the same size. The terms are unified pair-wise on the order in which they appear in the lists, and
     * the whole unification process aborts with a failure as soon as a conjunct cannot be unified with the
     * corresponding one in the other list.
     *
     * @param theta the input theta value containing all previously found substitutions
     * @param a the first sequence to be unified
     * @param b the second sequence to be unified
     *
     * @return the overall unification result
     */
    public static UnificationResult conjunctUnify(
            UnificationResult theta,
            List<? extends Term> a,
            List<? extends Term> b
    ) {
        if (theta.isFailure()) {
            return UnificationResult.FAILURE;
        }
        if (a.isEmpty() || b.isEmpty()) {
            return theta;
        }
        if (a.size() == 1 || b.size() == 1) {
            return unify(theta, a.get(0), b.get(0));
        }
        return conjunctUnify(
                unify(theta, a.get(0), b.get(0)),
                a.subList(1, a.size()),
                b.subList(1, b.size())
        );
    }
}
