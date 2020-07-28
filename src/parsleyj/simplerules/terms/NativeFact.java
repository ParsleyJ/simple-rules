package parsleyj.simplerules.terms;

import parsleyj.simplerules.unify.Substitution;
import parsleyj.simplerules.unify.UnificationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * A special kind of {@link Relation} used to provide facts that can be unified via a custom
 * hard-coded unification implementation.
 * Useful to create libraries of native facts (e.g. for arithmetical operations etc...).
 * Native facts are collected into modules for improved indexing; each module is represented by a string name.
 */
public class NativeFact extends RelationImpl implements CustomUnifiable {


    @FunctionalInterface
    public interface NativeFactCustomUnificationFunction {
        UnificationResult unify(NativeFact self, UnificationResult theta, Relation other);
    }

    public static final String NATIVEFACTS_DIR = "NATIVEFACTS";
    private final String module;
    private final NativeFactCustomUnificationFunction customUnification;


    /**
     * Creates a native fact with the specified type, module name and relation name.
     */
    public NativeFact(Type type,
                      String module,
                      String name,
                      NativeFactCustomUnificationFunction customUnification) {
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
                      NativeFactCustomUnificationFunction customUnification) {
        super(type, name, terms);
        this.module = module;
        this.customUnification = customUnification;
    }


    @Override
    public List<String> directoryPath() {
        return genDirectoryNameForNative(module, getName(), length() - 1);
    }


    static List<String> genDirectoryNameForNative(String module, String name, int arity) {
        ArrayList<String> result = new ArrayList<>();
        result.add(Term.GLOBAL_DIR);
        result.add(NATIVEFACTS_DIR);
        result.add(module);
        result.add(Relation.getPredicateStyleName(name, arity));
        return result;
    }


    @Override
    public UnificationResult customUnify(UnificationResult theta, Term other) {
        if(other instanceof Relation){
            Relation relation = (Relation) other;
            return customUnification.unify(this, theta, relation);
        }
        return UnificationResult.FAILURE; //other has to be a relation
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
