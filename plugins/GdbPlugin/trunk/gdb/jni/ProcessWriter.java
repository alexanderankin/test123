package gdb.jni;

import gdb.jni.GdbProcess.Writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ProcessWriter extends BufferedWriter implements Writer {

	public ProcessWriter(OutputStream outputStream) {
		super(new OutputStreamWriter(outputStream));
	}

	@Override
	public void write(String str) throws IOException {
		super.write(str);
		super.flush();
	}
	
}
