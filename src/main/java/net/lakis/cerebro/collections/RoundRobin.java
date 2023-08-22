package net.lakis.cerebro.collections;

public class RoundRobin  {
	private int start;
	private int end;
	private int current;

	public RoundRobin(int start, int end) {
		this.start = start;
		this.end = end;
		this.current = start - 1;
	}
	
	

	public synchronized int get() {
		if (++this.current > this.end)
			this.current = start;
		return this.current;
	}
}
