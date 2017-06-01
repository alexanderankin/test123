
/*
 * OuterBeautifier.java - Beautifier calling an external program
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2015, Eric Le Lay
 *
 * The OuterBeauty plugin is licensed under the GNU General Public License.
 */
package outerbeauty;


import beauty.beautifiers.Beautifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;

import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.IOUtilities;
import org.gjt.sp.util.Log;


/**
 * Base class for beautifiers using external programs.
 *
 * <p>Only {@link OuterBeautifier#getCommandLine()} is required
 * at the moment.</p>
 *
 * <p>The text to beautify is piped to the external program
 * and the result is gathered from its standard output.</p>
 *
 * <p>Standard error is logged.</p>
 **/
public abstract class OuterBeautifier extends Beautifier {

	/**
	 * Beauty plugin interface
	 **/
	@Override
	public String beautify( String text ) {
		if ( isTempFileRequired() ) {
			throw new UnsupportedOperationException( "only supports piping atm" );
		}


		return runBeautifier( text );
	}


	/**
	 * the external beautifier to run.
	 *
	 * This is the only required method for subclasses.
	 * First item in the returned list is the program
	 * to run. An absolute path may not be necessary.
	 *
	 * @return program and parameters to run
	 *
	 * @see ProcessBuilder#command(List)
	 **/
	protected abstract List<String> getCommandLine();


	protected void setEnvironment( Map<String, String> env ) {
		return;
	}


	protected File getWorkingDirectory() {
		return null;
	}


	/**
	 * Unimplemented.
	 * If the need for a temporary file arises,
	 * subclasses will be able to tell by overriding this method
	 * if they need it or not
	 * @return true if a temporary file is required
	 */
	protected boolean isTempFileRequired() {
		return false;
	}


	/**
	 * text to beautify is sent as UTF-8 by default.
	 * Override this method if the external program
	 * requires input in another encoding.
	 * @return the Charset to use. Not null, thanks!
	 */
	protected Charset getCharset() {
		try {
			return Charset.forName( "UTF-8" );
		}
		catch ( UnsupportedCharsetException e ) {
			throw new AssertionError( "UTF-8 should be available", e );
		}
	}


	/**
	 * beautify text using external program.
	 *
	 * Main method, starting the program, reading and writing to it, returning
	 * formatted text.
	 *
	 * Subclasses are not expected to override this.
	 *
	 * @param text text to format
	 * @return formatted text or null on error
	 */
	protected String runBeautifier( String text ) {
		List<String> cmdLine = getCommandLine();

		ProcessBuilder builder = new ProcessBuilder( cmdLine );
		if ( getWorkingDirectory() != null ) {
			builder.directory( getWorkingDirectory() );
		}


		Map<String, String> env = builder.environment();

		setEnvironment( env );

		Process p = null;
		try {
			p = builder.start();
		}
		catch ( IOException e ) {
			reportError( "Error launching external beautifier", e,
			null, null );
			return null;
		}

		Charset charset = getCharset();

		Outputter toProcess = new Outputter( new OutputStreamWriter( p.getOutputStream(), charset ), text );
		InputExhauster fromProcess = new InputExhauster( new InputStreamReader( p.getInputStream(), charset ), text.length() );
		InputExhauster errorsProcess = new InputExhauster( new InputStreamReader( p.getErrorStream(), charset ), 0 );

		fromProcess.start();
		errorsProcess.start();
		toProcess.start();

		int ret;
		try {
			ret = p.waitFor();
			toProcess.join();
			fromProcess.join();
			errorsProcess.join();
		}
		catch ( InterruptedException e ) {
			reportError( "Interrupted waiting for external beautifier", e,
			fromProcess, errorsProcess );
			return null;
		}
		if ( ret != 0 ) {
			reportError( "external beautifier non-zero exit code: "  +  ret, null,
			fromProcess, errorsProcess );
			return null;
		}
		else if ( toProcess.error != null ) {
			reportError( "couldn't send all the text to external beautifier", toProcess.error,
			fromProcess, errorsProcess );
			return null;
		}
		else if ( fromProcess.error != null ) {
			reportError( "couldn't read all the text from external beautifier", fromProcess.error,
			fromProcess, errorsProcess );
			return null;
		}
		else if ( errorsProcess.error != null ) {
			reportError( "couldn't read all the errors from external beautifier", errorsProcess.error,
			fromProcess, errorsProcess );
			return null;
		}
		else if ( errorsProcess.buffer.length()  >  0 ) {
			Log.log( Log.WARNING, "external beautifier outputs error",
			errorsProcess.buffer.toString() );
		}


		return fromProcess.buffer.toString();
	}


	protected void reportError( String message, Throwable cause,
	InputExhauster fromProcess, InputExhauster errorsProcess ) {

		Log.log( Log.ERROR, OuterBeautifier.class, message, cause );
		if ( fromProcess != null ) {
			Log.log( Log.ERROR, OuterBeautifier.class, "external beautifier output:\n"  +  fromProcess.buffer.toString() );
		}


		if ( errorsProcess != null ) {
			Log.log( Log.ERROR, OuterBeautifier.class, "external beautifier error:\n"  +  errorsProcess.buffer.toString() );
		}


		jEdit.getActiveView().getStatus().setMessage( "Error beautifying. See Activity log." );
	}



	/**
	 * reads fully from provided input and stores in buffer.
	 *
	 * It's not expected that large amounts of data are read by this
	 * class : it stores everything in memory.
	 **/
	protected static class InputExhauster extends Thread {

		private Reader in;
		private StringBuilder buffer;
		private IOException error;




		InputExhauster( Reader in, int expectedLength ) {
			buffer = ( expectedLength  >  0 ) ? new StringBuilder( expectedLength ) : new StringBuilder();
			this.in = in;
		}


		public void run() {
			char[] buf = new char [4096];
			int read;

			try {
				while ( ( read = in.read( buf ) ) !=  - 1 ) {
					if ( read  >  0 ) {
						buffer.append( buf, 0, read );
					}
				}
			}
			catch ( IOException e ) {
				error = e;
			}
			IOUtilities.closeQuietly( in );
		}
	}



	/**
	 * writes text to output and exits.
	 *
	 * Should it feed the external beautifier smaller pieces?
	 **/
	protected static class Outputter extends Thread {

		private Writer out;
		private String toWrite;
		private IOException error;




		Outputter( Writer out, String toWrite ) {
			this.out = out;
			this.toWrite = toWrite;
		}


		public void run() {
			try {
				out.write( toWrite );
			}
			catch ( IOException e ) {
				error = e;
			}
			IOUtilities.closeQuietly( out );
		}
	}
}
