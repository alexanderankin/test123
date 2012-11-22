
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
	private static Pattern errorPattern = Pattern.compile("((?:\\w:)?[^:]+?):(\\d+):(\\d+):[\\d:\\-\\{\\}]*\\s*([^:]+):(.+)"); 
	private static String test0 = "/Users/wangyifu/Developer/workspace/trunk/BDMobile/BDCasino/DDZ/Client/ios/ddz_pp/ddz/ios/main.m:9:9: fatal error: 'UIKit/UIKit.h' file not found";
	private static String test1 = "D:\\Developer\\workspace\\trunk\\BDMobile\\BDYY\\Client\\Classes\\scene/battle_layer.h:130:7: note: 'onBattleFinished' declared here";
	private static String test2 = "D:\\Developer\\workspace\\trunk\\BDMobile\\BDYY\\Client\\Classes\\scene\\battle_layer.cpp:100:2: error: use of undeclared identifier 'ss'";
	private static String test3 = "D:\\Developer\\workspace\\trunk\\BDMobile\\BDYY\\Client\\Classes\\scene\\battle_layer.cpp:102:27: error: use of undeclared identifier 'pAni'";
	private static String test4 = "D:\\Developer\\workspace\\trunk\\BDMobile\\BDYY\\Client\\Classes\\scene\\battle_layer.cpp:103:86: error: use of undeclared identifier 'pAni'";
	private static String test5 = "D:\\Developer\\workspace\\trunk\\BDMobile\\BDYY\\Client\\Classes\\scene\\battle_layer.cpp:493:46:{493:3-493:17}: error: too few arguments to function call, expected 3, have 2";
	
	private static String testErrs[] = new String[]{
		"COMPLETION: BOOLEAN : [#JsonBox::Value::Type#]BOOLEAN",
		"COMPLETION: clear : [#void#]clear()",
		"COMPLETION: data : [#JsonBox::Value::ValueDataPointer#]data",
		"COMPLETION: DOUBLE : [#JsonBox::Value::Type#]DOUBLE",
		"COMPLETION: EMPTY_ARRAY : [#const JsonBox::Array#]EMPTY_ARRAY",
		"COMPLETION: EMPTY_BOOL : [#const bool#]EMPTY_BOOL",
		"COMPLETION: EMPTY_DOUBLE : [#const double#]EMPTY_DOUBLE",
		"COMPLETION: EMPTY_INT : [#const int#]EMPTY_INT",
		"COMPLETION: EMPTY_OBJECT : [#const JsonBox::Object#]EMPTY_OBJECT",
		"COMPLETION: EMPTY_STRING : [#const std::string#]EMPTY_STRING",
		"COMPLETION: escapeAllCharacters : [#std::string#]escapeAllCharacters(<#const std::string &str#>)",
		"COMPLETION: escapeMinimumCharacters : [#std::string#]escapeMinimumCharacters(<#const std::string &str#>)",
		"COMPLETION: escapeToUnicode : [#const std::string#]escapeToUnicode(<#char charToEscape#>)",
		"COMPLETION: getArray : [#const JsonBox::Array &#]getArray()[# const#]",
		"COMPLETION: getBoolean : [#bool#]getBoolean()[# const#]",
		"COMPLETION: getDouble : [#double#]getDouble()[# const#]",
		"COMPLETION: getInt : [#int#]getInt()[# const#]",
		"COMPLETION: getObject : [#const JsonBox::Object &#]getObject()[# const#]",
		"COMPLETION: getString : [#const std::string &#]getString()[# const#]",
		"COMPLETION: getType : [#JsonBox::Value::Type#]getType()[# const#]",
		"COMPLETION: INTEGER : [#JsonBox::Value::Type#]INTEGER",
		"COMPLETION: isArray : [#bool#]isArray()[# const#]",
		"COMPLETION: isBoolean : [#bool#]isBoolean()[# const#]",
		"COMPLETION: isDouble : [#bool#]isDouble()[# const#]",
		"COMPLETION: isHexDigit : [#bool#]isHexDigit(<#char digit#>)",
		"COMPLETION: isInteger : [#bool#]isInteger()[# const#]",
		"COMPLETION: isNull : [#bool#]isNull()[# const#]",
		"COMPLETION: isObject : [#bool#]isObject()[# const#]",
		"COMPLETION: isString : [#bool#]isString()[# const#]",
		"COMPLETION: isWhiteSpace : [#bool#]isWhiteSpace(<#char whiteSpace#>)",
		"COMPLETION: loadFromFile : [#void#]loadFromFile(<#const std::string &filePath#>)",
		"COMPLETION: loadFromStream : [#void#]loadFromStream(<#std::istream &input#>)",
		"COMPLETION: loadFromString : [#void#]loadFromString(<#const std::string &json#>)",
		"COMPLETION: NULL_VALUE : [#JsonBox::Value::Type#]NULL_VALUE",
		"COMPLETION: OBJECT : [#JsonBox::Value::Type#]OBJECT",
		"COMPLETION: operator!= : [#bool#]operator!=(<#const JsonBox::Value &rhs#>)[# const#]",
		"COMPLETION: operator< : [#bool#]operator<(<#const JsonBox::Value &rhs#>)[# const#]",
		"COMPLETION: operator<= : [#bool#]operator<=(<#const JsonBox::Value &rhs#>)[# const#]",
		"COMPLETION: operator= : [#JsonBox::Value &#]operator=(<#const JsonBox::Value &src#>)",
		"COMPLETION: operator== : [#bool#]operator==(<#const JsonBox::Value &rhs#>)[# const#]",
		"COMPLETION: operator> : [#bool#]operator>(<#const JsonBox::Value &rhs#>)[# const#]",
		"COMPLETION: operator>= : [#bool#]operator>=(<#const JsonBox::Value &rhs#>)[# const#]",
		"COMPLETION: operator[] : [#JsonBox::Value &#]operator[](<#const std::string &key#>)",
		"COMPLETION: operator[] : [#JsonBox::Value &#]operator[](<#const char *key#>)",
		"COMPLETION: operator[] : [#JsonBox::Value &#]operator[](<#size_t index#>)",
		"COMPLETION: output : [#void#]output(<#std::ostream &output#>{#, <#bool indent#>{#, <#bool escapeAll#>#}#})[# const#]",
		"COMPLETION: readArray : [#void#]readArray(<#std::istream &input#>, <#JsonBox::Array &result#>)",
		"COMPLETION: readNumber : [#void#]readNumber(<#std::istream &input#>, <#JsonBox::Value &result#>)",
		"COMPLETION: readObject : [#void#]readObject(<#std::istream &input#>, <#JsonBox::Object &result#>)",
		"COMPLETION: readString : [#void#]readString(<#std::istream &input#>, <#std::string &result#>)",
		"COMPLETION: readToNonWhiteSpace : [#void#]readToNonWhiteSpace(<#std::istream &input#>, <#char &currentCharacter#>)",
		"COMPLETION: setArray : [#void#]setArray(<#const JsonBox::Array &newArray#>)",
		"COMPLETION: setBoolean : [#void#]setBoolean(<#bool newBoolean#>)",
		"COMPLETION: setDouble : [#void#]setDouble(<#double newDouble#>)",
		"COMPLETION: setInt : [#void#]setInt(<#int newInt#>)",
		"COMPLETION: setNull : [#void#]setNull()",
		"COMPLETION: setObject : [#void#]setObject(<#const JsonBox::Object &newObject#>)",
		"COMPLETION: setString : [#void#]setString(<#const std::string &newString#>)",
		"COMPLETION: STRING : [#JsonBox::Value::Type#]STRING",
		"COMPLETION: type : [#JsonBox::Value::Type#]type",
		"COMPLETION: UNKNOWN : [#JsonBox::Value::Type#]UNKNOWN",
		"COMPLETION: Value : Value::",
		"COMPLETION: writeToFile : [#void#]writeToFile(<#const std::string &filePath#>{#, <#bool indent#>{#, <#bool escapeAll#>#}#})[# const#]",
		"COMPLETION: writeToStream : [#void#]writeToStream(<#std::ostream &output#>{#, <#bool indent#>{#, <#bool escapeAll#>#}#})[# const#]",
	"COMPLETION: ~Value : [#void#]~Value()"};
	
	public static void main(String args[])
	{
		/* parseError(test0);
		parseError(test1);
		parseError(test2);
		parseError(test3);
		parseError(test4);
		parseError(test5); */
		int headLength = "COMPLETION: ".length();
		for(int l = 0; l < testErrs.length; l++)
		{
			String err = testErrs[l];
			System.out.println(err);
			
			StringBuilder label = new StringBuilder(err);
			int indexOfDesc = label.indexOf(":", headLength) + 1;
			if(indexOfDesc > 0)
			{
				label.delete(0, indexOfDesc);
			}
			
			int indexOfSep = 0;
			while((indexOfSep = label.indexOf("<#")) >= 0)
			{
				label.delete(indexOfSep, indexOfSep + 2);
			}
			
			indexOfSep = 0;
			while((indexOfSep = label.indexOf("#>")) >= 0)
			{
				label.delete(indexOfSep, indexOfSep + 2);
			}
			
			while((indexOfSep = label.indexOf("{#")) >= 0)
			{
				label.delete(indexOfSep, indexOfSep + 2);
			}
			
			indexOfSep = 0;
			while((indexOfSep = label.indexOf("#}")) >= 0)
			{
				label.delete(indexOfSep, indexOfSep + 2);
			}
			
			StringBuilder desc = new StringBuilder(label);
			indexOfSep = 0;
			while((indexOfSep = desc.indexOf("[#")) >= 0)
			{
				int indexOfSepend = desc.indexOf("#]", indexOfSep);
				if(indexOfSepend > indexOfSep)
				{
					desc.delete(indexOfSep, indexOfSepend + 2);
				}
			}
			
			System.out.println(label);
			System.out.println(desc);
		}
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
