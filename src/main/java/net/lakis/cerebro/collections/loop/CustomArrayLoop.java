package net.lakis.cerebro.collections.loop;

import java.util.Iterator;

public class CustomArrayLoop<T> implements Iterable<T> {
	private int idx;
	private int size;

	private T[] array;
	private int start;

	public CustomArrayLoop(T[] array, int start) {
		this.idx = 0;
		this.array = array;
		
		if (array != null)
			this.size = array.length;

		if (size > 0) 
			this.start = (start & 0x7FFFFFFF) % size;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return idx < size;
			}

			@Override
			public T next() {
				return array[(start + idx++) % size];
			}
		};
	}

}
