package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.dto.response.CarritoItemResponse;
import sv.edu.udb.InvestigacionDwf.dto.response.CarritoResponse;

import java.util.List;

public interface CarritoService {
    CarritoResponse getOrCreateByUser(Long idUser);
    List<CarritoItemResponse> getItems(Long idCarrito);
}