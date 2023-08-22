package net.lakis.cerebro.io;

import java.io.IOException;
import java.io.InputStream;

public class ByteArrayInputStream extends DataInputStream {
	private NestedByteArrayInputStream inputStream;

	public ByteArrayInputStream(byte... data) {
		this(new NestedByteArrayInputStream(data));

	}

	public ByteArrayInputStream(byte[] data, int offset, int length) {
		this(new NestedByteArrayInputStream(data, offset, length));
	}

	private ByteArrayInputStream(NestedByteArrayInputStream inputStream) {
		super(inputStream);
		this.inputStream = inputStream;
	}

	public boolean hasMoreData() {
		try {
			return this.inputStream.available() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public void mark() {
		this.inputStream.mark(0);
	}

	public void reset() {
		this.inputStream.reset();
	}

	public int getPosistion() {
		return this.inputStream.getPos();
	}

	public void setPosistion(int pos) {
		 this.inputStream.setPos(pos);
	}

	public byte[] readFully() throws IOException {
		return this.readFully(this.inputStream.available());
	}

	
	private static class NestedByteArrayInputStream extends InputStream {
		protected byte buf[];
		protected int pos;
		protected int mark = 0;
		protected int count;

		public NestedByteArrayInputStream(byte buf[]) {
			this.buf = buf;
			this.pos = 0;
			this.count = buf.length;
		}

		public NestedByteArrayInputStream(byte buf[], int offset, int length) {
			this.buf = buf;
			this.pos = offset;
			this.count = Math.min(offset + length, buf.length);
			this.mark = offset;
		}

		public synchronized int read() {
			return (pos < count) ? (buf[pos++] & 0xff) : -1;
		}

		public synchronized int read(byte b[], int off, int len) {
			if (b == null) {
				throw new NullPointerException();
			} else if (off < 0 || len < 0 || len > b.length - off) {
				throw new IndexOutOfBoundsException();
			}

			if (pos >= count) {
				return -1;
			}

			int avail = count - pos;
			if (len > avail) {
				len = avail;
			}
			if (len <= 0) {
				return 0;
			}
			System.arraycopy(buf, pos, b, off, len);
			pos += len;
			return len;
		}

		public synchronized long skip(long n) {
			long k = count - pos;
			if (n < k) {
				k = n < 0 ? 0 : n;
			}

			pos += k;
			return k;
		}

		public synchronized int available() {
			return count - pos;
		}

		public boolean markSupported() {
			return true;
		}

		public void mark(int readAheadLimit) {
			mark = pos;
		}

		public synchronized void reset() {
			pos = mark;
		}

		public void close() throws IOException {
		}

		public int getPos() {
			return pos;
		}

		public void setPos(int pos) {
			this.pos = pos;
			if (pos < mark)
				mark = pos;
		}

	}

}
