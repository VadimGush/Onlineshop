package net.thumbtack.onlineshop;

import com.fasterxml.jackson.databind.JsonNode;
import net.thumbtack.onlineshop.dto.AccountDto;
import net.thumbtack.onlineshop.dto.ClientDto;
import net.thumbtack.onlineshop.dto.DepositDto;
import net.thumbtack.onlineshop.dto.ProductDto;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = OnlineShopServer.class
)
@TestPropertySource("classpath:config-test.properties")
@AutoConfigureMockMvc
public class PurchasesIntegrationTest {

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
     * Получение историии покупок сгруппированных по товарам
     */
    @Test
    public void getPurchasesSortedByProducts() throws Exception {

        // Скупаем товары
        String session = utils.registerDefaultAdmin();
        Pair<List<AccountDto>, List<ProductDto>> data = buyProducts(session);
        List<AccountDto> clients = data.getFirst();
        List<ProductDto> products = data.getSecond();

        MvcResult result = utils.get("/api/purchases?target=product", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(10, node.get("totalCount").asInt());
        assertEquals(62_000, node.get("totalAmount").asInt());

        assertEquals(4, node.get("purchases").size());
        assertPurchase(clients.get(1), products.get(0), node.get("purchases").get(0));
        assertPurchase(clients.get(1), products.get(0), node.get("purchases").get(1));
        assertPurchase(clients.get(0), products.get(1), node.get("purchases").get(2));
        assertPurchase(clients.get(1), products.get(1), node.get("purchases").get(3));
    }

    /**
     * Получение истории покупок сгруппированных по клиентам
     */
    @Test
    public void getPurchasesSortedByClients() throws Exception {

        // Скупаем товары
        String session = utils.registerDefaultAdmin();
        Pair<List<AccountDto>, List<ProductDto>> data = buyProducts(session);
        List<AccountDto> clients = data.getFirst();
        List<ProductDto> products = data.getSecond();

        MvcResult result = utils.get("/api/purchases?target=client", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(10, node.get("totalCount").asInt());
        assertEquals(62_000, node.get("totalAmount").asInt());

        assertEquals(4, node.get("purchases").size());
        assertPurchase(clients.get(0), products.get(1), node.get("purchases").get(0));
        assertPurchase(clients.get(1), products.get(0), node.get("purchases").get(1));
        assertPurchase(clients.get(1), products.get(0), node.get("purchases").get(2));
        assertPurchase(clients.get(1), products.get(1), node.get("purchases").get(3));
    }

    /**
     * Проверяем историю покупок сгруппированых по товарам с ограничениями по выдаче
     */
    @Test
    public void getPurchasesSortedByProductsWithLimits() throws Exception {

        // Скупаем товары
        String session = utils.registerDefaultAdmin();
        Pair<List<AccountDto>, List<ProductDto>> data = buyProducts(session);
        List<AccountDto> clients = data.getFirst();
        List<ProductDto> products = data.getSecond();

        MvcResult result = utils.get("/api/purchases?target=product&offset=1&limit=2", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(5, node.get("totalCount").asInt());
        assertEquals(31_000, node.get("totalAmount").asInt());

        assertEquals(2, node.get("purchases").size());
        assertPurchase(clients.get(1), products.get(0), node.get("purchases").get(0));
        assertPurchase(clients.get(0), products.get(1), node.get("purchases").get(1));
    }

    /**
     * Проверяем историю покупок сгруппированных по клиентам с ограничениями по выдаче
     */
    @Test
    public void getPurchasesSortedByClientsWithLimits() throws Exception {

        // Скупаем товары
        String session = utils.registerDefaultAdmin();
        Pair<List<AccountDto>, List<ProductDto>> data = buyProducts(session);
        List<AccountDto> clients = data.getFirst();
        List<ProductDto> products = data.getSecond();

        MvcResult result = utils.get("/api/purchases?target=client&offset=1&limit=2", session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(4, node.get("totalCount").asInt());
        assertEquals(20_000, node.get("totalAmount").asInt());

        assertEquals(2, node.get("purchases").size());
        assertPurchase(clients.get(1), products.get(0), node.get("purchases").get(0));
        assertPurchase(clients.get(1), products.get(0), node.get("purchases").get(1));
    }


    /**
     * Получаем историю покупок для одного клиента
     */
    @Test
    public void getClientPurchases() throws Exception {

        // Скупаем товары
        String session = utils.registerDefaultAdmin();
        Pair<List<AccountDto>, List<ProductDto>> data = buyProducts(session);
        List<AccountDto> clients = data.getFirst();
        List<ProductDto> products = data.getSecond();

        // Получаем историю покупок для второго клиента
        MvcResult result = utils.get("/api/purchases?target=client&id=" + clients.get(1).getId(), session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(7, node.get("totalCount").asInt());
        assertEquals(41_000, node.get("totalAmount").asInt());

        assertEquals(3, node.get("purchases").size());
        assertPurchase(clients.get(1), products.get(0), node.get("purchases").get(0));
        assertPurchase(clients.get(1), products.get(0), node.get("purchases").get(1));
        assertPurchase(clients.get(1), products.get(1), node.get("purchases").get(2));
    }

    /**
     * Проверяем историю покупок для одного клиентиа с ограничениями по выдаче
     */
    @Test
    public void getClientPurchasesWithLimits() throws Exception {
        // Скупаем товары
        String session = utils.registerDefaultAdmin();
        Pair<List<AccountDto>, List<ProductDto>> data = buyProducts(session);
        List<AccountDto> clients = data.getFirst();
        List<ProductDto> products = data.getSecond();

        // Получаем историю покупок для второго клиента
        MvcResult result = utils.get(
                "/api/purchases?target=client&offset=1&limit=1&id=" + clients.get(1).getId()
                , session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(2, node.get("totalCount").asInt());
        assertEquals(10_000, node.get("totalAmount").asInt());

        assertEquals(1, node.get("purchases").size());
        assertPurchase(clients.get(1), products.get(0), node.get("purchases").get(0));
    }

    /**
     * Получаем историюп покупок для одного товара
     */
    @Test
    public void getProductPurchases() throws Exception {

        // Скупаем товары
        String session = utils.registerDefaultAdmin();
        Pair<List<AccountDto>, List<ProductDto>> data = buyProducts(session);
        List<AccountDto> clients = data.getFirst();
        List<ProductDto> products = data.getSecond();

        // Получаем историю покупок для второго товара
        MvcResult result = utils.get("/api/purchases?target=product&id=" + products.get(1).getId(), session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(6, node.get("totalCount").asInt());
        assertEquals(42_000, node.get("totalAmount").asInt());

        assertEquals(2, node.get("purchases").size());
        assertPurchase(clients.get(0), products.get(1), node.get("purchases").get(0));
        assertPurchase(clients.get(1), products.get(1), node.get("purchases").get(1));

    }

    /**
     * Получаем историю покупок одного товара с ограничениями по выдаче
     */
    @Test
    public void getProductPurchasesWithLimits() throws Exception {

        // Скупаем товары
        String session = utils.registerDefaultAdmin();
        Pair<List<AccountDto>, List<ProductDto>> data = buyProducts(session);
        List<AccountDto> clients = data.getFirst();
        List<ProductDto> products = data.getSecond();

        // Получаем историю покупок для второго товара
        MvcResult result = utils.get(
                "/api/purchases?target=product&offset=1&limit=1&id=" + products.get(1).getId()
                , session)
                .andExpect(status().isOk())
                .andReturn();

        JsonNode node = utils.read(result);
        assertEquals(3, node.get("totalCount").asInt());
        assertEquals(21_000, node.get("totalAmount").asInt());

        assertEquals(1, node.get("purchases").size());
        assertPurchase(clients.get(1), products.get(1), node.get("purchases").get(0));
    }

    /**
     * Нельзя получить историю покупок несуществующего товара
     */
    @Test
    public void getProductPurchasesProductNotFound() throws Exception {
        String session = utils.registerDefaultAdmin();

        // Получаем историю покупок для второго товара
        MvcResult result = utils.get("/api/purchases?target=product&id=" + 0L, session)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "ProductNotFound");
    }

    /**
     * Нельзя получить историю покупок для несуществующего клиента
     */
    @Test
    public void getClientPurchasesClientNotFound() throws Exception {
        String session = utils.registerDefaultAdmin();

        // Получаем историю покупок для второго товара
        MvcResult result = utils.get("/api/purchases?target=client&id=" + 0L, session)
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "UserNotFound");
    }

    /**
     * Проверяет что запись о покупке в JSON соответсвует переданному покупателю
     * и товару
     * @param client покупатель
     * @param product товар
     * @param purchase запись о покупке в JSON
     */
    private void assertPurchase(AccountDto client, ProductDto product, JsonNode purchase) {
        assertEquals((long)client.getId(), purchase.get("clientId").asLong());
        assertEquals(client.getFullName(), purchase.get("clientFullName").asText());

        assertEquals((long)product.getId(), purchase.get("productId").asLong());
        assertEquals(product.getName(), purchase.get("productName").asText());

        assertNotNull(purchase.get("date"));
        assertEquals((int)product.getPrice(), purchase.get("price").asInt());
        assertEquals((int)product.getCount(), purchase.get("count").asInt());
        assertEquals(
                product.getCount() * product.getPrice(),
                purchase.get("totalAmount").asInt()
        );
    }

    /**
     * От имени нескольких пользователей начинаем выкупать товары
     */
    private Pair<List<AccountDto>, List<ProductDto>> buyProducts(String adminSession) throws Exception {

        // Добавили двух клиентов
        String client1 = utils.register(utils.getDefaultClient());
        ClientDto temp = utils.getDefaultClient();
        temp.setLogin("vadim2");
        String client2 = utils.register(temp);

        // И не забудем добавить им деньги на счёт
        utils.put("/api/deposits", client1, new DepositDto(1_000_000))
                .andExpect(status().isOk());

        utils.put("/api/deposits", client2, new DepositDto(1_000_000))
                .andExpect(status().isOk());

        // Добавили два товара
        ProductDto product1 = new ProductDto("iphone", 5_000, 100);
        product1.setId(utils.register(adminSession, product1));

        ProductDto product2 = new ProductDto("samsung", 7_000, 100);
        product2.setId(utils.register(adminSession, product2));

        // Первый будем выкупать только в количестве двух товаров
        product1.setCount(2);
        // Второй будем выкупать только в количестве трёх товаров
        product2.setCount(3);

        utils.post("/api/purchases", client2, product1)
                .andExpect(status().isOk());

        utils.post("/api/purchases", client1, product2)
                .andExpect(status().isOk());

        utils.post("/api/purchases", client2, product1)
                .andExpect(status().isOk());

        utils.post("/api/purchases", client2, product2)
                .andExpect(status().isOk());

        // Вернём информацию о двух клиентах
        List<AccountDto> clients = new ArrayList<>();
        MvcResult result = utils.get("/api/accounts", client1)
                .andExpect(status().isOk())
                .andReturn();
        clients.add(utils.getMapper().readValue(result.getResponse().getContentAsString(), AccountDto.class));

        result = utils.get("/api/accounts", client2)
                .andExpect(status().isOk())
                .andReturn();
        clients.add(utils.getMapper().readValue(result.getResponse().getContentAsString(), AccountDto.class));

        return Pair.of(clients, Arrays.asList(product1, product2));
    }


}
