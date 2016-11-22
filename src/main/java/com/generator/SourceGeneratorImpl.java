package com.generator;

import com.info.ClassInfo;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Properties;

/**
 * Created by arabbani on 11/21/16.
 */
public abstract class SourceGeneratorImpl implements SourceGenerator {

    protected ClassInfo info;
    protected ProcessingEnvironment processingEnv;
    protected VelocityContext vc;
    protected VelocityEngine ve;
    protected String template;
    protected boolean alreadyGenerated;

    private static final String CLASS_NAME_SUFFIX = "Generated";
    private static final String VELOCITY_PROPERTIES_FILE = "velocity/velocity.properties";

    /**
     * Will create an instance of generate sources.
     *
     * Can be used to generate a source file using the given velocity template.
     *
     * @param processingEnv
     * @param info
     * @throws IOException
     */
    public SourceGeneratorImpl(
            final ProcessingEnvironment processingEnv,
            final ClassInfo info) throws IOException {

        this.info = info;
        this.processingEnv = processingEnv;
        this.alreadyGenerated = false;

        // Create Velocity Engine
        final Properties props = new Properties();
        final URL url = this.getClass().getClassLoader().getResource(VELOCITY_PROPERTIES_FILE);
        props.load(url.openStream());
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Loaded velocity.properties..");
        final VelocityEngine ve = new VelocityEngine(props);
        ve.init();
        this.ve = ve;

        // Load a new Velocity Context
        final VelocityContext vc = new VelocityContext();
        this.vc = vc;

        // Template From Super Class
        this.template = this.getModuleTemplate();

        // Model From Super Class
        this.addModelToContext();
    }

    /**
     * Subclasses must provide a valid module template name
     *
     * @return moduleTemplate
     */
    public abstract String getModuleTemplate();

    /**
     * Add an element to the velocity context
     */
    public abstract void addModelToContext();

    /**
     * Generate source file based on configuration
     *
     * @throws Exception
     */

    public void generate() throws Exception {

        // Get the name
        final String generatedClassName = info.getSimpleClassName() == null ? null : info.getSimpleClassName() + CLASS_NAME_SUFFIX;

        // Cannot generate without class name or a class that's already been generated
        if (generatedClassName == null || alreadyGenerated) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Not Generating From Class Again: " + generatedClassName );
            return;
        }

        // Update the state to denote that we've already generated this class information
        this.alreadyGenerated = true;

        Writer writer = null;
        try {
            // Message
            final Template vt = ve.getTemplate(this.template);

            // Create a new Java File Object
            final JavaFileObject jfo =  processingEnv.getFiler().createSourceFile(generatedClassName);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Creating source file: " + jfo.toUri());

            // Writer for java file object
            writer = jfo.openWriter();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Applying velocity template: " + vt.getName());

            // Merge the context with a writer to produce the output
            vt.merge(vc, writer);

        } catch (final Exception e) {
            throw e;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
