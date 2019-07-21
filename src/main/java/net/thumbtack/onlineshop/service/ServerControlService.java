package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.domain.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления состоянием сервера
 */
@Service
public class ServerControlService {

    private List<Dao> dao;

    @Autowired
    public ServerControlService(List<Dao> dao) {
        this.dao = dao;
    }

    /**
     * Удаляет все существующие таблицы в базе данных
     */
    public void clear() {
        dao.forEach(Dao::clear);
    }
}
