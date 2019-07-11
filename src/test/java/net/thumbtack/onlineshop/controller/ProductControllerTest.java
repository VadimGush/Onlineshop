package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.controller.validation.ValidationException;
import net.thumbtack.onlineshop.dto.ProductDto;
import net.thumbtack.onlineshop.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    private ProductController controller;

    @Mock
    private ProductService mockProductService;

    @Mock
    private BindingResult mockResult;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        controller = new ProductController(mockProductService);
    }

    @Test
    public void testAddProduct() throws Exception {

        ProductDto product = new ProductDto();
        ProductDto expected = new ProductDto();

        when(mockResult.hasErrors()).thenReturn(false);
        when(mockProductService.add("token", product)).thenReturn(expected);

        ProductDto result = controller.addProduct("token", product, mockResult);

        assertEquals(0, (int)product.getCount());
        assertEquals(expected, result);
        verify(mockProductService).add("token", product);

    }

    @Test(expected = ValidationException.class)
    public void testAddProductValidation() throws Exception {

        ProductDto product = new ProductDto();
        when(mockResult.hasErrors()).thenReturn(true);

        try {
            controller.addProduct("token", product, mockResult);
        } catch (ValidationException e) {
            verify(mockProductService, never()).add(any(), any());
            throw e;
        }

    }

    @Test
    public void testEditProduct() throws Exception {

        ProductDto product = new ProductDto();
        ProductDto expected = new ProductDto();

        when(mockResult.hasErrors()).thenReturn(false);
        when(mockProductService.edit("token", product, 0)).thenReturn(expected);

        ProductDto result = controller.editProduct("token", product, mockResult, 0);

        assertEquals(expected, result);
        verify(mockProductService).edit("token", product, 0);

    }

    @Test(expected = ValidationException.class)
    public void testEditProductValidation() throws Exception {

        ProductDto product = new ProductDto();
        when(mockResult.hasErrors()).thenReturn(true);

        try {
            controller.editProduct("token", product, mockResult, 0);
        } catch (ValidationException e) {
            verify(mockProductService, never()).edit(any(), any(), anyLong());
            throw e;
        }

    }

    @Test
    public void testDeleteProduct() throws Exception {

        String result = controller.deleteProduct("token", 0);

        assertEquals("{}", result);
        verify(mockProductService).delete("token", 0);

    }

    @Test
    public void testGetProduct() throws Exception {

        ProductDto expected = new ProductDto();

        when(mockProductService.get("token", 0)).thenReturn(expected);

        ProductDto result = controller.getProduct("token", 0);

        assertEquals(expected, result);
        verify(mockProductService).get("token", 0);

    }

    @Test
    public void testGetProducts() throws Exception {

        List<ProductDto> expected = new ArrayList<>();

        when(mockProductService.getAll("token", null, ProductService.SortOrder.PRODUCT))
                .thenReturn(expected);

        List<ProductDto> result = controller.getProducts("token", null, null);

        assertEquals(expected, result);
    }

    @Test
    public void testGetProductsProduct() throws Exception {

        List<ProductDto> expected = new ArrayList<>();

        when(mockProductService.getAll("token", null, ProductService.SortOrder.PRODUCT))
                .thenReturn(expected);

        List<ProductDto> result = controller.getProducts("token", null, "product");

        assertEquals(expected, result);
    }

    @Test
    public void testGetProductsSortWithError() throws Exception {

        List<ProductDto> expected = new ArrayList<>();

        when(mockProductService.getAll("token", null, ProductService.SortOrder.PRODUCT))
                .thenReturn(expected);

        List<ProductDto> result = controller.getProducts("token", null, "prodt");

        assertEquals(expected, result);
    }

    @Test
    public void testGetProductsCategorySorted() throws Exception {

        List<ProductDto> expected = new ArrayList<>();

        when(mockProductService.getAll("token", null, ProductService.SortOrder.CATEGORY))
                .thenReturn(expected);

        List<ProductDto> result = controller.getProducts("token", null, "category");

        assertEquals(expected, result);
    }

}
