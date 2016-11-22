package com.info;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;


/**
 * Created by arabbani on 11/21/16.
 */
public class DataInputClassInfo extends ClassInfo{


    /**
     * Create ModuleInputClass info class.
     *
     * @param processingEnv
     * @param classElement
     */
    public DataInputClassInfo(
            final ProcessingEnvironment processingEnv,
            final Element classElement) {
        super(processingEnv, classElement);

    }

}
