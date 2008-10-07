package superabbrevs.lexer.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import superabbrevs.lexer.template.tokens.Token;
import superabbrevs.template.Template;
import superabbrevs.template.TemplateInterpreter;
import superabbrevs.template.fields.Field;
import superabbrevs.template.fields.SelectableField;

class TemplateBuilder {

    public Template buildTemplate(Iterable<Token> tokens, TemplateInterpreter interpreter) {
        FieldListBuilder fieldListBuilder = new FieldListBuilder();
        SelectableFieldMapBuilder fieldMapCreator = new SelectableFieldMapBuilder();
        FieldListWirer fieldListWirer = new FieldListWirer(interpreter);

        // Build an initial field list from the token stream. The template will 
        // contain temp fields pointing to other fields though a index instead 
        // of a reference.
        List<Field> fieldList = fieldListBuilder.buildList(tokens);

        // Create a map from variable field indexes to the actually fields.
        // Only varaible fields and EndFields are included in this map.
        Map<Integer, SelectableField> fieldMap =
                fieldMapCreator.buildSelectableFieldMap(fieldList);

        // Make a new field list where the temp fields are replaced by the 
        // real VaraiblePointField and TransformationFields
        fieldList = fieldListWirer.wireFields(fieldList, fieldMap);

        // Create a list of the fields in the field map sorted by field index.
        List<SelectableField> selectableFieldList =
                buildSelectableFieldList(fieldList, fieldMap);
        
        return new Template(fieldList, selectableFieldList);
    }
    
    private List<SelectableField> buildSelectableFieldList(List<Field> template,
            Map<Integer, SelectableField> selectableFieldMap) {
        return new ArrayList<SelectableField>(selectableFieldMap.values());
    }
}
