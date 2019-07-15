package net.thumbtack.onlineshop;

import com.fasterxml.jackson.databind.JsonNode;
import net.thumbtack.onlineshop.dto.*;
import net.thumbtack.onlineshop.service.ServerControlService;
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
        classes = OnlineShopServerTest.class
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

    @Test
    public void testRegistration() throws Exception {

        // Регистриуем администратора
        AdminDto admin = createAdmin();

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

        // Логин не чувствителен к регистру, поэтому регистрация не пройдёт
        admin = createAdmin();
        admin.setLogin("vadim");

        result = utils.post("/api/admins", null, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Collections.singletonList(
                Pair.of("LoginInUse", "login")
        ));
    }

    /**
     * Проверка, что регистрация не пройдёт с пустыми или неверными полями
     */
    @Test
    public void testFailedRegistration() throws Exception {

        AdminDto admin = createAdmin();
        admin.setFirstName("");
        admin.setLastName("");
        admin.setLogin("");
        admin.setPosition("");
        admin.setPassword("");
        admin.setPatronymic("");

        // Пытаемся зарегаться с пустыми полями
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

        // Пытаемся зарегистрироваться с маленьким паролем
        // и слишком длинными именами
        admin = createAdmin();
        admin.setFirstName("eewrewrjlewkjrewrklwerjew");
        admin.setLastName("eewrewrjlewkjrewrklwerjew");
        admin.setPatronymic("eewrewrjlewkjrewrklwerjew");
        admin.setPosition("eewrewrjlewkjrewrklwerjew");
        admin.setLogin("eewrewrjlewkjrewrklwerjew");

        admin.setPassword("wew");

        result = utils.post("/api/admins", null, admin)
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

        // Имя не может состоять из английских букв, цифр и знаков препинания
        admin = createAdmin();
        admin.setFirstName("Vadim");
        admin.setLastName("234234");
        admin.setPatronymic("Ar.- ");
        admin.setPosition("programmer");
        admin.setLogin("vadim234");
        admin.setPassword("wererewrw");

        result = utils.post("/api/admins", null, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("OptionalRussianName", "patronymic")
        ));

        // Логин не может содержать в себе знаки препинания или пробелы
        admin = createAdmin();
        admin.setLogin("vadim.");

        result = utils.post("/api/admins", null, admin)
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertError(result, Pair.of("Login", "login"));

        admin = createAdmin();
        admin.setLogin("vad im");

        result = utils.post("/api/admins", null, admin)
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertError(result, Pair.of("Login", "login"));
    }

    @Test
    public void testRegistrationWithoutPatronymic() throws Exception {

        // Теперь проверяем что регистрация без отчества пройдёт успешно
        AdminDto admin = createAdmin();
        admin.setPatronymic(null);

        MvcResult result = utils.post("/api/admins", null, admin)
                .andExpect(status().isOk()).andReturn();

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

    @Test
    public void testLoginAndLogout() throws Exception {

        // Регистрируем администратора
        AdminDto admin = createAdmin();

        MvcResult result = utils.post("/api/admins", null, admin)
                .andExpect(status().isOk()).andReturn();

        String session = utils.getSession(result);

        // И выходим из аккунта по прошлой сессии
        utils.delete("/api/sessions", session)
                .andExpect(status().isOk()).andExpect(content().string("{}"));

        // Выход без сессии тоже работает
        utils.delete("/api/sessions", null)
                .andExpect(status().isOk()).andExpect(content().string("{}"));

        // Неверный пароль
        LoginDto login = new LoginDto("vadim", "werew8778");
        result = utils.post("/api/sessions", null, login)
                .andExpect(status().isBadRequest()).andReturn();

        utils.assertErrorsCodes(result, Collections.singletonList("UserNotFound"));

        // Неверный логин
        login = new LoginDto("vadi", "Iddqd225");
        result = utils.post("/api/sessions", null, login)
                .andExpect(status().isBadRequest()).andReturn();

        utils.assertErrorsCodes(result, Collections.singletonList("UserNotFound"));

        // Теперь повторяем логин
        login = new LoginDto("vadIm", "Iddqd225");

        result = utils.post("/api/sessions", null, login)
                .andExpect(status().isOk()).andReturn();

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

        utils.get("/api/clients", session ).andExpect(status().isOk());
    }

    @Test
    public void testGetAccount() throws Exception {

        String session = registerAdmin();
        AdminDto admin = createAdmin();

        MvcResult result = utils.get("/api/accounts", session)
                .andExpect(status().isOk()).andReturn();

        // Проверяем что данные правильные
        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(admin.getFirstName(), node.get("firstName").asText());
        assertEquals(admin.getLastName(), node.get("lastName").asText());
        assertEquals(admin.getPatronymic(), node.get("patronymic").asText());
        assertEquals(admin.getPosition(), node.get("position").asText());
        assertNull(node.get("login"));
        assertNull(node.get("password"));

        // Проверяем что с неверной сессией мы данные не получим
        result = utils.get("/api/accounts", "erew")
                .andExpect(status().isBadRequest()).andReturn();

        utils.assertErrorsCodes(result, Collections.singletonList("NotLogin"));
    }

    @Test
    public void testGetClients() throws Exception {

        // Проверяем что без логина не получится вызвать метод
        MvcResult result = utils.get("/api/clients", "werew")
                .andExpect(status().isBadRequest()).andReturn();

        utils.assertErrorsCodes(result, Collections.singletonList("NotLogin"));

        String session = registerAdmin();

        // Получаем пустой список
        utils.get("/api/clients", session)
                .andExpect(status().isOk()).andExpect(content().string("[]"));

        // Теперь зарегаем пару клиентов
        registerClient("client1");
        registerClient("client2");

        // Получаем уже не пустой список
        result = utils.get("/api/clients", session)
                .andExpect(status().isOk()).andReturn();

        // Проверяем этот список
        ClientDto client = createClient();

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

    @Test
    public void testEditAccount() throws Exception {

        String session = registerAdmin();

        AdminEditDto info = new AdminEditDto();
        info.setFirstName("Денис");
        info.setLastName("Овчаров");
        info.setPatronymic("Владиславович");
        info.setPosition("administrator");
        info.setOldPassword("erewr");
        info.setNewPassword("VadimGush225");

        // Проверяем что редактирование без логина не пройдёт
        MvcResult result = utils.put("/api/admins", "erwe", info)
                .andExpect(status().isBadRequest()).andReturn();
        
        utils.assertErrorsCodes(result, Collections.singletonList("NotLogin"));

        // Сначала пытаемся изменить инфу с неверным старым паролем
        result = utils.put("/api/admins", session, info)
                .andExpect(status().isBadRequest()).andReturn();
        
        utils.assertErrors(result, Collections.singletonList(Pair.of("WrongPassword", "oldPassword")));

        // Потом пытаемся изменить инфу с пустым новым паролем
        info.setOldPassword("Iddqd225");
        info.setNewPassword("");

        result = utils.put("/api/admins", session, info)
                .andExpect(status().isBadRequest()).andReturn();
        
        utils.assertErrors(result, Collections.singletonList(Pair.of("Password", "newPassword")));

        // Теперь наконец-то изменяем нормально
        info.setNewPassword("VadimGush225");

        result = utils.put("/api/admins", session, info)
                .andExpect(status().isOk()).andReturn();

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
     * На редактирование аккаунта те же ограничения, что и на регистрацию
     */
    @Test
    public void testFailedEditAccount() throws Exception {

        String session = registerAdmin();

        AdminEditDto admin = new AdminEditDto();
        admin.setFirstName("");
        admin.setLastName("");
        admin.setPosition("");
        admin.setPatronymic("");
        admin.setOldPassword("Iddqd225");
        admin.setNewPassword("Iddqd225");

        // Пытаемся зарегаться с пустыми полями
        MvcResult result = utils.put("/api/admins", session, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("RequiredName", "position")
        ));

        // Пытаемся зарегистрироваться с маленьким паролем
        // и слишком длинными именами
        admin = new AdminEditDto();
        admin.setFirstName("eewrewrjlewkjrewrklwerjew");
        admin.setLastName("eewrewrjlewkjrewrklwerjew");
        admin.setPatronymic("eewrewrjlewkjrewrklwerjew");
        admin.setPosition("eewrewrjlewkjrewrklwerjew");
        admin.setOldPassword("Iddqd225");
        admin.setNewPassword("Iddqd225");

        result = utils.put("/api/admins", session, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("RequiredName", "position"),
                Pair.of("OptionalRussianName", "patronymic")
        ));

        // Имя не может состоять из английских букв, цифр и знаков препинания
        admin = new AdminEditDto();
        admin.setFirstName("Vadim");
        admin.setLastName("234234");
        admin.setPatronymic("Ar.- ");
        admin.setPosition("programmer");
        admin.setOldPassword("Iddqd225");
        admin.setNewPassword("Iddqd225");

        result = utils.put("/api/admins", session, admin)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("RequiredRussianName", "firstName"),
                Pair.of("RequiredRussianName", "lastName"),
                Pair.of("OptionalRussianName", "patronymic")
        ));

    }

    @Test
    public void testAddCategory() throws Exception {

        String session = registerAdmin();

        // Добавление категории без логина
        CategoryDto category = new CategoryDto();
        category.setName("apple");

        MvcResult result = utils.post("/api/categories", "rewr", category)
                .andExpect(status().isBadRequest()).andReturn();
        
        utils.assertErrorsCodes(result, Collections.singletonList("NotLogin"));

        // Сначала попытаемся добавить категорию без имени
        category.setName(null);
        result = utils.post("/api/categories", session, category)
                .andExpect(status().isBadRequest()).andReturn();

        utils.assertError(result, Pair.of("NotBlank", "name"));

        // Теперь добавим новую категорию нормально
        category.setName("apple");

        result = utils.post("/api/categories", session, category)
                .andExpect(status().isOk()).andReturn();

        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertTrue(node.get("id").isInt());
        assertEquals(category.getName(), node.get("name").asText());

        long parentId = node.get("id").asLong();

        // Добавляем дочернюю с несуществующим родителем
        category.setName("iphone");
        category.setParentId(-1L);

        result = utils.post("/api/categories", session, category)
                .andExpect(status().isBadRequest()).andReturn();

        utils.assertError(result, Pair.of("CategoryNotFound", "parentId"));

        // Теперь добавляем дочернюю как надо
        category.setParentId(parentId);

        result = utils.post("/api/categories", session, category)
                .andExpect(status().isOk()).andReturn();

        node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(category.getName(), node.get("name").asText());
        assertEquals(parentId, node.get("parentId").asLong());
        assertEquals("apple", node.get("parentName").asText());

        // И нельзя добавить с тем же именем
        category.setName("apple");
        category.setParentId(null);

        result = utils.post("/api/categories", session, category)
                .andExpect(status().isBadRequest()).andReturn();

        utils.assertError(result, Pair.of("SameCategoryName", "name"));
    }

    @Test
    public void testGetCategory() throws Exception {

        String session = registerAdmin();

        // Получение категорий без логина
        MvcResult result = utils.get("/api/categories/1", "ere")
                .andExpect(status().isBadRequest()).andReturn();

        utils.assertErrorCode(result, "NotLogin");

        // Получение категории, которой нет в БД
        result = utils.get("/api/categories/1", session)
                .andExpect(status().isBadRequest()).andReturn();

        utils.assertErrorCode(result, "CategoryNotFound");

        // Регаем категорию
        long id = registerCategory(session, "apple", null);

        // Получаем данные о родительской категории
        result = utils.get("/api/categories/" + id, session)
                .andExpect(status().isOk()).andReturn();

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
                .andExpect(status().isOk()).andReturn();

        node = utils.read(result);
        assertNotNull(node.get("id"));
        assertTrue(node.get("id").isInt());
        assertEquals("iphone", node.get("name").asText());
        assertEquals(id, node.get("parentId").asLong());
        assertEquals("apple", node.get("parentName").asText());
    }

    @Test
    public void testEditCategory() throws Exception {

        String session = registerAdmin();

        CategoryEditDto info = new CategoryEditDto();
        info.setName("ikea");

        // Редактирование категории без логина
        MvcResult result = utils.put("/api/categories/1", "ewrwe", info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");

        // Редактирование категории, котороый нет в БД
        result = utils.put("/api/categories/1", session, info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "CategoryNotFound");

        // Регистрируем нормальную категорию
        long apple = registerCategory(session, "apple", null);
        long iphone = registerCategory(session, "iphone", apple);
        long msi = registerCategory(session, "msi", null);

        info.setParentId(iphone);
        // Теперь пытаемся сделать категорию подкатегорией (что непозволительно)

        result = utils.put("/api/categories/" + apple, session, info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "CategoryToSubcategory");

        // Пытаемся изменить имя категории на уже существующее

        info.setName("msi");
        info.setParentId(null);
        result = utils.put("/api/categories/" + apple, session, info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("SameCategoryName", "name"));

        // Теперь делаем оба поля пустыми
        info.setParentId(null);
        info.setName(null);

        result = utils.put("/api/categories/" + apple, session, info)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "EditCategoryEmpty");

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
     *
     * А удалённая категория просто исчезает из списка категорий товара
     */
    @Test
    public void testEditCategoryAndCheckProduct() throws Exception {

        String session = registerAdmin();
        long category = registerCategory(session, "category", null);
        long product = registerProduct(session, "product", Collections.singletonList(category));

        // Теперь изменяем информацию о категории

        CategoryEditDto info = new CategoryEditDto();
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

    @Test
    public void testDeleteCategory() throws Exception {

        String session = registerAdmin();

        // Удаление без логина
        MvcResult result = utils.delete("/api/categories/1", "ewrwe")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");

        // Удаление несуществующей
        result = utils.delete("/api/categories/1", session)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "CategoryNotFound");

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
        result = utils.get("/api/categories", session)
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
     * Получение списка категорий
     */
    @Test
    public void testGetCategories() throws Exception {

        String session = registerAdmin();

        // Получение списка категорий без логина
        MvcResult result = utils.get("/api/categories", "ewrew")
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertErrorCode(result, "NotLogin");

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

        result = utils.get("/api/categories", session)
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
     * Добавление товара
     */
    @Test
    public void testAddProduct() throws Exception {

        String session = registerAdmin();
        ProductDto product = new ProductDto();

        // Проверяем что нельзя добавить продукт без имени и цены
        MvcResult result = utils.post("/api/products", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("NotNull", "price"),
                Pair.of("RequiredName", "name")
        ));

        // Проверяем что нельзя добавить отрицательную цену
        product.setPrice(-1);

        result = utils.post("/api/products", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrors(result, Arrays.asList(
                Pair.of("DecimalMin", "price"),
                Pair.of("RequiredName", "name")
        ));

        // Теперь всё нормально, но категории нет
        product.setName("table");
        product.setPrice(1000);
        product.setCategories(Collections.singletonList(1L));

        result = utils.post("/api/products", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertError(result, Pair.of("CategoryNotFound", "categories"));

        // Добавление товара без логина
        result = utils.post("/api/products", "erwer", product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");

        // Добавим товар без категори
        // И с количеством по умолчанию ноль
        product.setCategories(null);

        result = utils.post("/api/products", session , product)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertNotNull(node.get("id"));
        assertEquals(product.getName(), node.get("name").asText());
        assertEquals((long)product.getPrice(), node.get("price").asLong());
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
        assertEquals((long)product.getPrice(), node.get("price").asLong());
        assertEquals(0L, node.get("count").asLong());
        assertEquals(category, node.get("categories").get(0).asLong());
    }

    /**
     * Изменение данных товара
     */
    @Test
    public void testEditProduct() throws Exception {

        String session = registerAdmin();
        ProductEditDto product = new ProductEditDto();

        // Изменение товара без логина
        MvcResult result = utils.put("/api/products/3", "erwe", product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");

        // Изменение несуществующего товара
        result = utils.put("/api/products/3", session, product)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "ProductNotFound");

        // Теперь создадим товар с категорией
        long category = registerCategory(session, "category", null);
        ProductDto newProduct = new ProductDto();
        newProduct.setName("cup");
        newProduct.setPrice(10_000);
        newProduct.setCount(15);
        newProduct.setCategories(Collections.singletonList(category));

        result = utils.post("/api/products", session, newProduct)
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

        // Проверим что нельзя изменить с несуществующей категорией
        product.setCategories(Collections.singletonList(-1L));
        result = utils.put("/api/products/" + productId, session, product)
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertError(result, Pair.of("CategoryNotFound", "categories"));

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
     * Удаление товара
     */
    @Test
    public void testDeleteProduct() throws Exception {

        String session = registerAdmin();

        // Удаление товара без логина
        MvcResult result = utils.delete("/api/products/3", "erwer")
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertErrorCode(result, "NotLogin");

        // Удаляем несуществующий
        result = utils.delete("/api/products/3", session)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "ProductNotFound");

        // Создаём товар
        ProductDto newProduct = new ProductDto();
        newProduct.setName("cup");
        newProduct.setPrice(10_000);
        newProduct.setCount(15);
        result = utils.post("/api/products", session, newProduct)
                .andExpect(status().isOk())
                .andReturn();

        long productId = utils.read(result).get("id").asLong();

        // И удалим его
        utils.delete("/api/products/" + productId, session)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    /**
     *  Получение информации о товаре
     */
    @Test
    public void testGetProduct() throws Exception {

        String session = registerAdmin();

        // Получение данных без логина
        MvcResult result = utils.get("/api/products/3", "ewrwe")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");

        // Получение данных о несуществующем
        result = utils.get("/api/products/3", session)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "ProductNotFound");

        // Создаём товар
        long category = registerCategory(session, "category", null);
        ProductDto newProduct = new ProductDto();
        newProduct.setName("cup");
        newProduct.setPrice(10_000);
        newProduct.setCount(15);
        newProduct.setCategories(Collections.singletonList(category));

        result = utils.post("/api/products", session, newProduct)
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
        assertEquals((int)newProduct.getCount(), node.get("count").asInt());
        assertEquals((int)newProduct.getPrice(), node.get("price").asInt());
        assertEquals(1, node.get("categories").size());
        assertEquals(category, node.get("categories").get(0).asLong());
    }

    /**
     * Получение списка товаров отсортированных по их именам
     */
    @Test
    public void testGetProductsByProductsNames() throws Exception {

        String session = registerAdmin();

        // Получение списка товаров без логина
        MvcResult result = utils.get("/api/products", "erew")
                .andExpect(status().isBadRequest())
                .andReturn();
        utils.assertErrorCode(result, "NotLogin");

        // Подгатавливаем список товаров
        long category = registerCategory(session, "category", null);

        long warcraft = registerProduct(session, "warcraft", null);
        long apple = registerProduct(session, "apple", Collections.singletonList(category));
        long berretta = registerProduct(session, "berretta", null);

        // Создадим удалённый товар и проверим что в список он не попадёт
        long deleted = registerProduct(session, "deleted", null);
        utils.delete("/api/products/" + deleted, session)
                .andExpect(status().isOk());

        result = utils.get("/api/products", session)
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

        String session = registerAdmin();

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

    private String registerAdmin() throws Exception {
        MvcResult result = utils.post("/api/admins", null, createAdmin())
                .andExpect(status().isOk()).andReturn();
        
        return utils.getSession(result);
    }

    private long registerCategory(String session, String name, Long parentId) throws Exception {
        CategoryDto category = new CategoryDto();
        category.setName(name);
        category.setParentId(parentId);

        MvcResult result = utils.post("/api/categories", session, category)
                .andExpect(status().isOk()).andReturn();

        return utils.read(result).get("id").asLong();
    }

    private void registerClient(String login) throws Exception {
        ClientDto client = createClient();
        client.setLogin(login);

        utils.post("/api/clients", null, client)
                .andExpect(status().isOk());
    }

    private ClientDto createClient() {
        ClientDto client = new ClientDto();
        client.setFirstName("Vadim");
        client.setLastName("Gush");
        client.setPatronymic("Vadimovich");
        client.setAddress("Somewhere");
        client.setEmail("vadim.djuke@yandex.ru");
        client.setPhone("+79649951843");
        client.setLogin("client");
        client.setPassword("VadimGush225");
        return client;
    }

    private AdminDto createAdmin() {
        AdminDto admin = new AdminDto();
        admin.setFirstName("Вадим");
        admin.setLastName("Гуш");
        admin.setPatronymic("Вадимович");
        admin.setPosition("Janitor");
        admin.setLogin("Vadim");
        admin.setPassword("Iddqd225");
        return admin;
    }

}
