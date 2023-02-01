package com.vartool.web.dto.valid;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 패스워드 정책 적용.  
* 
* @fileName	: PasswordConstraintValidator.java
* @author	: ytkim
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(final ValidPassword arg0) {

    }

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        //System.out.println("================================= password");
        return true;
    }

}