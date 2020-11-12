//------------------------------------------------------------------------------
// Copyright (c) 1997-2000 Servidium, Inc. All Rights Reserved.
//
// This SOURCE CODE FILE, which has been provided by Servidium as part
// of a Servidium product for use ONLY by licensed users of the product,
// includes CONFIDENTIAL and PROPRIETARY information of Servidium.  
//
// USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS OF THE
// LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH THE PRODUCT.
//
// IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD SERVIDIUM, ITS RELATED
// COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY CLAIMS
// OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR DISTRIBUTION OF
// YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES ARISING OUT OF
// OR RESULTING FROM THE USE, MODIFICATION, OR DISTRIBUTION OF PROGRAMS OR
// FILES CREATED FROM, BASED ON, AND/OR DERIVED FROM THIS SOURCE CODE FILE.
//------------------------------------------------------------------------------

package antfarm;

import java.util.Properties;

/**
 * 
 * @author <a href="mailto:support@servidium.com">Richard Wan</a>
 */

public class AntCommandParserTest extends junit.framework.TestCase
{
   //--------------------------------------------------------------------------
   //   Constants:                                                   
   //--------------------------------------------------------------------------
   
   //--------------------------------------------------------------------------
   //   Protected Variables:                                                   
   //--------------------------------------------------------------------------

   //--------------------------------------------------------------------------
   //   Private Variables:                                                     
   //--------------------------------------------------------------------------
   
   //--------------------------------------------------------------------------
   //   Constructors:                                                          
   //--------------------------------------------------------------------------
   public AntCommandParserTest(String s)
   {
      super(s);
   }
   //--------------------------------------------------------------------------
   //   Public Methods:                                                        
   //--------------------------------------------------------------------------
   public static void main(String[] args)
   {
      junit.textui.TestRunner.run(AntCommandParserTest.class);
   }
   
   public void testPairs()
   {
     String command = "!compile src=hello build.compiler=jikes";
     Properties props = AntCommandParser.parseAntCommandProperties(command);
     assertEquals("jikes", props.get("build.compiler"));
     assertEquals("hello", props.get("src"));
     assertEquals(null, props.get("compile"));
     assertEquals(null, props.get("!compile"));
   }

   //--------------------------------------------------------------------------
   //   Protected Methods:                                                   
   //--------------------------------------------------------------------------

   //--------------------------------------------------------------------------
   //   Private Methods:                                                       
   //--------------------------------------------------------------------------

   //--------------------------------------------------------------------------
   //   Nested Top-Level Classes or Interfaces
   //--------------------------------------------------------------------------
}

