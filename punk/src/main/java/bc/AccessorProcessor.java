package bc;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * javac -cp $JAVA_HOME/lib/tools.jar Acc* -d .
 * javac -processor bc.AccessorProcessor Baby.java
 * javap -p Baby
 */
@SupportedAnnotationTypes("bc.Accessor")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AccessorProcessor extends AbstractProcessor {

    private Messager messager;
    private JavacTrees javacTrees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        javacTrees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 拿到被注解标注的所有的类
        Set<? extends Element> elemAnnotatedWith = roundEnv.getElementsAnnotatedWith(Accessor.class);

        for (Element elem : elemAnnotatedWith) {
            // 得到类的抽象树结构
            JCTree tree = javacTrees.getTree(elem);

            // 遍历类，对类进行修改
            tree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    List<JCTree.JCVariableDecl> variableDecls = List.nil();

                    // 在抽象树中找出所有的变量
                    for (JCTree jcTree : jcClassDecl.defs) {
                        if (jcTree.getKind().equals(Tree.Kind.VARIABLE)) {
                            JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl) jcTree;
                            variableDecls = variableDecls.append(variableDecl);
                        }
                    }

                    for (JCTree.JCVariableDecl variableDecl : variableDecls) {
                        messager.printMessage(Diagnostic.Kind.NOTE, variableDecl.getName() + " has been processed");
                        jcClassDecl.defs = jcClassDecl.defs.prepend(makeGetMethodDecl(variableDecl));
                        // jcClassDecl.defs = jcClassDecl.defs.prepend(makeSetMethodDecl(variableDecl));
                    }

                    super.visitClassDef(jcClassDecl);
                }

                private JCTree.JCMethodDecl makeSetMethodDecl(JCTree.JCVariableDecl variableDecl) {
                    return null;
                }

                private JCTree.JCMethodDecl makeGetMethodDecl(JCTree.JCVariableDecl variableDecl) {
                    ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();

                    JCTree.JCExpressionStatement statement = makeAssign(
                            treeMaker.Select(
                                    treeMaker.Ident(names.fromString("this")),
                                    variableDecl.getName()),
                            treeMaker.Ident(variableDecl.getName())
                    );
                    statements.append(statement);

                    JCTree.JCBlock block = treeMaker.Block(0, statements.toList());
                    JCTree.JCVariableDecl param = treeMaker.VarDef(
                            treeMaker.Modifiers(Flags.PARAMETER),
                            variableDecl.getName(),
                            variableDecl.vartype,
                            null);
                    List<JCTree.JCVariableDecl> params = List.of(param);
                    JCTree.JCExpression methodType = treeMaker.Type(new Type.JCVoidType());
                    Name name = getGetMethodName(variableDecl.getName());

                    return treeMaker.MethodDef(
                            treeMaker.Modifiers(Flags.PUBLIC),
                            (com.sun.tools.javac.util.Name) name,
                            methodType,
                            List.nil(),
                            params,
                            List.nil(),
                            block,
                            null);
                }

                private JCTree.JCExpressionStatement makeAssign(JCTree.JCExpression expression1, JCTree.JCExpression expression2) {
                    return treeMaker.Exec(
                            treeMaker.Assign(expression1, expression2)
                    );
                }

                private Name getSetMethodName(Name name) {
                    String nameText = name.toString();
                    return names.fromString("set" + nameText.substring(0, 1).toUpperCase() + nameText.substring(1, name.length()));
                }

                private Name getGetMethodName(Name name) {
                    String nameText = name.toString();
                    return names.fromString("get" + nameText.substring(0, 1).toUpperCase() + nameText.substring(1, name.length()));
                }

            });
        }
        return false;
    }
}