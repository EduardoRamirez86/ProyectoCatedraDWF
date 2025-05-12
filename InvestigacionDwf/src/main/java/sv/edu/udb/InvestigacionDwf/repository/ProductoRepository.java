// src/main/java/sv/edu/udb/InvestigacionDwf/repository/ProductoRepository.java
package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.tipoProducto")
    List<Producto> findAll();

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.tipoProducto WHERE p.idProducto = :id")
    Optional<Producto> findByIdWithTipoProducto(Long id);

    List<Producto> findByTipoProducto_IdTipoProductoIn(List<Long> tipoIds);

    boolean existsById(Long id);

    // ← metodo para ingresar producto
    Optional<Producto> findByNombre(String nombre);
}



