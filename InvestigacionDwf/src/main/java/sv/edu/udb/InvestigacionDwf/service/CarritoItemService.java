package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;

import java.util.List;

public interface CarritoItemService {
    CarritoItemResponse addItem(CarritoItemRequest req);
    void removeItem(Long idCarritoItem);
    List<CarritoItemResponse> getAllItems();
    CarritoItemResponse getItemById(Long idCarritoItem);
    List<CarritoItemResponse> getItemsByCarritoId(Long idCarrito);
    CarritoItemResponse updateItem(Long id, CarritoItemRequest request);
}

