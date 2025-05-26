package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.Cupon;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CuponTest {

    @Test
    void testConstructorAndGetters() {
        User user = User.builder().idUser(1L).build();
        LocalDateTime now = LocalDateTime.now();
        Cupon cupon = Cupon.builder()
                .idCupon(1L)
                .user(user)
                .codigo("ABC123")
                .porcentajeDescuento(15.0)
                .usado(false)
                .fechaCreacion(now)
                .fechaExpiracion(now.plusDays(1))
                .build();

        assertEquals(1L, cupon.getIdCupon());
        assertEquals(user, cupon.getUser());
        assertEquals("ABC123", cupon.getCodigo());
        assertEquals(15.0, cupon.getPorcentajeDescuento());
        assertFalse(cupon.isUsado());
        assertEquals(now, cupon.getFechaCreacion());
        assertEquals(now.plusDays(1), cupon.getFechaExpiracion());
    }

    @Test
    void testSetters() {
        Cupon cupon = new Cupon();
        User user = new User();
        user.setIdUser(2L);
        LocalDateTime now = LocalDateTime.now();
        cupon.setIdCupon(2L);
        cupon.setUser(user);
        cupon.setCodigo("XYZ789");
        cupon.setPorcentajeDescuento(20.0);
        cupon.setUsado(true);
        cupon.setFechaCreacion(now);
        cupon.setFechaExpiracion(now.plusDays(2));

        assertEquals(2L, cupon.getIdCupon());
        assertEquals(user, cupon.getUser());
        assertEquals("XYZ789", cupon.getCodigo());
        assertEquals(20.0, cupon.getPorcentajeDescuento());
        assertTrue(cupon.isUsado());
        assertEquals(now, cupon.getFechaCreacion());
        assertEquals(now.plusDays(2), cupon.getFechaExpiracion());
    }

    @Test
    void testMarcarComoUsado() {
        Cupon cupon = Cupon.builder().usado(false).build();
        cupon.marcarComoUsado();
        assertTrue(cupon.isUsado());
    }

    @Test
    void testEstaExpirado() {
        Cupon cupon = Cupon.builder().fechaExpiracion(LocalDateTime.now().minusDays(1)).build();
        assertTrue(cupon.estaExpirado());
    }

    @Test
    void testRelacionesBidireccionales() {
        User user = new User();
        user.setIdUser(1L);

        Cupon cupon = new Cupon();
        cupon.setIdCupon(1L);
        cupon.setUser(user);

        assertNotNull(cupon.getUser());
        assertEquals(1L, cupon.getUser().getIdUser().longValue());
    }
}
