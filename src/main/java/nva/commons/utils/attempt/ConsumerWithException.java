package nva.commons.utils.attempt;

@FunctionalInterface
public interface ConsumerWithException<T, E extends Exception> {

    void consume(T t) throws E;
}
