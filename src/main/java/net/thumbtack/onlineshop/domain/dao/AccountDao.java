package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Transactional
@Repository
public class AccountDao implements Dao {

    private EntityManager manager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AccountDao(EntityManager manager, PasswordEncoder passwordEncoder) {
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Добавлет аккаунт в базу данных
     *
     * @param account аккаунт пользователя
     */
    public void insert(Account account) {
        // Хеширование паролей
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        manager.persist(account);
    }

    /**
     * Обновляет данные об аккаунте в БД
     *
     * @param account аккаунт пользователя
     */
    public void update(Account account) {
        // Хеширование паролей
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        manager.merge(account);
    }

    /**
     * Ищет пользователя в БД под данным логином и паролем
     *
     * @param login    логин
     * @param password пароль
     * @return аккаунт пользователя или null, если пользователь с данной парой не найден
     */
    public Account get(String login, String password) {

        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
        Root<Account> from = criteria.from(Account.class);
        criteria.select(from);
        criteria.where(
                builder.equal(from.get("login"), login)
        );
        TypedQuery<Account> typed = manager.createQuery(criteria);

        try {
            Account account = typed.getSingleResult();
            if (passwordEncoder.matches(password, account.getPassword())) {
                return account;
            } else {
                return null;
            }

        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Совпадает ли переданный пароль с паролем пользователя
     * под данным id
     * @param id id пользователя
     * @param password пароль для сравнения
     * @return совпадает ли пароль с паролем пользователя (если пользователь не найден
     * под данным id, то вернёт false)
     */
    public boolean isPasswordMatch(long id, String password) {
        Account account = get(id);

        if (account == null) {
            return false;
        }

        return passwordEncoder.matches(password, account.getPassword());
    }

    /**
     * Получает польователя по его Id
     *
     * @param accountId id пользователя
     * @return аккаунт пользователя или null, если он не найден
     */
    public Account get(long accountId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
        Root<Account> from = criteria.from(Account.class);

        criteria.select(from);
        criteria.where(builder.equal(from.get("id"), accountId));

        TypedQuery<Account> typed = manager.createQuery(criteria);
        try {
            return typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Проверяет существуют ли аккаунт под данным id
     *
     * @param id id аккаунта
     * @return true - если пользователь существует
     */
    public boolean exists(long id) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Account> from = criteria.from(Account.class);

        criteria.select(builder.count(from));
        criteria.where(builder.equal(from.get("id"), id));

        TypedQuery<Long> typed = manager.createQuery(criteria);
        return typed.getSingleResult() != 0;
    }

    /**
     * Проверяет наличие пользователя с данным логином в БД
     *
     * @param login логин пользователя
     * @return true - если такой пользователь уже есть
     */
    public boolean exists(String login) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Account> from = criteria.from(Account.class);

        criteria.select(builder.count(from));
        criteria.where(builder.equal(from.get("login"), login));

        TypedQuery<Long> typed = manager.createQuery(criteria);

        return typed.getSingleResult() != 0;
    }

    public void delete(Account account) {
        manager.remove(manager.merge(account));
    }

    /**
     * Полуает список всех клиентов
     *
     * @return список клиентов
     */
    public List<Account> getClients() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();

        CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
        Root<Account> from = criteria.from(Account.class);

        criteria.select(from);
        criteria.where(builder.equal(from.get("admin"), false));

        TypedQuery<Account> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }

    /**
     * Удаляет таблицу аккаунтов из БД
     */
    public void clear() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaDelete<Account> criteria = builder.createCriteriaDelete(Account.class);

        criteria.from(Account.class);

        manager.createQuery(criteria).executeUpdate();
    }

}
