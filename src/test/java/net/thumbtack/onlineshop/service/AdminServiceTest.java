package net.thumbtack.onlineshop.service;

import org.junit.Test;

public class AdminServiceTest {

    /*

    @Mock
    private AdminService adminService;

    @Before
    public void setUpClass() {
        MockitoAnnotation.initMocks(true);
        underTest = new PersonController(this.personService)
    }

    @Test
    public void testGetAllPersons() {
        List<Person> persons = Arrays.asList(
            new ...
            new ..
        );
        when() ...
    }

     */

    
    @Test
    public void testRegistration() {
        // Регистрируем администратора

        // Проверяем что администратора с тем же логином создать нельзя

        // Провряем что можно без отчества
    }


    @Test
    public void testLogin() {
        // Регистрируем администратора

        // Выходим из его сессии

        // Проверяем что запрос на получение клентов не работает

        // Логинимся

        // Проверяем что запрос на получение клиентов работает
    }

    @Test
    public void testGetAllClients() {
        // Регистрируем несколько пользователей

        // Регистрируем одного администратора

        // Получаем клиентов и проверяем списки
    }

    @Test
    public void testAccountInfo() {
        // Регистрируем администратора

        // Получаем информацию об администраторе
    }

    @Test
    public void testWrongSession() {
        // Проверяем что не один запрос из AdminService не выполнится с
        // Неверным session
    }

}
