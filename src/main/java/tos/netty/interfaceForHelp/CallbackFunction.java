package tos.netty.interfaceForHelp;

@FunctionalInterface
public interface CallbackFunction<T> {
    void apply(T t);
}
