Markdown Plugin
================
__Author:__ Vitaliy Berdinskikh  
__Version:__ 0.1.1

__MarkdownPlugin__ is a plugin for **jEdit** that can render _Markdown text_ from the current buffer or from selected text. The resulting _HTML_ is written to a new buffer or copied to the clipboard. The plugin can also show a preview in a web-browser (using the **Info Viewer** plugin).

[_Markdown_][Markdown] is an easy-to-read, easy-to-write plain text format. Using this plugin you can convert it to structurally valid _XHTML_ (or _HTML_).

__MarkdownPlugin__ based on [pegdown][] - the pure java Markdown processor.

- - -

Installation
-------------

Use the plugin manager for easiest installation.

For manual installation download [MarkdownPlugin.tar.bz2][bundle] then unpack it to _YOUR HOME DIRECTORY_/**.jedit/jars**.

Usage
------

Two actions provided by this plugin are available in the _Markdown_ menu: render text and show a preview in a web broser. For first action you can choose in plugin's options where plugin put rendered _HTML_: a new buffer, the jEdit's clipboard or show a preview in a web broser (the same as second action).

- - -

Markdown's Formatting Syntax
----------------------------

* __Paragraphs__  
  A paragraph is one or more consecutive lines of text separated by one or more blank lines. Normal paragraphs should not be indented with spaces or tabs:

        This is a paragraph. It has two sentences.

        This is another paragraph. It also has two sentences.

* __Line return__  
  Line breaks inserted in the text are removed from the final result: the web browser is in charge of breaking the lines depending of the available space. To force a line break, insert two spaces at the end of the line.
* __Emphasized text__

        *emphasis* or _emphasis_ (more common)  (e.g., italics)

        **strong emphasis** (more common) or __strong emphasis__ (e.g., boldface)

* __Code__  
  To include code (formatted in monospace font), you can either:
    * use &#96;&#96; for one line of code, like in

        &#96;some code&#96;

    * indent several lines of code by at least four spaces which prevents Markdown from removing all whitespaces, breaking indentation and code layout.
* __Lists__

        * An item in a bulleted (unordered) list
            * A subitem, indented with 4 spaces
        * Another item in a bulleted list

        1. An item in an enumerated (ordered) list
        2. Another item in an enumerated list

* __Headings__  
  HTML headings are produced by placing a number of hashes before the header text corresponding to the level of heading desired (HTML offers six levels of headings), like so:
  
        # First-level heading

        #### Fourth-level heading

    The first two heading levels also have an alternate syntax:

        First-level heading
        ===================

        Second-level heading
        --------------------

* __Blockquotes__

        > This text will be enclosed in an HTML blockquote element.
        > Blockquote elements are reflowable. You may arbitrarily
        > wrap the text to your liking, and it will all be parsed
        > into a single blockquote element.

* __Links__  
  Links may be included inline:

        [link text here](link.address.here "link title here")

    Alternatively, links can be placed in footnotes outside of the paragraph, being referenced with some sort of reference tag. For example, including the following inline:

        [link text here][linkref]

    would produce a link if the following showed up outside of the paragraph (or at the end of the document):

        [linkref]: link.address.here "link title here"

* __Images__  
  Referring to images is similar to including links. The syntax requires an exclamation point to indicate the link refers to an image.

    The image address may be included inline, as with links:

        ![Alt text here](Image URL here "Image title here")

    It may also be referred to via a reference:

        ![Alt text here][imageref]

    Here, imageref refers to information somewhere after the image:

        [imageref]: image.url.here "Image title here"

* __Horizontal rules__  
  Horizontal rules are created by placing three or more hyphens, asterisks, or underscores on a line by themselves. You may use spaces between the hyphens or asterisks. Each of the following lines will produce a horizontal rule:

        * * *
        ***
        *****
        - - -
        ---------------------------------------

- - -

Supported Markdown Extensions
-------------------
You can choose these extensions in plugin's options. 

* [__Abbreviation__][abbr]  
  Support for abbreviations (HTML tag <abbr>). How it works is pretty simple: create an abbreviation definition like this:

        *[HTML]: Hyper Text Markup Language
        *[W3C]:  World Wide Web Consortium

    then, elsewhere in the document, write text such as:

        The HTML specification
        is maintained by the W3C.

    and any instance of those words in the text will become:

        The <abbr title="Hyper Text Markup Language">HTML</abbr> specification
        is maintained by the <abbr title="World Wide Web Consortium">W3C</abbr>.

    Abbreviation are case-sensitive, and will span on multiple words when defined as such. An abbreviation may also have an empty definition, in which case <abbr> tags will be added in the text but the title attribute will be omitted.

        Operation Tigra Genesis is going well.

        *[Tigra Genesis]:

    Abbreviation definition can be anywhere in the document. They are stripped from the final document.
* [__Autolinks__][autolinks]  
  Markdown plugin will autolink standard URLs, so if you want to link to a URL (instead of setting link text), you can simply enter the URL and it will be turned into a link to that URL.
* [__Hardwraps__][hardwraps]  
  You can hard wrap paragraphs of text and they will be combined into a single paragraph. The next paragraph contains two phrases separated by a single newline character:

        Roses are red
        Violets are blue

* __Quotes__  
  Beautifys single quotes, double quotes and double angle quotes (&laquo; and &raquo;).
* __Smarts__  
  Beautifys apostrophes, ellipsises ("..." and ". . .") and dashes ("--" and "---").
* __Smartypants__  
  Convenience extension enabling both, **smarts** and **quotes**, at once.
* [__Tables__][tables]

        |             |          Grouping           ||
        First Header  | Second Header | Third Header |
         ------------ | :-----------: | -----------: |
        Content       |          *Long Cell*        ||
        Content       |   **Cell**    |         Cell |
        
        New section   |     More      |         Data |
        And more      |            And more          |
        [Prototype table]

* __No blocks__  
  Suppresses HTML blocks.
* __No inline HTML__  
  Suppresses inline HTML tags.
* __No hypertext__   
  Suppresses HTML blocks as well as inline HTML tags.

[Markdown]: http://daringfireball.net/projects/markdown/ "Markdown: Main"
[bundle]: http://code.pi.co.ua/jedit-markdown-plugin/downloads/MarkdownPlugin.tar.bz2
[pegdown]: http://wiki.github.com/sirthias/pegdown/
[abbr]: http://michelf.com/projects/php-markdown/extra/#abbr "PHP Markdown Extra: Abbreviation"
[autolinks]: http://github.github.com/github-flavored-markdown/ "Github-flavoured-Markdown: URL autolinking"
[hardwraps]: http://github.github.com/github-flavored-markdown/ "Github-flavoured-Markdown: Newlines"
[tables]: http://fletcherpenney.net/multimarkdown/users_guide/multimarkdown_syntax_guide/ "MultiMarkdown: tables"
