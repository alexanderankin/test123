
package clangbeauty;

import java.util.HashMap;

public class StyleOptions {

    private HashMap<String, String> options = new HashMap<String, String>();

    public String[] getOptionNames() {
        return new String[] {
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
            "Language",
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

    public String[] getOptionChoices( String optionName ) {
        switch ( optionName ) {
            // numeric options
            case "AccessModifierOffset":
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
                return new String[] {"0", "1", "2", "4", "8"};
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
                return new String[] {"true", "false"};
            // String options
            case "AlignAfterOpenBracket":
                return new String[] {"Align", "DontAlign", "AlwaysBreak"};
            case "BasedOnStyle":
                return new String[] {"LLVM", "Google", "Chromium", "Mozilla", "WebKit"};
            case "AllowShortFunctionsOnASingleLine":
                return new String[] {"None", "Empty", "Inline", "All"};
            case "AlwaysBreakAfterDefinitionReturnType":
                return new String[] {"None", "All", "TopLevel"};
            case "BraceWrapping":
                return new String[] {"AfterClass", "AfterControlStatement", "AfterEnum", "AfterFunction", "AfterNamespace", "AfterObjCDeclaration", "AfterStruct", "AfterUnion", "BeforeCatch", "BeforeElse", "IndentBraces"};
            case "BreakBeforeBinaryOperators":
                return new String[] {"None", "NonAssignment", "All"};
            case "BreakBeforeBraces":
                return new String[] {"Attach", "Linux", "Mozilla", "Stroustrup", "Allman", "GNU", "WebKit", "Custom"};
            case "ColumnLimit":
                return new String[] {"0", "72", "76", "80", "120"};
            case "Language":
                return new String[] {"None", "Cpp", "Java", "Javascript", "Proto"};
            case "NamespaceIndentation":
                return new String[] {"None", "Inner", "All"};
            case "PointerAlignment":
                return new String[] {"Left", "Right", "Middle"};
            case "SpaceBeforeParens":
                return new String[] {"Never", "ControlStatements", "Always"};
            case "Standard":
                return new String[] {"Cpp03", "Cpp11", "Auto"};
            case "UseTab":
                return new String[] {"Never", "ForIndentation", "Always"};
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
     * @param savedOptions An option string, as produced by the <code>toString<code> method.
     * The expectation is that this is a string that was saved in a jEdit property to be
     * used again.
     */
    public void setOptions( String savedOptions ) {
        if ( savedOptions != null ) {
            options = new HashMap<String, String>();
            String[] pairs = savedOptions.split( "," );
            for ( String pair : pairs ) {
                String[] parts = pair.split( ":" );
                options.put( parts[0].trim(), parts[1].trim() );
            }
        }
    }

    public String setOption( String name, String value ) {
        return options.put( name, value );
    }

    public String getOption( String optionName ) {
        return options.get( optionName );
    }

    /**
     * @return a string suitable to passing to the clang-format -style= parameter.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for ( String key : options.keySet() ) {
            sb.append( key ).append( ':' ).append( options.get( key ) ).append( ',' );
        }
        sb.deleteCharAt( sb.length() - 1 );
        sb.append('}');
        return sb.toString();
    }
}
