package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.dto.request.TipoProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.TipoProductoResponse;

import java.util.List;

public interface TipoProductoService {
    TipoProductoResponse crearTipoProducto(TipoProductoRequest requestDto);
    List<TipoProductoResponse> listarTipoProductos();
    TipoProductoResponse obtenerTipoProductoPorId(Long id);
    TipoProductoResponse actualizarTipoProducto(Long id, TipoProductoRequest requestDto);
    void eliminarTipoProducto(Long id);
}

