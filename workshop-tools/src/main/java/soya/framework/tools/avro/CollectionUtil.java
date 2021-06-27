package soya.framework.tools.avro;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CollectionUtil {

	@SafeVarargs
	public static <K extends Comparable<K>, V> Map<K, V> newMap(Entry<K, V>... entries) {

		Map<K, V> map = new TreeMap<>();

		for (Entry<K, V> entry : entries) {

			map.put(entry.getKey(), entry.getValue());

		}

		return map;

	}

	public static <K extends Comparable<K>, V> Map<K, V> mapOf(K k1, V v1) {

		return newMap(mapEntry(k1, v1));

	}

	public static <K extends Comparable<K>, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {

		return newMap(mapEntry(k1, v1), mapEntry(k2, v2));

	}

	public static <K extends Comparable<K>, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3) {

		return newMap(mapEntry(k1, v1), mapEntry(k2, v2), mapEntry(k3, v3));

	}

	public static <K extends Comparable<K>, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {

		return newMap(mapEntry(k1, v1), mapEntry(k2, v2), mapEntry(k3, v3), mapEntry(k4, v4));

	}

	private static <K, V> Entry<K, V> mapEntry(K k, V v) {

		return new AbstractMap.SimpleImmutableEntry<>(k, v);

	}

	public static <T> String join(Iterable<T> iterable, String delimiter) {

		StringJoiner joiner = new StringJoiner(delimiter);

		for (T element : iterable) {

			joiner.add(String.valueOf(element));

		}

		return joiner.toString();

	}

	public static <T> String join(Iterable<T> iterable) {

		return join(iterable, ",");

	}

	public static <T> String join(T[] in, String delimiter) {

		return join(asIterable(in), delimiter);

	}

	public static <T> String join(T[] in) {

		return join(asIterable(in));

	}

	public static <K1, K2, V1, V2> Map<K2, V2> map(Map<K1, V1> inMap, BiFunction<? super K1, V1, K2> keyMapper,

			BiFunction<? super K1, V1, V2> valueMapper) {

		Map<K2, V2> outMap = new ConcurrentHashMap<>();

		inMap.forEach((k1, v1) -> outMap.put(keyMapper.apply(k1, v1), valueMapper.apply(k1, v1)));

		return outMap;

	}

	public static <K, V1, V2> Map<K, V2> map(Map<K, V1> inMap, BiFunction<? super K, V1, V2> valueMapper) {

		return map(inMap, (k, v1) -> k, valueMapper);

	}

	public static <K, V1, V2> Map<K, V2> map(Map<K, V1> inMap, Function<V1, V2> valueMapper) {

		return map(inMap, (k, v) -> valueMapper.apply(v));

	}

	public static <I, K, V> Map<K, V> toMap(Iterable<I> it, Function<I, K> keyMapper, Function<I, V> valueMapper) {

		Map<K, V> map = new ConcurrentHashMap<>();

		it.forEach(i -> map.put(keyMapper.apply(i), valueMapper.apply(i)));

		return map;

	}

	public static <K, V> Map<K, V> toMap(Iterable<K> inMap, Function<K, V> valueMapper) {

		return toMap(inMap, Function.identity(), valueMapper);

	}

	public static <I, O, E extends Throwable> List<O> map(I[] in, FunctionWithException<? super I, O, E> mapper)

			throws E {

		return map(asIterable(in), mapper);

	}

	public static <I, O, E extends Throwable> List<O> map(Iterable<I> in, FunctionWithException<? super I, O, E> mapper)

			throws E {

		List<O> out = new ArrayList<>();

		for (I i : in) {

			O next = mapper.apply(i);

			if (next != null) {

				out.add(next);

			}

		}

		return out;

	}

	public static <I, E extends Throwable> List<I> filter(I[] in, PredicateWithException<? super I, E> predicate)

			throws E {

		return filter(asIterable(in), predicate);

	}

	public static <I, E extends Throwable> List<I> filter(Iterable<I> in,
			PredicateWithException<? super I, E> predicate)

			throws E {

		List<I> out = new ArrayList<>();

		for (I i : in) {

			if (predicate.test(i)) {

				out.add(i);

			}

		}

		return out;

	}

	public static <I, E extends Throwable> void forEach(I[] in, ConsumerWithException<? super I, E> consumer)

			throws E {

		forEach(asIterable(in), consumer);

	}

	public static <I, E extends Throwable> void forEach(Iterable<I> in, ConsumerWithException<? super I, E> consumer)

			throws E {

		for (I i : in) {

			consumer.apply(i);

		}

	}

	private static <T> Iterable<T> asIterable(T[] in) {

		return Arrays.asList(in);

	}
}
