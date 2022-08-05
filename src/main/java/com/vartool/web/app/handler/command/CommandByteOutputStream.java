package com.vartool.web.app.handler.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;

import com.vartool.web.constants.BlankConstants;
import com.vartool.web.constants.VartoolConstants;

/**
 * command output stream
* 
* @fileName	: CommandByteOutputStream.java
* @author	: ytkim
 */
public class CommandByteOutputStream extends OutputStream {
	private static final int INTIAL_SIZE = 132;

	private static final int CR = 13;

	private static final int LF = 10;

	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(INTIAL_SIZE);

	private boolean skip = false;

	private final int level;
	
	private final String charset;
	
	private StringBuffer sb = new StringBuffer();

	public CommandByteOutputStream() {
		this(VartoolConstants.CHAR_SET);
	}

	public CommandByteOutputStream(String charset) {
		this(charset, 999);
	}
	public CommandByteOutputStream(String charset, int level) {
		this.level = level;
		if(!StringUtils.isBlank(charset) && Charset.isSupported(charset)) {
			this.charset = charset;
		}else {
			this.charset = VartoolConstants.CHAR_SET;
		}
	}

	public void write(int cc) throws IOException {
		byte c = (byte) cc;
		if (c == LF || c == CR) {
			if (!this.skip)
				processBuffer();
		} else {
			this.buffer.write(cc);
		}
		this.skip = (c == CR);
	}

	public void flush() {
		if (this.buffer.size() > 0)
			processBuffer();
	}

	public void close() throws IOException {
		if (this.buffer.size() > 0)
			processBuffer();
		super.close();
	}

	public int getMessageLevel() {
		return this.level;
	}

	public void write(byte[] b, int off, int len) throws IOException {
		int offset = off;
		int blockStartOffset = offset;
		int remaining = len;
		while (remaining > 0) {
			while (remaining > 0 && b[offset] != 10 && b[offset] != 13) {
				offset++;
				remaining--;
			}
			int blockLength = offset - blockStartOffset;
			if (blockLength > 0)
				this.buffer.write(b, blockStartOffset, blockLength);
			while (remaining > 0 && (b[offset] == 10 || b[offset] == 13)) {
				write(b[offset]);
				offset++;
				remaining--;
			}
			blockStartOffset = offset;
		}
	}

	public void processBuffer() {
		try {
			processLine(this.buffer.toString(charset));
		} catch (UnsupportedEncodingException e) {
			processLine(this.buffer.toString());
			e.printStackTrace();
		}finally {
			this.buffer.reset();
		}
	}

	public void processLine(String line) {
		processLine(line, this.level);
	}
	
	public void processLine(String line, int arg1) {
		sb.append(line).append(BlankConstants.NEW_LINE_CHAR);
	}
	
	public String getLog() {
		String result = sb.toString();
		sb.setLength(0);
		return result;
	}

	public String getCharset() {
		return charset;
	}
}