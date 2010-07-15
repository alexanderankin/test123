<%-- a leading comment --%>
<%@ include file="/estore/common/common.jspf"%>
<%@ taglib uri="/WEB-INF/tlds/pageTemplate.tld" prefix="page"%>
<%--
  another comment

  with several


  lines of text

  just to test with
--%>
<%@ page import="com.avenueme.util.WebKeys"%>
<%@ page import="com.avenueme.beans.UserInfoBean"%>
<%@ page import="com.avenueme.beans.UserInfoFactory"%>
<html>
  <%--
    another comment


    with several


    lines of text


    just to test with


  --%>
  <head>
    <%--

      another comment


      with several



      lines of text




      just to test with





    --%>
    <style>
      /* a comment */
      li, p {
        background-color: lime
      }
      address {
        background-color: lime
      }
      * {
        color: lime
      }
      ul, p {
        color: red
      }
      *.t1 {
        color: lime
      }
      #foo {
        background-color: lime
      }
      p {
        background-color: red
      }
      p {
        background-color: red
      }
      p[title] {
        background-color: lime
      }
      address {
        background-color: red
      }
      address[title = "foo"] {
        background-color: lime
      }
      span[title = "a"] {
        background-color: red
      } </style>

    <script>
			/* Finds the lowest common multiple of two numbers */
			function LCMCalculator( x, y ) {
			    // constructor function
			    function checkInt( x ) {
			        // inner function
			        if( x % 1 != 0 ) 
			            throw new TypeError( x + " is not an integer" ); // exception throwing
			        return x;
			    } 
			    //semicolons are optional( but beware since this may cause consecutive lines to be
			    //erroneously treated as a single statement )
			    this.a = checkInt( x ) 
			    this.b = checkInt( y )
			} 
			// The prototype of object instances created by a constructor is 
			// that constructor's "prototype" property.
			LCMCalculator.prototype = {
			    // object literal
			    gcd : function( ) {
			        // method that calculates the greatest common divisor
			        // Euclidean algorithm:
			        var a = Math.abs( this.a ), b = Math.abs( this.b ), t; 
			        if( a < b ) {
			            t = b; b = a; a = t; // swap variables
			            
			        } 
			        while( b != 0 ) {
			            t = b; 
			            // Only need to calculate gcd once, so "redefine" this method.b = a % b; 
			            a = t;
			        } 
			        //( Actually not redefinition - it's defined on the instance itself,
			        // so that this.gcd refers to this "redefinition" instead of LCMCalculator.prototype.gcd.)
			        // Also, 'gcd' == "gcd", this[ 'gcd' ] == this.gcd
			        this[ 'gcd' ] = function( ) {
			            return a;
			        }; 
			        return a;
			    }, 
			    "lcm" /* can use strings here */ : function( ) {
			        // Variable names don't collide with object properties, e.g.|lcm| is not |this.lcm|.
			        // not using |this.a * this.b| to avoid FP precision issues 
			        var lcm = this.a / this.gcd( ) * this.b; 
			        // Only need to calculate lcm once, so "redefine" this method.
			        this.lcm = function( ) {
			            return lcm;
			        }; 
			        return lcm;
			    }, 
			    toString : function( ) {
			        return "LCMCalculator: a = " + this.a + ", b = " + this.b;
			    }
			};[[ 25, 55 ],[ 21, 56 ],[ 22, 58 ],[ 28, 56 ] ].map( function( pair ) {
			        // array literal + mapping function
			        return new LCMCalculator( pair[ 0 ], pair[ 1 ] );
			} ).sort( function( a, b ) {
			    // sort with this comparative function
			    return a.lcm( ) - b.lcm( );
			} ).forEach( function( obj ) {
			    /* Note: print( ) is a JS builtin function available in Mozilla's js CLI;
			    * it's functionally equivalent to Java's System.out.println( ).
			    * Within a web browser, print( ) is a very different function( opens the "Print Page" dialog ),
			    * so use something like document.write( ) instead.
			    */
			    print( obj + ", gcd = " + obj.gcd( ) + ", lcm = " + obj.lcm( ) );
			} ); 
			// Note: Array's map( ) and forEach( ) are predefined in JavaScript 1.6.
			// They are currently not available in all major JavaScript engines( including Internet Explorer's ),
			// but are shown here to demonstrate JavaScript's inherent functional nature.
    </script>
  </head>
  <body>
    <dsp:page xml="true">
      <table>
        <tr>
          <!-- a comment -->
          <td class="ma_mod_pageFrame_pageHeaderTD">
            <%-- insert ma_mod_pageHeader --%>
            <table cellpadding="0" cellspacing="0">
              <tr>
                <td class="ma_mod_pageHeader">
                  <c:set var="imgsrc" value="/estore/images/cart/ma/ma_mod/pageHeader_myAccount.jpg"/>
                  <img src="${imgsrc}"/>
                </td>
                <td class="ma_mod_pageHeader">
                  <img src="/estore/images/cart/ma/ma_mod/pageHeader_myAddressBook.jpg"/>
                  some text
                  <span>
                    some text
                  </span>
                  <strong>
                    <c:out value="${title}"/>
                  </strong>
                </td>
                <td width="100%">
                  <pre>
                  text
                  
                  
                  text
                  
                  more text
                  
                  
                  </pre>
                  &nbsp;
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </dsp:page>
    <%
			/* this property has a comma separated list of the just the names of the properties
			// files.  The files are located in the jar file at beauty/beautifiers/custom. */
			String propsFiles = jEdit.getProperty("plugin.beauty.beautifiers.custom");
			if (propsFiles == null || propsFiles.length() == 0) {
			    return ;
			} 
			String[] filenames = propsFiles.split(", ");
			File homeDir = jEdit.getPlugin("beauty.BeautyPlugin").getPluginHome();
			homeDir.mkdirs();
			for (String filename : filenames) {
			    filename = filename.trim();
			    File outfile = new File(homeDir, filename);
			    if (outfile.exists()) {
			        continue ;
			    } 
			    String resource = "beauty/beautifiers/custom/" + filename;
			    copyToFile(getClass().getClassLoader().getResourceAsStream(resource) , outfile);
    %>
    <b>
      some html goes here
    </b>
    <%
			
			} 
    %>
  </body>

</html>
