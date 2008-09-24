package superabbrevs.template;

import superabbrevs.template.fields.VariableField;
import superabbrevs.template.fields.SelectableField;
import superabbrevs.template.fields.Field;
import java.util.Iterator;
import java.util.List;

public class Template {

    private List<Field> template;
    private List<SelectableField> fieldList;
    private int currentField = 0;
    private int offset = 0;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
        updateOffsets();
    }
    private int length;

    public int getLength() {
        return length;
    }

    public Template(List<Field> fieldList, 
            List<SelectableField> selectableFieldList) {
        this.template = fieldList;
        this.fieldList = selectableFieldList;
    }

    @Override
    public String toString() {
        StringBuffer output = new StringBuffer();
        Iterator iter = template.iterator();
        while (iter.hasNext()) {
            Field field = (Field) iter.next();
            output.append(field.toString());
        }
        return output.toString();
    }

    private void updateOffsets() {
        int o = this.offset;

        Iterator iter = template.iterator();
        while (iter.hasNext()) {
            Field field = (Field) iter.next();

            if (field instanceof SelectableField) {
                SelectableField selectableField = (SelectableField) field;
                selectableField.setOffset(o);
            }

            o += field.getLength();
        }
        length = o - this.offset;
    }

    public void insert(int at, String s) throws WriteOutsideTemplateException {
        SelectableField field = getCurrentField();
        if (field instanceof VariableField) {
            VariableField variableField = (VariableField) field;
            variableField.insert(at, s);
            updateOffsets();
        } else {
            throw new WriteOutsideTemplateException("Insert in $end field");
        }
    }

    public void delete(int at, int length) throws WriteOutsideTemplateException {
        SelectableField field = getCurrentField();
        if (field instanceof VariableField) {
            VariableField variableField = (VariableField) field;
            variableField.delete(at, length);
            updateOffsets();
        } else {
            throw new WriteOutsideTemplateException("Delete in $end field");
        }

    }

    public SelectableField getCurrentField() {
        return fieldList.get(currentField);
    }

    public boolean inCurrentField(int pos) {
        SelectableField field = getCurrentField();
        return field.inField(pos);
    }

    public void nextField() {
        currentField++;

        // there is always the $end field 
        if (fieldList.size() <= currentField) {
            currentField = 0;
        }
    }

    public void prevField() {
        currentField--;

        // there is always the $end field 
        if (currentField < 0) {
            currentField = fieldList.size() - 1;
        }
    }
}
