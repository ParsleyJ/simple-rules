package parsleyj.simplerules.terms;

import parsleyj.simplerules.unify.SimpleUnify;
import parsleyj.simplerules.unify.Substitution;
import parsleyj.simplerules.unify.UnificationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple "stub" structured term, which can be used in knowledge base construction, to create terms that can eventually
 * unify native facts in the KB.
 */
public class NativeFactInvoker extends RelationImpl{

    private final String module;

    /**
     * Creates an 'invoker' Relation for the Native Fact with specified {@code type}, {@code module}, {@code name} and
     * sub-{@code terms}.
     */
    public NativeFactInvoker(Type type, String module, String name, List<Term> terms) {
        super(type, name, terms);

        this.module = module;
    }

    @Override
    public List<String> directoryPath() {
        return NativeFact.genDirectoryNameForNative(module, getName(), length() - 1);
    }

    @Override
    public RelationImpl applySubstitution(Substitution subs) {
        List<Term> newTerms = new ArrayList<>();
        List<Term> terms = toJavaList();
        for (Term term : terms.subList(1, terms.size())) {
            newTerms.add(term.applySubstitution(subs));
        }

        return new NativeFactInvoker(type(), module, getName(), newTerms);
    }




}
