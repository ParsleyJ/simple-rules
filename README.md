#Simple-rules
A simple rule engine and symbolic forward reasoning library, written in Java.

Use this library to write Java applications that can use symbolic reasoning to solve problems and be reactive to the environment. This library provides a set of classes and methods that can be used to build declarative rules and knowledge bases. Feed the reasoning engine with structured knowledge and watch it automatically draw conclusions and react to events, using the power of sound logical inference by forward-chaining and unification.

## How?

Use the static methods in `parsleyj.simplerules.KBBuilders` to build a Knowledge base. 
Knowledge bases are made of _facts_ and _rules_. 

Facts are just terms that represent something that is true in the world. 

Rules are pieces of data that tell the reasoning engine what to do when a particular set of facts (premises) becomes true.
In particular, a rule can tell the reasoning engine to do one, or both, of two things:
1. assert that a new fact has become true (_head of the rule_);
2. execute some Java code (_execute an action_). 

The next example shows how to define a simple knowledge base, that, when fed to the forward chaining algorithm, will compute the factorials of 1, 2, 3... up to 10, as new facts.

```java 
    // building the knowledge base
    FCKnowledgeBase factorialKB = kb()
            // add all the native integer facts, used to perform comparisons and arithmetical operations
            .withFacts(NativeFacts.nativeIntegerFacts())
            // fact for base case: factorial of 0 is 1.
            .withFact(rel("fact").withTerms(atom(0), atom(1)).build())
            .withRule(
                    // adds the rule for the inductive step
                    rule().withPremises(
                            // if there is a fact(M, F) in the kb
                            rel("fact").withTerms(var("M"), var("F")).build(),
                            // and M < 10 (just a termination condition for this example)
                            invokeNative("INT_LIB", "<", var("M"), atom(10)),
                            // compute N = M+1
                            invokeNative("INT_LIB", "+", var("M"), atom(1), var("N")),
                            // compute RESULT = F*N
                            invokeNative("INT_LIB", "*", var("F"), var("N"), var("RESULT"))
                    ).withHead(
                            // then it can be said that fact(N, RESULT) is true and it can be added to the kb
                            rel("fact").withTerms(var("N"), var("RESULT")).build()
                    ).build()
            ).build();
``` 

Execute the algorithm on the knowledge base, then extract the updated knowledge base and print it:

```java 
    FCResult result = SimpleForwardChaining.getToFixedPoint(factorialKB);
    System.out.println(result.getUpdatedKB());
```

Output:
```
    Rules(1):
    fact(N, RESULT) :- fact(M, F), <(M, «10»), +(M, «1», N), *(F, N, RESULT).
    
    Facts(24):
    R = - X  % Unary minus
    R = abs X  % Integer absolute value
    R = X1 + X2  % Integer sum
    R = X1 - X2  % Integer subtraction
    R = X1 * X2  % Integer multiplication
    R = X1 % X2  % Integer division remainder
    R = X1 / X2  % Integer division
    R = X1 min X2  % Integer binary minimum value
    R = X1 max X2  % Integer binary maximum value
    X1 > X2  % Integer 'greater than' comparison
    X1 >= X2  % Integer 'greater or equal than' comparison
    X1 < X2  % Integer 'less than' comparison
    X1 <= X2  % Integer 'less or equal than' comparison
    fact(«0», «1»)
    fact(«1», «1»)
    fact(«2», «2»)
    fact(«3», «6»)
    fact(«4», «24»)
    fact(«5», «120»)
    fact(«6», «720»)
    fact(«7», «5040»)
    fact(«8», «40320»)
    fact(«9», «362880»)
    fact(«10», «3628800»)
```

The facts from `fact(«1», «1»)` to `fact(«10», «3628800»)` are all inferred by the algorithm.


This, and other examples, can be found in the [examples](https://github.com/ParsleyJ/simple-rules/tree/master/src/parsleyj/simplerules/examples) package.


### Possible improvements/additions:
* Other structural terms: lists, dictionaries.
* Fact "annotations" (inspired by AgentSpeak) support.
* (Performance improvement) better rule filtering in FC algorithm: at each iteration, only the rules with premises candidate to match the facts added in the previous iterations should be (attempted to be) fired.
* (Performance improvement) investigate parallelism in rule checking in the FC algorithm.
* (Performance improvement) before unifying the premises of a rule, the conjuncts could be reordered with some heuristic from CSP theory, like MRV (for those knowledge bases/rules for which such reordering does not affect the semantics of the rule).
* (Nonmonotonicity) implement some kind of truth maintenance system that allows support for sound fact retraction. Such a system could be built on top of the fact annotation system (to implement a justification-based truth maintenance system).
* (Better action interface) when a rule fires, instead of automatically add the unified head of the rule to the knowledge base, allow the user to define what to do with it (i.e. explicit "assertion" commands).
* (Type compatibility) complete implementation of the term type system, with checks about compatibility between variables and terms at unification phase; the type system could support subtyping, generics and type qualifiers to express type invariance/covariance/contravariance; moreover, the types could be used by the FC algorithm to improve indexing and performances. 
