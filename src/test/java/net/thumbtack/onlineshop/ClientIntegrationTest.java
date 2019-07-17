package net.thumbtack.onlineshop;

import com.fasterxml.jackson.databind.JsonNode;
import net.thumbtack.onlineshop.dto.ClientDto;
import net.thumbtack.onlineshop.dto.DepositDto;
import net.thumbtack.onlineshop.dto.LoginDto;
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

    @Test
    public void testRegisterClient() throws Exception {
        ClientDto client = createClient();

        MvcResult result = utils.post("/api/clients", null, client)
                .andExpect(status().isOk()).andReturn();

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

    @Test
    public void testLoginAndLogout() throws Exception {

        // Этот самый клиент, который будет зареган через registerClient()
        ClientDto client = createClient();
        // Региструем пользователя под логином DeNis
        String session = registerClient();

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

    @Test
    public void testLoginWithWrongPassword() throws Exception {

        registerClient();

        MvcResult result = utils.post("/api/sessions", null, new LoginDto(
                "denis", "erew2342"))
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "UserNotFound");
    }

    @Test
    public void testLoginWithWrongLogin() throws Exception {

        MvcResult result = utils.post("/api/sessions", null, new LoginDto(
                "ewrw1", "wer2343242"))
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "UserNotFound");
    }

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

    @Test
    public void testLogoutWithoutSession() throws Exception {

        // Выход без сессии тоже работает
        utils.delete("/api/sessions", null)
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    @Test
    public void testGetAccountWithoutLogin() throws Exception {
        // Проверяем что с неверной сессией мы данные не получим
        MvcResult result = utils.get("/api/accounts", "erew")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    @Test
    public void testGetDepositWithoutLogin() throws Exception {
        MvcResult result = utils.get("/api/deposits", "erew")
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }

    @Test
    public void testPutDepositWithoutLogin() throws Exception {
        MvcResult result = utils.put("/api/deposits", "rwer", new DepositDto(34))
                .andExpect(status().isBadRequest())
                .andReturn();

        utils.assertErrorCode(result, "NotLogin");
    }


    private ClientDto createClient() {
        ClientDto client = new ClientDto();
        client.setFirstName("Денис");
        client.setLastName("Овчаров");
        client.setPatronymic("Сергеевич");
        client.setEmail("user@gmail.com");
        client.setAddress("something very long");
        client.setPhone("+79649951844");
        client.setLogin("DeNis");
        client.setPassword("Denis225");
        return client;
    }

    private String registerClient() throws Exception {
        ClientDto client = createClient();

        utils.post("/api/clients", null, client)
                .andExpect(status().isOk());

        MvcResult result = utils.post("/api/sessions", null, new LoginDto(
                client.getLogin(),
                client.getPassword()
        )).andExpect(status().isOk()).andReturn();

        return utils.getSession(result);
    }

}
