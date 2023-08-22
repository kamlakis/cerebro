package net.lakis.cerebro.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ByteArrayOutputStream extends DataOutputStream {
	private NestedByteArrayOutputStream outputStream;

	public ByteArrayOutputStream() {
		this(new NestedByteArrayOutputStream());

	}

	public ByteArrayOutputStream(int size) {
		this(new NestedByteArrayOutputStream(size));
	}

	private ByteArrayOutputStream(NestedByteArrayOutputStream outputStream) {
		super(outputStream);
		this.outputStream = outputStream;
	}

	public byte[] toByteArray() {
		return this.outputStream.toByteArray();
	}

	public synchronized int size() {
		return this.outputStream.size();
	}

	@Override
	public String toString() {
		return this.outputStream.toString();
	}

	public String toString(String charsetName) throws UnsupportedEncodingException {
		return this.outputStream.toString(charsetName);
	}

	public void reset() {
		this.outputStream.reset();
	}

	public int getPosition() {
		return this.outputStream.getPos();
	}

	public void setPosition(int position) {
		this.outputStream.setPos(position);
	}

	public void setPositionAtTheEnd() {
		this.outputStream.setPos(size());
	}

	public void setPositionAtTheStart() {
		this.outputStream.setPos(0);
	}

	private static class NestedByteArrayOutputStream extends OutputStream {

		protected byte buf[];
		protected int pos;
		protected int count;

		public NestedByteArrayOutputStream() {
			this(65535); // network packet max size 64K - 1
		}

		public NestedByteArrayOutputStream(int size) {
			if (size < 0) {
				throw new IllegalArgumentException("Negative initial size: " + size);
			}
			buf = new byte[size];
		}

		private void ensureCapacity(int capacity) {
			if (capacity < buf.length)
				return;

			if (capacity < 0)
				throw new OutOfMemoryError();

			buf = Arrays.copyOf(buf, bytesNeeded(capacity));
		}

		private static int bytesNeeded(int count) {
			if (count < 2)
				return count;
			if (count > (1 << 30))
				return Integer.MAX_VALUE;
			return 1 << (int) ((Math.log(count - 1) / Math.log(2)) + 1);
		}

		public synchronized void write(int b) {
			ensureCapacity(pos + 1);
			buf[pos] = (byte) b;
			pos++;
			if (pos > count)
				count = pos;
		}

		public synchronized void write(byte b[], int off, int len) {
			if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) - b.length > 0)) {
				throw new IndexOutOfBoundsException();
			}
			ensureCapacity(pos + len);
			System.arraycopy(b, off, buf, pos, len);
			pos += len;
			if (pos > count)
				count = pos;
		}

		public synchronized void reset() {
			this.pos = 0;
			this.count = 0;
		}

		public synchronized byte[] toByteArray() {
			return Arrays.copyOf(buf, count);
		}

		public synchronized int size() {
			return count;
		}

		public synchronized String toString() {
			return new String(buf, 0, count);
		}

		public synchronized String toString(String charsetName) throws UnsupportedEncodingException {
			return new String(buf, 0, count, charsetName);
		}

		public void close() throws IOException {
		}

		public int getPos() {
			return pos;
		}

		public void setPos(int pos) {
			this.pos = pos;
		}
	}

}
