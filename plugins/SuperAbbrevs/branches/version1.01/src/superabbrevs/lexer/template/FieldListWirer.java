package superabbrevs.lexer.template;

import org.gjt.sp.jedit.bsh.Interpreter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import superabbrevs.template.TemplateInterpreter;
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

class FieldListWirer implements TemplateFieldVisitor {

    private List<Field> newTemplate;
    private Map<Integer, SelectableField> selectableFieldMap;
    private TemplateInterpreter interpreter;

    public FieldListWirer(TemplateInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public List<Field> wireFields(List<Field> template,
            Map<Integer, SelectableField> selectableFieldMap) {
        this.selectableFieldMap = selectableFieldMap;
        newTemplate = new ArrayList<Field>();

        for (Field field : template) {
            field.accept(this);
        }

        return newTemplate;
    }

    private VariableField getVariableField(int index) {
        SelectableField field = selectableFieldMap.get(index);

        if (!(field instanceof VariableField)) {
            return null;
        }

        return (VariableField) field;
    }

    public void visit(TempTranformationField field) {
        int index = field.getIndex();
        VariableField variableField = getVariableField(index);
        if (variableField != null) {
            newTemplate.add(new TransformationField(variableField,
                    field.getCode(), interpreter));
        }
    }

    public void visit(TempVariableFieldPointer field) {
        int index = field.getIndex();
        VariableField variableField = getVariableField(index);
        if (variableField != null) {
            newTemplate.add(new VariableFieldPointer(variableField));
        }
    }

    public void visit(EndField field) {
        newTemplate.add(field);
    }

    public void visit(TextField field) {
        newTemplate.add(field);
    }

    public void visit(TransformationField field) {
        newTemplate.add(field);
    }

    public void visit(VariableField field) {
        newTemplate.add(field);
    }

    public void visit(VariableFieldPointer field) {
        newTemplate.add(field);
    }
}