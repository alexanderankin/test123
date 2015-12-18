
package clangbeauty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class StyleOptions {

    public static final String DEFAULT = "AAAAA_default_";      // lots of A's so this sorts first
    
    // <language, style options>
    private TreeMap<String, TreeMap<String, String>> options = new TreeMap<String, TreeMap<String, String>>();
    
    
    public String[] getLanguageNames() {
        String[] names = new String[options.size()];
        int i = 0;
        for (String name : options.keySet()) {
            names[i++] = name;
        }
        return names;
    }

    // these are all the names as of clang 3.8
    public String[] getOptionNames() {
        return new String[] {
            "Language",
            "BasedOnStyle",
            "AccessModifierOffset",
            "AlignAfterOpenBracket",
            "AlignConsecutiveAssignments",
            "AlignConsecutiveDeclarations",
            "AlignEscapedNewlinesLeft",
            "AlignOperands",
            "AlignTrailingComments",
            "AllowAllParametersOfDeclarationOnNextLine",
            "AllowShortBlocksOnASingleLine",
            "AllowShortCaseLabelsOnASingleLine",
            "AllowShortFunctionsOnASingleLine",
            "AllowShortIfStatementsOnASingleLine",
            "AllowShortLoopsOnASingleLine",
            "AlwaysBreakAfterDefinitionReturnType",
            "AlwaysBreakBeforeMultilineStrings",
            "AlwaysBreakTemplateDeclarations",
            "BinPackArguments",
            "BinPackParameters",
            "BraceWrapping",
            "BreakAfterJavaFieldAnnotations",
            "BreakBeforeBinaryOperators",
            "BreakBeforeBraces",
            "BreakBeforeTernaryOperators",
            "BreakConstructorInitializersBeforeComma",
            "ColumnLimit",
            "CommentPragmas",
            "ConstructorInitializerAllOnOneLineOrOnePerLine",
            "ConstructorInitializerIndentWidth",
            "ContinuationIndentWidth",
            "Cpp11BracedListStyle",
            "DerivePointerAlignment",
            "DisableFormat",
            "ExperimentalAutoDetectBinPacking",
            "ForEachMacros",
            "IncludeCategories",
            "IndentCaseLabels",
            "IndentWidth",
            "IndentWrappedFunctionNames",
            "KeepEmptyLinesAtTheStartOfBlocks",
            "MacroBlockBegin",
            "MacroBlockEnd",
            "MaxEmptyLinesToKeep",
            "NamespaceIndentation",
            "ObjCBlockIndentWidth",
            "ObjCSpaceAfterProperty",
            "ObjCSpaceBeforeProtocolList",
            "PenaltyBreakBeforeFirstCallParameter",
            "PenaltyBreakComment",
            "PenaltyBreakFirstLessLess",
            "PenaltyBreakString",
            "PenaltyExcessCharacter",
            "PenaltyReturnTypeOnItsOwnLine",
            "PointerAlignment",
            "SpaceAfterCStyleCast",
            "SpaceBeforeAssignmentOperators",
            "SpaceBeforeParens",
            "SpaceInEmptyParentheses",
            "SpacesBeforeTrailingComments",
            "SpacesInAngles",
            "SpacesInCStyleCastParentheses",
            "SpacesInContainerLiterals",
            "SpacesInParentheses",
            "SpacesInSquareBrackets",
            "Standard",
            "TabWidth",
            "UseTab"
        };
    }
    
    /**
     * These are the values for each style option as of clang 3.8. These are used
     * by DockablePanel to build the displayed choices
     * @return A string array of valid choices for each option. An empty string array
     * indicates the option allows a free-form text value, a string array containing
     * only "-1" indicates the option allows a numeric value.
     */
    public String[] getOptionChoices( String optionName ) {
        switch ( optionName ) {
            // numeric options
            case "AccessModifierOffset":
            case "ColumnLimit":
            case "ContinuationIndentWidth":
            case "IndentWidth":
            case "MaxEmptyLinesToKeep":
            case "ObjCBlockIndentWidth":
            case "PenaltyBreakBeforeFirstCallParameter":
            case "PenaltyBreakComment":
            case "PenaltyBreakFirstLessLess":
            case "PenaltyBreakString":
            case "PenaltyExcessCharacter":
            case "PenaltyReturnTypeOnItsOwnLine":
            case "SpacesBeforeTrailingComments":
            case "TabWidth":
                return new String[] {"-1"};     // indicates numeric field
            // boolean options
            case "AlignConsecutiveAssignments":
            case "AlignConsecutiveDeclarations":
            case "AlignEscapedNewlinesLeft":
            case "AlignOperands":
            case "AlignTrailingComments":
            case "AllowAllParametersOfDeclarationOnNextLine":
            case "AllowShortBlocksOnASingleLine":
            case "AllowShortCaseLabelsOnASingleLine":
            case "AllowShortIfStatementsOnASingleLine":
            case "AllowShortLoopsOnASingleLine":
            case "AlwaysBreakBeforeMultilineStrings":
            case "AlwaysBreakTemplateDeclarations":
            case "BinPackArguments":
            case "BinPackParameters":
            case "BreakAfterJavaFieldAnnotations":
            case "BreakBeforeTernaryOperators":
            case "BreakConstructorInitializersBeforeComma":
            case "ConstructorInitializerAllOnOneLineOrOnePerLine":
            case "Cpp11BracedListStyle":
            case "DerivePointerAlignment":
            case "DisableFormat":
            case "ExperimentalAutoDetectBinPacking":
            case "IndentCaseLabels":
            case "IndentWrappedFunctionNames":
            case "KeepEmptyLinesAtTheStartOfBlocks":
            case "ObjCSpaceAfterProperty":
            case "ObjCSpaceBeforeProtocolList":
            case "SpaceAfterCStyleCast":
            case "SpaceBeforeAssignmentOperators":
            case "SpaceInEmptyParentheses":
            case "SpacesInAngles":
            case "SpacesInCStyleCastParentheses":
            case "SpacesInContainerLiterals":
            case "SpacesInParentheses":
            case "SpacesInSquareBrackets":
                return new String[] {"", "true", "false"};
            // String options
            case "AlignAfterOpenBracket":
                return new String[] {"", "Align", "DontAlign", "AlwaysBreak"};
            case "BasedOnStyle":
                return new String[] {"", "LLVM", "Google", "Chromium", "Mozilla", "WebKit"};
            case "AllowShortFunctionsOnASingleLine":
                return new String[] {"", "None", "Empty", "Inline", "All"};
            case "AlwaysBreakAfterDefinitionReturnType":
                return new String[] {"", "None", "All", "TopLevel"};
            case "BraceWrapping":
                return new String[] {
                    "", "AfterClass", "AfterControlStatement", "AfterEnum",
                    "AfterFunction", "AfterNamespace", "AfterObjCDeclaration", "AfterStruct",
                    "AfterUnion", "BeforeCatch", "BeforeElse", "IndentBraces"
                };
            case "BreakBeforeBinaryOperators":
                return new String[] {"", "None", "NonAssignment", "All"};
            case "BreakBeforeBraces":
                return new String[] {
                    "", "Attach", "Linux", "Mozilla", "Stroustrup",
                    "Allman", "GNU", "WebKit", "Custom"
                };
            case "Language":
                return new String[] {"", "None", "Cpp", "Java", "JavaScript", "Proto"};
            case "NamespaceIndentation":
                return new String[] {"", "None", "Inner", "All"};
            case "PointerAlignment":
                return new String[] {"", "Left", "Right", "Middle"};
            case "SpaceBeforeParens":
                return new String[] {"", "Never", "ControlStatements", "Always"};
            case "Standard":
                return new String[] {"", "Cpp03", "Cpp11", "Auto"};
            case "UseTab":
                return new String[] {"", "Never", "ForIndentation", "Always"};
            // these options allow freeform strings
            case "CommentPragmas":
            case "ConstructorInitializerIndentWidth":
            case "ForEachMacros":
            case "IncludeCategories":
            case "MacroBlockBegin":
            case "MacroBlockEnd":
            default:
                return new String[] {};
        }
    }
    
    /**
     * .clang-format files use a simple YAML format, there is a section per language.
     * Each section starts with "---" followed by the Language property, then followed
     * by the properties for that language, then optionally ends with "...". If there is no 
     * Language property in the first section, then that first section is the default
     * settings. Here is an example taken from http://clang.llvm.org/docs/ClangFormatStyleOptions.html:
     * <pre>
     * ---
     * # We'll use defaults from the LLVM style, but with 4 columns indentation.
     * BasedOnStyle: LLVM
     * IndentWidth: 4
     * ---
     * Language: Cpp
     * # Force pointers to the type for C++.
     * DerivePointerAlignment: false
     * PointerAlignment: Left
     * ---
     * Language: JavaScript
     * # Use 100 columns for JS.
     * ColumnLimit: 100
     * ---
     * Language: Proto
     * # Don't format .proto files.
     * DisableFormat: true
     * ...
     * </pre>
     */
    public void load( File clangFormatFile ) {
        if ( clangFormatFile == null || !clangFormatFile.exists() ) {
            return;
        }

        options.clear();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(clangFormatFile));
            List<TreeMap<String, String>> maps = new ArrayList<TreeMap<String, String>>();
            TreeMap<String, String> currentMap = new TreeMap<String, String>();
            maps.add(currentMap);
            String line;
            while((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;   
                }
                if (line.startsWith("---")) {
                    // start of a new language section 
                    currentMap = new TreeMap<String, String>();
                    maps.add(currentMap);
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;   // skip comments
                }
                if (line.startsWith("...")) {
                    continue;    
                }
                int index = line.indexOf(':');
                String key = line.substring(0, index);
                String value = index + 1 < line.length() ? line.substring(index + 1).trim() : "";
                currentMap.put(key, value);
            }
            for (TreeMap<String, String> map : maps) {
                if (map.isEmpty()) {
                    continue;   
                }
                String language = map.get("Language");
                if (language == null) {
                    language = DEFAULT;    
                }
                options.put(language, map);
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public String setOption( String language, String name, String value ) {
        TreeMap<String, String> map = options.get(language);
        if (map == null) {
            map = new TreeMap<String, String>();
            options.put(language, map);
        }
        return map.put(name, value);
    }
    
    /**
     * @return The value for the given optionName for the given language or an empty
     * string if either the language is not found or the optionName is not found.
     */
    public String getOption( String language, String optionName ) {
        TreeMap<String, String> map = options.get(language);
        if (map == null) {
            return "";    
        }
        String value = map.get( optionName );
        return value == null ? "" : value;
    }

    /**
     * @return a string suitable for saving as a .clang-format file
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for ( String language : options.keySet() ) {
            sb.append("---\n");
            TreeMap<String, String> languageOptions = options.get(language);
            String languageValue = languageOptions.get("Language");
            if (languageValue != null && !languageValue.trim().isEmpty()) {
                sb.append("Language: ").append(languageValue).append('\n');    
            }
            for (String key : languageOptions.keySet()) {
                if ("Language".equals(key)) {
                    continue;    
                }
                String value = languageOptions.get(key);
                if (value == null || value.isEmpty()) {
                    continue;    
                }
                sb.append( key ).append( ": " ).append( value ).append( '\n' );
            }
        }
        sb.append("...\n");
        return sb.toString();
    }
}
