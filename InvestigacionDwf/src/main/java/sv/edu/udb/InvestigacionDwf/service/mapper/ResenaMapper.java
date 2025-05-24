package sv.edu.udb.InvestigacionDwf.service.mapper;

import sv.edu.udb.InvestigacionDwf.dto.response.ResenaResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Resena;

import java.util.Objects; // Importa Objects para las validaciones de nulidad

public class ResenaMapper {

    /**
     * Convierte una entidad Resena a un DTO de respuesta (ResenaResponse).
     * Se asegura de que la entidad y sus relaciones clave (Usuario, Producto) no sean nulas
     * antes de intentar mapear sus propiedades.
     * Utiliza el patrón Builder de Lombok para una creación de objeto más legible.
     *
     * @param resena La entidad Resena a convertir.
     * @return Un DTO ResenaResponse con los datos de la reseña.
     * @throws IllegalArgumentException si la entidad Resena o sus relaciones esenciales (User, Producto) son nulas.
     */
    public static ResenaResponse toDto(Resena resena) {
        // Valida que la entidad Resena no sea nula.
        if (Objects.isNull(resena)) {
            throw new IllegalArgumentException("La entidad Resena no puede ser nula para mapear a DTO.");
        }
        // Valida que las relaciones clave (User y Producto) no sean nulas, ya que se acceden sus propiedades.
        if (Objects.isNull(resena.getUser())) {
            throw new IllegalArgumentException("La entidad Resena debe tener un Usuario asociado para mapear a DTO.");
        }
        if (Objects.isNull(resena.getProducto())) {
            throw new IllegalArgumentException("La entidad Resena debe tener un Producto asociado para mapear a DTO.");
        }

        return ResenaResponse.builder()
                .idResena(resena.getIdResena())
                .username(resena.getUser().getUsername()) // Acceso seguro después de la validación
                .productoNombre(resena.getProducto().getNombre()) // Acceso seguro después de la validación
                .comentario(resena.getComentario())
                .fecha(resena.getFecha())
                .rating(resena.getRating())
                .build();
    }
}


