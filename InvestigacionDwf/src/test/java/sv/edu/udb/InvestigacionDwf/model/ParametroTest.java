package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.Parametro;

import static org.junit.jupiter.api.Assertions.*;

class ParametroTest {

    @Test
    void testConstructorAndGetters() {
        Parametro parametro = Parametro.builder()
                .idParametro(1L)
                .clave("IVA")
                .valor("13")
                .descripcion("Impuesto al valor agregado")
                .build();

        assertEquals(1L, parametro.getIdParametro());
        assertEquals("IVA", parametro.getClave());
        assertEquals("13", parametro.getValor());
        assertEquals("Impuesto al valor agregado", parametro.getDescripcion());
    }

    @Test
    void testSetters() {
        Parametro parametro = new Parametro();
        parametro.setIdParametro(2L);
        parametro.setClave("DESCUENTO");
        parametro.setValor("10");
        parametro.setDescripcion("Descuento general");

        assertEquals(2L, parametro.getIdParametro());
        assertEquals("DESCUENTO", parametro.getClave());
        assertEquals("10", parametro.getValor());
        assertEquals("Descuento general", parametro.getDescripcion());
    }

    @Test
    void testRelacionesBidireccionales() {
        // Parametro no tiene relaciones bidireccionales en el modelo actual.
        Parametro parametro = new Parametro();
        parametro.setIdParametro(1L);
        assertEquals(1L, parametro.getIdParametro());
    }
}
