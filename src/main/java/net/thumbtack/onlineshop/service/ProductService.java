package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.database.models.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductDao productDao;
    private SessionDao sessionDao;

    @Autowired
    public ProductService(ProductDao productDao, SessionDao sessionDao) {
        this.productDao = productDao;
        this.sessionDao = sessionDao;
    }

    public Product add(String sessionId, Product product) throws ServiceException {
        isAdmin(sessionId);

        return null;
    }

    public void edit(String sessionId, Product product) throws ServiceException {
        isAdmin(sessionId);

    }

    public void delete(String sessionId, long id) throws ServiceException {
        isAdmin(sessionId);
    }

    public void get(String sessionId, long id) throws ServiceException {

    }

    public List<Product> getAll(String sessionId) throws ServiceException {
        return productDao.getAll();
    }

    private void isAdmin(String sessionId) throws ServiceException {
        Session session = sessionDao.get(sessionId);

        if (!session.getAccount().isAdmin())
            throw new ServiceException(ServiceException.ErrorCode.NOT_ADMIN, "JAVASESSIONID");
    }
}
