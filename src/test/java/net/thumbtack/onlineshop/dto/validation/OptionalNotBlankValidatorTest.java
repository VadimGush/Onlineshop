package net.thumbtack.onlineshop.dto.validation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintValidatorContext;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OptionalNotBlankValidatorTest {

    private OptionalNotBlankValidator validator;

    @Mock
    private ConstraintValidatorContext mockContext;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        ConstraintValidatorContext.ConstraintViolationBuilder mockBuilder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(mockContext.buildConstraintViolationWithTemplate(any()))
                .thenReturn(mockBuilder);

        validator = new OptionalNotBlankValidator();
    }

    @Test
    public void testMaxNameLength() {
        assertTrue(validator.isValid(null, mockContext));

        assertFalse(validator.isValid("", mockContext));

        assertTrue(validator.isValid("fer", mockContext));
    }
}
