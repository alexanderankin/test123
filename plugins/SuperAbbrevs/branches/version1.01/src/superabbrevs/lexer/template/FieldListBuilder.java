package superabbrevs.lexer.template;

import com.sun.java_cup.internal.lexer;
import java.util.ArrayList;
import java.util.List;
import superabbrevs.lexer.template.tokens.EndFieldToken;
import superabbrevs.lexer.template.tokens.FieldPointerToken;
import superabbrevs.lexer.template.tokens.FieldToken;
import superabbrevs.lexer.template.tokens.TextFieldToken;
import superabbrevs.lexer.template.tokens.Token;
import superabbrevs.lexer.template.tokens.TransformationFieldToken;
import superabbrevs.lexer.template.tokens.visitor.TemplateGeneratorTokenVisitor;
import superabbrevs.template.fields.EndField;
import superabbrevs.template.fields.Field;
import superabbrevs.template.fields.TempTranformationField;
import superabbrevs.template.fields.TempVariableFieldPointer;
import superabbrevs.template.fields.TextField;
import superabbrevs.template.fields.VariableField;

class FieldListBuilder implements TemplateGeneratorTokenVisitor {

    private List<Field> template;
    private boolean endFieldFound = false;

    public List<Field> buildList(Iterable<Token> tokenList) {
        template = new ArrayList<Field>();

        for (Token token : tokenList) {
            token.accept(this);
        }

        if (!endFieldFound) {
            template.add(new EndField());
        }

        return template;
    }

    public void visit(EndFieldToken token) {
        template.add(new EndField());
        endFieldFound = true;
    }

    public void visit(FieldPointerToken token) {
        int index = token.getIndex();
        template.add(new TempVariableFieldPointer(index));
    }

    public void visit(FieldToken token) {
        int index = token.getIndex();
        String value = token.getInitialValue();
        template.add(new VariableField(index, value));
    }

    public void visit(TextFieldToken token) {
        template.add(new TextField(token.getText()));
    }

    public void visit(TransformationFieldToken token) {
        int index = token.getIndex();
        String code = token.getCode();
        template.add(new TempTranformationField(index, code));
    }
}
