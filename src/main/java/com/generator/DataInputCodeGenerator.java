package com.generator;

import com.info.DataInputClassInfo;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.io.IOException;

/**
 * Created by arabbani on 11/21/16.
 */
public class DataInputCodeGenerator extends SourceGeneratorImpl {

    public DataInputCodeGenerator(
            final ProcessingEnvironment processingEnv,
            final Element classElement ) throws IOException {
        super(processingEnv, new DataInputClassInfo(processingEnv, classElement));
    }

    /**
     * Add an element to the velocity context
     */
    @Override
    public void addModelToContext() {
        this.vc.put("className", info.getSimpleClassName());
        this.vc.put("packageName", info.getPackageName());
        this.vc.put("fields", info.getFields());
        this.vc.put("methods", info.getMethods());
        this.vc.put("methodReturnType", info.getMethodReturnTypes());
        this.vc.put("methodAnnotations", info.getMethodAnnotationValues());
    }


    /**
     *
     */
    @Override
    public String getModuleTemplate() {
        return "velocity/templates/datainput.vm";
    }
}
