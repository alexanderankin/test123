package superabbrevs.lexer.template;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import superabbrevs.template.fields.EndField;
import superabbrevs.template.fields.Field;
import superabbrevs.template.fields.SelectableField;
import superabbrevs.template.fields.TempTranformationField;
import superabbrevs.template.fields.TempVariableFieldPointer;
import superabbrevs.template.fields.TextField;
import superabbrevs.template.fields.TransformationField;
import superabbrevs.template.fields.VariableField;
import superabbrevs.template.fields.VariableFieldPointer;
import superabbrevs.template.fields.visitor.TemplateFieldVisitor;

class SelectableFieldMapBuilder implements TemplateFieldVisitor {

    private Map<Integer, SelectableField> fieldMap;

    public Map<Integer, SelectableField> buildSelectableFieldMap(List<Field> template) {
        fieldMap = new TreeMap<Integer, SelectableField>();
        for (Field field : template) {
            field.accept(this);
        }
        return fieldMap;
    }

    public void visit(VariableField field) {
        fieldMap.put(field.getIndex(), field);
    }

    public void visit(EndField field) {
        fieldMap.put(field.getIndex(), field);
    }

    public void visit(TempTranformationField field) {
    }

    public void visit(TempVariableFieldPointer field) {
    }

    public void visit(TextField field) {
    }

    public void visit(TransformationField field) {
    }

    public void visit(VariableFieldPointer field) {
    }
}
