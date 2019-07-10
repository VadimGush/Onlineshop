package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Category;
import net.thumbtack.onlineshop.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService extends GeneralService {

    private CategoryDao categoryDao;

    @Autowired
    public CategoriesService(SessionDao sessionDao, CategoryDao categoryDao) {
        super(sessionDao);
        this.categoryDao = categoryDao;
    }

    /**
     * Добавляет новую категорию
     * @param sessionId сессия админа
     * @param category информация о категории
     * @return добавленная категория
     * @throws ServiceException
     */
    public Category addCategory(String sessionId, CategoryDto category) throws ServiceException {

        getAdmin(sessionId);

        // Сначала проверим имя категории
        if (categoryDao.exists(category.getName()))
            throw new ServiceException(ServiceException.ErrorCode.SAME_CATEGORY_NAME, "name");

        // Проверим родителя
        Category parent = null;
        if (category.getParentId() != null) {
            parent = categoryDao.get(category.getParentId());

            if (parent == null)
                throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND, "parentId");

            if (parent.isSubcategory())
                throw new ServiceException(ServiceException.ErrorCode.SECOND_SUBCATEGORY, "parentId");

        }

        Category newCategory = new Category(category.getName(), parent);
        categoryDao.insert(newCategory);

        return newCategory;
    }

    /**
     * Получает информацию о категории
     * @param sessionId сессия админа
     * @param id id категории
     * @return категория из БД
     * @throws ServiceException
     */
    public Category getCategory(String sessionId, long id) throws ServiceException {

        getAdmin(sessionId);

        Category category = categoryDao.get(id);
        if (category == null)
            throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND);

        return category;
    }

    /**
     * Изменяет информацию о категории
     * @param sessionId сессия админа
     * @param categoryDto новая инфа о категории
     * @param id id категории
     * @return изменённая категория
     * @throws ServiceException
     */
    public Category editCategory(String sessionId, CategoryDto categoryDto, long id) throws ServiceException {

        getAdmin(sessionId);

        Category category = categoryDao.get(id);
        if (category == null)
            throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND);

        if (categoryDto.getName() != null)
            category.setName(categoryDto.getName());

        if (categoryDto.getParentId() != null) {

            if (!category.isSubcategory())
                throw new ServiceException(ServiceException.ErrorCode.CATEGORY_TO_SUBCATEGORY, "parentId");

            Category parent = categoryDao.get(categoryDto.getParentId());
            if (parent == null)
                throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND, "parentId");

            category.setParent(parent);

        }

        categoryDao.update(category);

        return category;
    }

    /**
     * Удаляет категорию
     * @param sessionId сессия админа
     * @param id id категории
     * @throws ServiceException
     */
    public void deleteCategory(String sessionId, long id) throws ServiceException {

        getAdmin(sessionId);

        Category category = categoryDao.get(id);
        if (category == null)
            throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND);

        categoryDao.delete(category);

    }

    /**
     * Получает список всех категорий
     * @param sessionId сессия админа
     * @return список категорий
     * @throws ServiceException
     */
    public List<Category> getCategories(String sessionId) throws ServiceException {

        getAdmin(sessionId);

        return categoryDao.getAll();
    }


}

