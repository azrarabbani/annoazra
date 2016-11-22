package com.info;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arabbani on 11/21/16.
 */
public class ClassInfo {

    /**
     * Basic class information
     */
    private String fullQualifiedClassName;
    private String className;
    private String packageName;

    /**
     * Utilities
     */
    protected Types typeUtils;
    protected Elements elementUtils;


    /**
     * All methods.
     * Keys are simpleName of methods.
     * Values are ExecutableElements describing the method.
     */
    @JsonIgnore
    private Map<String, ExecutableElement> methods = new HashMap<>();

    /**
     * Return types of methods as strings.
     * Keys are simpleName of methods
     * Values are fully qualified return types.
     */
    @JsonIgnore
    private Map<String, String> methodReturnTypes = new HashMap<>();

    /**
     * All fields declared in class.
     * Keys are simple names of fields.
     * Values are VariableElements describing the field.
     */
    @JsonIgnore
    private Map<String, VariableElement> fields = new HashMap<>();

    /**
     * Annotation values at the Class/Interface level.
     * Key is the name of the annotation
     * Value is the AnnotationValue
     */
    @JsonIgnore
    private Map<String, AnnotationValue> classAnnotationValues = new HashMap<>();

    /**
     * The names of the methods as designated
     * keys are simple names of methods
     * values are maps of annotation value (name/value pairs)
     */
    @JsonIgnore
    private Map<String, Map<String, String>> methodAnnotationValues = new HashMap<>();

    /**
     * Wild-card types to help identify certain
     * types.
     */
    protected TypeMirror listType;
    protected TypeMirror setType;
    protected TypeMirror mapType;

    /**
     * Initializes class information from a round environment, processing environment,
     * and only those classes annotated with the supplied annotation.
     *
     * @param processingEnv
     * @param classElement
     */
    public ClassInfo(
            final ProcessingEnvironment processingEnv,
            final Element classElement) {
        this.init(classElement);

        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();

        // Use TypeMirrors for comparing against similar types
        this.listType = this.typeUtils.getDeclaredType(elementUtils.getTypeElement("java.util.List"),typeUtils.getWildcardType(null, null));
        this.mapType = this.typeUtils.getDeclaredType(elementUtils.getTypeElement("java.util.Map"),typeUtils.getWildcardType(null, null),typeUtils.getWildcardType(null, null));
        this.setType = this.typeUtils.getDeclaredType(elementUtils.getTypeElement("java.util.Set"),typeUtils.getWildcardType(null, null));
    }

    // Initializes the ClassInfo from the RoundEnvironment and ProcessingEnvironment
    // Currently we do not support inner class definitions (no recursion)
    private void init(Element e) {
        if (e.getKind() == ElementKind.INTERFACE || e.getKind() == ElementKind.CLASS) {
            final TypeElement classElement = (TypeElement) e;
            final PackageElement packageElement = (PackageElement) classElement.getEnclosingElement();
            this.fullQualifiedClassName = classElement.getQualifiedName().toString();
            this.className = classElement.getSimpleName().toString();
            this.packageName = packageElement.getQualifiedName().toString();

            for (final Element enclosed : classElement.getEnclosedElements()) {
                if (enclosed.getKind() == ElementKind.FIELD) {
                    final VariableElement varElement = (VariableElement) enclosed;
                    fields.put(varElement.getSimpleName().toString(), varElement);
                } else if (enclosed.getKind() == ElementKind.METHOD) {
                    final ExecutableElement exeElement = (ExecutableElement) enclosed;
                    methods.put(exeElement.getSimpleName().toString(), exeElement);
                    this.populateAnnotationValuesFromMethod(exeElement);
                }
            }
            this.populateAnnotationValuesForClassOrInterface(e);
        }

        // Additional (derived) data
        this.populateReturnTypesFromMethodsAsStrings();
    }

    // AnnotationValues for an Element (assumed Class/Interface Kind)
    private void populateAnnotationValuesForClassOrInterface(Element e) {
        for (AnnotationMirror am : e.getAnnotationMirrors()) {
            for (final Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> values: am.getElementValues().entrySet()) {
                classAnnotationValues.put(values.getKey().getSimpleName().toString(), values.getValue());
            }
        }
    }

    // For each annotation on this method, grab the name and value and store in a name-value pair map
    // Then store that map in the methodAnnotationValues map with key equivalent to the method name.
    private void populateAnnotationValuesFromMethod(final ExecutableElement method) {
        for (AnnotationMirror am : method.getAnnotationMirrors()) {
            for (final Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> values: am.getElementValues().entrySet()) {
                Map<String, String> value = new HashMap<String, String>();
                value.put(values.getKey().getSimpleName().toString(), values.getValue().toString());
                this.methodAnnotationValues.put(method.getSimpleName().toString(), value);
            }
        }
    }

    // Create a map of return types for all methods
    private void populateReturnTypesFromMethodsAsStrings() {
        for (Map.Entry<String, ExecutableElement> entry : this.getMethods().entrySet()) {
            methodReturnTypes.put(entry.getKey(), entry.getValue().getReturnType().toString());
        }
    }

    public static Map<String, ExecutableElement> allMethodsInClassWithAnnotation(
            final Class<? extends Annotation> annotation,
            final Map<String, ExecutableElement> allMethods) {
        final Map<String, ExecutableElement> allMethodsWithAnnotation = new HashMap<String, ExecutableElement>();
        for (final Map.Entry<String, ExecutableElement> entry : allMethods.entrySet()) {
            if (entry.getValue().getAnnotation(annotation) != null) {
                allMethodsWithAnnotation.put(entry.getKey(), entry.getValue());
            }
        }
        return allMethodsWithAnnotation;
    }
    //
    //
    // PUBLIC INTERFACE BELOW
    //
    //

    /**
     * @return the fullQaulifiedClassName of the annotated class
     */
    public String getFullyQaulifiedClassName() {
        return fullQualifiedClassName;
    }

    /**
     * @return the className of the annotated class
     */
    public String getSimpleClassName() {
        return className;
    }

    /**
     * @return the packageName of the annotated class
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return the fields of the annotated class
     */
    public Map<String, VariableElement> getFields() {
        return fields;
    }

    /**
     * @return the methods of this class
     */
    public Map<String, ExecutableElement> getMethods() {
        return methods;
    }

    /**
     * Return the annotation values of this class
     * Keys are names of the annotation values.
     * Values are the values.
     * @return classAnnotationValues
     */
    public Map<String, AnnotationValue> getClassAnnotationValues() {
        return this.classAnnotationValues;
    }

    /**
     * @return the methodReturnTypes
     */
    public Map<String, String> getMethodReturnTypes() {
        return this.methodReturnTypes;
    }

    /**
     * Return the method annotation values.
     *
     * Keys are method names (simple name).
     *
     * Values are name-value pairs where names are
     * the names of the annotation keys, and values are the
     * values associated with those annotations.
     * @return methodAnnotationValues
     */
    public Map<String, Map<String, String>> getMethodAnnotationValues() {
        return this.methodAnnotationValues;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final ObjectMapper objectMapper = new ObjectMapper();
        String str = "";
        try {
            str = objectMapper.writeValueAsString(this);
        } catch(final Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}
