package gdb.jni;

import gdb.jni.GdbProcess.Reader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessReader extends BufferedReader implements Reader {

	public ProcessReader(InputStream inputStream) {
		super(new InputStreamReader(inputStream));
	}

}
