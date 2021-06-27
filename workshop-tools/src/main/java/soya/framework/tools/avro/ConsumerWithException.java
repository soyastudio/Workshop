package soya.framework.tools.avro;

@FunctionalInterface

public interface ConsumerWithException<T, E extends Throwable> {

	/**
	 * 
	 * Gets a result.
	 *
	 * 
	 * 
	 * @return a result
	 * 
	 */

	void apply(T type) throws E;

}