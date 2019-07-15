package net.thumbtack.onlineshop.controller.validation;

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

public class OptionalRussianNameValidatorTest {

    private OptionalRussianNameValidator validator;

    @Mock
    private ConstraintValidatorContext mockContext;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(mockContext.buildConstraintViolationWithTemplate(any()))
                .thenReturn(mockBuilder);

        validator = new OptionalRussianNameValidator();
        ReflectionTestUtils.setField(
                validator,
                "maxNameLength",
                10
        );
    }


    @Test
    public void testEmpty() {

        assertFalse(validator.isValid("", mockContext));

        assertTrue(validator.isValid(null, mockContext));

    }

    @Test
    public void testMaxNameLength() {
        assertFalse(validator.isValid("asdfqwerasd", mockContext));

        assertFalse(validator.isValid("asdfqweras", mockContext));

        assertTrue(validator.isValid("Рривет Мир", mockContext));

        assertTrue(validator.isValid("      ", mockContext));

        assertFalse(validator.isValid("234324", mockContext));

        assertTrue(validator.isValid("привет-Мир", mockContext));

        assertFalse(validator.isValid("привет3", mockContext));

        assertFalse(validator.isValid("привет.", mockContext));
    }
}
