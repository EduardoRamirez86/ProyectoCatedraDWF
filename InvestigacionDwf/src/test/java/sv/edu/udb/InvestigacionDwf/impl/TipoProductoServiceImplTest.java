package sv.edu.udb.InvestigacionDwf.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import sv.edu.udb.InvestigacionDwf.dto.request.TipoProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.TipoProductoResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;
import sv.edu.udb.InvestigacionDwf.repository.TipoProductoRepository;
import sv.edu.udb.InvestigacionDwf.service.impl.TipoProductoServiceImpl;
import sv.edu.udb.InvestigacionDwf.service.mapper.TipoProductoMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TipoProductoServiceImplTest {

    @InjectMocks
    private TipoProductoServiceImpl service;

    @Mock
    private TipoProductoRepository repository;
    @Mock
    private TipoProductoMapper mapper;

    @Test
    void crearTipoProducto_shouldSaveAndReturnResponse() {
        TipoProductoRequest req = new TipoProductoRequest();
        req.setTipo("A");
        req.setDescripcion("B");
        TipoProducto entity = new TipoProducto();
        TipoProducto saved = new TipoProducto();
        TipoProductoResponse resp = new TipoProductoResponse();

        when(mapper.toEntity(req)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(resp);

        assertEquals(resp, service.crearTipoProducto(req));
    }

    @Test
    void crearTipoProducto_shouldThrowIfInvalid() {
        assertThrows(IllegalArgumentException.class, () -> service.crearTipoProducto(null));
        TipoProductoRequest req = new TipoProductoRequest();
        assertThrows(IllegalArgumentException.class, () -> service.crearTipoProducto(req));
        req.setTipo(" ");
        req.setDescripcion("desc");
        assertThrows(IllegalArgumentException.class, () -> service.crearTipoProducto(req));
    }

    @Test
    void listarTipoProductos_shouldReturnList() {
        TipoProducto entity = new TipoProducto();
        TipoProductoResponse resp = new TipoProductoResponse();
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(resp);

        List<TipoProductoResponse> result = service.listarTipoProductos();
        assertEquals(1, result.size());
        assertEquals(resp, result.get(0));
    }

    @Test
    void obtenerTipoProductoPorId_shouldReturnResponse() {
        TipoProducto entity = new TipoProducto();
        TipoProductoResponse resp = new TipoProductoResponse();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(resp);

        assertEquals(resp, service.obtenerTipoProductoPorId(1L));
    }

    @Test
    void obtenerTipoProductoPorId_shouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () -> service.obtenerTipoProductoPorId(null));
    }

    @Test
    void obtenerTipoProductoPorId_shouldThrowIfNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.obtenerTipoProductoPorId(1L));
    }

    @Test
    void actualizarTipoProducto_shouldUpdateAndReturnResponse() {
        TipoProductoRequest req = new TipoProductoRequest();
        req.setTipo("A");
        req.setDescripcion("B");
        TipoProducto entity = new TipoProducto();
        TipoProductoResponse resp = new TipoProductoResponse();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(resp);

        assertEquals(resp, service.actualizarTipoProducto(1L, req));
    }

    @Test
    void actualizarTipoProducto_shouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () -> service.actualizarTipoProducto(null, new TipoProductoRequest()));
        assertThrows(IllegalArgumentException.class, () -> service.actualizarTipoProducto(1L, null));
    }

    @Test
    void actualizarTipoProducto_shouldThrowIfInvalid() {
        TipoProductoRequest req = new TipoProductoRequest();
        assertThrows(IllegalArgumentException.class, () -> service.actualizarTipoProducto(1L, req));
    }

    @Test
    void actualizarTipoProducto_shouldThrowIfNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        TipoProductoRequest req = new TipoProductoRequest();
        req.setTipo("A");
        req.setDescripcion("B");
        assertThrows(ResourceNotFoundException.class, () -> service.actualizarTipoProducto(1L, req));
    }

    @Test
    void eliminarTipoProducto_shouldDeleteIfExists() {
        when(repository.existsById(1L)).thenReturn(true);
        service.eliminarTipoProducto(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void eliminarTipoProducto_shouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () -> service.eliminarTipoProducto(null));
    }

    @Test
    void eliminarTipoProducto_shouldThrowIfNotFound() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.eliminarTipoProducto(1L));
    }
}
