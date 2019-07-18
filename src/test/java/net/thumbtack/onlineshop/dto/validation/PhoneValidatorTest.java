package net.thumbtack.onlineshop.dto.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PhoneValidatorTest {

    private PhoneValidator validator;

    @Mock
    private ConstraintValidatorContext mockContext;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(mockContext.buildConstraintViolationWithTemplate(any()))
                .thenReturn(mockBuilder);

        validator = new PhoneValidator();
    }

    @Test
    public void testStartWithWrongNumber() {

        assertFalse(validator.isValid("-79649951843", mockContext));

        assertFalse(validator.isValid("19649951843", mockContext));

        assertFalse(validator.isValid("2", mockContext));

    }

    @Test
    public void testEmpty() {

        assertFalse(validator.isValid(null, mockContext));

        assertFalse(validator.isValid("", mockContext));
    }

    @Test
    public void testLettersInPhoneNumber() {

        assertFalse(validator.isValid("+79649951a43", mockContext));

        assertFalse(validator.isValid("+79649951 43", mockContext));

        assertFalse(validator.isValid("+79649951.43", mockContext));

    }

    @Test
    public void testValid() {

        assertTrue(validator.isValid("+79649951843", mockContext));

        assertTrue(validator.isValid("+7-964-995-18-43", mockContext));

        assertTrue(validator.isValid("+7-964-995-1843", mockContext));

        assertTrue(validator.isValid("+7-9-6-4-9-9-5-1-8-4-3", mockContext));

        assertTrue(validator.isValid("89649951843", mockContext));

        assertTrue(validator.isValid("8-964-995-18-43", mockContext));

        assertTrue(validator.isValid("8-9-6-4-9-9-5-1-8-4-3", mockContext));
    }
}
