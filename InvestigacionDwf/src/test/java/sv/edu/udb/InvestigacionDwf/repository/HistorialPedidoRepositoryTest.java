package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.InvestigacionDwf.model.entity.HistorialPedido;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido.PENDIENTE;

@DataJpaTest
@ActiveProfiles("test")
class HistorialPedidoRepositoryTest {

    @Autowired
    private HistorialPedidoRepository historialPedidoRepository;

    private Long savedHistorialPedidoId;

    @BeforeEach
    void init() {
        HistorialPedido historial = new HistorialPedido();
        historial.setDescripcion("Test historial");
        historial.setEstado(PENDIENTE);
        historialPedidoRepository.save(historial);
        savedHistorialPedidoId = historial.getIdHistorialPedido();
    }

    @AfterEach
    void clean() {
        historialPedidoRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<HistorialPedido> historial = historialPedidoRepository.findById(savedHistorialPedidoId);
        assertTrue(historial.isPresent());
        assertEquals("Test historial", historial.get().getDescripcion());
    }

    @Test
    void testFindAll() {
        List<HistorialPedido> historiales = historialPedidoRepository.findAll();
        assertNotNull(historiales);
        assertFalse(historiales.isEmpty());
    }

    @Test
    void testSave() {
        HistorialPedido historial = new HistorialPedido();
        historial.setDescripcion("Otro historial");
        historial.setEstado(PENDIENTE);
        HistorialPedido saved = historialPedidoRepository.save(historial);
        assertNotNull(saved.getIdHistorialPedido());
        assertEquals("Otro historial", saved.getDescripcion());
    }

    @Test
    void testUpdate() {
        Optional<HistorialPedido> historialOpt = historialPedidoRepository.findById(savedHistorialPedidoId);
        assertTrue(historialOpt.isPresent());
        HistorialPedido historial = historialOpt.get();
        historial.setDescripcion("Historial actualizado");
        historialPedidoRepository.save(historial);

        Optional<HistorialPedido> updated = historialPedidoRepository.findById(savedHistorialPedidoId);
        assertTrue(updated.isPresent());
        assertEquals("Historial actualizado", updated.get().getDescripcion());
    }

    @Test
    void testDelete() {
        Optional<HistorialPedido> historialOpt = historialPedidoRepository.findById(savedHistorialPedidoId);
        assertTrue(historialOpt.isPresent());
        historialPedidoRepository.delete(historialOpt.get());
        Optional<HistorialPedido> deleted = historialPedidoRepository.findById(savedHistorialPedidoId);
        assertFalse(deleted.isPresent());
    }
}
