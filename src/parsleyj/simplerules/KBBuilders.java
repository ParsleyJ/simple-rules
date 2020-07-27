package parsleyj.simplerules;

import parsleyj.simplerules.forward.FCKnowledgeBase;
import parsleyj.simplerules.terms.*;
import parsleyj.simplerules.utils.Uniquer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Collection of static methods, created to ease knowledge base definition in Java sources.
 */
public class KBBuilders {
    private KBBuilders() {
    } //don't instantiate

    /**
     * Creates a new {@link Atom} with the specified java object as wrapped value.
     *
     * @param javaValue the wrapped object
     * @param <T>       the type of the wrapped object
     * @return the atom
     */
    public static <T> Atom<T> atom(T javaValue) {
        return new Atom<>(javaValue);
    }

    /**
     * Creates a new {@link Variable} with type {@code Type.ANY} and specified name.
     *
     * @param name the name of the variable
     * @return the variable
     */
    public static Variable var(String name) {
        return new Variable(Type.ANY, name);
    }


    /**
     * Creates a new {@link Variable} with specified type and name.
     *
     * @param type the type of the variable
     * @param name the name of the variable
     * @return the variable
     */
    public static Variable var(Type type, String name) {
        return new Variable(type, name);
    }


    /**
     * Starts the method call chain used to build a {@link Relation}.
     *
     * @param name the name of the relation to be built
     * @return a relation builder object
     */
    public static RelationBuilder rel(String name) {
        return new RelationBuilder(name);
    }


    /**
     * Creates a {@link Relation} with specified name and no terms.
     *
     * @param name the name of the relation
     * @return the relation
     */
    public static Relation smallRel(String name) {
        return new Relation(Type.ANY, name);
    }

    /**
     * Creates a {@link Relation} with specified name and terms.
     *
     * @param name  the name of the relation
     * @param terms the terms of the relation
     * @return the relation
     */
    public static Relation relation(String name, Term... terms) {
        return new Relation(Type.ANY, name, Arrays.asList(terms));
    }

    /**
     * TODO: EXPERIMENTAL
     */
    public static SRList list(Term... terms) {
        return new SRList(terms);
    }

    /**
     * TODO: EXPERIMENTAL
     */
    public static SRList list(List<Term> terms) {
        return new SRList(terms);
    }

    /**
     * Creates a simple structural term with the provided list of sub-terms
     *
     * @param terms the sub-terms
     * @return the struct
     */
    public static Struct struct(List<Term> terms) {
        return new StructImpl(Type.ANY, terms);
    }

    /**
     * Creates a simple structural term with the provided sub-terms
     *
     * @param terms the sub-terms
     * @return the struct
     */
    public static Struct struct(Term... terms) {
        return new StructImpl(Type.ANY, Arrays.asList(terms));
    }

    /**
     * Creates a {@link NativeFactInvoker}, which is a relation which can unify natively-defined relation structures
     * in a knowledge base.
     *
     * @param module the module of the native fact to be "invoked"
     * @param name   the name of the native fact to be "invoked"
     * @param terms  the terms of the native fact to be "invoked"
     * @return the invoker structure
     */
    public static NativeFactInvoker invokeNative(String module, String name, Term... terms) {
        return new NativeFactInvoker(Type.ANY, module, name, Arrays.asList(terms));
    }

    /**
     * Starts a method call chain used to build a {@link Rule}
     *
     * @return a rule builder
     */
    public static RuleBuilder rule() {
        return new RuleBuilder();
    }

    /**
     * Starts a method call chain used to build a {@link FCKnowledgeBase}
     *
     * @return a knowledge-base builder
     */
    public static FCKnowledgeBaseBuilder kb() {
        return new FCKnowledgeBaseBuilder();
    }

    /**
     * Class used to build relations.
     */
    public static class RelationBuilder {
        /**
         * The chosen name of the to-be-created {@link Relation}
         */
        private final String name;

        /**
         * The chosen terms of the to-be-created {@link Relation}
         */
        private final List<Term> terms = new ArrayList<>();

        public RelationBuilder(String name) {
            this.name = name;
        }

        /**
         * Adds the specified terms to this relation builder.
         *
         * @param terms terms that will be appended to the terms of the to-be-created {@link Relation}
         * @return this builder object for method-call-chaining
         */
        public RelationBuilder withTerms(Term... terms) {
            this.terms.addAll(Arrays.asList(terms));
            return this;
        }

        /**
         * Adds the specified list of terms to this relation builder.
         *
         * @param terms terms that will be appended to the terms of the to-be-created {@link Relation}
         * @return this builder object for method-call-chaining
         */
        public RelationBuilder withTerms(List<Term> terms) {
            this.terms.addAll(terms);
            return this;
        }

        /**
         * Adds the specified term to this relation builder.
         *
         * @param term term that will be appended to the terms of the to-be-created {@link Relation}
         * @return this builder object for method-call-chaining
         */
        public RelationBuilder withTerm(Term term) {
            this.terms.add(term);
            return this;
        }

        /**
         * Builds a new {@link Relation} with the provided information.
         *
         * @return the relation
         */
        public Relation build() {
            return new Relation(Type.ANY, name, terms);
        }
    }

    /**
     * Class used to build rules.
     */
    public static class RuleBuilder {
        /**
         * The chosen premises of the to-be-created {@link Rule}
         */
        private final List<Term> premises = new ArrayList<>();

        /**
         * The chosen head of the to-be-created {@link Rule}
         */
        private Term head = null;

        /**
         * The action to be performed when the rule engine infers that all the premises has been satisfied.
         */
        private Consumer<Term> action = null;

        /**
         * Adds the premise to the premises of the to-be-created {@link Rule}
         * @param premise the premise to be appended
         * @return this builder object for method-call-chaining
         */
        public RuleBuilder withPremise(Term premise) {
            premises.add(premise);
            return this;
        }

        /**
         * Adds the specified premises to the premises of the to-be-created {@link Rule}
         * @param premises the premises to be appended
         * @return this builder object for method-call-chaining
         */
        public RuleBuilder withPremises(Term... premises) {
            this.premises.addAll(Arrays.asList(premises));
            return this;
        }

        /**
         * Adds the list of premises to the premises of the to-be-created {@link Rule}
         * @param premises the premises to be appended
         * @return this builder object for method-call-chaining
         */
        public RuleBuilder withPremises(List<Term> premises) {
            this.premises.addAll(premises);
            return this;
        }

        /**
         * Sets the head as head of the to-be-created {@link Rule}
         * @param head the head to be set
         * @return this builder object for method-call-chaining
         */
        public RuleBuilder withHead(Term head) {
            this.head = head;
            return this;
        }

        /**
         * Sets the head of the to-be-created {@link Rule} and builds the rule, in one call.
         * @param head the head of the resulting rule
         * @return the rule
         */
        public Rule buildWithHead(Term head) {
            this.head = head;
            return build();
        }

        /**
         * Sets the action as action of the to-be-created {@link Rule}
         * @param action the action
         * @return this builder object for method-call-chaining
         */
        public RuleBuilder withAction(Consumer<Term> action) {
            this.action = action;
            return this;
        }

        /**
         * Builds the {@link Rule} with the information provided to this builder.
         * @return the rule
         * @throws UnsupportedOperationException if no head was provided
         */
        public Rule build() {
            if (head == null) {
                throw new UnsupportedOperationException("Missing head definition!");
            }
            return new Rule(premises, head, action);
        }
    }

    /**
     * Class used to build knowledge bases.
     */
    public static class FCKnowledgeBaseBuilder {

        /**
         * Chosen rules of the to-be-created knowledge base.
         */
        final List<Rule> rules = new ArrayList<>();

        /**
         * Chosen facts of the to-be-created knowledge base.
         */
        final List<Term> facts = new ArrayList<>();

        /**
         * Adds a fact to the to-be-created knowledge base.
         * @param fact the fact
         * @return this builder object for method-call-chaining
         */
        public FCKnowledgeBaseBuilder withFact(Term fact) {
            this.facts.add(fact);
            return this;
        }

        /**
         * Adds the specified facts to the to-be-created knowledge base.
         * @param facts the facts
         * @return this builder object for method-call-chaining
         */
        public FCKnowledgeBaseBuilder withFacts(Term... facts) {
            this.facts.addAll(Arrays.asList(facts));
            return this;
        }

        /**
         * Adds the list of facts to the to-be-created knowledge base.
         * @param facts the facts
         * @return this builder object for method-call-chaining
         */
        public FCKnowledgeBaseBuilder withFacts(List<Term> facts) {
            this.facts.addAll(facts);
            return this;
        }

        /**
         * Adds a rule to the to-be-created knowledge base.
         * @param rule the rule
         * @return this builder object for method-call-chaining
         */
        public FCKnowledgeBaseBuilder withRule(Rule rule) {
            this.rules.add(rule);
            return this;
        }

        /**
         * Adds the specified rules to the to-be-created knowledge base.
         * @param rules the rules
         * @return this builder object for method-call-chaining
         */
        public FCKnowledgeBaseBuilder withRules(Rule... rules) {
            this.rules.addAll(Arrays.asList(rules));
            return this;
        }

        /**
         * Adds the specified rules to the to-be-created knowledge base.
         * @param rules the rules
         * @return this builder object for method-call-chaining
         */
        public FCKnowledgeBaseBuilder withRules(List<Rule> rules) {
            this.rules.addAll(rules);
            return this;
        }

        /**
         * Builds the knowledge base object with the provided information
         * @return the knowledge base
         */
        public FCKnowledgeBase build() {
            FCKnowledgeBase fckb = new FCKnowledgeBase();
            fckb.addFacts(facts);
            fckb.getRules().addAll(rules);
            return fckb;
        }
    }

    private static final Uniquer<Variable> placeholderVarGenerator = new Uniquer<>(i -> new Variable("_ignore_"+i){
        @Override
        public String toString() {
            return "_";
        }
    });

    /**
     * Creates a placeholder (i.e. Prolog's "_") variable.
     * @return a new placeholder variable
     */
    public static Variable ignoreVar(){
        return placeholderVarGenerator.next();
    }
}
