package com.generator;

import com.type.DataInput;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arabbani on 11/21/16.
 */

/**
 * Created by arabbani on 11/21/16.
 */
public class AggregatorDataInputGenerator  implements SourceGenerator{

    private RoundEnvironment roundEvn;

    private final Class<? extends Annotation> annotation = DataInput.class;
    private List<DataInputCodeGenerator> allInputGenerators = new ArrayList<DataInputCodeGenerator>();
    private ProcessingEnvironment processingEnvironment;

    public AggregatorDataInputGenerator(RoundEnvironment roundEnvironment, ProcessingEnvironment processingEnvironment)
            throws IOException {
        this.roundEvn = roundEnvironment;
        this.processingEnvironment = processingEnvironment;
        init();

    }

    private void init() throws IOException {
        for (Element e : this.roundEvn.getElementsAnnotatedWith(annotation)) {
            if (e.getKind() == ElementKind.INTERFACE || e.getKind() == ElementKind.CLASS) {
                allInputGenerators.add(new DataInputCodeGenerator(this.processingEnvironment, e));
            }
        }
    }

    public void generate() throws Exception {
        for (DataInputCodeGenerator mig : allInputGenerators) {
            mig.generate();
        }
        // Prevent them from being created in subsequent rounds (prevents warning messages)
        allInputGenerators.clear();
    }

}
