package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.Ropa;

import static org.junit.jupiter.api.Assertions.*;

class RopaTest {

    @Test
    void testConstructorAndGetters() {
        Ropa ropa = new Ropa();
        ropa.setIdRopa(1L);
        ropa.setNombre("Camisa");
        ropa.setPrecio(15.5);

        assertEquals(1L, ropa.getIdRopa());
        assertEquals("Camisa", ropa.getNombre());
        assertEquals(15.5, ropa.getPrecio());
    }

    @Test
    void testSetters() {
        Ropa ropa = new Ropa();
        ropa.setIdRopa(2L);
        ropa.setNombre("Pantalón");
        ropa.setPrecio(20.0);

        assertEquals(2L, ropa.getIdRopa());
        assertEquals("Pantalón", ropa.getNombre());
        assertEquals(20.0, ropa.getPrecio());
    }

    @Test
    void testRelacionesBidireccionales() {
        // Ropa no tiene relaciones bidireccionales directas en el modelo actual.
        Ropa ropa = new Ropa();
        ropa.setIdRopa(1L);

        assertEquals(1L, ropa.getIdRopa());
    }
}
