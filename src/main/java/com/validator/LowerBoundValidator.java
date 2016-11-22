package com.validator;

import com.type.LowerBound;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by arabbani on 11/21/16.
 */
public class LowerBoundValidator extends NumericComparableCollectionValidator<LowerBound> {
    private String value;



    @Override
    public void initialize(LowerBound constraintAnnotation) {
        this.value = constraintAnnotation.value();
    }



    @Override
    protected <C extends Comparable<C>> void validateComparableList(
            List<C> comparableList
            , Class<C> comparableClass
    )  {

        C lowerBound = null;
        try {
            lowerBound
                    = comparableClass.cast(comparableClass.getDeclaredMethod("valueOf", String.class).invoke(null, value));
        } catch (
                IllegalAccessException
                        | IllegalArgumentException
                        | InvocationTargetException
                        | NoSuchMethodException
                        | SecurityException exception
                ) {
            //throw new Exception("String is not a valid numeric representation");
        }

        for(Comparable<C> comparableElement : comparableList) {
            if(comparableElement.compareTo(lowerBound) < 0) {
//                throw new Exception(
//                        "A value less than the lower bound was detected: "
//                                + comparableElement);
            }
        }
    }

}
