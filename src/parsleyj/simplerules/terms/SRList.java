package parsleyj.simplerules.terms;

import parsleyj.simplerules.terms.StructImpl;
import parsleyj.simplerules.terms.Term;
import parsleyj.simplerules.terms.Type;

import java.util.Arrays;
import java.util.List;



/**
 * TODO: EXPERIMENTAL
 */
public class SRList extends StructImpl {

    public static final String LIST_DIR = "LIST";

    @Override
    public List<String> directoryPath() {
        return Struct.mutListAppend(super.directoryPath(), LIST_DIR);
    }

    public SRList(List<Term> terms) {
        super(Type.ANY, terms);
    }

    public SRList(Term... terms){
        super(Type.ANY, Arrays.asList(terms));
    }


    @Override
    public String toString() {
        String s = super.toString();
        return "[" + s.substring(1, s.length() - 1) + "]";
    }
}
