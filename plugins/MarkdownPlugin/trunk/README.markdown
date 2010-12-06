Introduction
-------------

__MarkdownPlugin__ is a plugin for [jEdit][] that can render _Markdown text_ from the current buffer or from selected text. The resulting _HTML_ is written to a new buffer or copied to the clipboard. The plugin can also show a preview in a Web browser (using the **Info Viewer** plugin).

[_Markdown_][Markdown] is an easy-to-read, easy-to-write plain text format. Using this plugin you can convert it to structurally valid _XHTML_ (or _HTML_).

The plugin uses [pegdown][] as dependency. _Pegdown_ is a pure Java library for _Markdown_ processing. It's based on a [parboiled][] _PEG_ parser.

Installation
-------------

Download [MarkdownPlugin.tar.bz2][bundle] then unpack it to _YOUR HOME DIRECTORY_/**.jedit/jars**. It's all.

Run _jEdit_ and find _Markdown_ submenu in plugins' menu.

[jEdit]: http://jedit.org/ "jEdit - Programmer's Text Editor"
[Markdown]: http://daringfireball.net/projects/markdown/ "Markdown: Main"
[parboiled]: http://wiki.github.com/sirthias/parboiled/
[pegdown]: http://wiki.github.com/sirthias/pegdown/
[bundle]: http://code.pi.co.ua/jedit-markdown-plugin/downloads/MarkdownPlugin.tar.bz2
