package sv.edu.udb.InvestigacionDwf.service;

import sv.edu.udb.InvestigacionDwf.dto.request.PagoRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;

import java.util.List;

public interface PedidoService {
    PedidoResponse checkout(PedidoRequest req);
    PedidoResponse confirmar(Long idPedido);
    PedidoResponse pagar(Long idPedido, PagoRequest pagoReq);
    PedidoResponse iniciarEnvio(Long idPedido);
    PedidoResponse marcarEntregado(Long idPedido);
    PedidoResponse cancelar(Long idPedido, String motivo);
    List<PedidoResponse> findAllByUser(Long idUser);
    List<PedidoResponse> findAll();
}

