package sv.edu.udb.InvestigacionDwf.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.service.assembler.ProductoAssembler;
import sv.edu.udb.InvestigacionDwf.service.impl.ProductoServiceImpl;
import sv.edu.udb.InvestigacionDwf.service.mapper.ProductoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ProductoServiceImplTest {

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Mock
    private ProductoRepository repo;
    @Mock
    private ProductoAssembler assembler;
    @Mock
    private ProductoMapper mapper;
    @Mock
    private PagedResourcesAssembler<Producto> pagedAssembler;

    @Test
    void create_shouldSaveAndReturnResponse() {
        ProductoRequest req = new ProductoRequest();
        Producto prod = new Producto();
        Producto saved = new Producto();
        ProductoResponse resp = new ProductoResponse();

        when(mapper.toEntity(req)).thenReturn(prod);
        when(repo.save(prod)).thenReturn(saved);
        when(assembler.toModel(saved)).thenReturn(resp);

        ProductoResponse result = productoService.create(req);

        assertEquals(resp, result);
        verify(repo).save(prod);
    }

    @Test
    void create_shouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () -> productoService.create(null));
    }

    @Test
    void update_shouldUpdateAndReturnResponse() {
        Long id = 1L;
        ProductoRequest req = new ProductoRequest();
        Producto prod = new Producto();
        Producto updated = new Producto();
        ProductoResponse resp = new ProductoResponse();

        when(repo.findById(id)).thenReturn(Optional.of(prod));
        doNothing().when(mapper).updateEntityFromRequest(req, prod);
        when(repo.save(prod)).thenReturn(updated);
        when(assembler.toModel(updated)).thenReturn(resp);

        ProductoResponse result = productoService.update(id, req);

        assertEquals(resp, result);
        verify(repo).save(prod);
    }

    @Test
    void update_shouldThrowIfIdNull() {
        assertThrows(IllegalArgumentException.class, () -> productoService.update(null, new ProductoRequest()));
    }

    @Test
    void update_shouldThrowIfRequestNull() {
        assertThrows(IllegalArgumentException.class, () -> productoService.update(1L, null));
    }

    @Test
    void update_shouldThrowIfNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productoService.update(1L, new ProductoRequest()));
    }

    @Test
    void delete_shouldDeleteIfExists() {
        when(repo.existsById(1L)).thenReturn(true);
        productoService.delete(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void delete_shouldThrowIfIdNull() {
        assertThrows(IllegalArgumentException.class, () -> productoService.delete(null));
    }

    @Test
    void delete_shouldThrowIfNotFound() {
        when(repo.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> productoService.delete(1L));
    }

    @Test
    void findAll_shouldReturnPagedModel() {
        Pageable pageable = mock(Pageable.class);
        Page<Producto> page = new PageImpl<>(List.of());
        PagedModel<ProductoResponse> pagedModel = PagedModel.empty();

        when(repo.findAll(pageable)).thenReturn(page);
        when(pagedAssembler.toModel(page, assembler)).thenReturn(pagedModel);

        assertEquals(pagedModel, productoService.findAll(pageable));
    }

    @Test
    void findAll_shouldThrowIfPageableNull() {
        assertThrows(IllegalArgumentException.class, () -> productoService.findAll(null));
    }

    @Test
    void getById_shouldReturnResponse() {
        Producto prod = new Producto();
        ProductoResponse resp = new ProductoResponse();
        when(repo.findByIdWithTipoProducto(1L)).thenReturn(Optional.of(prod));
        when(assembler.toModel(prod)).thenReturn(resp);

        assertEquals(resp, productoService.getById(1L));
    }

    @Test
    void getById_shouldThrowIfIdNull() {
        assertThrows(IllegalArgumentException.class, () -> productoService.getById(null));
    }

    @Test
    void getById_shouldThrowIfNotFound() {
        when(repo.findByIdWithTipoProducto(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productoService.getById(1L));
    }

    @Test
    void findRecommendedByUser_shouldReturnList() {
        Producto prod = new Producto();
        ProductoResponse resp = new ProductoResponse();
        when(repo.findTop5ByOrderByFechaCreacionDesc()).thenReturn(List.of(prod));
        when(assembler.toModel(prod)).thenReturn(resp);

        List<ProductoResponse> result = productoService.findRecommendedByUser(1L);
        assertEquals(1, result.size());
        assertEquals(resp, result.get(0));
    }

    @Test
    void findRecommendedByUser_shouldThrowIfIdNull() {
        assertThrows(IllegalArgumentException.class, () -> productoService.findRecommendedByUser(null));
    }
}
