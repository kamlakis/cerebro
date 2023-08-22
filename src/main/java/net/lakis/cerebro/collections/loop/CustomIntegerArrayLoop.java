package net.lakis.cerebro.collections.loop;

import java.util.Iterator;

public class CustomIntegerArrayLoop implements Iterable<Integer> {
	private int idx;
	private int size;

	private int[] array;
	private int start;

	public CustomIntegerArrayLoop(int[] array, int start) {
		this.idx = 0;
		this.array = array;

		if (array != null)
			this.size = array.length;
		
		if (size > 0)
			this.start = (start & 0x7FFFFFFF) % size;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			@Override
			public boolean hasNext() {
				return idx < size;
			}

			@Override
			public Integer next() {
				return array[(start + idx++) % size];
			}
		};
	}

}
