package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class OnlineShopServer {

    public static void main(String... args) throws ServiceException {
        ApplicationContext context = SpringApplication.run(OnlineShopServer.class);

        AccountDao accountDao = context.getBean(AccountDao.class);

        Account account = AccountFactory.createAdmin(
                "Vadim", "Gush", "Vadimovich", "programmer", "vadim", "iddqd225"
        );

        accountDao.insert(account);
        System.out.println(accountDao.exists("vadim"));
        System.out.println(accountDao.exists("vadim12"));
    }



}
