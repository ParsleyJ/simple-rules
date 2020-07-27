package parsleyj.simplerules.utils;

import java.util.Iterator;
import java.util.function.Function;

/**
 * Class that generates a sequence of unique identifiers of type T.
 */
public class Uniquer<T> implements Iterator<T> {
    private final Function<Long, T> generator;
    private long counter = 0;

    public Uniquer(Function<Long, T> generator) {
        this.generator = generator;
    }


    private long nextUID() {
        return counter++;
    }

    @Override
    public boolean hasNext() {
        return counter < Long.MAX_VALUE;
    }

    @Override
    public T next() {
        return generator.apply(nextUID());
    }
}
