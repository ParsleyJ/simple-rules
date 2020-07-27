package parsleyj.simplerules.terms;

import parsleyj.simplerules.unify.Substitution;
import parsleyj.simplerules.unify.UnificationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A native fact is a special kind of {@link Relation} used to provide custom hard-coded unification implementations.
 * Useful to create libraries of native facts (e.g. for arithmetical operations etc...).
 * Native facts are collected into modules; each module is represented by a string name.
 */
public class NativeFact extends Relation {

    @FunctionalInterface
    public interface NativeFactCustomUnification {
        UnificationResult unify(NativeFact self, UnificationResult theta, Relation other);
    }


    public static final String NATIVEFACTS_DIR = "NATIVEFACTS";
    private final String module;
    private final NativeFactCustomUnification customUnification;


    /**
     * Creates a native fact with the specified type, module name and relation name.
     */
    public NativeFact(Type type,
                      String module,
                      String name,
                      NativeFactCustomUnification customUnification) {
        super(type, name);
        this.module = module;
        this.customUnification = customUnification;
    }

    /**
     * Creates a native fact with the specified type, module name, relation name and list of sub-terms.
     */
    public NativeFact(Type type,
                      String module,
                      String name,
                      List<Term> terms,
                      NativeFactCustomUnification customUnification) {
        super(type, name, terms);
        this.module = module;
        this.customUnification = customUnification;
    }


    @Override
    public List<String> directoryPath() {
        return genDirectoryNameForNative(module, getName(), length() - 1);
    }


    private static List<String> genDirectoryNameForNative(String module, String name, int arity) {
        ArrayList<String> result = new ArrayList<>();
        result.add(Term.GLOBAL_DIR);
        result.add(NATIVEFACTS_DIR);
        result.add(module);
        result.add(getPredicateStyleName(name, arity));
        return result;
    }

    /**
     * The unification operation for this native fact. This method internally calls the provided callback used to
     * implement the semantics of unification for this fact.
     *
     * @param theta theta value as input of unification, containing the partial variable bindings.
     * @param other the other term to be unified with this native fact; since native fact has the form of a relation,
     *              it has to be a relation.
     * @return theta value after the unification. It may be a solution with eventual updated variable bindings, or a
     * failure value.
     */
    public UnificationResult customUnify(UnificationResult theta, Relation other) {
        return customUnification.unify(this, theta, other);
    }


    @Override
    public NativeFact applySubstitution(Substitution subs) {
        List<Term> newTerms = new ArrayList<>();
        List<Term> terms = toJavaList();
        for (Term term : terms.subList(1, terms.size())) {
            newTerms.add(term.applySubstitution(subs));
        }

        return new NativeFact(type(), module, getName(), newTerms, customUnification);
    }

}
