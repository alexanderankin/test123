package jimporter.importer;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;
import gnu.regexp.REMatchEnumeration;
import gnu.regexp.RESyntax;
import java.util.StringTokenizer;
import jimporter.importer.ImportItem;
import org.gjt.sp.jedit.Buffer;

public class JSPImportList extends ImportList {
    public JSPImportList() {
    }

    public JSPImportList(Buffer sourceBuffer) {
        setSourceBuffer(sourceBuffer);
    }

    protected void parseImports() {
        RE re;

        try {
            re = new RE("<%@[[:space:]]page.*?import=\".*\".*%>", RE.REG_MULTILINE, RESyntax.RE_SYNTAX_POSIX_EXTENDED);

            //MAFTODO finish writing the parser
            //We'll need to put a grouing in to collect all of the import 
            //statements, then use a StringTokenizer to separate the individual 
            //import statements.  Another idea is to get an initial match, then
            //run a second regular expression on the match you find so we can 
            //use existing constructors.  This is probably a problem though 
            //because we need to maintain offsets.  (Perhaps gnu.regexp has some
            //methods we can use to get around this problem.)
            REMatch importMatch = re.getMatch(sourceBuffer.getText(0, sourceBuffer.getLength()));
            String importStatements = importMatch.toString(1);
            
            StringTokenizer tok = new StringTokenizer(importStatements, ",");
            while (tok.hasMoreTokens()) {
                String importStatement = tok.nextToken().trim();
                //ImportItem ii = new ImportItem();                                
            }

            sourceBufferParsed = true;
        } catch (REException ree) {
            throw new RuntimeException("Unexpected error while creating regular expression: " + ree);
        }
    }

    public boolean checkForDuplicateImport(String className, ImportList importList) {
        return false;
    }
}
