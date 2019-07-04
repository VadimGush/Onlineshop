package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {

    private SessionDao sessionDao;
    private CategoryDao categoryDao;

    @Autowired
    public CategoriesService(SessionDao sessionDao, CategoryDao categoryDao) {
        this.sessionDao = sessionDao;
        this.categoryDao = categoryDao;
    }

    public Category addCategory(String sessionId, Category category) {
        return null;
    }

    public Category getCategory(String sessionId, long id) {
        return null;
    }

    public Category editCategory(String sessionId, Category category) {
        return null;
    }

    public void deleteCategory(String sessionId, long id) {
    }

    public List<Category> getCategories(String sessionId) {
        return categoryDao.getAll();
    }


}

