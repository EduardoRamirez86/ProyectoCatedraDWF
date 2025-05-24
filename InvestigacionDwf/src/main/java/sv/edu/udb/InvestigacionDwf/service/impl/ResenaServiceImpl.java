package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.dto.request.ResenaRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ResenaResponse;
import sv.edu.udb.InvestigacionDwf.service.mapper.ResenaMapper;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.Resena;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.enums.RatingEnum; // Asegúrate de que tu enum esté importado
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.repository.ResenaRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.service.ResenaService;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository resenaRepository;
    private final UserRepository userRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public ResenaResponse crearResena(ResenaRequest request) {
        // Valida que el objeto request y sus IDs esenciales no sean nulos.
        // Utiliza Objects.isNull() para una comprobación explícita.
        if (Objects.isNull(request) || Objects.isNull(request.getIdUser()) || Objects.isNull(request.getIdProducto())) {
            throw new IllegalArgumentException("La solicitud de reseña es inválida: ID de usuario o producto no proporcionado.");
        }
        // También valida que el comentario o la calificación no sean inválidos si son obligatorios.
        // Por ejemplo, si el comentario es String y no puede estar vacío:
        if (Objects.isNull(request.getComentario()) || request.getComentario().isBlank()) {
            throw new IllegalArgumentException("El comentario de la reseña no puede estar vacío.");
        }

        // --- CORRECCIÓN AQUÍ ---
        // Para validar un RatingEnum, necesitas acceder a su valor numérico interno si lo tiene.
        // Asumo que tu RatingEnum tiene un método como 'getValue()' o un campo 'value'.
        // Si tu RatingEnum es simplemente las constantes (ONE, TWO, etc.) sin un valor numérico,
        // tendrías que validar contra otras constantes del enum o convertirlo a ordinal().
        // EJEMPLO 1: Si RatingEnum tiene un método getValue() que devuelve int.
        if (Objects.isNull(request.getRating()) || request.getRating().getValue() < 1 || request.getRating().getValue() > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5.");
        }
        // O EJEMPLO 2: Si RatingEnum se compara por su posición ordinal (menos recomendado para valores explícitos).
        // if (Objects.isNull(request.getRating()) || request.getRating().ordinal() < 0 || request.getRating().ordinal() > 4) { // ordinal() es base 0
        //     throw new IllegalArgumentException("La calificación debe ser un valor válido del enum.");
        // }
        // --- FIN CORRECCIÓN ---


        // Busca el usuario por ID; si no se encuentra, lanza ResourceNotFoundException.
        // Se prefiere ResourceNotFoundException sobre RuntimeException genérica para semántica.
        User user = userRepository.findById(request.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getIdUser()));

        // Busca el producto por ID; si no se encuentra, lanza ResourceNotFoundException.
        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + request.getIdProducto()));

        // Construye la entidad Resena utilizando el Lombok Builder, lo que hace el código más legible.
        Resena resena = Resena.builder()
                .user(user)
                .producto(producto)
                .comentario(request.getComentario())
                .rating(request.getRating())
                .fecha(LocalDateTime.now()) // Establece la fecha y hora actuales para la reseña
                .build();

        resenaRepository.save(resena); // Guarda la nueva reseña en la base de datos

        return ResenaMapper.toDto(resena); // Convierte la entidad guardada a un DTO de respuesta
    }

    @Override
    @Transactional(readOnly = true) // Marca el método como transaccional de solo lectura para optimización
    public List<ResenaResponse> obtenerResenasPorProducto(Long idProducto) {
        // Valida que el ID del producto no sea nulo.
        if (Objects.isNull(idProducto)) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo para obtener reseñas.");
        }

        // Busca las reseñas por el ID del producto.
        // Luego, usa un stream para mapear cada entidad Resena a un DTO ResenaResponse.
        return resenaRepository.findByProducto_IdProducto(idProducto)
                .stream()
                .map(ResenaMapper::toDto) // Utiliza el método estático toDto del Mapper
                .collect(Collectors.toList()); // Recopila los DTOs en una lista
    }
}