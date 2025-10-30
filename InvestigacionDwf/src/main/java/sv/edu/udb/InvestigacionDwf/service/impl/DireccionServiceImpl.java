package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.DireccionRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.DireccionResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.Direccion;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.repository.DireccionRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.service.DireccionService;
import sv.edu.udb.InvestigacionDwf.service.mapper.DireccionMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DireccionServiceImpl implements DireccionService {

    private final DireccionRepository repo;
    private final UserRepository userRepo;
    private final DireccionMapper mapper;

    @Override
    @Transactional
    public DireccionResponse save(DireccionRequest req, Long idUser) {
        if (Objects.isNull(idUser)) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo para guardar una dirección.");
        }
        if (Objects.isNull(req) || Objects.isNull(req.getAlias()) || req.getAlias().isBlank() ||
                Objects.isNull(req.getCalle()) || req.getCalle().isBlank() ||
                Objects.isNull(req.getCiudad()) || req.getCiudad().isBlank() ||
                Objects.isNull(req.getDepartamento()) || req.getDepartamento().isBlank()) {
            throw new IllegalArgumentException("Datos de la dirección incompletos o inválidos.");
        }

        User user = userRepo.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUser));

        Direccion direccion = mapper.toEntity(req);
        direccion.setUser(user);
        // El campo 'activa' será 'true' por defecto gracias a la definición en la entidad.

        return mapper.toResponse(repo.save(direccion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DireccionResponse> findByUser(Long idUser) {
        if (Objects.isNull(idUser)) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo para buscar direcciones.");
        }

        // --- 👇 USAREMOS EL NUEVO MÉTODO DEL REPOSITORIO 👇 ---
        // Ahora solo se devolverán las direcciones que no han sido "borradas".
        return repo.findByUser_IdUserAndActivaTrue(idUser).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        // --- ---------------------------------------------- ---
    }

    @Override
    @Transactional
    public DireccionResponse update(Long idDireccion, DireccionRequest req) {
        if (Objects.isNull(idDireccion)) {
            throw new IllegalArgumentException("El ID de la dirección no puede ser nulo para la actualización.");
        }
        if (Objects.isNull(req) || Objects.isNull(req.getAlias()) || req.getAlias().isBlank() ||
                Objects.isNull(req.getCalle()) || req.getCalle().isBlank() ||
                Objects.isNull(req.getCiudad()) || req.getCiudad().isBlank() ||
                Objects.isNull(req.getDepartamento()) || req.getDepartamento().isBlank()) {
            throw new IllegalArgumentException("Datos de actualización de la dirección incompletos o inválidos.");
        }

        Direccion existingDireccion = repo.findById(idDireccion)
                .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada con ID: " + idDireccion));

        existingDireccion.setAlias(req.getAlias());
        existingDireccion.setCalle(req.getCalle());
        existingDireccion.setCiudad(req.getCiudad());
        existingDireccion.setDepartamento(req.getDepartamento());
        if (Objects.nonNull(req.getLatitud())) {
            existingDireccion.setLatitud(req.getLatitud());
        }
        if (Objects.nonNull(req.getLongitud())) {
            existingDireccion.setLongitud(req.getLongitud());
        }

        return mapper.toResponse(repo.save(existingDireccion));
    }

    /**
     * "Elimina" una dirección marcándola como inactiva (borrado lógico).
     * Esto evita errores de foreign key con la tabla de pedidos.
     *
     * @param idDireccion ID de la dirección a "eliminar".
     * @throws IllegalArgumentException si el ID es nulo.
     * @throws ResourceNotFoundException si la dirección no se encuentra.
     */
    @Override // <-- ¡MUY IMPORTANTE QUE ESTÉ ESTE @Override!
    @Transactional
    public void delete(Long idDireccion) {
        if (Objects.isNull(idDireccion)) {
            throw new IllegalArgumentException("El ID de la dirección no puede ser nulo para la eliminación.");
        }

        // Buscamos la dirección que queremos "borrar"
        Direccion direccion = repo.findById(idDireccion)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar la dirección con ID: " + idDireccion + " porque no fue encontrada."));

        // --- 👇 ESTA ES TODA LA NUEVA LÓGICA DE BORRADO 👇 ---
        // 1. La marcamos como inactiva.
        direccion.setActiva(false);
        // 2. Guardamos la entidad actualizada. Hibernate hará un UPDATE en la base de datos.
        repo.save(direccion);
        // YA NO USAMOS repo.deleteById(idDireccion);
        // --- --------------------------------------------- ---
    }
}
