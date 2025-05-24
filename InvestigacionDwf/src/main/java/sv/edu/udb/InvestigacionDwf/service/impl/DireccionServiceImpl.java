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
import java.util.Objects; // ¡Importa Objects para las validaciones de nulidad!
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Genera un constructor con todos los campos 'final', inyectando dependencias.
public class DireccionServiceImpl implements DireccionService {

    private final DireccionRepository repo;
    private final UserRepository userRepo;
    private final DireccionMapper mapper;

    @Override
    @Transactional // Marca el método como transaccional para asegurar la operación de guardado.
    public DireccionResponse save(DireccionRequest req, Long idUser) {
        // Valida que el ID de usuario no sea nulo, ya que es esencial para asociar la dirección.
        if (Objects.isNull(idUser)) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo para guardar una dirección.");
        }
        // Valida que la solicitud de dirección no sea nula y que sus campos obligatorios no estén vacíos.
        if (Objects.isNull(req) || Objects.isNull(req.getAlias()) || req.getAlias().isBlank() ||
                Objects.isNull(req.getCalle()) || req.getCalle().isBlank() ||
                Objects.isNull(req.getCiudad()) || req.getCiudad().isBlank() ||
                Objects.isNull(req.getDepartamento()) || req.getDepartamento().isBlank()) {
            throw new IllegalArgumentException("Datos de la dirección incompletos o inválidos. Alias, calle, ciudad y departamento son obligatorios.");
        }
        // Puedes añadir validación para latitud y longitud si son obligatorios y tienen un rango específico
        // if (Objects.isNull(req.getLatitud()) || Objects.isNull(req.getLongitud())) {
        //     throw new IllegalArgumentException("Latitud y longitud son obligatorias.");
        // }

        // Busca el usuario por ID; si no se encuentra, lanza una ResourceNotFoundException.
        User user = userRepo.findById(idUser)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + idUser));

        // Mapea el DTO de solicitud a una entidad Direccion.
        Direccion direccion = mapper.toEntity(req);
        direccion.setUser(user); // Asocia la dirección al usuario encontrado.

        // Guarda la nueva dirección en la base de datos y la convierte a DTO de respuesta.
        return mapper.toResponse(repo.save(direccion));
    }

    @Override
    @Transactional(readOnly = true) // Marca el método como transaccional de solo lectura para optimización.
    public List<DireccionResponse> findByUser(Long idUser) {
        // Valida que el ID de usuario no sea nulo.
        if (Objects.isNull(idUser)) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo para buscar direcciones.");
        }

        // Busca todas las direcciones asociadas a un usuario específico.
        // Luego, utiliza un stream para mapear cada entidad Direccion a un DTO DireccionResponse.
        return repo.findByUser_IdUser(idUser).stream()
                .map(mapper::toResponse) // Utiliza el método toResponse del mapper.
                .collect(Collectors.toList()); // Recopila los DTOs en una lista.
    }



    /**
     * Actualiza una dirección existente.
     * Realiza validaciones de entrada y de existencia de la dirección.
     *
     * @param idDireccion ID de la dirección a actualizar.
     * @param req Datos para la actualización de la dirección.
     * @return DireccionResponse con los datos actualizados.
     * @throws IllegalArgumentException si los IDs o el request son nulos/inválidos.
     * @throws ResourceNotFoundException si la dirección no se encuentra.
     */
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

        // Actualiza los campos de la entidad existente con los datos del request.
        // Se puede usar un método en el mapper para esto (ej: mapper.updateEntityFromRequest(req, existingDireccion);)
        // o manualmente como se muestra.
        existingDireccion.setAlias(req.getAlias());
        existingDireccion.setCalle(req.getCalle());
        existingDireccion.setCiudad(req.getCiudad());
        existingDireccion.setDepartamento(req.getDepartamento());
        // Solo actualiza latitud y longitud si no son nulas en el request, permitiendo actualizaciones parciales si se desea.
        if (Objects.nonNull(req.getLatitud())) {
            existingDireccion.setLatitud(req.getLatitud());
        }
        if (Objects.nonNull(req.getLongitud())) {
            existingDireccion.setLongitud(req.getLongitud());
        }

        return mapper.toResponse(repo.save(existingDireccion));
    }



    /**
     * Elimina una dirección por su ID.
     * Realiza validaciones de entrada y verifica la existencia de la dirección.
     *
     * @param idDireccion ID de la dirección a eliminar.
     * @throws IllegalArgumentException si el ID es nulo.
     * @throws ResourceNotFoundException si la dirección no se encuentra.
     */
    @Transactional
    public void delete(Long idDireccion) {
        if (Objects.isNull(idDireccion)) {
            throw new IllegalArgumentException("El ID de la dirección no puede ser nulo para la eliminación.");
        }
        if (!repo.existsById(idDireccion)) {
            throw new ResourceNotFoundException("Dirección no encontrada con ID: " + idDireccion);
        }
        repo.deleteById(idDireccion);
    }
}
