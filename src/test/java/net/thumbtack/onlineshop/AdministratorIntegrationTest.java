package net.thumbtack.onlineshop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.onlineshop.dto.*;
import net.thumbtack.onlineshop.service.ServerControlService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = OnlineShopServer.class)
@AutoConfigureMockMvc
public class AdministratorIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ServerControlService serverControl;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void cleanDatabase() {
        serverControl.clear();
    }

    @Test
    public void testRegistration() throws Exception {

        // Регистриуем администратора
        // Login: VADIM
        AdminDto admin = createAdmin();

        MvcResult result = mvc.perform(
                post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(admin)))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertNotNull(result.getResponse().getCookie("JAVASESSIONID"));

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
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

        mvc.perform(
                post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(admin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegistrationWithoutPatronymic() throws Exception {

        // Теперь проверяем что регистрация без отчества пройдёт успешно
        AdminDto admin = createAdmin();
        admin.setPatronymic(null);

        MvcResult result = mvc.perform(
                post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(admin)))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());
        assertNotNull(result.getResponse().getCookie("JAVASESSIONID"));

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
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

        MvcResult result = mvc.perform(
                post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(admin)))
                .andExpect(status().isOk()).andReturn();

        String session = result.getResponse().getCookie("JAVASESSIONID").getValue();

        // И выходим из аккунта по прошлой сессии
        mvc.perform(
                delete("/api/sessions")
                        .cookie(new Cookie("JAVASESSIONID", session)
        )).andExpect(status().isOk()).andExpect(content().string("{}"));

        // Теперь повторяем логин
        LoginDto login = new LoginDto("vadIm", "Iddqd225");

        result = mvc.perform(
                post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login))
        ).andExpect(status().isOk()).andReturn();

        // Проверяем что login вернул информацию об аккаунте
        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node.get("id"));
        assertEquals(admin.getFirstName(), node.get("firstName").asText());
        assertEquals(admin.getLastName(), node.get("lastName").asText());
        assertEquals(admin.getPatronymic(), node.get("patronymic").asText());
        assertEquals(admin.getPosition(), node.get("position").asText());
        assertNull(node.get("login"));
        assertNull(node.get("password"));

        // С помощью полученной сессии мы должны успешно выполнить какой-нибудь запрос
        session = result.getResponse().getCookie("JAVASESSIONID").getValue();

        mvc.perform(
                get("/api/clients")
                        .cookie(new Cookie("JAVASESSIONID", session))
        ).andExpect(status().isOk());
    }

    @Test
    public void testGetAccount() throws Exception {

        String session = registerAdmin();
        AdminDto admin = createAdmin();

        MvcResult result = mvc.perform(
                get("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isOk()).andReturn();

        // Проверяем что данные правильные
        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node.get("id"));
        assertEquals(admin.getFirstName(), node.get("firstName").asText());
        assertEquals(admin.getLastName(), node.get("lastName").asText());
        assertEquals(admin.getPatronymic(), node.get("patronymic").asText());
        assertEquals(admin.getPosition(), node.get("position").asText());
        assertNull(node.get("login"));
        assertNull(node.get("password"));

    }

    @Test
    public void testGetClients() throws Exception {

        String session = registerAdmin();

        // Получаем пустой список
        mvc.perform(
                get("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isOk()).andExpect(content().string("[]"));

        // Теперь зарегаем пару клиентов
        registerClient("client1");
        registerClient("client2");

        // Получаем уже не пустой список
        MvcResult result = mvc.perform(
                get("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isOk()).andReturn();

        // Проверяем этот список
        ClientDto client = createClient();

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
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
        info.setFirstName("Denis");
        info.setLastName("Ovcharov");
        info.setPatronymic("Vladimirovich");
        info.setPosition("administrator");
        info.setOldPassword("erewr");
        info.setNewPassword("VadimGush225");

        // Сначала пытаемся изменить инфу с неверным старым паролем
        mvc.perform(
                put("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(info)))
                .andExpect(status().isBadRequest());

        // Потом пытаемся изменить инфу с пустым новым паролем
        info.setOldPassword("Iddqd225");
        info.setNewPassword("");

        mvc.perform(
                put("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(info)))
                .andExpect(status().isBadRequest());

        // Теперь наконец-то изменяем нормально
        info.setNewPassword("VadimGush225");

        MvcResult result = mvc.perform(
                put("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(info)))
                .andExpect(status().isOk()).andReturn();

        // Теперь проверяем что вернул верные данные
        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node.get("id"));
        assertEquals(info.getFirstName(), node.get("firstName").asText());
        assertEquals(info.getLastName(), node.get("lastName").asText());
        assertEquals(info.getPatronymic(), node.get("patronymic").asText());
        assertEquals(info.getPosition(), node.get("position").asText());
        assertNull(node.get("login"));
        assertNull(node.get("password"));

    }

    @Test
    public void testAddCategory() throws Exception {

        String session = registerAdmin();

        // Сначала попытаемся добавить категорию без имени
        CategoryDto category = new CategoryDto();

        mvc.perform(
                post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest());

        // Теперь добавим новую категорию нормально
        category.setName("apple");

        MvcResult result = mvc.perform(
                post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(category)))
                .andExpect(status().isOk()).andReturn();

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node.get("id"));
        assertTrue(node.get("id").isInt());
        assertEquals(category.getName(), node.get("name").asText());

        long parentId = node.get("id").asLong();

        // Добавляем дочернюю с несуществующим родителем
        category.setName("iphone");
        category.setParentId(-1L);

        mvc.perform(
                post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest());

        // Теперь добавляем дочернюю как надо
        category.setParentId(parentId);

        result = mvc.perform(
                post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(category)))
                .andExpect(status().isOk()).andReturn();

        node = mapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node.get("id"));
        assertEquals(category.getName(), node.get("name").asText());
        assertEquals(parentId, node.get("parentId").asLong());
        assertEquals("apple", node.get("parentName").asText());

        // И нельзя добавить с тем же именем
        category.setName("apple");
        category.setParentId(null);

        mvc.perform(
                post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetCategory() throws Exception {

        String session = registerAdmin();

        // Получение категории, которой нет в БД
        mvc.perform(
                get("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isBadRequest());

        // Регаем категорию
        long id = registerCategory(session, "apple", null);

        // Получаем данные о родительской категории
        MvcResult result = mvc.perform(
                get("/api/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
        ).andExpect(status().isOk()).andReturn();

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node.get("id"));
        assertTrue(node.get("id").isInt());
        assertEquals("apple", node.get("name").asText());
        assertNull(node.get("parentId"));
        assertNull(node.get("parentName"));

        // Создаём дочернюю
        long childId = registerCategory(session, "iphone", id);

        // Получаем инфу о дочерней
        result = mvc.perform(
                get("/api/categories/" + childId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
        ).andExpect(status().isOk()).andReturn();

        node = mapper.readTree(result.getResponse().getContentAsString());
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

        // Редактирование категории, котороый нет в БД
        mvc.perform(
                put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(info)))
                .andExpect(status().isBadRequest());

        // Регистрируем нормальную категорию
        long apple = registerCategory(session, "apple", null);
        long iphone = registerCategory(session, "iphone", apple);
        long msi = registerCategory(session, "msi", null);

        info.setParentId(iphone);
        // Теперь пытаемся сделать категорию подкатегорией (что непозволительно)
        mvc.perform(
                put("/api/categories/" + apple)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(info)))
                .andExpect(status().isBadRequest());

        // Теперь делаем оба поля пустыми
        info.setParentId(null);
        info.setName(null);

        mvc.perform(
                put("/api/categories/" + apple)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(info)))
                .andExpect(status().isBadRequest());

        // Теперь переместим одну подкатегорию к другому родителю
        info.setParentId(msi);

        MvcResult result = mvc.perform(
                put("/api/categories/" + iphone)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(info)))
                .andExpect(status().isOk()).andReturn();

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(iphone, node.get("id").asLong());
        assertEquals("iphone", node.get("name").asText());
        assertEquals("msi", node.get("parentName").asText());
        assertEquals(msi, node.get("parentId").asLong());

        // Теперь просто изменим имя родительской
        info.setParentId(null);
        info.setName("new");

        result = mvc.perform(
                put("/api/categories/" + apple)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(info)))
                .andExpect(status().isOk()).andReturn();

        node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(apple, node.get("id").asLong());
        assertEquals("new", node.get("name").asText());
        assertNull(node.get("parentName"));
        assertNull(node.get("parentId"));

    }

    @Test
    public void testDeleteCategory() throws Exception {

        String session = registerAdmin();

        // Удаление несуществующей
        mvc.perform(
                delete("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isBadRequest());

        // Создаём категорию и удаляем её сразу
        long category = registerCategory(session, "category", null);

        mvc.perform(
                delete("/api/categories/" + category)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isOk()).andExpect(content().string("{}"));

        // После удаления проверяем что список категорий и вправду пустой
        mvc.perform(
                get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isOk()).andExpect(content().string("[]"));

        // Теперь создаём дочернюю и удаляем её
        long parent = registerCategory(session, "apple", null);
        long child = registerCategory(session, "iphone", null);

        mvc.perform(
                delete("/api/categories/" + child)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isOk()).andExpect(content().string("{}"));

        // Получаем список категорий и проверяем что родительская категория на месте
        MvcResult result = mvc.perform(
                get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isOk()).andReturn();

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(1, node.size());

        // Теперь добавляем подкатегорию и удалим родительскую
        registerCategory(session, "ipod", parent);
        mvc.perform(
                delete("/api/categories/" + parent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isOk()).andExpect(content().string("{}"));

        // Проверяем что список категорий пустой
        mvc.perform(
                get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isOk()).andExpect(content().string("[]"));
    }

    @Test
    public void testGetCategories() throws Exception {

        String session = registerAdmin();
        /*
        Ожидаем такой порядок:
        apple
        apple - iphone
        apple - ipod
        msi
        msi - lenovo
         */

        long msi = registerCategory(session, "msi", null);
        long apple = registerCategory(session, "apple", null);
        long lenovo = registerCategory(session, "lenovo", msi);
        long ipod = registerCategory(session, "ipod", apple);
        long iphone = registerCategory(session, "iphone", apple);

        MvcResult result = mvc.perform(
                get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session)))
                .andExpect(status().isOk()).andReturn();

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());

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

    @Test
    public void testAddProduct() throws Exception {

        String session = registerAdmin();

        ProductDto product = new ProductDto();

        // Проверяем что нельзя добавить продукт без имени и цены

        MvcResult result = mvc.perform(
                post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest()).andReturn();

        assertErrors(result, Arrays.asList(
                Pair.of("NotNull", "price"),
                Pair.of("RequiredName", "name")
        ));

        // Проверяем что нельзя добавить отрицательную цену
        product.setPrice(-1);

        result = mvc.perform(
                post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest()).andReturn();

        assertErrors(result, Arrays.asList(
                Pair.of("DecimalMin", "price"),
                Pair.of("RequiredName", "name")
        ));

        // Теперь всё нормально, но категории нет
        product.setName("table");
        product.setPrice(1000);
        product.setCategories(Arrays.asList(1L));

        result = mvc.perform(
                post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest()).andReturn();

        assertErrors(result, Arrays.asList(
                Pair.of("CategoryNotFound", "categories")
        ));

        // Добавим товар без категори
        // И с количеством по умолчанию ноль
        product.setCategories(null);

        result = mvc.perform(
                post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(product)))
                .andExpect(status().isOk()).andReturn();

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node.get("id"));
        assertEquals(product.getName(), node.get("name").asText());
        assertEquals((long)product.getPrice(), node.get("price").asLong());
        assertEquals(0L, node.get("count").asLong());
        assertNull(node.get("categories"));

        // Теперь добавим товар с тем же именем и плюс с категорией
        long category = registerCategory(session, "category", null);
        product.setCategories(Arrays.asList(category));

        result = mvc.perform(
                post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(product)))
                .andExpect(status().isOk()).andReturn();

        node = mapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(node.get("id"));
        assertEquals(product.getName(), node.get("name").asText());
        assertEquals((long)product.getPrice(), node.get("price").asLong());
        assertEquals(0L, node.get("count").asLong());
        assertEquals(category, node.get("categories").get(0).asLong());
    }

    @Test
    public void testEditProduct() throws Exception {

        String session = registerAdmin();

        // Изменение несуществующего товара
        ProductEditDto product = new ProductEditDto();

        MvcResult result = mvc.perform(
                put("/api/products/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest()).andReturn();

        assertErrorsCodes(result, Arrays.asList(
               "ProductNotFound"
        ));

        // Теперь создадим товар с категорией
        long category = registerCategory(session, "category", null);
        ProductDto newProduct = new ProductDto();
        newProduct.setName("cup");
        newProduct.setPrice(10_000);
        newProduct.setCount(15);
        newProduct.setCategories(Arrays.asList(category));
        result = mvc.perform(
                post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk()).andReturn();

        long productId = mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        // И изменим несколько его полей
        product.setName("new name");
        product.setCount(20);
        product.setPrice(5_000);

        result = mvc.perform(
                put("/api/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest()).andReturn();

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        assertEquals(productId, node.get("id").asLong());
        assertEquals("new name", node.get("name").asText());
        assertEquals(20, node.get("count").asInt());
        assertEquals(5_000, node.get("price").asInt());
        assertEquals(1, node.get("categories").size());

    }

    /**
     * Проверяет JSON на содержание ошибок
     * Пара: errorCode - field
     */
    private void assertErrors(MvcResult result, List<Pair<String, String>> errors) throws Exception {

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        JsonNode errorList = node.get("errors");

        for (Pair<String, String> error : errors) {
            boolean contains = false;
            for (int i = 0; i < errorList.size(); ++i) {

                if (error.getFirst().equals(errorList.get(i).get("errorCode").asText())
                        && error.getSecond().equals(errorList.get(i).get("field").asText())) {
                    contains = true;
                    break;
                }

            }
            if (!contains)
                fail();
        }
    }

    /**
     * Проверяет JSON на содержание ошибок
     * Пара: errorCode - field
     */
    private void assertErrorsCodes(MvcResult result, List<String> errors) throws Exception {

        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        JsonNode errorList = node.get("errors");

        for (String error : errors) {
            boolean contains = false;
            for (int i = 0; i < errorList.size(); ++i) {

                if (error.equals(errorList.get(i).get("errorCode").asText())) {
                    contains = true;
                    break;
                }

            }
            if (!contains)
                fail();
        }
    }

    private String registerAdmin() throws Exception {
        MvcResult result = mvc.perform(
                post("/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createAdmin())))
                .andExpect(status().isOk()).andReturn();
        return result.getResponse().getCookie("JAVASESSIONID").getValue();
    }

    private long registerCategory(String session, String name, Long parentId) throws Exception {
        CategoryDto category = new CategoryDto();
        category.setName(name);
        category.setParentId(parentId);

        MvcResult result = mvc.perform(
                post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("JAVASESSIONID", session))
                        .content(mapper.writeValueAsString(category)))
                .andExpect(status().isOk()).andReturn();

        return mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private void registerClient(String login) throws Exception {
        ClientDto client = createClient();
        client.setLogin(login);

        mvc.perform(
                post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(client)))
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
        admin.setFirstName("Vadim");
        admin.setLastName("Gush");
        admin.setPatronymic("Vadimovich");
        admin.setPosition("Janitor");
        admin.setLogin("Vadim");
        admin.setPassword("Iddqd225");
        return admin;
    }

}
