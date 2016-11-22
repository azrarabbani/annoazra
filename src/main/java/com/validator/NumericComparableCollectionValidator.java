package com.validator;

/**
 * Created by arabbani on 11/21/16.
 */

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


import org.apache.commons.collections4.CollectionUtils;



/**
 * The NumericComparableCollectionValidatorTest leverages the template method pattern for validation Collections of
 * Number objects that must also implement the Comparable interface.
 * </p>
 *
 * @param <A> The annotation type that will be used for this validation.
 */
public abstract class NumericComparableCollectionValidator<A extends Annotation>
        implements ConstraintValidator<A, Collection<? extends Number>> {

    protected abstract <C extends Comparable<C>> void validateComparableList(
            List<C> comparableList, Class<C> comparableClass);



    private <C extends Comparable<C>> void transformAndValidateComparableList(
            Collection<? extends Number> numberCollection
            , Class<C> comparableClass
    ) throws Exception{
        try {
            List<C> comparableList = new ArrayList<C>(numberCollection.size());
            for (Number number : numberCollection) {
                comparableList.add(comparableClass.cast(number));
            }
            this.validateComparableList(comparableList, comparableClass);
        } catch (ClassCastException classCastException) {
            throw new Exception("Mixed number types in a collection is not supported for this validation");
        }
    }



    /**
     * Provides a template method algorithm for validators of Number Collections where the Number is expected to be of a
     * Comparable type as well. Primitive types are cast to either Long or Double for integral and fractional types
     * respectively.
     * </p>
     * <strong>Please note: A null or empty collection is considered invalid in this algorithm.</strong> To allow an
     * empty collection to pass with size 0, please implement this in the collectionValidationTemplateMethod
     * implementation.
     * </p>
     */

    public boolean isValid(
            Collection<? extends Number> numberCollection, ConstraintValidatorContext context) {

        try {

            // Validation incoming collection is comparable and setup comparable list

            if (CollectionUtils.isNotEmpty(numberCollection)) {
                Number firstNumber = numberCollection.iterator().next();
                if (firstNumber instanceof Double) {
                    this.transformAndValidateComparableList(numberCollection, Double.class);
                } else if (firstNumber instanceof Float) {
                    this.transformAndValidateComparableList(numberCollection, Float.class);
                    /*
                     * List<Comparable<Float>> comparableList = new
                     * ArrayList<Comparable<Float>>(numberCollection.size()); for(Number number : numberCollection) {
                     * comparableList.add(number.floatValue()); } this.validateComparableList(comparableList);
                     */
                } else if (firstNumber instanceof Long) {
                    this.transformAndValidateComparableList(numberCollection, Long.class);
                } else if (firstNumber instanceof Integer) {
                    this.transformAndValidateComparableList(numberCollection, Integer.class);
                } else if (firstNumber instanceof Short) {
                    this.transformAndValidateComparableList(numberCollection, Short.class);
                } else if (firstNumber instanceof Byte) {
                    this.transformAndValidateComparableList(numberCollection, Byte.class);
                } else {
                    throw new Exception(
                            "An unsupported type is detected: "
                                    + firstNumber.getClass());
                }
            }

        } catch (Exception validationException) {
            // Validation fails if a ValidationException was thrown
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    validationException.getMessage())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
