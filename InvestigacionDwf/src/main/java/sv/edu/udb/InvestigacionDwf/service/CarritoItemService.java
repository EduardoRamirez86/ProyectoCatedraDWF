package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.dto.request.CarritoItemRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;

public interface CarritoItemService {
    CarritoItemResponse addItem(CarritoItemRequest req);
    void removeItem(Long idCarritoItem);
}
