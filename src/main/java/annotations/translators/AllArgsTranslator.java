package annotations.translators;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.ProcessingEnvironment;

public class AllArgsTranslator extends TreeTranslator {
    private final TreeMaker maker;
    private final JavacElements elements;
    private ProcessingEnvironment env;

    public AllArgsTranslator(ProcessingEnvironment _env) {
        env = _env;
        JavacProcessingEnvironment javacEnv = (JavacProcessingEnvironment) _env;
        maker = TreeMaker.instance(javacEnv.getContext());
        elements = JavacElements.instance(javacEnv.getContext());
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl classDecl) {
        super.visitClassDef(classDecl);

        List<JCTree> members = classDecl.getMembers();
        // members to params
        List<JCTree.JCVariableDecl> params = List.nil();
        List<JCTree.JCVariableDecl> fields = List.nil();
        for(JCTree decl : members)
        {
            if(!(decl instanceof JCTree.JCVariableDecl))
                continue; // Uye alan degisken degilse (metod vb. ise) devam et

            JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl) decl;
            fields = fields.append(variableDecl);

            String _name = variableDecl.name.toString();
            JCTree.JCVariableDecl d = maker.VarDef(maker.Modifiers(Flags.PARAMETER),
                    elements.getName('_' + _name),
                    variableDecl.vartype, null);
            params = params.append(d);
        }

        // yapılandırıcı gövdesi
        List<JCTree.JCStatement> statements = List.nil();
        for(int i = 0; i < params.size(); i++)
        {
            JCTree.JCExpression lhs = maker.Ident(fields.get(i).name); // field sec
            JCTree.JCExpression rhs = maker.Ident(params.get(i).name); // param sec
            statements = statements.append(maker.Exec(maker.Assign(lhs, rhs)));
        }

        JCTree.JCBlock body = maker.Block(0, statements);

        // Yapılandırıcı metod
        JCTree.JCMethodDecl methodDecl = maker.MethodDef(
                maker.Modifiers(Flags.PUBLIC),
                elements.getName("<init>"), // <init> = constructor
                null, List.<JCTree.JCTypeParameter>nil(),
                params, List.<JCTree.JCExpression>nil(), body, null);

        // Yapılandırıcının sınıf tanımına eklenmesi
        classDecl.defs = classDecl.defs.append(methodDecl);

    }

}
