package sv.edu.udb.InvestigacionDwf.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import sv.edu.udb.InvestigacionDwf.dto.request.DireccionRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.DireccionResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.Direccion;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.repository.DireccionRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.service.impl.DireccionServiceImpl;
import sv.edu.udb.InvestigacionDwf.service.mapper.DireccionMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class DireccionServiceImplTest {

    @InjectMocks
    private DireccionServiceImpl service;

    @Mock
    private DireccionRepository repo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private DireccionMapper mapper;

    @Test
    void save_shouldSaveAndReturnResponse() {
        DireccionRequest req = new DireccionRequest();
        req.setAlias("Casa");
        req.setCalle("Calle 1");
        req.setCiudad("Ciudad");
        req.setDepartamento("Depto");
        User user = new User();
        Direccion dir = new Direccion();
        Direccion saved = new Direccion();
        DireccionResponse resp = new DireccionResponse();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toEntity(req)).thenReturn(dir);
        when(repo.save(dir)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(resp);

        assertEquals(resp, service.save(req, 1L));
    }

    @Test
    void save_shouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () -> service.save(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> service.save(new DireccionRequest(), null));
    }

    @Test
    void save_shouldThrowIfInvalidFields() {
        DireccionRequest req = new DireccionRequest();
        req.setAlias(" ");
        req.setCalle("Calle");
        req.setCiudad("Ciudad");
        req.setDepartamento("Depto");
        assertThrows(IllegalArgumentException.class, () -> service.save(req, 1L));
    }

    @Test
    void save_shouldThrowIfUserNotFound() {
        DireccionRequest req = new DireccionRequest();
        req.setAlias("Casa");
        req.setCalle("Calle 1");
        req.setCiudad("Ciudad");
        req.setDepartamento("Depto");
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.save(req, 1L));
    }

    @Test
    void findByUser_shouldReturnList() {
        Direccion dir = new Direccion();
        DireccionResponse resp = new DireccionResponse();
        when(repo.findByUser_IdUser(1L)).thenReturn(List.of(dir));
        when(mapper.toResponse(dir)).thenReturn(resp);

        List<DireccionResponse> result = service.findByUser(1L);
        assertEquals(1, result.size());
        assertEquals(resp, result.get(0));
    }

    @Test
    void findByUser_shouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () -> service.findByUser(null));
    }

    @Test
    void update_shouldUpdateAndReturnResponse() {
        DireccionRequest req = new DireccionRequest();
        req.setAlias("Casa");
        req.setCalle("Calle 1");
        req.setCiudad("Ciudad");
        req.setDepartamento("Depto");
        Direccion dir = new Direccion();
        DireccionResponse resp = new DireccionResponse();

        when(repo.findById(1L)).thenReturn(Optional.of(dir));
        when(repo.save(dir)).thenReturn(dir);
        when(mapper.toResponse(dir)).thenReturn(resp);

        assertEquals(resp, service.update(1L, req));
    }

    @Test
    void update_shouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () -> service.update(null, new DireccionRequest()));
        assertThrows(IllegalArgumentException.class, () -> service.update(1L, null));
    }

    @Test
    void update_shouldThrowIfInvalidFields() {
        DireccionRequest req = new DireccionRequest();
        req.setAlias(" ");
        req.setCalle("Calle");
        req.setCiudad("Ciudad");
        req.setDepartamento("Depto");
        assertThrows(IllegalArgumentException.class, () -> service.update(1L, req));
    }

    @Test
    void update_shouldThrowIfNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        DireccionRequest req = new DireccionRequest();
        req.setAlias("Casa");
        req.setCalle("Calle 1");
        req.setCiudad("Ciudad");
        req.setDepartamento("Depto");
        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, req));
    }

    @Test
    void delete_shouldDeleteIfExists() {
        when(repo.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(repo).deleteById(1L);
    }

    @Test
    void delete_shouldThrowIfNull() {
        assertThrows(IllegalArgumentException.class, () -> service.delete(null));
    }

    @Test
    void delete_shouldThrowIfNotFound() {
        when(repo.existsById(1L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}
