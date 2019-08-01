package net.thumbtack.onlineshop.dto.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.ConstraintValidatorContext;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequiredRequiredPasswordValidatorTest {

    private RequiredPasswordValidator validator;

    @Mock
    private ConstraintValidatorContext mockContext;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(mockContext.buildConstraintViolationWithTemplate(any()))
                .thenReturn(mockBuilder);

        validator = new RequiredPasswordValidator();
        ReflectionTestUtils.setField(
                validator,
                "minPasswordLength",
                4
        );
    }

    @Test
    public void testEmptyPassword() {

        assertFalse(validator.isValid(null, mockContext));

        assertFalse(validator.isValid("", mockContext));

    }

    @Test
    public void testMinPasswordLength() {

        assertFalse(validator.isValid("khl", mockContext));

        assertTrue(validator.isValid("asdf", mockContext));

    }

    @Test
    public void testValidPasswords() {
        assertTrue(validator.isValid("ewr234.2!@#!l", mockContext));

        assertTrue(validator.isValid("Hello world", mockContext));

        assertTrue(validator.isValid("Привет мир", mockContext));

        assertTrue(validator.isValid("     ", mockContext));
    }
}
