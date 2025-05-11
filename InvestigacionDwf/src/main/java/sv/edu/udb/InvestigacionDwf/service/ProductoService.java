// src/main/java/sv/edu/udb/InvestigacionDwf/service/ProductoService.java
package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.dto.request.ProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ProductoResponse;

import java.util.List;

public interface ProductoService {
    List<ProductoResponse> findAll();
    ProductoResponse findById(Long id);
    ProductoResponse save(ProductoRequest request);
    ProductoResponse update(Long id, ProductoRequest request);
    void delete(Long id);
    boolean existsById(Long id);

    List<ProductoResponse> findRecommendedByUser(Long idUser);
}


