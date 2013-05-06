package com.vedicis.csm.lua;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;

import com.kpouer.jedit.lua.FunctionAsset;
import errorlist.DefaultErrorSource;
import errorlist.ErrorSource;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.buffer.JEditBuffer;
import org.gjt.sp.util.Log;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.ast.Block;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.FuncArgs;
import org.luaj.vm2.ast.FuncBody;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.NameScope;
import org.luaj.vm2.ast.ParList;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.TableConstructor;
import org.luaj.vm2.ast.TableField;
import org.luaj.vm2.ast.Visitor;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.luaj.vm2.parser.ParseException;
import org.luaj.vm2.parser.TokenMgrError;

/**
 * @author Matthieu Casanova
 */
public class LuaParser
{
	private static void parse(String lua)
	{
		org.luaj.vm2.parser.LuaParser parser = new MyLuaParser(new StringReader(lua));
		try
		{
			Chunk chunk = parser.Chunk();


			chunk.accept(new Visitor()
			{
				@Override
				public void visit(Exp.FieldExp field)
				{
					if (field.lhs instanceof Exp.NameExp)
					{
						String name = ((Exp.NameExp) field.lhs).name.name;
						if ("P".equals(name))
						{
							System.out.println("UserPackage var : " + field.name.name);
						}
						else if ("M".equals(name))
						{
							System.out.println("state var : " + field.name.name);
						}
						else if ("S".equals(name))
						{
							System.out.println("service var : " + field.name.name);
						}
						else if ("G".equals(name))
						{
							System.out.println("Global parameter: " + field.name.name);
						}
						else if ("N".equals(name))
						{
							System.out.println("Using service: " + field.name.name);
						}
					}
				}

			});

		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		catch (TokenMgrError e)
		{
			e.printStackTrace();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	private static void execute(String lua)
	{
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine e = mgr.getEngineByExtension(".lua");
		try
		{
			Object eval = e.eval(lua);
//			System.out.println(eval);
		}
		catch (ScriptException e1)
		{
			e1.printStackTrace();
		}
	}

//	public static void main(String[] args)
//	{
//		parse("tutu=3\nP.MAXVOL = 2\n"
//			  + "Print(T.QUOTA) ");
//	}

	public static void main(String[] args) throws IOException
	{
		if (args.length != 0)
		{
			File file = new File(args[0]);
			if (file.isDirectory())
			{
				File[] files = file.listFiles();
				for (File f : files)
				{
					if (f.getName().endsWith(".lua"))
					{
						System.out.println("Parsing " + f.getName());
						parseFile(f);
						System.out.println();
						System.out.println();
					}
				}
			}
			else
			{
				execute(toString(file));
			}
		}
	}

	private static String toString(File file) throws IOException
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			StringWriter writer = new StringWriter((int) file.length());
			char[] buf = new char[100000];
			int read = reader.read(buf);
			while (read != -1)
			{
				writer.write(buf, 0, read);
				read = reader.read(buf);
			}
			return writer.toString();
		}
		finally
		{
			reader.close();
		}
	}

	private static void parseFile(File file) throws IOException
	{
		String lua = toString(file);
		parse(lua);
	}

	private static class MyLuaParser extends org.luaj.vm2.parser.LuaParser
	{
		private MyLuaParser(Reader reader)
		{
			super(reader);
		}

		@Override
		public ParseException generateParseException()
		{
			ParseException parseException = super.generateParseException();
			parseException.currentToken = token;
			return parseException;
		}
	}
}
