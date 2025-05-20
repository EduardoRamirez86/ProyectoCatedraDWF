// src/main/java/sv/edu/udb/InvestigacionDwf/repository/ProductoRepository.java
package sv.edu.udb.InvestigacionDwf.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.tipoProducto")
    Page<Producto> findAll(Pageable pageable);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.tipoProducto WHERE p.idProducto = :id")
    Optional<Producto> findByIdWithTipoProducto(Long id);

    Optional<Producto> findByNombre(String nombre);

    List<Producto> findTop5ByOrderByFechaCreacionDesc();



    boolean existsById(Long id);
}



