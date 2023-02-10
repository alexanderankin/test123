package beauty.parsers.csv;

import org.antlr.v4.runtime.tree.ErrorNode;


/**
 * Arranges a csv file into columns, or collapses the columns, so this is a
 * toggling beautifier -- beautify once and you get columns, again and you get
 * the columns collapsed. Note this is for COMMA separated files, there is no 
 * facility for using a different separator.
 */
public class CSVBeautyListener extends CSVBaseListener {
    private TableArray table;

    private int row = 0;

    private int col = 0;


    /**
     * This should be called AFTER beautifying, otherwise no text will be available.
     * @return The formatted string for the file contents or an empty string if no
     * text was available.
     */
    public String getText() {
        if (table == null) {
            return "";
        }
        // are all the values the same width within each column?
        // if so, trim spaces on each value, otherwise, pad them
        boolean pad = !sameSame();

        // find max width of each column
        for (col = 0; col < table.getColumnCount(); col++) {
            int width = 0;

            for (row = 0; row < table.getRowCount(); row++) {
                String value = (String) table.get(col, row);

                if (value == null) {
                    value = "";
                    table.put(value, col, row);
                }
                width = Math.max(width, value.length());
            }

            // pad or trim each value in the column
            for (row = 0; row < table.getRowCount(); row++) {
                StringBuilder value = new StringBuilder((String) table.get(col, row));

                if (pad) {
                    while (value.length() < width) {
                        value.append(' ');
                    }
                }
                else {
                    String v = value.toString().trim();
                    value = new StringBuilder(v);
                }

                if (col < table.getColumnCount() - 1) {
                    value.append(',');
                }
                table.put(value.toString(), col, row);
            }
        }
        // stringify
        StringBuilder sb = new StringBuilder();

        for (row = 0; row < table.getRowCount(); row++) {
            for (col = 0; col < table.getColumnCount(); col++) {
                String value = (String) table.get(col, row);
                sb.append(value);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // check if all the values in each column are all the same length
    private boolean sameSame() {
        for (col = 0; col < table.getColumnCount(); col++) {
            int width = 0;

            for (row = 0; row < table.getRowCount(); row++) {
                String value = (String) table.get(col, row);

                if (value == null) {
                    value = "";
                    table.put(value, col, row);
                }

                if (row == 0) {
                    width = value.length();
                }
                else if (width != value.length()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void enterCsvFile(CSVParser.CsvFileContext ctx) {
        row = 0;
        col = 0;
        table = new TableArray();
    }

    @Override
    public void exitHdr(CSVParser.HdrContext ctx) {
    }

    @Override
    public void enterRow(CSVParser.RowContext ctx) {
        col = 0;
    }

    @Override
    public void exitRow(CSVParser.RowContext ctx) {
        ++row;
    }

    @Override
    public void exitField(CSVParser.FieldContext ctx) {
        String field = null;

        if (ctx.STRING() != null) {
            field = ctx.STRING().getText();
        }
        else if (ctx.TEXT() != null) {
            field = ctx.TEXT().getText();
        }

        if (field == null) {
            field = "";
        }
        table.put(field, col, row);
        ++col;
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
    }
}