package net.lakis.cerebro.collections.loop;

import java.util.Iterator;

public class CustomIntegerRangeLoop implements Iterable<Integer> {
	private int idx;
	private int size;

	private int start;
	private int begin;

	public CustomIntegerRangeLoop(int begin, int end, int start) {
		this.idx = 0;
		this.begin = begin;

		if(end >= begin)
			this.size = end - begin + 1;
 		
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
				return begin + (start + idx++) % size;
			}
		};
	}

}
