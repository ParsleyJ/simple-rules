package parsleyj.simplerules.terms;

import parsleyj.simplerules.unify.Substitution;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of {@link Struct}.
 */
public class StructImpl implements Struct {

    public static final Struct EMPTY = new StructImpl();
    private final Type type;
    private final List<Term> terms;

    public StructImpl() {
        terms = new ArrayList<>();
        type = Type.ANY;
    }

    public StructImpl(Type type, List<Term> terms) {
        this.type = type;
        this.terms = terms;
    }


    @Override
    public List<Term> toJavaList() {
        return terms;
    }

    @Override
    public Term first() {
        if (terms.isEmpty()) {
            return EMPTY;
        }
        return terms.get(0);
    }


    @Override
    public Struct rest() {
        if (empty()) {
            return EMPTY;
        }
        if (terms.size() == 1) {
            return EMPTY;
        }
        return new StructImpl(Type.ANY, new ArrayList<>(terms.subList(1, terms.size())));
    }

    @Override
    public boolean empty() {
        return terms.isEmpty();
    }

    @Override
    public Struct applySubstitution(Substitution subs) {
        Struct result = new StructImpl();
        List<Term> newTerms = result.toJavaList();
        for (Term term : toJavaList()) {
            newTerms.add(term.applySubstitution(subs));
        }
        return result;
    }



    @Override
    public Type type() {
        return type;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < terms.size(); i++) {
            Term term = terms.get(i);
            if(i!=0){
                sb.append(", ");
            }
            sb.append(term.toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
