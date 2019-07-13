package net.thumbtack.onlineshop.controller;

import net.thumbtack.onlineshop.dto.ServerConfigurationDto;
import net.thumbtack.onlineshop.service.ServerControlService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ServerControllerTest {

    private ServerController controller;

    @Mock
    private ServerControlService mockService;

    @Before
    public void setUpClass() {
        MockitoAnnotations.initMocks(this);

        controller = new ServerController(mockService);

        ReflectionTestUtils.setField(
                controller,
                "maxNameLength",
                10
        );
        ReflectionTestUtils.setField(
                controller,
                "minPasswordLength",
                15
        );
    }

    @Test
    public void testGetServerConfiguration() {

        ServerConfigurationDto result = controller.getServerConfiguration();
        assertEquals(10, result.getMaxNameLength());
        assertEquals(15, result.getMinPasswordLength());

    }

    @Test
    public void testClearDatabase() {

        ReflectionTestUtils.setField(
                controller,
                "debug",
                true
        );
        String result = controller.clearDatabase();

        verify(mockService).clear();
        assertEquals("{}", result);


    }

    @Test
    public void testClearDatabaseProduction() {

        ReflectionTestUtils.setField(
                controller,
                "debug",
                false
        );
        String result = controller.clearDatabase();

        verify(mockService, never()).clear();
        assertEquals("{}", result);

    }
}
