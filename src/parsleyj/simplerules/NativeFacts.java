package parsleyj.simplerules;

import parsleyj.simplerules.terms.*;
import parsleyj.simplerules.unify.UnificationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static parsleyj.simplerules.KBBuilders.atom;
import static parsleyj.simplerules.KBBuilders.var;
import static parsleyj.simplerules.unify.SimpleUnify.unify;

/**
 * A collection of some static methods about native facts.
 * <br>
 * - some static methods are used to ease native fact implementation.
 * <br>
 * - some static methods are used to easily add a set of common and useful native facts to knowledge bases.
 */
public class NativeFacts {
    private NativeFacts() {
    } // don't instantiate

    /**
     * Creates a native fact that represents a binary relation between two values of the specified types.
     *
     * @param type      the type of the fact
     * @param module    the module of this native fact
     * @param name      the name of the relation
     * @param t1        the (Java) type of the first sub-term
     * @param t2        the (Java) type of the second sub-term
     * @param predicate predicate used to check if the provided terms unify with the fact
     * @param <T1>      the (Java) type of the first sub-term
     * @param <T2>      the (Java) type of the second sub-term
     * @return a native fact representing a binary predicate
     */
    public static <T1, T2> NativeFact binaryPredicate(Type type,
                                                      String module,
                                                      String name,
                                                      Class<T1> t1,
                                                      Class<T2> t2,
                                                      BiPredicate<T1, T2> predicate,
                                                      String shortDescription) {
        Variable x1Var = var("X1");
        Variable x2Var = var("X2");
        return new NativeFact(type, module, name, Arrays.asList(x1Var, x2Var), (self, theta, other) -> {
            UnificationResult tempTheta = unify(theta.copy(), new StructImpl(Type.ANY, self.toJavaList()), other);
            if (tempTheta.isFailure()) {
                return UnificationResult.FAILURE;
            }
            Term tmp2 = self.rest().applySubstitution(tempTheta.getSubstitution());

            if (!(tmp2 instanceof Struct)) {
                return UnificationResult.FAILURE;
            }
            Struct tmp = ((Struct) tmp2);

            if (tmp.toJavaList().size() != 2) {
                return UnificationResult.FAILURE;
            }
            Term term1 = tmp.toJavaList().get(0);
            Term term2 = tmp.toJavaList().get(1);

            if (term1 instanceof Variable) {
                term1 = tempTheta.getSubstitution().get(((Variable) term1).getName());
            }
            if (term2 instanceof Variable) {
                term2 = tempTheta.getSubstitution().get(((Variable) term2).getName());
            }
            if (term1 == null || term2 == null) {
                return UnificationResult.FAILURE;
            }
            if (!(term1 instanceof Atom) || !t1.isAssignableFrom(((Atom) term1).getType())) {
                return UnificationResult.FAILURE;
            }
            if (!(term2 instanceof Atom) || !t2.isAssignableFrom(((Atom) term2).getType())) {
                return UnificationResult.FAILURE;
            }
            UnificationResult result = theta.copy();
            @SuppressWarnings("unchecked") T1 x1 = ((Atom<T1>) term1).getWrappedValue();
            @SuppressWarnings("unchecked") T2 x2 = ((Atom<T2>) term2).getWrappedValue();
            if (predicate.test(x1, x2)) {
                return result;
            } else {
                return UnificationResult.FAILURE;
            }

        }) {
            @Override
            public String toString() {
                return "" + x1Var + " " + name + " " + x2Var + "  % " + shortDescription;
            }
        };
    }

    /**
     * Creates a native fact that represents a relation between two arguments and the result of a function.
     *
     * @param type     the type of the fact
     * @param module   the module of this native fact
     * @param name     the name of the relation
     * @param t1       the (Java) type of the first sub-term (argument)
     * @param t2       the (Java) type of the second sub-term (argument)
     * @param tr       the (Java) type of the third sub-term (result)
     * @param function the function used to unify the provided terms with the fact
     * @param <T1>     the (Java) type of the first sub-term
     * @param <T2>     the (Java) type of the second sub-term
     * @param <R>      the (Java) type of the third sub-term
     * @return a native fact representing a binary function application
     */
    public static <T1, T2, R> NativeFact binaryOperator(Type type,
                                                        String module,
                                                        String name,
                                                        Class<T1> t1,
                                                        Class<T2> t2,
                                                        Class<R> tr,
                                                        BiFunction<T1, T2, R> function,
                                                        String shortDescription) {
        Variable x1Var = var("X1");
        Variable x2Var = var("X2");
        Variable rVar = var("R");
        return new NativeFact(type, module, name, Arrays.asList(x1Var, x2Var, rVar),
                ((self, theta, other) -> {
                    UnificationResult tempTheta = unify(theta.copy(), new StructImpl(type, self.toJavaList()), other);
                    if (tempTheta.isFailure()) {
                        return UnificationResult.FAILURE;
                    }
                    Term tmp2 = self.rest().applySubstitution(tempTheta.getSubstitution());
                    if (!(tmp2 instanceof Struct)) {
                        return UnificationResult.FAILURE;
                    }
                    Struct tmp = ((Struct) tmp2);

                    if (tmp.toJavaList().size() != 3) {
                        return UnificationResult.FAILURE;
                    }
                    Term term1 = tmp.toJavaList().get(0);
                    Term term2 = tmp.toJavaList().get(1);
                    Term term3 = tmp.toJavaList().get(2);

                    if (term1 instanceof Variable) {
                        term1 = tempTheta.getSubstitution().get(((Variable) term1).getName());
                    }
                    if (term2 instanceof Variable) {
                        term2 = tempTheta.getSubstitution().get(((Variable) term2).getName());
                    }
                    if (term1 == null || term2 == null) {
                        return UnificationResult.FAILURE;
                    }
                    if (!(term1 instanceof Atom) || !t1.isAssignableFrom(((Atom) term1).getType())) {
                        return UnificationResult.FAILURE;
                    }
                    if (!(term2 instanceof Atom) || !t2.isAssignableFrom(((Atom) term2).getType())) {
                        return UnificationResult.FAILURE;
                    }
                    UnificationResult result = theta.copy();
                    @SuppressWarnings("unchecked") T1 x1 = ((Atom<T1>) term1).getWrappedValue();
                    @SuppressWarnings("unchecked") T2 x2 = ((Atom<T2>) term2).getWrappedValue();
                    if (!(term3 instanceof Variable) ||
                            tempTheta.getSubstitution().contains(((Variable) term3).getName())) {
                        //noinspection rawtypes
                        if (term3 instanceof Atom && tr.isAssignableFrom(((Atom) term3).getType())) {
                            @SuppressWarnings("unchecked") R r = ((Atom<R>) term3).getWrappedValue();
                            return r.equals(function.apply(x1, x2)) ? result : UnificationResult.FAILURE;
                        } else {
                            return UnificationResult.FAILURE;
                        }
                    }
                    result.getSubstitution().put(((Variable) term3).getName(), atom(function.apply(x1, x2)));
                    return result;

                })) {
            @Override
            public String toString() {
                return "" + rVar + " = " + x1Var + " " + name + " " + x2Var + "  % " + shortDescription;
            }
        };
    }

    /**
     * Creates a native fact that represents a relation between an argument and the result of a function.
     *
     * @param type     the type of the fact
     * @param module   the module of this native fact
     * @param name     the name of the relation
     * @param t        the (Java) type of the first sub-term (argument)
     * @param tr       the (Java) type of the second sub-term (result)
     * @param function the function used to unify the provided terms with the fact
     * @param <T>      the (Java) type of the first sub-term
     * @param <R>      the (Java) type of the second sub-term
     * @return a native fact representing a single-argument function application
     */
    public static <T, R> NativeFact unaryOperator(Type type,
                                                  String module,
                                                  String name,
                                                  Class<T> t,
                                                  Class<R> tr,
                                                  Function<T, R> function,
                                                  String shortDescription) {
        Variable xVar = var("X");
        Variable rVar = var("R");
        return new NativeFact(type, module, name, Arrays.asList(xVar, rVar), ((self, theta, other) -> {
            UnificationResult tempTheta = unify(theta.copy(), new StructImpl(type, self.toJavaList()), other);
            if (tempTheta.isFailure()) {
                return UnificationResult.FAILURE;
            }
            Term tmp2 = self.rest().applySubstitution(tempTheta.getSubstitution());

            if (!(tmp2 instanceof Struct)) {
                return UnificationResult.FAILURE;
            }
            Struct tmp = ((Struct) tmp2);

            if (tmp.toJavaList().size() != 2) {
                return UnificationResult.FAILURE;
            }
            Term term1 = tmp.toJavaList().get(0);
            Term term2 = tmp.toJavaList().get(1);

            if (term1 instanceof Variable) {
                term1 = tempTheta.getSubstitution().get(((Variable) term1).getName());
            }

            if (term1 == null) {
                return UnificationResult.FAILURE;
            }
            if (!(term1 instanceof Atom) || !t.isAssignableFrom(((Atom) term1).getType())) {
                return UnificationResult.FAILURE;
            }

            UnificationResult result = theta.copy();
            @SuppressWarnings("unchecked") T x1 = ((Atom<T>) term1).getWrappedValue();
            if (!(term2 instanceof Variable) ||
                    tempTheta.getSubstitution().contains(((Variable) term2).getName())) {
                //noinspection rawtypes
                if (term2 instanceof Atom && tr.isAssignableFrom(((Atom) term2).getType())) {
                    @SuppressWarnings("unchecked") R r = ((Atom<R>) term2).getWrappedValue();
                    return r.equals(function.apply(x1)) ? result : UnificationResult.FAILURE;
                } else {
                    return UnificationResult.FAILURE;
                }
            }
            result.getSubstitution().put(((Variable) term2).getName(), atom(function.apply(x1)));
            return result;

        })){
            @Override
            public String toString() {
                return ""+rVar + " = " + name + " " + xVar+ "  % "+shortDescription;
            }
        };
    }

    /**
     * Commonly-used native facts regarding equality
     */
    public static List<Term> nativeCommonFacts() {
        List<Term> result = new ArrayList<>();

        result.add(new NativeFact(JavaType.ANY, "COMMON_LIB", "==", Arrays.asList(var("X"), var("Y")), ((self, theta, other) -> {
            UnificationResult tempTheta = unify(theta.copy(), new StructImpl(Type.ANY, self.toJavaList()), other);
            if (tempTheta.isFailure()) {
                return UnificationResult.FAILURE;
            }
            Term tmp2 = self.rest().applySubstitution(tempTheta.getSubstitution());
            if (!(tmp2 instanceof Struct)) {
                return UnificationResult.FAILURE;
            }
            Struct tmp = ((Struct) tmp2);
            if (tmp.toJavaList().size() != 2) {
                return UnificationResult.FAILURE;
            }
            Term term1 = tmp.toJavaList().get(0);
            Term term2 = tmp.toJavaList().get(1);

            UnificationResult result1 = theta.copy();
            if (term1 instanceof Variable && !(term2 instanceof Variable)) {
                return unify(result1, term2, term1);
            } else if (!(term1 instanceof Variable) && term2 instanceof Variable) {
                return unify(result1, term1, term2);
            } else if (!(term1 instanceof Variable) /*&& !(term2 instanceof Variable)*/) {
                return term1.eq(term2) ? result1 : UnificationResult.FAILURE;
            } else {
                return UnificationResult.FAILURE;
            }
        })));

        result.add(new NativeFact(JavaType.ANY, "COMMON_LIB", "!=", Arrays.asList(var("X"), var("Y")), (self, theta, other) -> {
            UnificationResult tempTheta = unify(theta.copy(), new StructImpl(Type.ANY, self.toJavaList()), other);
            if (tempTheta.isFailure()) {
                return UnificationResult.FAILURE;
            }
            Term tmp2 = self.rest().applySubstitution(tempTheta.getSubstitution());
            if (!(tmp2 instanceof Struct)) {
                return UnificationResult.FAILURE;
            }
            Struct tmp = ((Struct) tmp2);
            if (tmp.toJavaList().size() != 2) {
                return UnificationResult.FAILURE;
            }
            Term term1 = tmp.toJavaList().get(0);
            Term term2 = tmp.toJavaList().get(1);

            UnificationResult result1 = theta.copy();
            if (!(term1 instanceof Variable) && !(term2 instanceof Variable)) {
                return term1.eq(term2) ? UnificationResult.FAILURE : result1;
            } else {
                return UnificationResult.FAILURE;
            }
        }));

        return result;
    }

    /**
     * Commonly-used native facts regarding simple integer arithmetic and comparisons
     */
    public static List<Term> nativeIntegerFacts() {
        List<Term> result = new ArrayList<>();

        result.add(unaryOperator(JavaType.Integer, "INT_LIB", "-", Integer.class, Integer.class, a -> -a, "Unary minus"));
        result.add(unaryOperator(JavaType.Integer, "INT_LIB", "abs", Integer.class, Integer.class, Math::abs, "Integer absolute value"));
        result.add(binaryOperator(JavaType.Integer, "INT_LIB", "+", Integer.class, Integer.class, Integer.class, Integer::sum, "Integer sum"));
        result.add(binaryOperator(JavaType.Integer, "INT_LIB", "-", Integer.class, Integer.class, Integer.class, (a, b) -> a - b, "Integer subtraction"));
        result.add(binaryOperator(JavaType.Integer, "INT_LIB", "*", Integer.class, Integer.class, Integer.class, (a, b) -> a * b, "Integer multiplication"));
        result.add(binaryOperator(JavaType.Integer, "INT_LIB", "%", Integer.class, Integer.class, Integer.class, (a, b) -> a % b, "Integer division remainder"));
        result.add(binaryOperator(JavaType.Integer, "INT_LIB", "/", Integer.class, Integer.class, Integer.class, (a, b) -> a / b, "Integer division"));
        result.add(binaryOperator(JavaType.Integer, "INT_LIB", "min", Integer.class, Integer.class, Integer.class, Integer::min, "Integer binary minimum value"));
        result.add(binaryOperator(JavaType.Integer, "INT_LIB", "max", Integer.class, Integer.class, Integer.class, Integer::max, "Integer binary maximum value"));
        result.add(binaryPredicate(JavaType.Integer, "INT_LIB", ">", Integer.class, Integer.class, (a, b) -> a > b, "Integer 'greater than' comparison"));
        result.add(binaryPredicate(JavaType.Integer, "INT_LIB", ">=", Integer.class, Integer.class, (a, b) -> a >= b, "Integer 'greater or equal than' comparison"));
        result.add(binaryPredicate(JavaType.Integer, "INT_LIB", "<", Integer.class, Integer.class, (a, b) -> a < b, "Integer 'less than' comparison"));
        result.add(binaryPredicate(JavaType.Integer, "INT_LIB", "<=", Integer.class, Integer.class, (a, b) -> a <= b, "Integer 'less or equal than' comparison"));

        return result;
    }

}
