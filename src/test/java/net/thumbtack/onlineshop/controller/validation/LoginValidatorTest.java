package net.thumbtack.onlineshop.controller.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginValidatorTest {

    private LoginValidator validator;

    @Mock
    private ConstraintValidatorContext mockContext;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        ConstraintViolationBuilder mockBuilder = mock(ConstraintViolationBuilder.class);
        when(mockContext.buildConstraintViolationWithTemplate(any()))
                .thenReturn(mockBuilder);

        validator = new LoginValidator();
        ReflectionTestUtils.setField(
                validator,
                "maxNameLength",
                10
        );
    }

    @Test
    public void testEmpty() {
        assertFalse(validator.isValid(null, mockContext));

        assertFalse(validator.isValid("", mockContext));
    }

    @Test
    public void testMaxLength() {
        assertFalse(validator.isValid("asdfgqwerqw", mockContext));

        assertTrue(validator.isValid("asdfqweras", mockContext));
    }

    @Test
    public void testNumbersAndLetters() {
        assertTrue(validator.isValid("ere", mockContext));

        assertTrue(validator.isValid("2342", mockContext));

        assertTrue(validator.isValid("e3423", mockContext));

        assertTrue(validator.isValid("привет", mockContext));

        assertTrue(validator.isValid("при343", mockContext));

        assertFalse(validator.isValid("hell.", mockContext));

        assertFalse(validator.isValid("hell_", mockContext));

        assertFalse(validator.isValid("hell+", mockContext));

        assertFalse(validator.isValid("   ", mockContext));
    }


}
