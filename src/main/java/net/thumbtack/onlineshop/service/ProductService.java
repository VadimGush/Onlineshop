package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.database.models.ProductCategory;
import net.thumbtack.onlineshop.database.models.Session;
import net.thumbtack.onlineshop.dto.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductDao productDao;
    private SessionDao sessionDao;
    private CategoryDao categoryDao;

    public enum SortOrder {
        PRODUCT, CATEGORY
    }

    @Autowired
    public ProductService(ProductDao productDao, SessionDao sessionDao, CategoryDao categoryDao) {
        this.productDao = productDao;
        this.sessionDao = sessionDao;
        this.categoryDao = categoryDao;
    }

    public Product add(String sessionId, ProductDto product) throws ServiceException {
        isAdmin(sessionId);

        return null;
    }

    public Product edit(String sessionId, ProductDto product, long productId) throws ServiceException {
        isAdmin(sessionId);

        return null;
    }

    public void delete(String sessionId, long id) throws ServiceException {
        isAdmin(sessionId);

    }

    public Product get(String sessionId, long id) throws ServiceException {
        isLogin(sessionId);

        return null;
    }

    public List<ProductCategory> getCategories(String sessionId, long productId) throws ServiceException {
        isLogin(sessionId);

        return null;
    }

    public List<Product> getAll(String sessionId, List<Integer> categories, SortOrder order) throws ServiceException {
        isLogin(sessionId);

        return productDao.getAll();
    }

    private void isAdmin(String sessionId) throws ServiceException {
        Session session = sessionDao.get(sessionId);

        if (session == null)
            throw new ServiceException(ServiceException.ErrorCode.NOT_LOGIN);

        if (!session.getAccount().isAdmin())
            throw new ServiceException(ServiceException.ErrorCode.NOT_ADMIN);
    }

    private void isLogin(String sessionId) throws ServiceException {
        Session session = sessionDao.get(sessionId);

        if (session == null)
            throw new ServiceException(ServiceException.ErrorCode.NOT_LOGIN);
    }
}
