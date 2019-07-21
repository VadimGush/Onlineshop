package net.thumbtack.onlineshop.domain.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface Dao {

    /**
     * Удаление всех таблиц из БД, которые управляются DTO
     */
    void clear();

}
