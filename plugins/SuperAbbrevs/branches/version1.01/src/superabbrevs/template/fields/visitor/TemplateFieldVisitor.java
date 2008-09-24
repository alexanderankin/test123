/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package superabbrevs.template.fields.visitor;

import superabbrevs.template.fields.EndField;
import superabbrevs.template.fields.TempTranformationField;
import superabbrevs.template.fields.TempVariableFieldPointer;
import superabbrevs.template.fields.TextField;
import superabbrevs.template.fields.TransformationField;
import superabbrevs.template.fields.VariableField;
import superabbrevs.template.fields.VariableFieldPointer;

/**
 *
 * @author sune
 */
public interface TemplateFieldVisitor {

    public void visit(EndField field);
    public void visit(TempTranformationField field);
    public void visit(TempVariableFieldPointer field);
    public void visit(TextField field);
    public void visit(TransformationField field);
    public void visit(VariableField field);
    public void visit(VariableFieldPointer field);
}
