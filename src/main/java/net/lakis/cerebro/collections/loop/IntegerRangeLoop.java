package net.lakis.cerebro.collections.loop;

import java.util.Iterator;

public class IntegerRangeLoop implements Iterable<Integer> {
	private int idx;
	private int end;

	public IntegerRangeLoop(int begin, int end) {
		this.idx = begin;
		this.end = end;

	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			@Override
			public boolean hasNext() {
				return idx <= end;
			}

			@Override
			public Integer next() {
				return  idx++;
			}
		};
	}

}
