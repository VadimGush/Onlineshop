package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.controller.validation.ValidationException;
import net.thumbtack.onlineshop.dto.CategoryDto;
import net.thumbtack.onlineshop.service.CategoriesService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CategoriesControllerTest {

    private CategoriesController categoriesController;

    @Mock
    private CategoriesService mockCategoriesService;

    @Mock
    private BindingResult mockResult;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        categoriesController = new CategoriesController(mockCategoriesService);
    }

    @Test
    public void testAddCategory() throws Exception {

        CategoryDto category = new CategoryDto();
        CategoryDto expected = new CategoryDto();

        when(mockResult.hasErrors()).thenReturn(false);
        when(mockCategoriesService.addCategory("token", category)).thenReturn(expected);

        CategoryDto result = categoriesController.addCategory("token", category, mockResult);

        verify(mockCategoriesService).addCategory("token", category);
        assertEquals(expected, result);
    }

    @Test(expected = ValidationException.class)
    public void testAddCategoryValidation() throws Exception {

        CategoryDto category = new CategoryDto();
        when(mockResult.hasErrors()).thenReturn(true);

        try {
            categoriesController.addCategory("token", category, mockResult);
        } catch (ValidationException e) {
            verify(mockCategoriesService, never()).addCategory(any(), any());
            throw e;
        }

    }

    @Test
    public void testGetCategoryById() throws Exception {

        CategoryDto expected = new CategoryDto();
        when(mockCategoriesService.getCategory("token", 0)).thenReturn(expected);

        CategoryDto result = categoriesController.getCategoryById(0, "token");

        verify(mockCategoriesService).getCategory("token", 0);
        assertEquals(expected, result);
    }

    @Test
    public void testEditCategory() throws Exception {

        CategoryDto category = new CategoryDto();
        CategoryDto expected = new CategoryDto();

        when(mockResult.hasErrors()).thenReturn(false);
        when(mockCategoriesService.editCategory("token", category, 0))
                .thenReturn(expected);

        CategoryDto result = categoriesController.editCategory("token", 0, category, mockResult);

        verify(mockCategoriesService).editCategory("token", category, 0);
        assertEquals(expected, result);
    }

    @Test(expected = ValidationException.class)
    public void testEditCategoryValidation() throws Exception {

        CategoryDto category = new CategoryDto();
        when(mockResult.hasErrors()).thenReturn(true);

        try {
            categoriesController.editCategory("token", 0, category, mockResult);
        } catch (ValidationException e) {
            verify(mockCategoriesService, never()).editCategory(any(), any(), anyLong());
            throw e;
        }

    }

    @Test
    public void testDeleteCategory() throws Exception {

        String result = categoriesController.deleteCategory("token", 0);

        verify(mockCategoriesService).deleteCategory("token", 0);
        assertEquals("{}", result);
    }

    @Test
    public void testGetCategories() throws Exception {

        List<CategoryDto> expected = new ArrayList<>();
        when(mockCategoriesService.getCategories("token"))
                .thenReturn(expected);

        List<CategoryDto> result = categoriesController.getCategories("token");

        verify(mockCategoriesService).getCategories("token");
        assertEquals(expected, result);
    }
}
