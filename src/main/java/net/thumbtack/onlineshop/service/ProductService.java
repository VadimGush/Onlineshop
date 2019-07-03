package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductDao productDao;

    @Autowired
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public Product add(String sessionId, Product product) {
        return null;
    }

    public void edit(String sessionId, Product product) {

    }

    public void delete(String sessionId, long id) {

    }

    public void get(String sessionId, long id) {

    }

    public List<Product> getAll(String sessionId) {
        return productDao.getAll();
    }

}
