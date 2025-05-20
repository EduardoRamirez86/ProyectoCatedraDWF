// src/main/java/sv/edu/udb/InvestigacionDwf/service/ProductoService.java
package sv.edu.udb.InvestigacionDwf.service;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;

import java.util.List;

public interface ProductoService {
    ProductoResponse create(ProductoRequest req);
    ProductoResponse update(Long id, ProductoRequest req);
    void delete(Long id);
    PagedModel<ProductoResponse> findAll(Pageable pageable);
    ProductoResponse getById(Long id);


    List<ProductoResponse> findRecommendedByUser(Long idUser);
}

