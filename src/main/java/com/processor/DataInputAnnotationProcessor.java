package com.processor;

import com.generator.AggregatorDataInputGenerator;
import com.generator.DataInputCodeGenerator;
import com.generator.SourceGenerator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by arabbani on 11/21/16.
 */

@SupportedAnnotationTypes({
        "com.type.DataInput"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class DataInputAnnotationProcessor extends AbstractProcessor {

    private SourceGenerator gen;

    /**
     *
     * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Thread thread = Thread.currentThread();
        final ClassLoader loader = thread.getContextClassLoader();
        thread.setContextClassLoader(this.getClass().getClassLoader());
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"Generating Sources...");
        try{
            if (gen == null) {
                gen = new AggregatorDataInputGenerator(roundEnv, processingEnv);
            }
            gen.generate();
        } catch(Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Error writing writing!! " + e.getMessage());
        } finally {
            thread.setContextClassLoader(loader);
        }
        return true;
    }

    /**
     * @see javax.annotation.processing.AbstractProcessor#getSupportedAnnotationTypes()
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new HashSet<String>();
        supportedAnnotations.add("com.type.DataInput");
        return supportedAnnotations;
    }
}

