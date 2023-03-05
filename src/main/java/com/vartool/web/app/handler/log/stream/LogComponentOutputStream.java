package com.vartool.web.app.handler.log.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * log component output stream
 * 
 * @fileName : LogComponentOutputStream.java
 * @author : ytkim
 */
public class LogComponentOutputStream extends OutputStream {

	private final Charset charset;

	private boolean skip = false;

	private static final byte LINE_FEED = 0x0A;
	private static final byte CARRIAGE_RETURN = 0x0D;

	public static final int LIMIT_CHAR = 20000;
	private int charCount = 0;

	private final CustomByteArrayOutputStream buffer = new CustomByteArrayOutputStream(132);

	private Deque<String> logQueue = new ConcurrentLinkedDeque<String>();
	
	public LogComponentOutputStream(Charset charset) {
		this.charset = charset;
	}

	@Override
	public void write(final int cc) throws IOException {
		final byte c = (byte) cc;

		if (c == '\n' || c == '\r') {
			if (!skip) {
				processBuffer();
			}
		} else {
			++charCount;
			buffer.write(cc);
			if (charCount > LIMIT_CHAR) {
				processBuffer();
			}
		}
		skip = c == '\r';
	}

	public void write(final byte[] b, final int off, final int len) throws IOException {

		int offset = off;
		int blockStartOffset = offset;
		int remaining = len;
		while (remaining > 0) {
			while (remaining > 0 && b[offset] != LINE_FEED && b[offset] != CARRIAGE_RETURN) {
				offset++;
				remaining--;
			}

			final int blockLength = offset - blockStartOffset;
			if (blockLength > 0) {
				buffer.write(b, blockStartOffset, blockLength);
			}
			while (remaining > 0 && (b[offset] == LINE_FEED || b[offset] == CARRIAGE_RETURN)) {
				write(b[offset]);
				offset++;
				remaining--;
			}
			blockStartOffset = offset;
		}
	}

	private void processBuffer() {
		charCount = 0;
		processLine(buffer.toString(charset));
		buffer.reset();
	}

	public Deque<String> getLog() {
		Deque<String> reval = new ArrayDeque<>();
		
		for(int i =0, len = logQueue.size();i < len;i++) {
			reval.add(logQueue.poll());
		}
		
		return reval;
	}

	@Override
	public void flush() {

	}

	public void processLine(String log) {
		logQueue.add(log);
	}

	@Override
	public void close() throws IOException {
		if (buffer.size() > 0) {
			processBuffer();
		}
		super.close();
	}

	private static final class CustomByteArrayOutputStream extends ByteArrayOutputStream {
		private CustomByteArrayOutputStream(final int size) {
			super(size);
		}

		public synchronized String toString(final Charset charset) {
			return new String(buf, 0, count, charset);
		}
	}
}
