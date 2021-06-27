package soya.framework.tools.avro;

@FunctionalInterface
public interface RunnableWithException<E extends Throwable> {

	void run() throws E;

}
