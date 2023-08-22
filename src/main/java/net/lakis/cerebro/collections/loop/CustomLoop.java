package net.lakis.cerebro.collections.loop;

import java.util.Collection;

public class CustomLoop {

	public static Iterable<Integer> of(int start, int end) {
		return of(start, end, (int) System.nanoTime());
	}

	public static Iterable<Integer> of(int begin, int end, int start) {
		return new CustomIntegerRangeLoop(begin, end, start);
	}

	public static Iterable<Integer> of(int[] array) {
		return of(array, (int) System.nanoTime());
	}

	public static Iterable<Integer> of(int[] array, int start) {
		return new CustomIntegerArrayLoop(array, start);
	}

	public static <T> Iterable<T> of(T[] array) {
		return of(array, (int) System.nanoTime());
	}

	public static <T> Iterable<T> of(T[] array, int start) {
		return new CustomArrayLoop<T>(array, start);
	}

	public static <T> Iterable<T> of(Collection<T> collection) {
		return of(collection, (int) System.nanoTime());
	}

	public static <T> Iterable<T> of(Collection<T> collection, int start) {
		return new CustomCollectionLoop<T>(collection, start);
	}
}
