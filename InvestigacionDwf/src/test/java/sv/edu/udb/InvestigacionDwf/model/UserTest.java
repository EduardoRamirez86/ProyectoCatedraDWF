package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructorAndGetters() {
        // Crear objeto Role (necesario para el constructor)
        Role role = new Role();
        role.setIdRol(1L);
        role.setName("CLIENTE");

        // Fecha de nacimiento de ejemplo
        LocalDate fechaNac = LocalDate.of(1990, 5, 15);

        // Construir usuario con constructor
        User user = User.builder()
                .idUser(1L)
                .role(role)
                .primerNombre("Juan")
                .segundoNombre("Carlos")
                .primerApellido("Pérez")
                .segundoApellido("López")
                .fechaNacimiento(fechaNac)
                .email("juan@example.com")
                .username("juan123")
                .password("pass123")
                .telefono("1234-5678")
                .DUI("12345678-9")
                .direccion("San Salvador")
                .puntos(100)
                .build();

        // Verificar campos individuales
        assertEquals(1L, user.getIdUser().longValue());
        assertEquals("Juan", user.getPrimerNombre());
        assertEquals("Carlos", user.getSegundoNombre());
        assertEquals("Pérez", user.getPrimerApellido());
        assertEquals("López", user.getSegundoApellido());
        assertEquals(fechaNac, user.getFechaNacimiento());
        assertEquals("juan@example.com", user.getEmail());
        assertEquals("juan123", user.getUsername());
        assertEquals("pass123", user.getPassword());
        assertEquals("1234-5678", user.getTelefono());
        assertEquals("12345678-9", user.getDUI());
        assertEquals("San Salvador", user.getDireccion());
        assertEquals(100, user.getPuntos());
        assertNotNull(user.getRole());
        assertEquals("CLIENTE", user.getRole().getName());
    }

    @Test
    void testSetters() {
        User user = new User();

        user.setIdUser(2L);
        user.setPrimerNombre("María");
        user.setPrimerApellido("González");
        user.setEmail("maria@example.com");
        user.setUsername("maria123");
        user.setPassword("pass456");
        user.setTelefono("8765-4321");
        user.setDUI("98765432-1");
        user.setDireccion("Santa Ana");
        user.setPuntos(200);

        assertEquals(2L, user.getIdUser().longValue());
        assertEquals("María", user.getPrimerNombre());
        assertEquals("González", user.getPrimerApellido());
        assertEquals("maria@example.com", user.getEmail());
        assertEquals("maria123", user.getUsername());
        assertEquals("pass456", user.getPassword());
        assertEquals("8765-4321", user.getTelefono());
        assertEquals("98765432-1", user.getDUI());
        assertEquals("Santa Ana", user.getDireccion());
        assertEquals(200, user.getPuntos());
    }

    @Test
    void testRelacionesBidireccionales() {
        // Crear objetos relacionados
        Role role = new Role();
        role.setIdRol(1L);
        role.setName("ADMIN");

        Carrito carrito = new Carrito();
        carrito.setIdCarrito(1L);

        HistorialPedido pedido = new HistorialPedido();
        pedido.setIdHistorialPedido(1L);

        // Configurar relaciones
        User user = new User();
        user.setIdUser(1L);
        user.setRole(role);
        carrito.setUser(user); // Relación inversa
        pedido.setUser(user);  // Relación inversa

        // Verificar que las relaciones están bien configuradas
        assertNotNull(carrito.getUser());
        assertEquals(1L, carrito.getUser().getIdUser().longValue());

        assertNotNull(pedido.getUser());
        assertEquals(1L, pedido.getUser().getIdUser().longValue());
    }
}
