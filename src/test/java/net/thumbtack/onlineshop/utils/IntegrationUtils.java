package net.thumbtack.onlineshop.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.thumbtack.onlineshop.dto.AdminDto;
import net.thumbtack.onlineshop.dto.CategoryDto;
import net.thumbtack.onlineshop.dto.ClientDto;
import net.thumbtack.onlineshop.dto.ProductDto;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static junit.framework.TestCase.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Вспомогательный класс с набором удобных методов для быстрого формирования запросов,
 * проверки ошибок и остального.
 */
public class IntegrationUtils {

    private MockMvc mvc;
    private ObjectMapper mapper = new ObjectMapper();

    public IntegrationUtils(MockMvc mvc) {
        this.mvc = mvc;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public ResultActions post(String url, String session, Object content) throws Exception {

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(url);
        addParameters(builder, session, content);
        return mvc.perform(builder);
    }

    public ResultActions get(String url, String session) throws Exception {

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(url);
        addParameters(builder, session, null);
        return mvc.perform(builder);
    }

    public ResultActions put(String url, String session, Object content) throws Exception {

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(url);
        addParameters(builder, session, content);
        return mvc.perform(builder);
    }

    public ResultActions delete(String url, String session) throws Exception {

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(url);
        addParameters(builder, session, null);
        return mvc.perform(builder);
    }

    public JsonNode read(MvcResult result) throws Exception {
        return mapper.readTree(result.getResponse().getContentAsString());
    }

    public String getSession(MvcResult result) {
        return Objects.requireNonNull(result.getResponse().getCookie("JAVASESSIONID")).getValue();
    }

    public String getContent(MvcResult result) throws Exception {
        return result.getResponse().getContentAsString();
    }

    public void assertError(MvcResult result, Pair<String, String> error) throws Exception {
        assertErrors(result, Collections.singletonList(error));
    }

    public void assertErrorCode(MvcResult result, String errorCode) throws Exception {
        assertErrorsCodes(result, Collections.singletonList(errorCode));
    }

    public void assertErrors(MvcResult result, List<Pair<String, String>> errors) throws Exception {

        JsonNode node = read(result);
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
            if (!contains) {
                fail("Error [" + error.getFirst() + " : " + error.getSecond() + "] not found");
            }
        }
    }

    public void assertErrorsCodes(MvcResult result, List<String> errors) throws Exception {

        JsonNode node = read(result);
        JsonNode errorList = node.get("errors");

        for (String error : errors) {
            boolean contains = false;
            for (int i = 0; i < errorList.size(); ++i) {

                if (error.equals(errorList.get(i).get("errorCode").asText())) {
                    contains = true;
                    break;
                }

            }
            if (!contains) {
                fail("Error " + error + " not found");
            }
        }
    }

    private void addParameters(MockHttpServletRequestBuilder builder, String session, Object content) throws Exception {
        if (session != null) {
            builder.cookie(new Cookie("JAVASESSIONID", session));
        }

        builder.contentType(MediaType.APPLICATION_JSON);
        builder.characterEncoding("utf-8");

        if (content != null) {
            builder.content(mapper.writeValueAsBytes(content));
        }
    }

    public String register(AdminDto admin) throws Exception {
        MvcResult result = post("/api/admins", null, admin)
                .andExpect(status().isOk())
                .andReturn();

        return getSession(result);
    }

    public String register(ClientDto client) throws Exception {
        MvcResult result = post("/api/clients", null, client)
                .andExpect(status().isOk())
                .andReturn();

        return getSession(result);
    }

    public long register(String session, ProductDto product) throws Exception {
        MvcResult result = post("/api/products", session, product)
                .andExpect(status().isOk())
                .andReturn();

        return read(result).get("id").asLong();
    }

    public long register(String session, CategoryDto category) throws Exception {
        MvcResult result = post("/api/categories", session, category)
                .andExpect(status().isOk())
                .andReturn();

        return read(result).get("id").asLong();
    }

    public String registerDefaultAdmin() throws Exception {
        return register(getDefaultAdmin());
    }

    public AdminDto getDefaultAdmin() {
        AdminDto admin = new AdminDto();
        admin.setFirstName("Вадим");
        admin.setLastName("Гуш");
        admin.setPatronymic("Вадимович");
        admin.setPosition("Janitor");
        admin.setLogin("Vadim");
        admin.setPassword("Iddqd225");
        return admin;
    }

    public ClientDto getDefaultClient() {
        ClientDto client = new ClientDto();
        client.setFirstName("Денис ");
        client.setLastName("Овчаров-Алекий");
        client.setPatronymic("Сергеевич");
        client.setEmail("vadim.djuke@yandex.ru");
        client.setAddress("something very long");
        client.setPhone("89649951844");
        client.setLogin("DeNis");
        client.setPassword("Denis225");
        return client;
    }

    public ProductDto getProduct(String name, Integer price, List<Long> categories) {
        ProductDto product = new ProductDto();
        product.setName(name);
        product.setPrice(price);
        product.setCategories(categories);
        return product;
    }
}
