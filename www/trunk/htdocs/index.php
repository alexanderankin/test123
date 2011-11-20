<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link href="https://plus.google.com/105573242513550708576" rel="publisher" />
<?php
		$page = trim($_GET['page']);

		if ($page == "")
			$page = "main";
?>
<title> jEdit - Programmer's Text Editor - 
<?php include($page.".title"); ?>
</title>

<link href="stylesheet.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
  var uvOptions = {};
  (function() {
    var uv = document.createElement('script'); uv.type = 'text/javascript'; uv.async = true;
    uv.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + 'widget.uservoice.com/PBf9IJPQtpxVh9tbgSth2A.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(uv, s);
  })();
</script>
</head>

<body>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr align="center" valign="middle" bgcolor="#DDDDDD">
    <td colspan="7" nowrap="nowrap"><center>
      <p class="header_text">Last Site Update: 19 November 2011 | <?php /**/?>Latest Version: <a class="header_text" href="CHANGES45.txt">4.5pre1</a> | <?php /**/?>Stable Version: <a class="header_text" href="CHANGES44.txt">4.4.2</a></p>
    </center></td>
  </tr>
  <tr bgcolor="#666666">
    <td height="1" colspan="7"></td>
  </tr>
  <tr>
    <td height="9" colspan="7"></td>
  </tr>
  <tr>
    <td width="9" rowspan="3">&nbsp;</td>
    <td height="100" colspan="3" align="center" valign="middle">
	<table width="100%"  border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="200" height="100" align="center" valign="middle">&nbsp;</td>
        <td align="center" valign="middle">

        <img src="images/logo.png" width="270" height="101" />

  </td>
        <td width="200" align="left" valign="middle">
			<table border="0" align="right" cellpadding="3" cellspacing="0">
            <tr valign="middle">
              <td colspan="2"><center>
                  <a href="index.php?page=download"><img src="images/logo64.png" width="64" height="64" border="0" /></a>
              </center></td>
            </tr>
            <tr valign="middle">
              <td><a href="index.php?page=download"><img src="images/button_large.png" width="18" height="18" border="0" /></a></td>
              <td><a class="download_text" href="index.php?page=download">Download</a></td>
            </tr>
        </table></td>
      </tr>
    </table></td>
    <td width="9" rowspan="3" align="right" valign="top" nowrap="nowrap">&nbsp;</td>
  </tr>
  <tr>
    <td height="3" colspan="3">&nbsp;</td>
  </tr>
  <tr>
    <td valign="top">
    <?php include($page.".html"); ?>
    </td>
    <td width="9" valign="top">&nbsp;</td>
    <td width="200" align="right" valign="top"><table border="0" cellpadding="1" cellspacing="0" bgcolor="#666666">
      <tr>
        <td align="right" valign="top"><table width="200" border="0" cellpadding="3" cellspacing="0" bgcolor="#E6E6E6">
            <tr bgcolor="#BBBBBB">
              <td colspan="2" class="menu_headings">About</td>
            </tr>
            <tr>
              <td><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php">Main Site </a></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php?page=features">Features</a></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php?page=compatibility">Compatibility</a></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php?page=screenshots">Screenshots</a></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php?page=images">Icons and Images</a></td>
            </tr>
            <tr>
              <td><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php?page=reviews">Reviews</a></td>
            </tr>
            <tr>
              <td><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php?page=download">Download</a></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="http://plugins.jedit.org">Plugins</a></td>
            </tr>
            <tr bgcolor="#BBBBBB">
              <td colspan="2"><span class="menu_headings">Community</span></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="http://community.jedit.org/">jEdit Community</a></td>
            </tr>         
            <tr bgcolor="#BBBBBB">
              <td colspan="2"><span class="menu_headings">Help</span></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php?page=quickstart">Quick Start Guide</a></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php?page=docs">Online Documentation</a></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php?page=feedback">Feedback and Support</a></td>
            </tr>
            <tr bgcolor="#BBBBBB">
              <td colspan="2"><span class="menu_headings">Development Links</span></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="index.php?page=devel">Development</a></td>
            </tr>
            <tr>
              <td width="12"><img src="images/button.png" width="12" height="12" /></td>
              <td><a class="menu_links" href="http://www.sourceforge.net/projects/jedit/">SourceForge Project</a></td>
            </tr>
            <tr>
              <td colspan="2" class="menu_links" align="center">
                <a href="http://sourceforge.net">
				   <img src="http://sourceforge.net/sflogo.php?group_id=588"
				    title="online services provided by Sourceforge.net"
                   width="88" height="31" border="0" alt="SourceForge Logo" />
			    </a>
              </td>
            </tr>
            <tr>
              <td colspan="2" class="menu_links" align="center">
                <A href="http://www.ej-technologies.com/products/jprofiler/overview.html"><IMG
                   src="images/jProfiler.png"
				   title="JProfiler licenses provided free to jEdit developers for improving jEdit."
                   width="100" height="26" border="0" alt="JProfiler Logo"></A>
              </td>
            </tr>
            
            <tr  bgcolor="#BBBBBB">
              <td colspan="2"><span class="menu_headings">Donate</span></td>
            </tr>
            <tr>
              <td colspan="2" class="menu_links" align="center">
                <a href="http://sourceforge.net/project/project_donations.php?group_id=588"><img border="0" width="72" height="29" src="http://sourceforge.net/images/x-click-but7.gif" 
				title="Make a donation with PayPal - it's fast, free and secure!" /></a>
              </td>
            </tr>
        </table></td>
      </tr>
    </table></td>
  </tr>
  <tr align="center" valign="middle">
    <td height="46" colspan="7"><img src="made-with-jedit-9.png" width="120" height="40" />    </td>
  </tr>
</table>
</body>
</html>
