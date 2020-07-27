package parsleyj.simplerules.unify;

/**
 * Class for objects that represent a result of an unification process.
 * An unification result may be a failure or may be a success.
 * In the latter case, the result value might contain a set of modifications (variable substitutions) in order to
 * unify one term with another.
 */
public class UnificationResult {

    /**
     * Special Unification result value representing a failed unification (i.e. where there is no valid substitution
     * of variables in order to successfully unify two terms).
     */
    public static final UnificationResult FAILURE = new UnificationResult();

    /**
     * The substitutions of a successful unification.
     */
    private Substitution subs;

    /**
     * Creates a new result representing a failure.
     */
    private UnificationResult() {
        this.subs = null;
    }

    /**
     * Creates a new successful result containing the specified substitutions.
     *
     * @param substitution the substitutions
     */
    public UnificationResult(Substitution substitution) {
        this.subs = substitution;
    }

    /**
     * Creates a new successful result with no substitutions
     *
     * @return a new successful result with no substitutions
     */
    public static UnificationResult empty() {
        return new UnificationResult(new Substitution());
    }

    /**
     * Returns true if this result represents an unification failure, false otherwise.
     *
     * @return true if this result represents an unification failure, false otherwise.
     */
    public boolean isFailure() {
        return subs == null;
    }

    /**
     * Returns the substitutions contained in this unification result.
     *
     * @return the substitutions
     */
    public Substitution getSubstitution() {
        return subs;
    }

    /**
     * Creates a copy of this result.
     *
     * @return a copy of this result.
     */
    public UnificationResult copy() {
        if (isFailure()) {
            return FAILURE;
        } else {
            if (this.subs != null) {
                Substitution s = this.subs.copy();

                return new UnificationResult(s);
            } else {
                return new UnificationResult();
            }
        }
    }

}
