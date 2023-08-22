package net.lakis.cerebro.checksum;

import java.util.zip.Checksum;

import net.lakis.cerebro.lang.Numbers;

 
public class Mml32bitXor implements Checksum {
	private static final long LONG_MASK = 0xFFFFFFFFL;

	private int idx;
	private byte[] checksum;

	public Mml32bitXor() {
		this.reset();
	}

	public static String checksum(String message, int offset) {
		byte[] data = message.getBytes();
		Mml32bitXor mml32bitXor = new Mml32bitXor();
		mml32bitXor.update(data, offset, data.length - offset);
		return String.format("%08X", mml32bitXor.getValue());
	}

	@Override
	public void update(int b) {
		checksum[idx] ^= b;
		idx = (idx + 1) % 4;
	}

	@Override
	public void update(byte[] b, int off, int len) {
		for (int i = off; i < off + len; i++) {
			this.update(b[i]);
		}

	}

	@Override
	public long getValue() {
		int ret = Numbers.of(checksum).getInt();
		return (ret ^ LONG_MASK) & LONG_MASK;
	}

	@Override
	public void reset() {
		this.checksum = new byte[4];
		this.idx = 0;
	}
}
