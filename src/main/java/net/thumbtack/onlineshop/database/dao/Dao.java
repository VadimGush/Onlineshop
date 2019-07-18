package net.thumbtack.onlineshop.database.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface Dao {

    /**
     * Удаление всех таблиц из БД, которые управляются DTO
     */
    void clear();

}
