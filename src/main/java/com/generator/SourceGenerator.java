package com.generator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;

/**
 * Created by arabbani on 11/21/16.
 */
public interface SourceGenerator {


    void generate() throws Exception;
}
