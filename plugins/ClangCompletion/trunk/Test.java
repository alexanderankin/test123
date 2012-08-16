
import java.util.Vector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.SwingUtilities;

public class Test
{
	private static Pattern errorPattern = Pattern.compile("((?:\\w:)?[^:]+?):(\\d+):(\\d+):\\s*(\\w+):(.+)"); 
	private static String test0 = "D:\\Developer\\workspace\\trunk\\BDMobile\\BDYY\\Client\\Classes\\scene\\battle_layer.cpp:95:3: error: use of undeclared identifier 'ssonBattleFinished'; did you mean 'onBattleFinished'?";
	private static String test1 = "D:\\Developer\\workspace\\trunk\\BDMobile\\BDYY\\Client\\Classes\\scene/battle_layer.h:130:7: note: 'onBattleFinished' declared here";
	private static String test2 = "D:\\Developer\\workspace\\trunk\\BDMobile\\BDYY\\Client\\Classes\\scene\\battle_layer.cpp:100:2: error: use of undeclared identifier 'ss'";
	private static String test3 = "D:\\Developer\\workspace\\trunk\\BDMobile\\BDYY\\Client\\Classes\\scene\\battle_layer.cpp:102:27: error: use of undeclared identifier 'pAni'";
	private static String test4 = "D:\\Developer\\workspace\\trunk\\BDMobile\\BDYY\\Client\\Classes\\scene\\battle_layer.cpp:103:86: error: use of undeclared identifier 'pAni'";
	
	public static void main(String args[])
	{
		parseError(test0);
		parseError(test1);
		parseError(test2);
		parseError(test3);
		parseError(test4);
	}
	
	private static void parseError(String clangOutput)
	{
		final Matcher matcher = errorPattern.matcher(clangOutput);
		if(matcher.find() && matcher.groupCount() >= 5)
		{
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
			System.out.println(matcher.group(4));
			System.out.println(matcher.group(5));
		}else
		{
			System.out.println("Invalid: " + clangOutput);
		}
	}
}
