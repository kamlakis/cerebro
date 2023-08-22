package net.lakis.cerebro.collections.loop;

import java.util.Collection;
import java.util.Iterator;

public class CustomCollectionLoop<T> implements Iterable<T> {
	private int idx;
	private int size;

	private Collection<T> collection;
	private Iterator<T> iterator;

	public CustomCollectionLoop(Collection<T> collection, int start) {
		this.idx = 0;
		this.collection = collection;
		
		if (collection != null)
			this.size = collection.size();
		
		if (size > 0) {
			this.iterator = collection.iterator();

			int skip = (start & 0x7FFFFFFF) % size;
			for (int i = 0; i < skip; i++)
				this.iterator.next();
		}
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
				idx++;
				if (!iterator.hasNext())
					iterator = collection.iterator();
				return iterator.next();
			}
		};
	}

}