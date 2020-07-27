package parsleyj.simplerules.terms;

/**
 * Interface for Type objects, used to describe compatibility relationships between Terms and Variables.
 */
public interface Type {
    Type ANY = arg -> true;

    /**
     * Returns true only if, by definition of this {@link Type}, the {@code arg} term is compatible.
     */
    boolean compatible(Term arg);
}
