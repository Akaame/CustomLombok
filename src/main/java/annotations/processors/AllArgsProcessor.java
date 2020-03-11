package annotations.processors;

import annotations.translators.AllArgsTranslator;
import aop.AllArgsConstructor;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("aop.AllArgsConstructor") // Hangi annotation'lar islenecek
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AllArgsProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // processingEnv alanı AbstractProcessor ust sinifina iattir
        Trees trees = Trees.instance(processingEnv); // AST listesi
        TreeTranslator translator = new AllArgsTranslator(processingEnv); // Translator icine pasla

        // Round bitti mi?
        if(!roundEnv.processingOver()) {
            // Butun AllArgsConstructor'a ait
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(AllArgsConstructor.class);
            for (Element element : elements) {
                // Bu elemente ait alt agaci elde et
                JCTree tree = (JCTree) trees.getTree(element);
                tree.accept(translator); // Agac = Acceptor -> Translator = Visitor.
                // Bu Acceptor uzerinde kendi override ettigimiz visit metodunu calistir.
            }
        }
        return true;
    }
}
