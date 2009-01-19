package superabbrevs.lexer.template;

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

    public void visit(TempTranformationField field) {
        int index = field.getIndex();
        SelectableField selectableField = selectableFieldMap.get(index);
        
        if (selectableField != null && selectableField instanceof VariableField) {
            VariableField variableField = (VariableField) selectableField;
            // If we found a variable field in the selectable field map that is 
            // instance of a variable field we will make transformation field 
            // pointing to that variable field.
            newTemplate.add(new TransformationField(variableField,
                    field.getCode(), interpreter));
        }
    }

    public void visit(TempVariableFieldPointer field) {
        int index = field.getIndex();
        SelectableField selectableField = selectableFieldMap.get(index);
        
        if (selectableField == null) {
            // if the variable we are pointing to does not exists we will add 
            // this pointers as an empty variable field.
            VariableField variableField = new VariableField(index, "");
            newTemplate.add(variableField);
            selectableFieldMap.put(index, variableField);
        } else if (selectableField instanceof VariableField) {
            // if the variable is a temp variable pointer pointing to a variable 
            // field we will add a real variable field pointer to the template.
            VariableField variableField = (VariableField) selectableField;
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