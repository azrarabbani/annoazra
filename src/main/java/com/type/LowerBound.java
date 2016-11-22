package com.type;

import com.validator.LowerBoundValidator;

import javax.validation.*;
import javax.annotation.*;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by arabbani on 11/21/16.
 */

@Constraint(validatedBy = LowerBoundValidator.class)

@Documented
@Target({
        ElementType.METHOD
        , ElementType.FIELD
        , ElementType.ANNOTATION_TYPE
        , ElementType.CONSTRUCTOR
        , ElementType.PARAMETER
})

public @interface LowerBound {

    /**
     * @return The String value of the lower bound; default is 0.
     */
    String value() default "0";



    /**
     * @return The default error message when validation fails.
     */
    String message() default "No value in the collection can violate the provided lower bound";



    /**
     * @return No groups by default.
     */
    Class<?>[] groups() default {};



    /**
     * @return No payloads by default.
     */
    Class<? extends Payload>[] payload() default {};
}
