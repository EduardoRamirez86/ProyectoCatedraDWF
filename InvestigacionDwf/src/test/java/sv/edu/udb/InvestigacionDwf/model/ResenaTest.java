package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.*;
import sv.edu.udb.InvestigacionDwf.model.enums.RatingEnum;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ResenaTest {

    @Test
    void testConstructorAndGetters() {
        User user = User.builder().idUser(1L).build();
        Producto producto = Producto.builder().idProducto(1L).build();
        LocalDateTime fecha = LocalDateTime.now();
        Resena resena = Resena.builder()
                .idResena(1L)
                .user(user)
                .producto(producto)
                .comentario("Muy bueno")
                .fecha(fecha)
                .rating(RatingEnum.FIVE)
                .build();

        assertEquals(1L, resena.getIdResena());
        assertEquals(user, resena.getUser());
        assertEquals(producto, resena.getProducto());
        assertEquals("Muy bueno", resena.getComentario());
        assertEquals(fecha, resena.getFecha());
        assertEquals(RatingEnum.FIVE, resena.getRating());
    }

    @Test
    void testSetters() {
        Resena resena = new Resena();
        User user = new User();
        user.setIdUser(2L);
        Producto producto = new Producto();
        producto.setIdProducto(2L);
        LocalDateTime fecha = LocalDateTime.now();
        resena.setIdResena(2L);
        resena.setUser(user);
        resena.setProducto(producto);
        resena.setComentario("Comentario");
        resena.setFecha(fecha);
        resena.setRating(RatingEnum.THREE);

        assertEquals(2L, resena.getIdResena());
        assertEquals(user, resena.getUser());
        assertEquals(producto, resena.getProducto());
        assertEquals("Comentario", resena.getComentario());
        assertEquals(fecha, resena.getFecha());
        assertEquals(RatingEnum.THREE, resena.getRating());
    }

    @Test
    void testRelacionesBidireccionales() {
        User user = new User();
        user.setIdUser(1L);
        Producto producto = new Producto();
        producto.setIdProducto(1L);

        Resena resena = new Resena();
        resena.setIdResena(1L);
        resena.setUser(user);
        resena.setProducto(producto);

        assertNotNull(resena.getUser());
        assertEquals(1L, resena.getUser().getIdUser().longValue());
        assertNotNull(resena.getProducto());
        assertEquals(1L, resena.getProducto().getIdProducto().longValue());
    }
}
