package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Category;
import net.thumbtack.onlineshop.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы с категориями
 */
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
     *
     * @param sessionId сессия админа
     * @param category  информация о категории
     * @return добавленная категория
     */
    public CategoryDto addCategory(String sessionId, CategoryDto category) throws ServiceException {

        getAdmin(sessionId);

        // Сначала проверим имя категории
        if (categoryDao.exists(category.getName())) {
            throw new ServiceException(ServiceException.ErrorCode.SAME_CATEGORY_NAME, "name");
        }

        // Проверим родителя
        Category parent = null;
        if (category.getParentId() != null) {
            parent = categoryDao.get(category.getParentId());

            if (parent == null) {
                throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND, "parentId");
            }

            if (parent.isSubcategory()) {
                throw new ServiceException(ServiceException.ErrorCode.SECOND_SUBCATEGORY, "parentId");
            }

        }

        Category newCategory = new Category(category.getName(), parent);
        categoryDao.insert(newCategory);

        return new CategoryDto(newCategory);
    }

    /**
     * Получает информацию о категории
     *
     * @param sessionId сессия админа
     * @param id        id категории
     * @return категория из БД
     */
    public CategoryDto getCategory(String sessionId, long id) throws ServiceException {

        getAdmin(sessionId);

        Category category = categoryDao.get(id);
        if (category == null) {
            throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND);
        }

        return new CategoryDto(category);
    }

    /**
     * Изменяет информацию о категории
     *
     * @param sessionId   сессия админа
     * @param categoryDto новая инфа о категории
     * @param id          id категории
     * @return изменённая категория
     */
    public CategoryDto editCategory(String sessionId, CategoryDto categoryDto, long id) throws ServiceException {

        getAdmin(sessionId);

        Category category = categoryDao.get(id);

        if (category == null) {
            throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND);
        }

        // Не могут отсутствовать два поля одновременно
        if (categoryDto.getName() == null && categoryDto.getParentId() == null) {
            throw new ServiceException(ServiceException.ErrorCode.EDIT_CATEGORY_EMPTY);
        }

        // Меняем имя
        if (categoryDto.getName() != null && !categoryDto.getName().isEmpty()) {
            // Нельзя изменить на уже существующее
            if (categoryDao.exists(categoryDto.getName())) {
                throw new ServiceException(ServiceException.ErrorCode.SAME_CATEGORY_NAME, "name");
            }
            category.setName(categoryDto.getName());
        }

        // Меняем родителя
        if (categoryDto.getParentId() != null) {

            if (!category.isSubcategory()) {
                throw new ServiceException(ServiceException.ErrorCode.CATEGORY_TO_SUBCATEGORY, "parentId");
            }
            Category parent = categoryDao.get(categoryDto.getParentId());
            if (parent == null) {
                throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND, "parentId");
            }
            category.setParent(parent);
        }

        categoryDao.update(category);

        return new CategoryDto(category);
    }

    /**
     * Удаляет категорию
     *
     * @param sessionId сессия админа
     * @param id        id категории
     */
    public void deleteCategory(String sessionId, long id) throws ServiceException {

        getAdmin(sessionId);

        Category category = categoryDao.get(id);
        if (category == null) {
            throw new ServiceException(ServiceException.ErrorCode.CATEGORY_NOT_FOUND);
        }

        categoryDao.delete(category);

    }

    /**
     * Получает список всех категорий
     *
     * @param sessionId сессия админа
     * @return список категорий
     */
    public List<CategoryDto> getCategories(String sessionId) throws ServiceException {

        getAdmin(sessionId);

        List<CategoryDto> result = new ArrayList<>();
        categoryDao.getAll().forEach(category -> result.add(new CategoryDto(category)));

        /*
        Сортируем

        Все родительские должны быть отсортированны по имени
        Под каждой родительской должны быть дочерние тоже отсортированные по имени
        Таким образом сортировку мы выполняем по строкам (name + parentName)
         */
        result.sort((left, right) -> {
            String leftFull = left.getName();
            if (left.getParentName() != null)
                leftFull = left.getParentName() + left.getName();

            String rightFull = right.getName();
            if (right.getParentName() != null)
                rightFull = right.getParentName() + right.getName();

            return leftFull.compareTo(rightFull);
        });

        return result;
    }


}

