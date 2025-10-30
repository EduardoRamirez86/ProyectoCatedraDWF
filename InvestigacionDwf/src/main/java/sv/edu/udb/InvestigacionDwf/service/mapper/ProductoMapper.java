package sv.edu.udb.InvestigacionDwf.service.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto; // <-- Asegúrate de importar TipoProducto
import sv.edu.udb.InvestigacionDwf.repository.TipoProductoRepository;
import jakarta.persistence.EntityNotFoundException; // <-- Importa esto
import java.util.List;
import java.util.Objects;


@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ProductoMapper {

    // La inyección de campo @Autowired aquí está bien
    @Autowired
    protected TipoProductoRepository tipoProductoRepository;

    /**
     * Mapea un ProductoRequest a una entidad Producto.
     * El 'tipoProducto' se maneja en el método @AfterMapping.
     */
    // --- 👇 BORRAMOS LA 'expression' DE AQUÍ 👇 ---
    @Mapping(target = "tipoProducto", ignore = true) // Ignoramos el mapeo automático
    public abstract Producto toEntity(ProductoRequest request);

    /**
     * Mapea una entidad Producto a un ProductoResponse.
     * Extrae el ID y el nombre del TipoProducto.
     */
    @Mapping(source = "tipoProducto.idTipoProducto", target = "idTipoProducto")
    // --- 👇 CORREGIDO: Tu TipoProducto usa 'tipo', no 'nombreTipo' ---
    @Mapping(source = "tipoProducto.tipo", target = "nombreTipo")
    public abstract ProductoResponse toResponse(Producto producto);


    public abstract List<ProductoResponse> toResponseList(List<Producto> productos);

    /**
     * Actualiza una entidad existente desde un request.
     * El 'tipoProducto' se maneja en el método @AfterMapping.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    // --- 👇 BORRAMOS LA 'expression' DE AQUÍ 👇 ---
    @Mapping(target = "tipoProducto", ignore = true)
    public abstract void updateEntityFromRequest(ProductoRequest request, @MappingTarget Producto producto);


    // --- 👇 MÉTODO AÑADIDO PARA ASIGNAR TipoProducto DESPUÉS DEL MAPEO 👇 ---

    /**
     * Este método se llama automáticamente después de 'toEntity' o 'updateEntityFromRequest'.
     * Asegura que 'tipoProductoRepository' no sea nulo y asigna la referencia.
     */
    @AfterMapping
    protected void linkTipoProducto(ProductoRequest request, @MappingTarget Producto producto) {
        // 1. Comprobación de Nulo (Imagen) - ¡Buena práctica!
        // Si el request no trae imagen, no intentes ponerla (para que no borre la existente en un UPDATE)
        if (request.getImagen() != null) {
            if (request.getImagen().isEmpty()) {
                producto.setImagen(null); // Si envía string vacío, poner null
            } else {
                producto.setImagen(request.getImagen());
            }
        }

        // 2. Asignación de TipoProducto (La causa del NPE)
        if (request.getIdTipoProducto() != null) {
            if (tipoProductoRepository == null) {
                // Esto no debería pasar si el mapper es un Bean, pero es una buena guarda
                throw new IllegalStateException("TipoProductoRepository no fue inyectado en ProductoMapper");
            }

            // Busca la referencia del TipoProducto usando el ID del request
            TipoProducto tipo = tipoProductoRepository.findById(request.getIdTipoProducto())
                    .orElseThrow(() -> new EntityNotFoundException("TipoProducto no encontrado con id: " + request.getIdTipoProducto()));

            // Asigna el objeto TipoProducto completo a la entidad Producto
            producto.setTipoProducto(tipo);
        }
        // Si no viene idTipoProducto en el request, MapStruct (con IGNORE) no tocará
        // el tipoProducto existente, lo cual es correcto para un update parcial.
        // Para 'toEntity' (creación), tu @JoinColumn(nullable=false) en Producto.java
        // hará que la base de datos falle si es nulo, lo cual es la validación correcta.
    }
}