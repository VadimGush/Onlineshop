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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = OnlineShopServer.class
)
@TestPropertySource("classpath:config-test.properties")
@AutoConfigureMockMvc
public class AdministratorIntegrationTest {

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
     * Регистрация администратора
     */
    @Test
    public void testRegistration() throws Exception {
        AdminDto admin = utils.getDefaultAdmin();

        MvcResult result = utils.post("/api/admins", null, admin)
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result.getResponse().getCookie("JAVASESSIONID"));

        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(admin.getFirstName(), node.get("firstName").asText());
        assertEquals(admin.getLastName(), node.get("lastName").asText());
        assertEquals(admin.getPatronymic(), node.get("patronymic").asText());
        assertEquals(admin.getPosition(), node.get("position").asText());
        assertNull(node.get("login"));
        assertNull(node.get("password"));

    }

    /**
     * Проверяем что нельзя зарегать с занятым логином
     */
    @Test
    public void testRegistrationWithSameLogin() throws Exception {
        utils.register(utils.getDefaultAdmin());

        // Логин не чувствителен к регистру, поэтому регистрация не пройдёт
        AdminDto admin = utils.getDefaultAdmin();
        admin.setLogin("vadim");

        MvcResult result = utils.post("/api/admins", null, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Collections.singletonList(
                Pair.of("LoginInUse", "login")
        ));

    }

    /**
     * Проверяем что нельзя зарегать со слишком длинными именами
     * и коротким паролем
     */
    @Test
    public void testFailedRegistrationLongFieldsShortPassword() throws Exception {
        AdminDto admin = utils.getDefaultAdmin();
        admin.setFirstName("eewrewrjlewkjrewrklwerjew");
        admin.setLastName("eewrewrjlewkjrewrklwerjew");
        admin.setPatronymic("eewrewrjlewkjrewrklwerjew");
        admin.setPosition("eewrewrjlewkjrewrklwerjew");
        admin.setLogin("eewrewrjlewkjrewrklwerjew");

        admin.setPassword("wew");

        MvcResult result = utils.post("/api/admins", null, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("Login", "login"),
                Pair.of("RequiredName", "position"),
                Pair.of("OptionalRussianName", "patronymic"),
                Pair.of("Password", "password")
        ));

    }

    /**
     * Проверяем валидацию формата полей
     */
    @Test
    public void testFailedRegistrationWrongNamesFormat() throws Exception {
        AdminDto admin = utils.getDefaultAdmin();
        // Имя не может состоять из английских букв, цифр и знаков препинания
        admin.setFirstName("Vadim");
        admin.setLastName("234234");
        admin.setPatronymic("Ar.- ");
        admin.setPosition("programmer");
        admin.setLogin("vadim234");
        admin.setPassword("wererewrw");

        MvcResult result = utils.post("/api/admins", null, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("OptionalRussianName", "patronymic")
        ));
    }

    /**
     * Проверка, что регистрация не пройдёт с пустыми или неверными полями
     */
    @Test
    public void testFailedRegistrationEmptyFields() throws Exception {
        AdminDto admin = utils.getDefaultAdmin();
        admin.setFirstName("");
        admin.setLastName("");
        admin.setLogin("");
        admin.setPosition("");
        admin.setPassword("");
        admin.setPatronymic("");

        MvcResult result = utils.post("/api/admins", null, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("Login", "login"),
                Pair.of("RequiredName", "position"),
                Pair.of("Password", "password"),
                Pair.of("OptionalRussianName", "patronymic")
        ));
    }

    /**
     * Проверяем валидацию формата поля логина
     */
    @Test
    public void testFailedRegistrationWrongLoginFormat() throws Exception {

        // Логин не может содержать в себе знаки препинания или пробелы
        AdminDto admin = utils.getDefaultAdmin();
        admin.setLogin("vadim.");

        MvcResult result = utils.post("/api/admins", null, admin)
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertError(result, Pair.of("Login", "login"));

        admin = utils.getDefaultAdmin();
        admin.setLogin("vad im");

        result = utils.post("/api/admins", null, admin)
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertError(result, Pair.of("Login", "login"));

    }

    /**
     * Проверяем что можно зарегаться без отчества
     */
    @Test
    public void testRegistrationWithoutPatronymic() throws Exception {
        // Теперь проверяем что регистрация без отчества пройдёт успешно
        AdminDto admin = utils.getDefaultAdmin();
        admin.setPatronymic(null);

        MvcResult result = utils.post("/api/admins", null, admin)
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result.getResponse().getCookie("JAVASESSIONID"));

        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(admin.getFirstName(), node.get("firstName").asText());
        assertEquals(admin.getLastName(), node.get("lastName").asText());
        assertEquals(admin.getPosition(), node.get("position").asText());
        assertNull(node.get("patronymic"));
        assertNull(node.get("login"));
        assertNull(node.get("password"));
    }

    /**
     * Проверяем что нельзя войти с неверным паролем
     */
    @Test
    public void testLoginWithWrongPassword() throws Exception {
        utils.register(utils.getDefaultAdmin());

        // Неверный пароль
        LoginDto login = new LoginDto("vadim", "werew8778");
        MvcResult result = utils.post("/api/sessions", null, login)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorsCodes(result, Collections.singletonList("UserNotFound"));

    }

    /**
     * Проверяем что нельзя войти с неверным логином
     */
    @Test
    public void testLoginWithWrongLogin() throws Exception {
        // Неверный логин
        LoginDto login = new LoginDto("vadi", "Iddqd225");

        MvcResult result = utils.post("/api/sessions", null, login)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorsCodes(result, Collections.singletonList("UserNotFound"));
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
     * Проверяем что выход из аккаунта без сессии тоже работает
     */
    @Test
    public void testLogoutWithoutSession() throws Exception {
        utils.delete("/api/sessions", null)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    /**
     * Проверяем вход и выход из аккаунта
     */
    @Test
    public void testLoginAndLogout() throws Exception {
        // Регистрируем администратора
        AdminDto admin = utils.getDefaultAdmin();

        MvcResult result = utils.post("/api/admins", null, admin)
                .andExpect(status().isOk())
                .andReturn();

        String session = utils.getSession(result);

        // И выходим из аккунта по прошлой сессии
        utils.delete("/api/sessions", session)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));


        // Делаем успешный логин
        LoginDto login = new LoginDto("vadIm", "Iddqd225");

        result = utils.post("/api/sessions", null, login)
                .andExpect(status().isOk())
                .andReturn();

        // Проверяем что login вернул информацию об аккаунте
        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(admin.getFirstName(), node.get("firstName").asText());
        assertEquals(admin.getLastName(), node.get("lastName").asText());
        assertEquals(admin.getPatronymic(), node.get("patronymic").asText());
        assertEquals(admin.getPosition(), node.get("position").asText());
        assertNull(node.get("login"));
        assertNull(node.get("password"));

        // С помощью полученной сессии мы должны успешно выполнить какой-нибудь запрос
        session = utils.getSession(result);

        utils.get("/api/clients", session)
                .andExpect(status().isOk());
    }

    /**
     * Получение информации о текущем аккаунте
     */
    @Test
    public void testGetAccount() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());
        AdminDto admin = utils.getDefaultAdmin();

        MvcResult result = utils.get("/api/accounts", session)
                .andExpect(status().isOk())
                .andReturn();

        // Проверяем что данные правильные
        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(admin.getFirstName(), node.get("firstName").asText());
        assertEquals(admin.getLastName(), node.get("lastName").asText());
        assertEquals(admin.getPatronymic(), node.get("patronymic").asText());
        assertEquals(admin.getPosition(), node.get("position").asText());
        assertNull(node.get("login"));
        assertNull(node.get("password"));

    }

    /**
     * Проверяем что нельзя получить текущую инфу об аккаунте
     * с неверной сессией
     */
    @Test
    public void testGetAccountWithoutLogin() throws Exception {
        // Проверяем что с неверной сессией мы данные не получим
        MvcResult result = utils.get("/api/accounts", "erew")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorsCodes(result, Collections.singletonList("NotLogin"));
    }

    /**
     * Нельзя получить список клиентов с неверной сессией
     */
    @Test
    public void testGetClientsWithoutLogin() throws Exception {
        MvcResult result = utils.get("/api/clients", "werew")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorsCodes(result, Collections.singletonList("NotLogin"));
    }

    /**
     * Получение пустого списка клиентов
     */
    @Test
    public void testGetClientsEmpty() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Получаем пустой список
        utils.get("/api/clients", session)
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    /**
     * Получение списка клиентов
     */
    @Test
    public void testGetClients() throws Exception {

        String session = utils.register(utils.getDefaultAdmin());

        // Теперь зарегаем пару клиентов
        ClientDto client1 = utils.getDefaultClient();
        client1.setLogin("client1");
        utils.register(client1);

        ClientDto client2 = utils.getDefaultClient();
        client2.setLogin("client2");
        utils.register(client2);

        // Получаем уже не пустой список
        MvcResult result = utils.get("/api/clients", session)
                .andExpect(status().isOk())
                .andReturn();

        // Проверяем этот список
        ClientDto client = utils.getDefaultClient();

        JsonNode node = utils.read(result);
        assertEquals(2, node.size());

        for (int i = 0; i < 2; ++i) {
            JsonNode child = node.get(i);

            assertNull(child.get("deposit"));
            assertNull(child.get("login"));
            assertNull(child.get("password"));

            assertEquals("client", child.get("userType").asText());
            assertEquals(client.getFirstName(), child.get("firstName").asText());
            assertEquals(client.getLastName(), child.get("lastName").asText());
            assertEquals(client.getPatronymic(), child.get("patronymic").asText());
            assertEquals(client.getAddress(), child.get("address").asText());
            assertEquals(client.getEmail(), child.get("email").asText());
            assertEquals(client.getPhone(), child.get("phone").asText());
        }
    }

    /**
     * Нельзя изменить инфу об аккаунте c неверной сессией
     */
    @Test
    public void testEditAccountWithoutLogin() throws Exception {
        AdminDto info = new AdminDto();
        info.setFirstName("Денис");
        info.setLastName("Овчаров");
        info.setPatronymic("Владиславович");
        info.setPosition("administrator");
        info.setOldPassword("erewr");
        info.setNewPassword("VadimGush225");

        // Проверяем что редактирование без логина не пройдёт
        MvcResult result = utils.put("/api/admins", "erwe", info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorsCodes(result, Collections.singletonList("NotLogin"));
    }

    /**
     * Нельзя изменить инфу об аккаунте с неверным старым паролем
     * и пустым новым паролем
     */
    @Test
    public void testEditAccountWithWrongPassword() throws Exception {

        String session = utils.register(utils.getDefaultAdmin());

        AdminDto info = new AdminDto();
        info.setFirstName("Денис");
        info.setLastName("Овчаров");
        info.setPatronymic("Владиславович");
        info.setPosition("administrator");
        info.setOldPassword("erewr");
        info.setNewPassword("VadimGush225");

        // Сначала пытаемся изменить инфу с неверным старым паролем
        MvcResult result = utils.put("/api/admins", session, info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Collections.singletonList(Pair.of("WrongPassword", "oldPassword")));

        // Потом пытаемся изменить инфу с пустым новым паролем
        info.setOldPassword("Iddqd225");
        info.setNewPassword("");

        result = utils.put("/api/admins", session, info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Collections.singletonList(Pair.of("Password", "newPassword")));
    }

    /**
     * Редактирование аккаунта
     */
    @Test
    public void testEditAccount() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        AdminDto info = new AdminDto();
        info.setFirstName("Денис");
        info.setLastName("Овчаров");
        info.setPatronymic("Владиславович");
        info.setPosition("administrator");
        info.setOldPassword("Iddqd225");
        info.setNewPassword("VadimGush225");

        MvcResult result = utils.put("/api/admins", session, info)
                .andExpect(status().isOk())
                .andReturn();

        // Теперь проверяем что вернул верные данные
        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(info.getFirstName(), node.get("firstName").asText());
        assertEquals(info.getLastName(), node.get("lastName").asText());
        assertEquals(info.getPatronymic(), node.get("patronymic").asText());
        assertEquals(info.getPosition(), node.get("position").asText());
        assertNull(node.get("login"));
        assertNull(node.get("password"));

    }

    /**
     * Нельзя с помощью редактирование сделать все поля пустыми
     */
    @Test
    public void testFailedEditAccountWithEmptyFields() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        AdminDto admin = new AdminDto();
        admin.setFirstName("");
        admin.setLastName("");
        admin.setPosition("");
        admin.setPatronymic("");
        admin.setOldPassword("Iddqd225");
        admin.setNewPassword("Iddqd225");

        MvcResult result = utils.put("/api/admins", session, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("RequiredName", "position")
        ));

    }

    /**
     * Нельзя изменить поля на сликшом длинные или на короткий пароль
     */
    @Test
    public void testFailedEditAccountWithLongNamesShortPassword() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Пытаемся зарегистрироваться с маленьким паролем
        // и слишком длинными именами
        AdminDto admin = new AdminDto();
        admin.setFirstName("eewrewrjlewkjrewrklwerjew");
        admin.setLastName("eewrewrjlewkjrewrklwerjew");
        admin.setPatronymic("eewrewrjlewkjrewrklwerjew");
        admin.setPosition("eewrewrjlewkjrewrklwerjew");
        admin.setOldPassword("Iddqd225");
        admin.setNewPassword("Id");

        MvcResult result = utils.put("/api/admins", session, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("RequiredName", "position"),
                Pair.of("OptionalRussianName", "patronymic"),
                Pair.of("Password", "newPassword")
        ));

    }

    /**
     * Нельзя изменить поля на неверные форматом
     */
    @Test
    public void testFailedEditAccountWithWrongNameFormat() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Имя не может состоять из английских букв, цифр и знаков препинания
        AdminDto admin = new AdminDto();
        admin.setFirstName("Vadim");
        admin.setLastName("234234");
        admin.setPatronymic("Ar.- ");
        admin.setPosition("programmer");
        admin.setOldPassword("Iddqd225");
        admin.setNewPassword("Iddqd225");

        MvcResult result = utils.put("/api/admins", session, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("OptionalRussianName", "patronymic")
        ));
    }

    /**
     * Нельзя добавить категорию с неверной сессией
     */
    @Test
    public void testAddCategoryWithoutLogin() throws Exception {
        // Добавление категории без логина
        CategoryDto category = new CategoryDto();
        category.setName("apple");

        MvcResult result = utils.post("/api/categories", "rewr", category)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorsCodes(result, Collections.singletonList("NotLogin"));
    }

    /**
     * Нельзя добавить категорию без имени
     */
    @Test
    public void testAddCategoryWithoutName() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Сначала попытаемся добавить категорию без имени
        CategoryDto category = new CategoryDto();
        category.setName(null);
        MvcResult result = utils.post("/api/categories", session, category)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("NotBlank", "name"));
    }

    /**
     * Добавление категории и подкатегории
     */
    @Test
    public void testAddCategory() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Сначала попытаемся добавить категорию без имени
        CategoryDto category = new CategoryDto();
        category.setName("apple");

        MvcResult result = utils.post("/api/categories", session, category)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertTrue(node.get("id").isInt());
        assertEquals(category.getName(), node.get("name").asText());

        long parentId = node.get("id").asLong();

        // Добавляем дочернюю с несуществующим родителем
        category.setName("iphone");
        category.setParentId(-1L);

        result = utils.post("/api/categories", session, category)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("CategoryNotFound", "parentId"));

        // Теперь добавляем дочернюю как надо
        category.setParentId(parentId);

        result = utils.post("/api/categories", session, category)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(category.getName(), node.get("name").asText());
        assertEquals(parentId, node.get("parentId").asLong());
        assertEquals("apple", node.get("parentName").asText());

    }

    /**
     * Нельзя добавить категорию с тем же именем
     */
    @Test
    public void testAddCategoryWithSameName() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());
        registerCategory(session, "apple", null);

        CategoryDto category = new CategoryDto();
        category.setName("apple");
        category.setParentId(null);

        MvcResult result = utils.post("/api/categories", session, category)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("SameCategoryName", "name"));
    }

    /**
     * Нельзя получить информацию о категории с неверной сессией
     */
    @Test
    public void testGetCategoryWithoutLogin() throws Exception {
        MvcResult result = utils.get("/api/categories/1", "ere")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Получение информации о категориях и подкатегориях
     */
    @Test
    public void testGetCategory() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Получение категории, которой нет в БД
        MvcResult result = utils.get("/api/categories/1", session)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "CategoryNotFound");

        // Регаем категорию
        long id = registerCategory(session, "apple", null);

        // Получаем данные о родительской категории
        result = utils.get("/api/categories/" + id, session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertTrue(node.get("id").isInt());
        assertEquals("apple", node.get("name").asText());
        assertNull(node.get("parentId"));
        assertNull(node.get("parentName"));

        // Создаём дочернюю
        long childId = registerCategory(session, "iphone", id);

        // Получаем инфу о дочерней
        result = utils.get("/api/categories/" + childId, session)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        assertNotNull(node.get("id"));
        assertTrue(node.get("id").isInt());
        assertEquals("iphone", node.get("name").asText());
        assertEquals(id, node.get("parentId").asLong());
        assertEquals("apple", node.get("parentName").asText());
    }

    /**
     * Нельзя редактировать категорию с неверной сессией
     */
    @Test
    public void testEditCategoryWithoutLogin() throws Exception {
        CategoryDto info = new CategoryDto();
        info.setName("ikea");

        // Редактирование категории без логина
        MvcResult result = utils.put("/api/categories/1", "ewrwe", info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя редактировать несуществующую категорию
     */
    @Test
    public void testEditCategoryNotFound() throws Exception {

        String session = utils.register(utils.getDefaultAdmin());

        CategoryDto info = new CategoryDto();
        info.setName("ikea");

        // Редактирование категории, котороый нет в БД
        MvcResult result = utils.put("/api/categories/1", session, info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "CategoryNotFound");
    }

    /**
     * Нельзя изменить имя категории на уже занятое
     */
    @Test
    public void testEditCategoryWithSameName() throws Exception {
        // Пытаемся изменить категорию на имя, которое уже занято
        // другой категорией

        String session = utils.register(utils.getDefaultAdmin());

        registerCategory(session, "msi", null);
        long apple = registerCategory(session, "apple", null);

        CategoryDto info = new CategoryDto();
        info.setName("msi");

        MvcResult result = utils.put("/api/categories/" + apple, session, info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("SameCategoryName", "name"));

    }

    /**
     * Проверяем что при редактировании категории оба поля
     * не могут быть пустыми
     */
    @Test
    public void testEditCategoryEmptyField() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());
        long apple = registerCategory(session, "apple", null);

        CategoryDto info = new CategoryDto();
        info.setParentId(null);
        info.setName(null);

        MvcResult result = utils.put("/api/categories/" + apple, session, info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "EditCategoryEmpty");
    }

    /**
     * Редактирование категории
     */
    @Test
    public void testEditCategory() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        CategoryDto info = new CategoryDto();

        // Регистрируем нормальную категорию
        long apple = registerCategory(session, "apple", null);
        long iphone = registerCategory(session, "iphone", apple);
        long msi = registerCategory(session, "msi", null);

        info.setParentId(iphone);

        // Теперь пытаемся сделать категорию подкатегорией (что непозволительно)
        MvcResult result = utils.put("/api/categories/" + apple, session, info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "CategoryToSubcategory");

        // Теперь переместим одну подкатегорию к другому родителю
        info.setParentId(msi);

        result = utils.put("/api/categories/" + iphone, session, info)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(iphone, node.get("id").asLong());
        assertEquals("iphone", node.get("name").asText());
        assertEquals("msi", node.get("parentName").asText());
        assertEquals(msi, node.get("parentId").asLong());

        // Теперь просто изменим имя родительской
        info.setParentId(null);
        info.setName("new");

        result = utils.put("/api/categories/" + apple, session, info)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        assertEquals(apple, node.get("id").asLong());
        assertEquals("new", node.get("name").asText());
        assertNull(node.get("parentName"));
        assertNull(node.get("parentId"));
    }

    /**
     * Проверяем что изменение категории не влияет на товары, которые принадлежат
     * этой самой категории
     * <p>
     * А удалённая категория просто исчезает из списка категорий товара
     */
    @Test
    public void testEditCategoryAndCheckProduct() throws Exception {

        String session = utils.register(utils.getDefaultAdmin());
        long category = registerCategory(session, "category", null);
        long product = registerProduct(session, "product", Collections.singletonList(category));

        // Теперь изменяем информацию о категории

        CategoryDto info = new CategoryDto();
        info.setName("ikea");

        // Редактирование категории без логина
        utils.put("/api/categories/" + category, session, info)
                .andExpect(status().isOk());

        // Проверяем что товар всё так же принадлежит данной категории
        MvcResult result = utils.get("/api/products/" + product, session)
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = utils.read(result);
        assertEquals(category, node.get("categories").get(0).asLong());

        // Удаляем категорию
        utils.delete("/api/categories/" + category, session)
                .andExpect(status().isOk());

        // Проверяем что у товара теперь просто пустой список категорий
        result = utils.get("/api/products/" + product, session)
                .andExpect(status().isOk())
                .andReturn();
        node = utils.read(result);
        assertNull(node.get("categories"));

    }

    /**
     * Нельзя удалить категорию с неверной сессией
     */
    @Test
    public void testDeleteCategoryWithoutLogin() throws Exception {
        // Удаление без логина
        MvcResult result = utils.delete("/api/categories/1", "ewrwe")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя удалить несуществующую категорию
     */
    @Test
    public void testDeleteCategoryNotFound() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Удаление несуществующей
        MvcResult result = utils.delete("/api/categories/1", session)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "CategoryNotFound");
    }

    /**
     * Удаление категории
     */
    @Test
    public void testDeleteCategory() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Создаём категорию и удаляем её сразу
        long category = registerCategory(session, "category", null);

        utils.delete("/api/categories/" + category, session)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));

        // После удаления проверяем что список категорий и вправду пустой
        utils.get("/api/categories", session)
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));

        // Теперь создаём дочернюю и удаляем её
        long parent = registerCategory(session, "apple", null);
        long child = registerCategory(session, "iphone", null);

        utils.delete("/api/categories/" + child, session)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));

        // Получаем список категорий и проверяем что родительская категория на месте
        MvcResult result = utils.get("/api/categories", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(1, node.size());

        // Теперь добавляем подкатегорию и удалим родительскую
        registerCategory(session, "ipod", parent);
        utils.delete("/api/categories/" + parent, session)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));

        // Проверяем что список категорий пустой
        utils.get("/api/categories", session)
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    /**
     * Нельзя получить инфу о категории с неверной сесссией
     */
    @Test
    public void testGetCategoriesWithoutLogin() throws Exception {
        // Получение списка категорий без логина
        MvcResult result = utils.get("/api/categories", "ewrew")
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Получение списка категорий
     */
    @Test
    public void testGetCategories() throws Exception {

        String session = utils.register(utils.getDefaultAdmin());

        /*
        Ожидаем такой порядок:

        Категория    |     Подкатегория
        -------------------------------
        apple
        apple           iphone
        apple           ipod
        msi
        msi             lenovo
         */

        long msi = registerCategory(session, "msi", null);
        long apple = registerCategory(session, "apple", null);
        long lenovo = registerCategory(session, "lenovo", msi);
        long ipod = registerCategory(session, "ipod", apple);
        long iphone = registerCategory(session, "iphone", apple);

        MvcResult result = utils.get("/api/categories", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);

        assertEquals(apple, node.get(0).get("id").asLong());
        assertEquals("apple", node.get(0).get("name").asText());

        assertEquals(iphone, node.get(1).get("id").asLong());
        assertEquals("iphone", node.get(1).get("name").asText());
        assertEquals("apple", node.get(1).get("parentName").asText());
        assertEquals(apple, node.get(1).get("parentId").asLong());

        assertEquals(ipod, node.get(2).get("id").asLong());
        assertEquals("ipod", node.get(2).get("name").asText());
        assertEquals("apple", node.get(2).get("parentName").asText());
        assertEquals(apple, node.get(2).get("parentId").asLong());

        assertEquals(msi, node.get(3).get("id").asLong());
        assertEquals("msi", node.get(3).get("name").asText());

        assertEquals(lenovo, node.get(4).get("id").asLong());
        assertEquals("lenovo", node.get(4).get("name").asText());
        assertEquals("msi", node.get(4).get("parentName").asText());
        assertEquals(msi, node.get(4).get("parentId").asLong());
    }

    /**
     * Нельзя добавить товар с неверной сессией
     */
    @Test
    public void testAddProductWithoutLogin() throws Exception {
        ProductDto product = new ProductDto();
        product.setName("table");
        product.setPrice(1000);

        // Добавление товара без логина
        MvcResult result = utils.post("/api/products", "erwer", product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя добавить товар без полей
     */
    @Test
    public void testAddProductWithoutFields() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Проверяем что нельзя добавить продукт без имени и цены

        ProductDto product = new ProductDto();

        MvcResult result = utils.post("/api/products", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("NotNull", "price"),
                Pair.of("RequiredName", "name")
        ));
    }

    /**
     * Нельзя добавить товар с отрицаительным количеством и ценою
     */
    @Test
    public void testAddProductWithNegativePriceAndCount() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Проверяем что нельзя добавить отрицательную цену
        ProductDto product = new ProductDto();
        product.setPrice(-1);
        product.setCount(-1);

        MvcResult result = utils.post("/api/products", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("DecimalMin", "price"),
                Pair.of("RequiredName", "name"),
                Pair.of("DecimalMin", "count")
        ));
    }

    /**
     * Нельзя добавить товар с категорией, которой не существует
     */
    @Test
    public void testAddProductWithWrongCategory() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Указываем категорию, которая не существует
        ProductDto product = new ProductDto();
        product.setName("table");
        product.setPrice(1000);
        product.setCategories(Collections.singletonList(1L));

        MvcResult result = utils.post("/api/products", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("CategoryNotFound", "categories"));
    }

    /**
     * Добавление товара
     */
    @Test
    public void testAddProduct() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        ProductDto product = new ProductDto();
        // Теперь всё нормально, но категории нет
        product.setName("table");
        product.setPrice(1000);

        // Добавим товар без категори
        // И с количеством по умолчанию ноль
        product.setCategories(null);

        MvcResult result = utils.post("/api/products", session, product)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(product.getName(), node.get("name").asText());
        assertEquals((long) product.getPrice(), node.get("price").asLong());
        assertEquals(0L, node.get("count").asLong());
        assertNull(node.get("categories"));

        // Теперь добавим товар с тем же именем и плюс с категорией
        long category = registerCategory(session, "category", null);
        product.setCategories(Collections.singletonList(category));

        result = utils.post("/api/products", session, product)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(product.getName(), node.get("name").asText());
        assertEquals((long) product.getPrice(), node.get("price").asLong());
        assertEquals(0L, node.get("count").asLong());
        assertEquals(category, node.get("categories").get(0).asLong());
    }

    /**
     * Нельзя редактировать товар с неверной сессией
     */
    @Test
    public void testEditProductWithoutLogin() throws Exception {
        ProductDto product = new ProductDto();

        // Изменение товара без логина
        MvcResult result = utils.put("/api/products/3", "erwe", product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя редактировать несуществующий товар
     */
    @Test
    public void testEditProductNotFound() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Изменение несуществующего товара
        ProductDto product = new ProductDto();

        MvcResult result = utils.put("/api/products/3", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "ProductNotFound");
    }

    /**
     * Нельзя изменть цену и количество товара на отрицательную
     */
    @Test
    public void testEditProductWithNegativeCountAndPrice() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        ProductDto product = new ProductDto();
        product.setPrice(-1);
        product.setCount(-1);

        MvcResult result = utils.put("/api/products/3", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("DecimalMin", "price"),
                Pair.of("DecimalMin", "count")
        ));
    }

    /**
     * Нельзя изменить инфу несуществующего товара
     */
    @Test
    public void testEditProductCategoryNotFound() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Проверим что нельзя изменить с несуществующей категорией
        long product = registerProduct(session, "table", null);

        ProductDto productEdit = new ProductDto();
        productEdit.setCategories(Collections.singletonList(-1L));

        MvcResult result = utils.put("/api/products/" + product, session, productEdit)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("CategoryNotFound", "categories"));
    }

    /**
     * Изменение данных товара
     */
    @Test
    public void testEditProduct() throws Exception {

        String session = utils.register(utils.getDefaultAdmin());
        ProductDto product = new ProductDto();

        // Теперь создадим товар с категорией
        long category = registerCategory(session, "category", null);
        ProductDto newProduct = new ProductDto();
        newProduct.setName("cup");
        newProduct.setPrice(10_000);
        newProduct.setCount(15);
        newProduct.setCategories(Collections.singletonList(category));

        MvcResult result = utils.post("/api/products", session, newProduct)
                .andExpect(status().isOk())
                .andReturn();

        long productId = utils.read(result).get("id").asLong();

        // И изменим несколько его полей
        product.setName("new name");
        product.setCount(20);
        product.setPrice(5_000);

        result = utils.put("/api/products/" + productId, session, product)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(productId, node.get("id").asLong());
        assertEquals("new name", node.get("name").asText());
        assertEquals(20, node.get("count").asInt());
        assertEquals(5_000, node.get("price").asInt());
        assertEquals(1, node.get("categories").size());

        // Теперь заменим список категорий
        category = registerCategory(session, "other category", null);
        product.setName(null);
        product.setCount(null);
        product.setPrice(null);
        product.setCategories(Collections.singletonList(category));

        result = utils.put("/api/products/" + productId, session, product)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        // Проверяем что данные не изменились
        assertEquals(productId, node.get("id").asLong());
        assertEquals("new name", node.get("name").asText());
        assertEquals(20, node.get("count").asInt());
        assertEquals(5_000, node.get("price").asInt());
        // Проверяем новую категорию
        assertEquals(1, node.get("categories").size());
        assertEquals(category, node.get("categories").get(0).asLong());

        // Теперь удаляем категори
        product.setCategories(Collections.emptyList());

        result = utils.put("/api/products/" + productId, session, product)
                .andExpect(status().isOk())
                .andReturn();

        node = utils.read(result);
        assertNull(node.get("categories"));
    }

    /**
     * Нельзя удалить товар с неверной сессией
     */
    @Test
    public void testDeleteProductWithoutLogin() throws Exception {
        // Удаление товара без логина
        MvcResult result = utils.delete("/api/products/3", "erwer")
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя удалить несуществующий товар
     */
    @Test
    public void testDeleteProductNotFound() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Удаляем несуществующий
        MvcResult result = utils.delete("/api/products/3", session)
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertErrorCode(result, "ProductNotFound");
    }

    /**
     * Удаление товара
     */
    @Test
    public void testDeleteProduct() throws Exception {

        String session = utils.register(utils.getDefaultAdmin());

        // Создаём товар
        ProductDto newProduct = new ProductDto();
        newProduct.setName("cup");
        newProduct.setPrice(10_000);
        newProduct.setCount(15);
        MvcResult result = utils.post("/api/products", session, newProduct)
                .andExpect(status().isOk())
                .andReturn();

        long productId = utils.read(result).get("id").asLong();

        // И удалим его
        utils.delete("/api/products/" + productId, session)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    /**
     * Нельзя получить инфу о товара с неверной сессией
     */
    @Test
    public void testGetProductWithoutLogin() throws Exception {
        // Получение данных без логина
        MvcResult result = utils.get("/api/products/3", "ewrwe")
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Нельзя получить инфу о несуществующем товаре
     */
    @Test
    public void testGetProductNotFound() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Получение данных о несуществующем
        MvcResult result = utils.get("/api/products/3", session)
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertErrorCode(result, "ProductNotFound");
    }

    /**
     * Получение инфы о товаре
     */
    @Test
    public void testGetProduct() throws Exception {
        String session = utils.register(utils.getDefaultAdmin());

        // Создаём товар
        long category = registerCategory(session, "category", null);
        ProductDto newProduct = new ProductDto();
        newProduct.setName("cup");
        newProduct.setPrice(10_000);
        newProduct.setCount(15);
        newProduct.setCategories(Collections.singletonList(category));

        MvcResult result = utils.post("/api/products", session, newProduct)
                .andExpect(status().isOk())
                .andReturn();

        long productId = utils.read(result).get("id").asLong();

        // Получаем данные
        result = utils.get("/api/products/" + productId, session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(productId, node.get("id").asLong());
        assertEquals(newProduct.getName(), node.get("name").asText());
        assertEquals((int) newProduct.getCount(), node.get("count").asInt());
        assertEquals((int) newProduct.getPrice(), node.get("price").asInt());
        assertEquals(1, node.get("categories").size());
        assertEquals(category, node.get("categories").get(0).asLong());
    }

    /**
     * Нельзя получить список товаров с неверной сессией
     */
    @Test
    public void testGetProductsWithoutLogin() throws Exception {
        MvcResult result = utils.get("/api/products", "erew")
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertErrorCode(result, "NotLogin");
    }

    /**
     * Получение списка товаров отсортированных по их именам
     */
    @Test
    public void testGetProductsByProductsNames() throws Exception {

        String session = utils.register(utils.getDefaultAdmin());


        // Подгатавливаем список товаров
        long category = registerCategory(session, "category", null);

        long warcraft = registerProduct(session, "warcraft", null);
        long apple = registerProduct(session, "apple", Collections.singletonList(category));
        long berretta = registerProduct(session, "berretta", null);

        // Создадим удалённый товар и проверим что в список он не попадёт
        long deleted = registerProduct(session, "deleted", null);
        utils.delete("/api/products/" + deleted, session)
                .andExpect(status().isOk());

        MvcResult result = utils.get("/api/products", session)
                .andExpect(status().isOk())
                .andReturn();

        String firstResult = utils.getContent(result);
        JsonNode node = utils.read(result);

        assertEquals(3, node.size());

        assertEquals(apple, node.get(0).get("id").asLong());
        assertEquals("apple", node.get(0).get("name").asText());
        assertEquals(10_000, node.get(0).get("price").asInt());
        assertEquals(15, node.get(0).get("count").asInt());
        assertEquals(1, node.get(0).get("categories").size());
        assertEquals(category, node.get(0).get("categories").get(0).asLong());

        assertEquals(berretta, node.get(1).get("id").asLong());
        assertEquals("berretta", node.get(1).get("name").asText());
        assertEquals(10_000, node.get(1).get("price").asInt());
        assertEquals(15, node.get(1).get("count").asInt());

        assertEquals(warcraft, node.get(2).get("id").asLong());
        assertEquals("warcraft", node.get(2).get("name").asText());
        assertEquals(10_000, node.get(2).get("price").asInt());
        assertEquals(15, node.get(2).get("count").asInt());

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
     */
    @Test
    public void testGetProductsByCategories() throws Exception {

        String session = utils.register(utils.getDefaultAdmin());

        // Подгатавливаем список
        long pen = registerProduct(session, "pen", null);
        long array = registerProduct(session, "array", null);

        long bat = registerCategory(session, "bat", null);
        long wat = registerCategory(session, "wat", null);
        long at = registerCategory(session, "at", null);

        registerProduct(session, "xen", Collections.singletonList(at));
        registerProduct(session, "apple", Collections.singletonList(wat));
        long berretta = registerProduct(session, "berretta", Arrays.asList(at, wat));
        registerProduct(session, "warcraft", Collections.singletonList(bat));

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
        assertEquals(15, node.get(0).get("count").asInt());
        assertNull(node.get(0).get("categories"));

        assertEquals(pen, node.get(1).get("id").asLong());
        assertEquals("pen", node.get(1).get("name").asText());
        assertEquals(10_000, node.get(1).get("price").asInt());
        assertEquals(15, node.get(1).get("count").asInt());
        assertNull(node.get(1).get("categories"));

        // Теперь проверяем список с категориями
        // at   -> berretta
        assertEquals(berretta, node.get(2).get("id").asLong());
        assertEquals("berretta", node.get(2).get("name").asText());
        assertEquals(10_000, node.get(2).get("price").asInt());
        assertEquals(15, node.get(2).get("count").asInt());
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

    private long registerProduct(String session, String name, List<Long> categories) throws Exception {
        ProductDto newProduct = new ProductDto();
        newProduct.setName(name);
        newProduct.setPrice(10_000);
        newProduct.setCount(15);
        newProduct.setCategories(categories);
        MvcResult result = utils.post("/api/products", session, newProduct)
                .andExpect(status().isOk()).andReturn();

        return utils.read(result).get("id").asLong();
    }

    private long registerCategory(String session, String name, Long parentId) throws Exception {
        CategoryDto category = new CategoryDto();
        category.setName(name);
        category.setParentId(parentId);

        MvcResult result = utils.post("/api/categories", session, category)
                .andExpect(status().isOk()).andReturn();

        return utils.read(result).get("id").asLong();
    }


}
