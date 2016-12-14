package com.ecsteam.pcf;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DelegatingServletInputStream extends ServletInputStream {

	private final InputStream sourceStream;

	private boolean eof = false;

	public DelegatingServletInputStream(InputStream sourceStream) {
		this.sourceStream = sourceStream;
	}

	@Override
	public boolean isFinished() {
		return eof;
	}

	@Override
	public boolean isReady() {
		return !eof;
	}

	@Override
	public void setReadListener(ReadListener listener) {

	}

	@Override
	public int read() throws IOException {
		int readVal = this.sourceStream.read();

		eof = (readVal == -1);
		return readVal;
	}
}
