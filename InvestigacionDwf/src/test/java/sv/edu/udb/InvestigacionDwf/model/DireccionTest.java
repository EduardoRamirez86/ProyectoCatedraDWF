package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.Direccion;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class DireccionTest {

    @Test
    void testConstructorAndGetters() {
        User user = User.builder().idUser(1L).build();
        Direccion direccion = Direccion.builder()
                .idDireccion(1L)
                .user(user)
                .alias("Casa")
                .calle("Calle Principal")
                .ciudad("San Salvador")
                .departamento("San Salvador")
                .latitud(13.7)
                .longitud(-89.2)
                .build();

        assertEquals(1L, direccion.getIdDireccion());
        assertEquals(user, direccion.getUser());
        assertEquals("Casa", direccion.getAlias());
        assertEquals("Calle Principal", direccion.getCalle());
        assertEquals("San Salvador", direccion.getCiudad());
        assertEquals("San Salvador", direccion.getDepartamento());
        assertEquals(13.7, direccion.getLatitud());
        assertEquals(-89.2, direccion.getLongitud());
    }

    @Test
    void testSetters() {
        Direccion direccion = new Direccion();
        User user = new User();
        user.setIdUser(2L);
        direccion.setIdDireccion(2L);
        direccion.setUser(user);
        direccion.setAlias("Trabajo");
        direccion.setCalle("Calle Secundaria");
        direccion.setCiudad("Santa Ana");
        direccion.setDepartamento("Santa Ana");
        direccion.setLatitud(13.5);
        direccion.setLongitud(-89.1);

        assertEquals(2L, direccion.getIdDireccion());
        assertEquals(user, direccion.getUser());
        assertEquals("Trabajo", direccion.getAlias());
        assertEquals("Calle Secundaria", direccion.getCalle());
        assertEquals("Santa Ana", direccion.getCiudad());
        assertEquals("Santa Ana", direccion.getDepartamento());
        assertEquals(13.5, direccion.getLatitud());
        assertEquals(-89.1, direccion.getLongitud());
    }

    @Test
    void testRelacionesBidireccionales() {
        // Crear objetos relacionados
        User user = new User();
        user.setIdUser(1L);

        Direccion direccion = new Direccion();
        direccion.setIdDireccion(1L);
        direccion.setUser(user);

        // Verificar que la relación está bien configurada
        assertNotNull(direccion.getUser());
        assertEquals(1L, direccion.getUser().getIdUser().longValue());
    }
}
