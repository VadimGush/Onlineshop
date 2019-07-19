package net.thumbtack.onlineshop;

import com.fasterxml.jackson.databind.JsonNode;
import net.thumbtack.onlineshop.dto.*;
import net.thumbtack.onlineshop.service.ServerControlService;
import net.thumbtack.onlineshop.utils.IntegrationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = OnlineShopServer.class
)
@TestPropertySource("classpath:config-test.properties")
@AutoConfigureMockMvc
public class ClientIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ServerControlService serverControl;

    private IntegrationUtils utils;

    @Before
    public void cleanDatabase() {
        serverControl.clear();
        utils = new IntegrationUtils(mvc);
    }

    /**
     * Успешная регистрация клиента
     */
    @Test
    public void testRegisterClient() throws Exception {
        ClientDto client = utils.getDefaultClient();

        // Тире должны будут удалится из записи
        client.setPhone("+7-964-995-18-43");

        MvcResult result = utils.post("/api/clients", null, client)
                .andExpect(status().isOk())
                .andReturn();

        // Проверяем информацию
        JsonNode node = utils.read(result);

        assertNotNull(node.get("id"));
        assertEquals(client.getFirstName(), node.get("firstName").asText());
        assertEquals(client.getLastName(), node.get("lastName").asText());
        assertEquals(client.getPatronymic(), node.get("patronymic").asText());
        assertEquals(client.getEmail(), node.get("email").asText());
        assertEquals("+79649951843", node.get("phone").asText());
        assertEquals(client.getAddress(), node.get("address").asText());
        assertEquals(0, node.get("deposit").asInt());

        assertNull(node.get("login"));
        assertNull(node.get("password"));
    }

    /**
     * Проверяем, что логин администратора и клиента не могут совпадать
     */
    @Test
    public void testRegisterClientWithSameAdminLogin() throws Exception {
        utils.register(utils.getDefaultAdmin());

        ClientDto client = utils.getDefaultClient();
        client.setLogin(utils.getDefaultAdmin().getLogin());

        MvcResult result = utils.post("/api/clients", null, client)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("LoginInUse", "login"));
    }

    /**
     * Нельзя создать двух клиентов с одинаковыми логинами
     */
    @Test
    public void testRegisterClientWithSameLogin() throws Exception {
        utils.register(utils.getDefaultClient());

        ClientDto client = utils.getDefaultClient();

        MvcResult result = utils.post("/api/clients", null, client)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("LoginInUse", "login"));
    }

    /**
     * Нельзя получить список товаров с неверной сессией
     */
    @Test
    public void testGetProductWithoutLogin() throws Exception {
        MvcResult result = utils.get("/api/products/3", "werew")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Проверяем получение информации о несуществующем товаре
     */
    @Test
    public void testGetProductNotExist() throws Exception {
        String session = utils.register(utils.getDefaultClient());

        MvcResult result = utils.get("/api/products/3", session)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "ProductNotFound");
    }

    /**
     * Получение информации о товарах
     */
    @Test
    public void testGetProduct() throws Exception {
        // Получем полную информацию о товаре

        // Создаём товар от имени админа (и пару категорий к этому товару)
        String adminSession = utils.register(utils.getDefaultAdmin());
        long category1 = utils.register(adminSession, new CategoryDto("category1"));
        long category2 = utils.register(adminSession, new CategoryDto("category2"));

        // Количество товара по умолчанию = 0
        long product = utils.register(adminSession,
                utils.getProduct("iphone", 10, Arrays.asList(category1, category2))
        );

        // Теперь получем информацию о товаре от имени клиента
        String session = utils.register(utils.getDefaultClient());
        MvcResult result = utils.get("/api/products/" + product, session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(product, node.get("id").asLong());
        assertEquals("iphone", node.get("name").asText());
        assertEquals(10, node.get("price").asInt());
        assertEquals(0, node.get("count").asInt());

        assertEquals(2, node.get("categories").size());
        assertEquals(category1, node.get("categories").get(0).asLong());
        assertEquals(category2, node.get("categories").get(1).asLong());

    }

    /**
     * Проверяем что нельзя зарегестрироваться с пустым отчеством
     */
    @Test
    public void testRegisterWithoutPatronymic() throws Exception {
        ClientDto client = utils.getDefaultClient();
        client.setPatronymic("");

        MvcResult result = utils.post("/api/clients", null, client)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("OptionalRussianName", "patronymic"));
    }

    /**
     * Проверяем что можно зарегистрироваться без отчества
     */
    @Test
    public void testRegisterWithoutPatronymicNull() throws Exception {
        ClientDto client = utils.getDefaultClient();
        client.setPatronymic(null);

        MvcResult result = utils.post("/api/clients", null, client)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertNull(node.get("patronymic"));
    }

    /**
     * Проверяем валидацию по обязательным полям (они не могут быть пустыми)
     */
    @Test
    public void testRegisterWithRequiredFields() throws Exception {
        ClientDto client = new ClientDto();
        client.setFirstName("");
        client.setLastName("");
        client.setEmail("");
        client.setAddress("");
        client.setPhone("");
        client.setLogin("");
        client.setPassword("");

        MvcResult result = utils.post("/api/clients", null, client)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("NotBlank", "email"),
                Pair.of("Phone", "phone"),
                Pair.of("NotBlank", "address"),
                Pair.of("Login", "login"),
                Pair.of("Password", "password")
        ));
    }

    /**
     * Проверяем валидацию формата полей
     */
    @Test
    public void testRegistrationWithWrongFields() throws Exception {
        ClientDto client = new ClientDto();
        // Неверный формат почты
        client.setEmail("vadim.");
        // На адрес не накладывается никаких ограничений
        client.setAddress("dfrer23423423432j2r 2..1121");
        // Пароль не должен быть сликшом коротким
        client.setPassword("ewr");
        // Номер телефона тоже проверяется
        client.setPhone("+79123123");
        // Логин не может содержать знаки препинания
        client.setLogin("vadim!");
        // ФИО не может состоять не из русских букв и пробела
        client.setFirstName("Vadim");
        client.setLastName("Gush");
        client.setPatronymic("Vadimovich");

        MvcResult result = utils.post("/api/clients", null, client)
                .andExpect(status().isBadRequest())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(7, node.get("errors").size());

        utils.assertErrors(result, Arrays.asList(
                Pair.of("Email", "email"),
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("OptionalRussianName", "patronymic"),
                Pair.of("Phone", "phone"),
                Pair.of("Login", "login"),
                Pair.of("Password", "password")
        ));
    }

    /**
     * Логин и логаут клиента
     */
    @Test
    public void testLoginAndLogout() throws Exception {
        // Этот самый клиент, который будет зареган через registerClient()
        ClientDto client = utils.getDefaultClient();
        // Региструем пользователя под логином DeNis
        String session = utils.register(client);

        // Logout
        utils.delete("/api/sessions", session)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));

        // Login
        // Логин не чувствителен к регистру
        MvcResult result = utils.post("/api/sessions", session, new LoginDto(
                "denis", "Denis225"))
                .andExpect(status().isOk())
                .andReturn();

        // Проверяем информацию
        JsonNode node = utils.read(result);

        assertNotNull(node.get("id"));
        assertEquals(client.getFirstName(), node.get("firstName").asText());
        assertEquals(client.getLastName(), node.get("lastName").asText());
        assertEquals(client.getPatronymic(), node.get("patronymic").asText());
        assertEquals(client.getEmail(), node.get("email").asText());
        assertEquals(client.getPhone(), node.get("phone").asText());
        assertEquals(client.getAddress(), node.get("address").asText());
        assertEquals(0, node.get("deposit").asInt());

        assertNull(node.get("login"));
        assertNull(node.get("password"));
    }

    /**
     * Проверем логин с неверным паролем
     */
    @Test
    public void testLoginWithWrongPassword() throws Exception {
        utils.register(utils.getDefaultClient());

        MvcResult result = utils.post("/api/sessions", null, new LoginDto(
                "denis", "erew2342"))
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "UserNotFound");
    }

    /**
     * Проверяем логин с неверным логином
     */
    @Test
    public void testLoginWithWrongLogin() throws Exception {
        MvcResult result = utils.post("/api/sessions", null,
                new LoginDto("ewrw1", "wer2343242"))
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "UserNotFound");
    }

    /**
     * Проверяем что нельзя войти без логина и пароля
     */
    @Test
    public void testLoginWithEmptyFields() throws Exception {
        MvcResult result = utils.post("/api/sessions", null, new LoginDto())
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("Login", "login"),
                Pair.of("Password", "password")
        ));

    }

    /**
     * Проверяем что выход без сессии работает
     */
    @Test
    public void testLogoutWithoutSession() throws Exception {
        utils.delete("/api/sessions", null)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    /**
     * Проверяем что выход с неверной сессией тоже работает
     */
    @Test
    public void testLogoutWithWrongSession() throws Exception {
        utils.delete("/api/sessions", null)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    /**
     * Получение информации о текущем аккаунте
     */
    @Test
    public void testGetAccount() throws Exception {
        ClientDto client = utils.getDefaultClient();
        String session = utils.register(client);

        MvcResult result = utils.get("/api/accounts", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(client.getFirstName(), node.get("firstName").asText());
        assertEquals(client.getLastName(), node.get("lastName").asText());
        assertEquals(client.getPatronymic(), node.get("patronymic").asText());
        assertEquals(client.getEmail(), node.get("email").asText());
        assertEquals(client.getPhone(), node.get("phone").asText());
        assertEquals(client.getAddress(), node.get("address").asText());
        assertEquals(0, node.get("deposit").asInt());

        assertNull(node.get("login"));
        assertNull(node.get("password"));
    }

    /**
     * Проверяем что получить информацию об аккаунте с неверной сессией
     * нельзя
     */
    @Test
    public void testGetAccountWithoutLogin() throws Exception {
        // Проверяем что с неверной сессией мы данные не получим
        MvcResult result = utils.get("/api/accounts", "erew")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Проверяем получение информации о счёте клиента
     */
    @Test
    public void testGetDeposit() throws Exception {
        ClientDto client = utils.getDefaultClient();
        String session = utils.register(client);

        // Ложим ему деньги на счёт
        utils.put("/api/deposits", session, new DepositDto(12001))
                .andExpect(status().isOk());

        // Запрос get deposit должен вернуть всю информацию
        // о клиенте ровно так же как и запрос на регистрацию
        MvcResult result = utils.get("/api/deposits", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(client.getFirstName(), node.get("firstName").asText());
        assertEquals(client.getLastName(), node.get("lastName").asText());
        assertEquals(client.getPatronymic(), node.get("patronymic").asText());
        assertEquals(client.getEmail(), node.get("email").asText());
        assertEquals(client.getPhone(), node.get("phone").asText());
        assertEquals(client.getAddress(), node.get("address").asText());
        assertEquals(12001, node.get("deposit").asInt());

        assertNull(node.get("login"));
        assertNull(node.get("password"));
    }

    /**
     * Занесение денег на счёт клиента
     */
    @Test
    public void testAddDeposit() throws Exception {
        ClientDto client = utils.getDefaultClient();
        String session = utils.register(client);

        MvcResult result = utils.put("/api/deposits", session, new DepositDto(15))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(client.getFirstName(), node.get("firstName").asText());
        assertEquals(client.getLastName(), node.get("lastName").asText());
        assertEquals(client.getPatronymic(), node.get("patronymic").asText());
        assertEquals(client.getEmail(), node.get("email").asText());
        assertEquals(client.getPhone(), node.get("phone").asText());
        assertEquals(client.getAddress(), node.get("address").asText());
        assertEquals(15, node.get("deposit").asInt());

        assertNull(node.get("login"));
        assertNull(node.get("password"));
    }

    /**
     * Нельзя положить на счёт клиента отрицательное количество денег
     */
    @Test
    public void testAddDepositNegative() throws Exception {

        String session = utils.register(utils.getDefaultClient());

        MvcResult result = utils.put("/api/deposits", session, new DepositDto(-13))
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("DecimalMin", "deposit"));
    }

    /**
     * Нельзя получить информацию о счёте с неверной сессией
     */
    @Test
    public void testGetDepositWithoutLogin() throws Exception {
        MvcResult result = utils.get("/api/deposits", "erew")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя положить деньги на счёт с неверной сессией
     */
    @Test
    public void testPutDepositWithoutLogin() throws Exception {
        MvcResult result = utils.put("/api/deposits", "rwer", new DepositDto(34))
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя купить товар с неверной сессией
     */
    @Test
    public void testBuyProductWithoutLogin() throws Exception {
        ProductDto product = utils.getProduct("product", 1, null);

        MvcResult result = utils.post("/api/purchases", "rwe", product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя купить товар, которого нет в БД
     */
    @Test
    public void testBuyProductNotExist() throws Exception {
        String session = utils.register(utils.getDefaultClient());

        ProductDto product = new ProductDto();
        product.setId(1L);
        product.setName("erw");
        product.setPrice(2);

        MvcResult result = utils.post("/api/purchases", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("ProductNotFound", "id"));
    }

    /**
     * Нельзя купить товар если данные о нём указаны неверно
     */
    @Test
    public void testBuyProductWithWrongInfo() throws Exception {
        // Создадим товар от имени администратора
        String adminSession = utils.register(utils.getDefaultAdmin());
        ProductDto product = new ProductDto();
        product.setName("iphone");
        product.setPrice(39_999);
        long productId = utils.register(adminSession, product);

        String session = utils.register(utils.getDefaultClient());

        // Неверное имя
        ProductDto toBuy = new ProductDto();
        toBuy.setId(productId);
        toBuy.setName("other");
        toBuy.setPrice(product.getPrice());

        MvcResult result = utils.post("/api/purchases", session, toBuy)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("WrongProductInfo", "name"));

        // Неверная цена
        toBuy = new ProductDto();
        toBuy.setId(productId);
        toBuy.setName(product.getName());
        toBuy.setPrice(1100);

        result = utils.post("/api/purchases", session, toBuy)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("WrongProductInfo", "price"));
    }

    /**
     * Нельзя купить товар если его недостаточно на складе
     */
    @Test
    public void testBuyProductNotEnough() throws Exception {
        // Создадим товар
        String adminSession = utils.register(utils.getDefaultAdmin());
        ProductDto product = new ProductDto();
        product.setName("iphone");
        product.setPrice(39_999);
        product.setCount(3);
        long productId = utils.register(adminSession, product);

        String session = utils.register(utils.getDefaultClient());

        // Купим больше чем есть
        ProductDto toBuy = new ProductDto();
        toBuy.setId(productId);
        toBuy.setName(product.getName());
        toBuy.setPrice(product.getPrice());
        toBuy.setCount(4);

        MvcResult result = utils.post("/api/purchases", session, toBuy)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("NotEnoughProduct", "count"));

    }

    /**
     * Нельзя купить товар если сумма покупки превышает количество денег на счету
     */
    @Test
    public void testBuyProductNotEnoughMoney() throws Exception {
        // Создадим товар
        String adminSession = utils.register(utils.getDefaultAdmin());
        ProductDto product = new ProductDto();
        product.setName("iphone");
        product.setPrice(39_999);
        product.setCount(3);
        long productId = utils.register(adminSession, product);

        // Создадим клиента и положим немного денег
        String session = utils.register(utils.getDefaultClient());
        utils.put("/api/deposits", session, new DepositDto(100_000))
                .andExpect(status().isOk());

        // Теперь скупаем весь товар что есть на складе
        ProductDto toBuy = new ProductDto();
        toBuy.setId(productId);
        toBuy.setName(product.getName());
        toBuy.setPrice(product.getPrice());
        toBuy.setCount(3);

        MvcResult result = utils.post("/api/purchases", session, toBuy)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotEnoughMoney");
    }

    /**
     * Успешная покупка товара
     */
    @Test
    public void testBuyProduct() throws Exception {
        // Покупаем товар

        // Создадим товар
        String adminSession = utils.register(utils.getDefaultAdmin());
        ProductDto product = new ProductDto();
        product.setName("iphone");
        product.setPrice(39_999);
        product.setCount(4);
        long productId = utils.register(adminSession, product);

        // Создадим клиента и положим немного денег
        String session = utils.register(utils.getDefaultClient());
        utils.put("/api/deposits", session, new DepositDto(200_000))
                .andExpect(status().isOk());

        // Теперь скупаем весь товар что есть на складе
        ProductDto toBuy = new ProductDto();
        toBuy.setId(productId);
        toBuy.setName(product.getName());
        toBuy.setPrice(product.getPrice());
        toBuy.setCount(3);

        // Проверяем что результат содержит данные о купленном товаре
        MvcResult result = utils.post("/api/purchases", session, toBuy)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(productId, node.get("id").asLong());
        assertEquals(product.getName(), node.get("name").asText());
        assertEquals((int)product.getPrice(), node.get("price").asInt());
        assertEquals((int)toBuy.getCount(), node.get("count").asInt());

        // Проверяем что количество денег на счету изменилось
        result = utils.get("/api/deposits", session)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        assertEquals(200_000 - 3 * 39_999, node.get("deposit").asInt());

        // Проверяем что количество товара на складе изменилось
        result = utils.get("/api/products/" + productId, session)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        assertEquals(1, node.get("count").asInt());

        // Если мы не указываем сколько хотим купить. то количество равно одному
        toBuy = new ProductDto();
        toBuy.setId(productId);
        toBuy.setName(product.getName());
        toBuy.setPrice(product.getPrice());
        toBuy.setCount(null);

        utils.post("/api/purchases", session, toBuy)
                .andExpect(status().isOk());

        // И товара должно не остаться на складе, так как выкупили последний
        result = utils.get("/api/products/" + productId, session)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        assertEquals(0, node.get("count").asInt());
    }

    /**
     * Получаем список товаров отсортированных по именам
     * Требования и проверки точно такие же как и в интеграционных администратора
     *
     * Создавать товары мы будем через аккунт админа, а через
     * клиента получать список товаров
     */
    @Test
    public void testGetProductsByProductsNames() throws Exception {

        String adminSession = utils.register(utils.getDefaultAdmin());
        String session = utils.register(utils.getDefaultClient());

        // Подгатавливаем список товаров
        long category = utils.register(adminSession, new CategoryDto("category"));

        long warcraft = utils.register(adminSession,
                utils.getProduct("warcraft", 10_000, null));

        long apple = utils.register(adminSession,
                utils.getProduct("apple", 10_000, Collections.singletonList(category)));

        long berretta = utils.register(adminSession,
                utils.getProduct("berretta", 10_000, null));

        // Создадим удалённый товар и проверим что в список он не попадёт
        long deleted = utils.register(adminSession,
                utils.getProduct("deleted", 10_000, null));

        utils.delete("/api/products/" + deleted, adminSession)
                .andExpect(status().isOk());

        // Теперь получем список всех товаров от имени клиента
        MvcResult result = utils.get("/api/products", session)
                .andExpect(status().isOk())
                .andReturn();

        String firstResult = utils.getContent(result);
        JsonNode node = utils.read(result);

        assertEquals(3, node.size());

        assertEquals(apple, node.get(0).get("id").asLong());
        assertEquals("apple", node.get(0).get("name").asText());
        assertEquals(10_000, node.get(0).get("price").asInt());
        assertEquals(0, node.get(0).get("count").asInt());
        assertEquals(1, node.get(0).get("categories").size());
        assertEquals(category, node.get(0).get("categories").get(0).asLong());

        assertEquals(berretta, node.get(1).get("id").asLong());
        assertEquals("berretta", node.get(1).get("name").asText());
        assertEquals(10_000, node.get(1).get("price").asInt());
        assertEquals(0, node.get(1).get("count").asInt());

        assertEquals(warcraft, node.get(2).get("id").asLong());
        assertEquals("warcraft", node.get(2).get("name").asText());
        assertEquals(10_000, node.get(2).get("price").asInt());
        assertEquals(0, node.get(2).get("count").asInt());

        // Указываем сортировку по товарам явно и проверяем, что ответ будет тот же самый
        // потому что по умолчанию всегда используется сортировка по товарам
        utils.get("/api/products?order=product", session)
                .andExpect(status().isOk())
                .andExpect(content().string(firstResult));

        // Сортировка по товарам, но только товары, которые не принадлежат не одной категории
        result = utils.get("/api/products?order=product&category=", session)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        assertEquals(2, node.size());
        assertEquals("berretta", node.get(0).get("name").asText());
        assertEquals("warcraft", node.get(1).get("name").asText());

        // Список товаров, которые принадлежат данным категориям
        result = utils.get("/api/products?order=product&category=" + category, session)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        assertEquals(1, node.size());
        assertEquals("apple", node.get(0).get("name").asText());
    }

    /**
     * Получение списка товаров отсортированных по именам категорий, а внутри
     * категорий по именам товаров
     *
     * Опять же проверки такие же как и в интгеграционных для администратора
     */
    @Test
    public void testGetProductsByCategories() throws Exception {

        String adminSession = utils.register(utils.getDefaultAdmin());
        String session = utils.register(utils.getDefaultClient());

        // Подгатавливаем список товаров
        long pen = utils.register(adminSession, utils.getProduct("pen", 10_000, null));
        long array = utils.register(adminSession, utils.getProduct("array", 10_000, null));

        // Список категорий для товаров
        long bat = utils.register(adminSession, new CategoryDto("bat"));
        long wat = utils.register(adminSession, new CategoryDto("wat"));
        long at = utils.register(adminSession, new CategoryDto("at"));

        // И список товаров с категориями
        utils.register(adminSession, utils.getProduct("xen", 10_000, Collections.singletonList(at)));
        utils.register(adminSession, utils.getProduct("apple", 10_000, Collections.singletonList(wat)));

        // Id одного товара обязательно запомним
        long berretta = utils.register(adminSession,
                utils.getProduct("berretta", 10_000, Arrays.asList(at, wat)));

        // И ещё один товар
        utils.register(adminSession, utils.getProduct("warcraft", 10_000, Collections.singletonList(bat)));

        // И теперь получем списки от имени клиента
        MvcResult result = utils.get("/api/products?order=category", session)
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = utils.read(result);

        /*
        Ожидаем такой порядок:

        Категория   |   Товар
        ---------------------------
                        array
                        pen
        at              berretta
        at              xen
        bat             warcraft
        wat             apple
        wat             berretta

         */

        // Проверяем первые два продукта без категорий
        assertEquals(array, node.get(0).get("id").asLong());
        assertEquals("array", node.get(0).get("name").asText());
        assertEquals(10_000, node.get(0).get("price").asInt());
        assertEquals(0, node.get(0).get("count").asInt());
        assertNull(node.get(0).get("categories"));

        assertEquals(pen, node.get(1).get("id").asLong());
        assertEquals("pen", node.get(1).get("name").asText());
        assertEquals(10_000, node.get(1).get("price").asInt());
        assertEquals(0, node.get(1).get("count").asInt());
        assertNull(node.get(1).get("categories"));

        // Теперь проверяем список с категориями
        // at   -> berretta
        assertEquals(berretta, node.get(2).get("id").asLong());
        assertEquals("berretta", node.get(2).get("name").asText());
        assertEquals(10_000, node.get(2).get("price").asInt());
        assertEquals(0, node.get(2).get("count").asInt());
        assertEquals(1, node.get(2).get("categories").size());
        assertEquals(at, node.get(2).get("categories").get(0).asLong());

        // Теперь будем проверять только именами и категории, так как
        // формат однозначно верный
        // at   -> xen
        assertEquals("xen", node.get(3).get("name").asText());
        assertEquals(1, node.get(3).get("categories").size());
        assertEquals(at, node.get(3).get("categories").get(0).asLong());

        // bat  -> warcraft
        assertEquals("warcraft", node.get(4).get("name").asText());
        assertEquals(1, node.get(4).get("categories").size());
        assertEquals(bat, node.get(4).get("categories").get(0).asLong());

        // wat  -> apple
        assertEquals("apple", node.get(5).get("name").asText());
        assertEquals(1, node.get(5).get("categories").size());
        assertEquals(wat, node.get(5).get("categories").get(0).asLong());

        // wat  -> berretta
        assertEquals("berretta", node.get(6).get("name").asText());
        assertEquals(1, node.get(6).get("categories").size());
        assertEquals(wat, node.get(6).get("categories").get(0).asLong());

        // Сортировка по именам категорий товаров, которые не принадлежат не одной категории
        result = utils.get("/api/products?order=category&category=", session)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);

        // Должна быть сортировка по именам первых двух товаров
        assertEquals(2, node.size());
        assertEquals("array", node.get(0).get("name").asText());
        assertEquals("pen", node.get(1).get("name").asText());

        // Сортировка по именам категорий товаров, которые принадлежат категориям
        result = utils.get("/api/products?order=category&category=" + wat + "," + bat, session)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);

        // Должны получить три товара которые содержат категории
        // wat и bat, отсортированные по категориям, а затем по именам товаров
        assertEquals(3, node.size());
        assertEquals("warcraft", node.get(0).get("name").asText());
        assertEquals("apple", node.get(1).get("name").asText());
        assertEquals("berretta", node.get(2).get("name").asText());

    }


    /**
     * Нельзя добавить товар в корзину с неверной сессией
     */
    @Test
    public void testAddProductToBasketWithoutLogin() throws Exception {
        ProductDto product = utils.getProduct("product", 1, null);

        MvcResult result = utils.post("/api/baskets", "erwe", product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Успешное добавление товара
     */
    @Test
    public void testAddProduct() throws Exception {
        String session = utils.register(utils.getDefaultClient());
        String adminSession = utils.register(utils.getDefaultAdmin());

        // По умолчанию количество товара = 0
        ProductDto product = utils.getProduct("product", 100, null);
        long productId = utils.register(adminSession, product);

        ProductDto toBasket = new ProductDto();
        toBasket.setId(productId);
        toBasket.setName(product.getName());
        toBasket.setPrice(product.getPrice());
        // Добавим больше товара чем есть на складе
        toBasket.setCount(10);

        // Денег у клиента тоже нет, но это не должно помешать
        // добавить товар в корзину

        MvcResult result = utils.post("/api/baskets", session, toBasket)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(1, node.size());
        assertEquals(productId, node.get(0).get("id").asLong());
        assertEquals(product.getName(), node.get(0).get("name").asText());
        assertEquals((int)product.getPrice(), node.get(0).get("price").asInt());
        assertEquals((int)toBasket.getCount(), node.get(0).get("count").asInt());

    }

    /**
     * Нельзя добавить товар в корзину с неверной информацией
     */
    @Test
    public void testAddProductWrongInfo() throws Exception {
        String session = utils.register(utils.getDefaultClient());
        String adminSession = utils.register(utils.getDefaultAdmin());

        ProductDto product = utils.getProduct("product", 100, null);
        long productId = utils.register(adminSession, product);

        // Товар с неверным Id
        ProductDto toBasket = new ProductDto();
        toBasket.setId(-1L);
        toBasket.setName(product.getName());
        toBasket.setPrice(product.getPrice());

        MvcResult result = utils.post("/api/baskets", session, toBasket)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("ProductNotFound", "id"));

        // Товар с неверным именем
        toBasket.setId(productId);
        toBasket.setName("something");
        toBasket.setPrice(product.getPrice());

        result = utils.post("/api/baskets", session, toBasket)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("WrongProductInfo", "name"));

        // Товар с неверной ценою
        toBasket.setId(productId);
        toBasket.setName(product.getName());
        toBasket.setPrice(123);

        result = utils.post("/api/baskets", session, toBasket)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("WrongProductInfo", "price"));
    }

    /**
     * Проверяем что по умолчанию количество товара равно единице
     */
    @Test
    public void testAddProductWithDefaultCount() throws Exception {
        // По умолчанию количество товаар равно единицы

        String session = utils.register(utils.getDefaultClient());
        String adminSession = utils.register(utils.getDefaultAdmin());

        ProductDto product = utils.getProduct("product", 100, null);
        long productId = utils.register(adminSession, product);

        ProductDto toBasket = new ProductDto();
        toBasket.setId(productId);
        toBasket.setName(product.getName());
        toBasket.setPrice(product.getPrice());
        toBasket.setCount(null);

        MvcResult result = utils.post("/api/baskets", session, toBasket)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(1, node.size());
        assertEquals(1, node.get(0).get("count").asInt());
    }

    /**
     * Проверяем что нельзя добавить товар с отрицательным количеством
     */
    @Test
    public void testAddProductWithNegativeCount() throws Exception {
        String session = utils.register(utils.getDefaultClient());

        ProductDto toBasket = new ProductDto();
        toBasket.setName(null);
        toBasket.setPrice(null);
        toBasket.setCount(-2);

        MvcResult result = utils.post("/api/baskets", session, toBasket)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("DecimalMin", "count"),
                Pair.of("NotNull", "price"),
                Pair.of("RequiredName", "name")
        ));
    }

    /**
     * Удаление товара из корзины
     */
    @Test
    public void testDeleteFromBasket() throws Exception {
        String adminSession = utils.register(utils.getDefaultAdmin());
        String session = utils.register(utils.getDefaultClient());

        long productId = utils.register(adminSession, utils.getProduct("name", 1, null));

        // Добавляем в коризну
        ProductDto product = new ProductDto();
        product.setId(productId);
        product.setName("name");
        product.setPrice(1);
        utils.post("/api/baskets", session, product)
                .andExpect(status().isOk());

        // Нельзя удалить несуществующий
        MvcResult result = utils.delete("/api/baskets/" + (productId + 1), session)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "ProductNotFound");

        // После удаляем товар из корзины (он у нас один
        utils.delete("/api/baskets/" + productId, session)
                .andExpect(status().isOk());

        // Проверяем что в корзине ничего не осталоьс
        result = utils.get("/api/baskets", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(0, node.size());
    }


    /**
     * Нельзя удалить товар из корзины с неверной сессией
     */
    @Test
    public void testDeleteFromBasketWithoutLogin() throws Exception {
        MvcResult result = utils.delete("/api/baskets/3", "wrew")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя изменить количество товара в корзине с неверной сессией
     */
    @Test
    public void testEditProductCountWithoutLogin() throws Exception {
        ProductDto product = utils.getProduct("product", null, null);

        MvcResult result = utils.put("/api/baskets", "werwe", product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя изменить количество товара в корзине не указав количество в запросе
     */
    @Test
    public void testEditCountWithoutCount() throws Exception {
        String session = utils.register(utils.getDefaultClient());

        // Добавлаем товар
        String adminSession = utils.register(utils.getDefaultAdmin());
        long product = utils.register(adminSession, utils.getProduct("product", 1, null));
        // Добавляем товар в корзину
        utils.post("/api/baskets", session, new ProductDto(product, "product", 1, 10))
                .andExpect(status().isOk());

        // Пытаемся изменить количество без указания количества
        ProductDto edit = new ProductDto();
        edit.setId(product);
        edit.setName("product");
        edit.setPrice(1);
        edit.setCount(null);

        MvcResult result = utils.put("/api/baskets", session, edit)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("RequiredCount", "count"));

    }

    /**
     * Нельзя изменить количество товара в корзине указав неверные данные
     */
    @Test
    public void testEditCountProductWithWrongInfo() throws Exception {
        String session = utils.register(utils.getDefaultClient());
        // Регистрируем товар
        String adminSession = utils.registerDefaultAdmin();
        long productId = utils.register(adminSession, utils.getProduct("product", 100, null));

        // id не найден, потому что товара ещё нет в корзине
        ProductDto product = new ProductDto();
        product.setId(1L);

        MvcResult result = utils.put("/api/baskets", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("ProductNotFound", "id"));

        // Добавляем товар в корзину
        utils.post("/api/baskets", session, new ProductDto(productId, "product", 100, 100));

        // Название неверно
        product = new ProductDto();
        product.setId(productId);
        product.setName("produc234jt");
        product.setPrice(product.getPrice());
        product.setCount(10);

        result = utils.put("/api/baskets", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("WrongProductInfo", "name"));

        // Цена товара не верно
        product = new ProductDto();
        product.setId(productId);
        product.setName("product");
        product.setPrice(1);
        product.setCount(11);

        result = utils.put("/api/baskets", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("WrongProductInfo", "price"));

        // Нельзя отрицательное количество
        product = new ProductDto();
        product.setId(productId);
        product.setName("product");
        product.setPrice(1);
        product.setCount(-1);

        result = utils.put("/api/baskets", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("DecimalMin", "count"));
    }

    /**
     * Изменение количества товара в корзине
     */
    @Test
    public void testEditProductCount() throws Exception {
        String session = utils.register(utils.getDefaultClient());
        String adminSession = utils.registerDefaultAdmin();

        // Создаём два товара
        long product1 = utils.register(adminSession, new ProductDto("product1", 100, 100));
        long product2 = utils.register(adminSession, new ProductDto("product2", 100, 100));

        // Добавляем товары в корзину
        utils.post("/api/baskets", session, new ProductDto(product1, "product1", 100, 100));
        utils.post("/api/baskets", session, new ProductDto(product2, "product2", 100, 100));

        // Проверим что можно изменить количество
        // вне зависимости от того, сколько есть его на складе
        MvcResult result = utils.put("/api/baskets", session,
                new ProductDto(product1, "product1", 100, 1000))
                .andExpect(status().isOk())
                .andReturn();

        // Проверяем что запрос возвращает содержимое корзины
        JsonNode node = utils.read(result);
        assertEquals(2, node.size());
        assertEquals(product1, node.get(0).get("id").asLong());
        assertEquals("product1", node.get(0).get("name").asText());
        assertEquals(100, node.get(0).get("price").asInt());
        assertEquals(1000, node.get(0).get("count").asInt());

        assertEquals(product2, node.get(1).get("id").asLong());
        assertEquals("product2", node.get(1).get("name").asText());
        assertEquals(100, node.get(1).get("price").asInt());
        assertEquals(100, node.get(1).get("count").asInt());
    }

    /**
     * Можно изменить количество удалённого товара в корзине
     * (удалённого из списка товаров, а не из корзины)
     */
    @Test
    public void testEditProductCountDeleted() throws Exception {
        String session = utils.register(utils.getDefaultClient());
        String adminSession = utils.registerDefaultAdmin();

        // Создаём два товара
        long product1 = utils.register(adminSession, new ProductDto("product1", 100, 100));
        long product2 = utils.register(adminSession, new ProductDto("product2", 100, 100));

        // Добавляем товары в корзину
        utils.post("/api/baskets", session, new ProductDto(product1, "product1", 100, 100));
        utils.post("/api/baskets", session, new ProductDto(product2, "product2", 100, 100));

        // Удаляем второй товар
        utils.delete("/api/products/" + product1, adminSession)
                .andExpect(status().isOk());

        // Проверим что можно изменить количество удалённого товара
        MvcResult result = utils.put("/api/baskets", session,
                new ProductDto(product1, "product1", 100, 1000))
                .andExpect(status().isOk())
                .andReturn();

        // Проверяем что запрос возвращает содержимое корзины
        JsonNode node = utils.read(result);
        assertEquals(2, node.size());
        assertEquals(product1, node.get(0).get("id").asLong());
        assertEquals("product1", node.get(0).get("name").asText());
        assertEquals(100, node.get(0).get("price").asInt());
        assertEquals(1000, node.get(0).get("count").asInt());

        assertEquals(product2, node.get(1).get("id").asLong());
        assertEquals("product2", node.get(1).get("name").asText());
        assertEquals(100, node.get(1).get("price").asInt());
        assertEquals(100, node.get(1).get("count").asInt());
    }

    /**
     * Нельзя получить содержимое корзины без логина
     */
    @Test
    public void testGetBasketWithoutLogin() throws Exception {
        MvcResult result = utils.get("/api/baskets", "werwe")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Получение содержимого корзины (и удалённых товаров в том числе)
     */
    @Test
    public void testGetBasket() throws Exception {
        // Добавим два товара
        String adminSession = utils.register(utils.getDefaultAdmin());

        ProductDto product = new ProductDto();
        product.setName("product");
        product.setPrice(1);
        long productId = utils.register(adminSession, product);

        ProductDto deleted = new ProductDto();
        deleted.setName("deleted");
        deleted.setPrice(2);
        long deletedId = utils.register(adminSession, deleted);

        // Добавляем оба товаара в корзину
        String session = utils.register(utils.getDefaultClient());
        utils.post("/api/baskets", session, new ProductDto(
                productId, product.getName(), product.getPrice(), 10
        )).andExpect(status().isOk());

        utils.post("/api/baskets", session, new ProductDto(
                deletedId, deleted.getName(), deleted.getPrice(), 15
        )).andExpect(status().isOk());

        // Удаляем второй товар
        utils.delete("/api/products/" + deletedId, adminSession)
                .andExpect(status().isOk());

        // Проверям список товаров
        MvcResult result = utils.get("/api/baskets", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(productId, node.get(0).get("id").asLong());
        assertEquals(product.getName(), node.get(0).get("name").asText());
        assertEquals((int)product.getPrice(), node.get(0).get("price").asInt());
        assertEquals(10, node.get(0).get("count").asInt());

        assertEquals(deletedId, node.get(1).get("id").asLong());
        assertEquals(deleted.getName(), node.get(1).get("name").asText());
        assertEquals((int)deleted.getPrice(), node.get(1).get("price").asInt());
        assertEquals(15, node.get(1).get("count").asInt());
    }

    /**
     * Нельзя выкупить корзине с неверной сессией
     */
    @Test
    public void testBuyBasketWithoutLogin() throws Exception {

        MvcResult result = utils.post("/api/purchases/baskets", "wre",
                Collections.singletonList(utils.getProduct("product", null, null)))
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Покупка товаров из корзины
     */
    @Test
    public void testBuyBasket() throws Exception {

        // Сначала создадим товары
        String adminSession = utils.registerDefaultAdmin();

        List<Long> products = new ArrayList<>();
        for (int i = 0; i < 7; ++i) {
            products.add(
                    utils.register(adminSession, new ProductDto("product" + i, 12, 100))
            );
        }

        // Все товары добавляем в корзину
        String session = utils.register(utils.getDefaultClient());

        for (int i = 0; i < products.size(); ++i) {
            utils.post("/api/baskets", session, new ProductDto(products.get(i), "product" + i, 12, 100))
                    .andExpect(status().isOk());
        }

        // Одного товара у нас будет больше чем на складе
        // Пусть это будет пятый товар
        utils.put("/api/baskets", session, new ProductDto(products.get(4), "product4", 12, 1000))
                .andExpect(status().isOk());
        // Один товар был удалён из базы данных (пусть будет шестой)
        utils.delete("/api/products/" + products.get(5), adminSession)
                .andExpect(status().isOk());

        // Теперь начнём составлять корзину
        List<ProductDto> toBuy = new ArrayList<>();
        // (успешный) первый товар мы выкупим частично
        toBuy.add(new ProductDto(products.get(0), "product0", 12, 50));
        // (успешный) второй товар выкупил полностью
        toBuy.add(new ProductDto(products.get(1), "product1", 12, 100));
        // (успешный) третий товар без количество (будет выкуплен полностью, сколько указано в корзине)
        toBuy.add(new ProductDto(products.get(2), "product2", 12, null));
        // (успешный) четвёртый товар количество больше чем в корзине
        // будет куплено столько, сколько есть в корзине
        toBuy.add(new ProductDto(products.get(3), "product3", 12, 120));

        // (отклонён) пятый товар выкупит сколько в корзине, а в корзине больше чем на складе
        toBuy.add(new ProductDto(products.get(4), "product4", 12, null));
        // (отклёнён) шестой товар удалён из базы данных
        toBuy.add(new ProductDto(products.get(5), "product5", 12, 100));
        // (отклёнён) товар с неверными данным
        toBuy.add(new ProductDto(products.get(6), "prdct6", 13, 100));

        // Теперь выполняем запрос на покупку товаров
        MvcResult result = utils.post("/api/purchases/baskets", session, toBuy)
                .andExpect(status().isBadRequest())
                .andReturn();

        // Денег-то у нас нет!
        utils.assertErrorCode(result, "NotEnoughMoney");

        // Теперь положим деньги на счёт и заново выполним запрос
        utils.put("/api/deposits", session, new DepositDto(100_000))
                .andExpect(status().isOk());

        // И выполним запрос снова
        result = utils.post("/api/purchases/baskets", session, toBuy)
                .andExpect(status().isOk())
                .andReturn();

        // Запрос вернёт нам два списка, вот их отдельно и будет обрабатывать
        JsonNode bought = utils.read(result).get("bought");
        JsonNode remaining = utils.read(result).get("remaining");

        // У нас куплено четыре товара
        assertEquals(4, bought.size());

        // Первый товар
        assertEquals((long)products.get(0), bought.get(0).get("id").asLong());
        assertEquals("product0", bought.get(0).get("name").asText());
        assertEquals(12, bought.get(0).get("price").asInt());
        assertEquals(50, bought.get(0).get("count").asInt());

        // Другие трое с одинаковым количеством
        for (int i = 1; i <= 3; ++i) {
            assertEquals((long) products.get(i), bought.get(i).get("id").asLong());
            assertEquals("product" + i, bought.get(i).get("name").asText());
            assertEquals(12, bought.get(i).get("price").asInt());
            assertEquals(100, bought.get(i).get("count").asInt());
        }

        // Теперь проверяем что осталось в корзине
        // Должно остаться четыре наименования
        assertEquals(4, remaining.size());

        // Первый товар мы не выкупили полностью
        assertEquals((long)products.get(0), remaining.get(0).get("id").asLong());
        assertEquals("product0", remaining.get(0).get("name").asText());
        assertEquals(12, remaining.get(0).get("price").asInt());
        assertEquals(50, remaining.get(0).get("count").asInt());

        assertEquals((long)products.get(4), remaining.get(1).get("id").asLong());
        assertEquals("product4", remaining.get(1).get("name").asText());
        assertEquals(12, remaining.get(1).get("price").asInt());
        assertEquals(1000, remaining.get(1).get("count").asInt());

        // Остальные два товара без изменений с одинаковым количеством
        for (int i = 2; i <= 3; ++i) {
            assertEquals((long)products.get(i+3), remaining.get(i).get("id").asLong());
            assertEquals("product" + (i+3), remaining.get(i).get("name").asText());
            assertEquals(12, remaining.get(i).get("price").asInt());
            assertEquals(100, remaining.get(i).get("count").asInt());
        }

        // Проверим что деньги на счету были сняты
        result = utils.get("/api/deposits", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(100_000 - 350 * 12, node.get("deposit").asInt());

    }

}
