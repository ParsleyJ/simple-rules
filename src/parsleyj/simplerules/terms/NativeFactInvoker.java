package parsleyj.simplerules.terms;

import parsleyj.simplerules.unify.SimpleUnify;
import parsleyj.simplerules.unify.UnificationResult;

import java.util.List;

/**
 * Simple "stub" structured term, which can be used in knowledge base construction, to create terms that can eventually
 * unify native facts in the KB.
 */
public class NativeFactInvoker extends NativeFact{

    /**
     * Creates an 'invoker' Relation for the Native Fact with specified {@code type}, {@code module}, {@code name} and
     * sub-{@code terms}.
     */
    public NativeFactInvoker(Type type, String module, String name, List<Term> terms) {
        super(type, module, name, terms, (self, theta, other) -> SimpleUnify.unify(theta, self, other));

    }

}
