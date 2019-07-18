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

public class RequiredNameValidatorTest {

    private RequiredNameValidator validator;

    @Mock
    private ConstraintValidatorContext mockContext;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(mockContext.buildConstraintViolationWithTemplate(any()))
                .thenReturn(mockBuilder);

        validator = new RequiredNameValidator();
        ReflectionTestUtils.setField(
                validator,
                "maxNameLength",
                10
        );
    }


    @Test
    public void testEmpty() {

        assertFalse(validator.isValid("", mockContext));

        assertFalse(validator.isValid(null, mockContext));

    }

    @Test
    public void testMaxNameLength() {
        assertFalse(validator.isValid("asdfqwerasd", mockContext));

        assertTrue(validator.isValid("asdfqweras", mockContext));

        assertTrue(validator.isValid("привет мир", mockContext));

        assertTrue(validator.isValid("      ", mockContext));

    }
}
