// src/main/java/sv/edu/udb/InvestigacionDwf/service/impl/ResenaServiceImpl.java
package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.ResenaRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ResenaResponse;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.Resena;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.repository.ResenaRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.service.ResenaService;
import sv.edu.udb.InvestigacionDwf.service.assembler.ResenaAssembler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para la gestión de reseñas.
 * Proporciona la lógica de negocio para crear, obtener y gestionar reseñas de productos.
 */
@Service // Indica que esta clase es un componente de servicio de Spring, manejado por el contenedor.
@RequiredArgsConstructor // Genera automáticamente un constructor con todos los campos 'final', facilitando la inyección de dependencias.
public class ResenaServiceImpl implements ResenaService {

    // Instancia del Logger para registrar información, advertencias y errores específicos de este servicio.
    private static final Logger logger = LoggerFactory.getLogger(ResenaServiceImpl.class);

    // Inyección de dependencias a través del constructor generado por Lombok.
    private final ResenaRepository resenaRepository; // Repositorio para operaciones CRUD sobre la entidad Resena.
    private final UserRepository userRepository;     // Repositorio para obtener información de usuarios.
    private final ProductoRepository productoRepository; // Repositorio para obtener información de productos.
    private final ResenaAssembler assembler; // Ensamblador que convierte entidades Resena en DTOs ResenaResponse con enlaces HATEOAS.
    private final PagedResourcesAssembler<Resena> pagedAssembler; // Ensamblador especializado para convertir objetos Page a PagedModel (colecciones paginadas con HATEOAS).

    /**
     * Crea una nueva reseña para un producto específico por parte de un usuario.
     * Realiza validaciones robustas de los datos de entrada y busca las entidades relacionadas antes de persistir la reseña.
     *
     * @param request Los datos de la reseña a crear, encapsulados en un {@link ResenaRequest}.
     * @return Un {@link ResenaResponse} que representa la reseña creada, incluyendo enlaces HATEOAS.
     * @throws IllegalArgumentException Si la solicitud de reseña o cualquiera de sus campos obligatorios (ID de usuario, ID de producto, comentario, calificación) es nula, vacía o inválida.
     * @throws ResourceNotFoundException Si el usuario o el producto especificado en la solicitud no se encuentran en la base de datos.
     */
    @Override // Indica que este método implementa un método de la interfaz ResenaService.
    @Transactional // Asegura que este método se ejecute dentro de una transacción de base de datos. Si alguna operación falla, la transacción se revertirá.
    public ResenaResponse crearResena(ResenaRequest request) {
        logger.info("Iniciando la creación de una nueva reseña.");

        // --- Validaciones de la Solicitud (Request) ---
        // Verifica si el objeto request completo es nulo o si los IDs de usuario/producto son nulos.
        // Se combinan varias verificaciones iniciales para una validación rápida de los campos esenciales.
        if (Objects.isNull(request) || Objects.isNull(request.getIdUser()) || Objects.isNull(request.getIdProducto())) {
            logger.error("Datos de reseña inválidos: la solicitud o IDs de usuario/producto son nulos.");
            throw new IllegalArgumentException("Datos de reseña inválidos");
        }
        // Valida si el comentario es nulo o si solo contiene espacios en blanco.
        if (Objects.isNull(request.getComentario()) || request.getComentario().isBlank()) {
            logger.error("Comentario de reseña nulo o vacío.");
            throw new IllegalArgumentException("El comentario no puede estar vacío.");
        }
        // Valida si la calificación (rating) es nula.
        // Si request.getRating() es un RatingEnum, solo podemos validar si es null,
        // ya que el rango de valores válidos lo define el propio enum.
        if (Objects.isNull(request.getRating())) {
            logger.error("La calificación de la reseña es nula.");
            throw new IllegalArgumentException("La calificación es obligatoria.");
        }
        logger.debug("Validaciones iniciales de la solicitud de reseña superadas. User ID: {}, Producto ID: {}", request.getIdUser(), request.getIdProducto());


        // --- Búsqueda y Validación de Entidades Relacionadas ---
        // Busca al usuario por su ID. Si no se encuentra, lanza una excepción de Recurso No Encontrado.
        User user = userRepository.findById(request.getIdUser())
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con ID: {}", request.getIdUser());
                    return new ResourceNotFoundException("Usuario no encontrado: " + request.getIdUser());
                });
        // Busca el producto por su ID. Si no se encuentra, lanza una excepción de Recurso No Encontrado.
        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> {
                    logger.warn("Producto no encontrado con ID: {}", request.getIdProducto());
                    return new ResourceNotFoundException("Producto no encontrado: " + request.getIdProducto());
                });
        logger.debug("Usuario '{}' y Producto '{}' encontrados y validados para la reseña.", user.getUsername(), producto.getNombre());


        // --- Construcción y Persistencia de la Reseña ---
        // Construye la entidad Resena utilizando el patrón Builder de Lombok.
        // Se trimea el comentario para eliminar espacios en blanco al inicio y al final.
        Resena resena = Resena.builder()
                .user(user) // Asigna el usuario encontrado.
                .producto(producto) // Asigna el producto encontrado.
                .comentario(request.getComentario().trim()) // Asigna el comentario, eliminando espacios superfluos.
                .rating(request.getRating()) // Asigna la calificación (que es un RatingEnum).
                .fecha(LocalDateTime.now()) // Establece la fecha y hora actual de la creación de la reseña.
                .build();

        // Guarda la nueva entidad Resena en la base de datos.
        resenaRepository.save(resena);
        logger.info("Reseña creada exitosamente con ID: {} para el producto ID: {} por el usuario ID: {}",
                resena.getIdResena(), producto.getIdProducto(), user.getIdUser());

        // Convierte la entidad Resena persistida a un DTO de respuesta y lo devuelve.
        return assembler.toModel(resena);
    }

    /**
     * Obtiene una lista de reseñas asociadas a un producto específico.
     * Este método solo realiza operaciones de lectura en la base de datos.
     *
     * @param idProducto El ID del producto cuyas reseñas se desean obtener.
     * @return Una {@link List} de {@link ResenaResponse}, que puede estar vacía si no hay reseñas para el producto o si el producto no existe.
     * @throws IllegalArgumentException Si el ID del producto proporcionado es nulo.
     */
    @Override
    @Transactional(readOnly = true) // Optimiza la transacción para operaciones de solo lectura, lo que puede mejorar el rendimiento.
    public List<ResenaResponse> obtenerResenasPorProducto(Long idProducto) {
        // Valida que el ID del producto no sea nulo.
        if (Objects.isNull(idProducto)) {
            logger.error("ID de producto nulo al intentar obtener reseñas por producto.");
            throw new IllegalArgumentException("El ID de producto no puede ser nulo.");
        }
        logger.debug("Buscando reseñas para el producto con ID: {}", idProducto);

        // Busca todas las reseñas que estén asociadas al producto con el ID dado.
        // Si no se encuentran reseñas, se devuelve una lista vacía, lo cual es un comportamiento esperado.
        List<Resena> resenas = resenaRepository.findByProducto_IdProducto(idProducto);

        // Registra el número de reseñas encontradas.
        logger.info("Encontradas {} reseñas para el producto ID: {}", resenas.size(), idProducto);

        // Convierte cada entidad Resena a su DTO ResenaResponse utilizando el ensamblador y recolecta los resultados en una nueva lista.
        return resenas.stream()
                .map(assembler::toModel) // Aplica la conversión a cada elemento del stream.
                .collect(Collectors.toList()); // Convierte el stream de vuelta a una List.
    }


    /**
     * Obtiene todas las reseñas en el sistema de forma paginada.
     * Este método es de solo lectura y utiliza el {@link PagedResourcesAssembler} para formatear los resultados.
     *
     * @param pageable Objeto {@link Pageable} que contiene la información de paginación (número de página, tamaño de página, criterios de ordenamiento).
     * @return Un {@link PagedModel} de {@link ResenaResponse} que representa la página de reseñas, incluyendo metadatos de paginación y enlaces HATEOAS.
     * @throws IllegalArgumentException Si el objeto Pageable proporcionado es nulo.
     */
    @Override
    @Transactional(readOnly = true)
    public PagedModel<ResenaResponse> findAll(Pageable pageable) {
        // Valida que el objeto Pageable no sea nulo.
        if (Objects.isNull(pageable)) {
            logger.error("Objeto Pageable nulo al intentar obtener todas las reseñas.");
            throw new IllegalArgumentException("La paginación no puede ser nula.");
        }
        logger.debug("Buscando todas las reseñas con paginación: {}", pageable);

        // Realiza la búsqueda de todas las reseñas de forma paginada.
        Page<Resena> page = resenaRepository.findAll(pageable);

        // Registra información detallada sobre la página obtenida.
        logger.info("Obtenidas {} reseñas en la página {} de un total de {} páginas ({} elementos totales).",
                page.getNumberOfElements(), page.getNumber(), page.getTotalPages(), page.getTotalElements());

        // Convierte la 'Page' de entidades Resena a un 'PagedModel' de ResenaResponse utilizando el ensamblador paginado.
        return pagedAssembler.toModel(page, assembler);
    }

    /**
     * Obtiene una reseña específica por su ID.
     * Este método es de solo lectura.
     *
     * @param idResena El ID de la reseña a buscar.
     * @return Un {@link ResenaResponse} que representa la reseña encontrada, incluyendo enlaces HATEOAS.
     * @throws IllegalArgumentException Si el ID de la reseña proporcionado es nulo.
     * @throws ResourceNotFoundException Si la reseña con el ID especificado no se encuentra en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public ResenaResponse getById(Long idResena) {
        // Valida que el ID de la reseña no sea nulo.
        if (Objects.isNull(idResena)) {
            logger.error("ID de reseña nulo al intentar obtener una reseña por ID.");
            throw new IllegalArgumentException("El ID de reseña no puede ser nulo.");
        }
        logger.debug("Buscando reseña con ID: {}", idResena);

        // Busca la reseña por su ID. Si no se encuentra, lanza una excepción de Recurso No Encontrado.
        Resena r = resenaRepository.findById(idResena)
                .orElseThrow(() -> {
                    logger.warn("Reseña no encontrada con ID: {}", idResena);
                    return new ResourceNotFoundException("Reseña no encontrada: " + idResena);
                });
        logger.info("Reseña encontrada con ID: {}", idResena);

        // Convierte la entidad Resena encontrada a su DTO de respuesta y la devuelve.
        return assembler.toModel(r);
    }

    /**
     * Obtiene reseñas asociadas a un producto específico de forma paginada.
     * Este método es de solo lectura y utiliza el {@link PagedResourcesAssembler} para formatear los resultados.
     *
     * @param idProducto El ID del producto cuyas reseñas paginadas se desean obtener.
     * @param pageable   Objeto {@link Pageable} que contiene la información de paginación.
     * @return Un {@link PagedModel} de {@link ResenaResponse} que representa la página de reseñas para el producto dado, incluyendo metadatos de paginación y enlaces HATEOAS.
     * @throws IllegalArgumentException Si el ID del producto o el objeto Pageable proporcionado es nulo.
     * @throws ResourceNotFoundException Si el producto especificado no se encuentra en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public PagedModel<ResenaResponse> obtenerResenasPorProductoPaginadas(Long idProducto, Pageable pageable) {
        // Validaciones
        if (Objects.isNull(idProducto)) {
            logger.error("ID de producto nulo al intentar obtener reseñas paginadas por producto.");
            throw new IllegalArgumentException("El ID de producto no puede ser nulo.");
        }
        if (Objects.isNull(pageable)) {
            logger.error("Objeto Pageable nulo al intentar obtener reseñas paginadas por producto.");
            throw new IllegalArgumentException("La paginación no puede ser nula.");
        }

        logger.debug("Buscando reseñas paginadas para el producto con ID: {} con paginación: {}", idProducto, pageable);

        // Verifica si el producto existe antes de buscar sus reseñas
        if (!productoRepository.existsById(idProducto)) {
            logger.warn("Producto no encontrado con ID: {} al intentar obtener reseñas paginadas.", idProducto);
            throw new ResourceNotFoundException("Producto no encontrado: " + idProducto);
        }

        // Realiza la búsqueda de reseñas por ID de producto de forma paginada.
        Page<Resena> page = resenaRepository.findByProducto_IdProducto(idProducto, pageable);

        logger.info("Obtenidas {} reseñas para producto ID: {} en la página {} de un total de {} páginas ({} elementos totales).",
                page.getNumberOfElements(), page.getNumber(), page.getTotalPages(), page.getTotalElements(), idProducto);

        // Convierte la 'Page' de entidades Resena a un 'PagedModel' de ResenaResponse utilizando el ensamblador paginado.
        // Se pasa el idProducto para construir los enlaces HATEOAS específicos de este recurso paginado.
        return pagedAssembler.toModel(page, assembler);
    }
}