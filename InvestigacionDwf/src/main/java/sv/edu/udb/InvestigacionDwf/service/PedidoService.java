package sv.edu.udb.InvestigacionDwf.service;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import sv.edu.udb.InvestigacionDwf.dto.request.PagoRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoItemDto;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PedidoService {
    PedidoResponse checkout(PedidoRequest req);
    PedidoResponse confirmar(Long idPedido);
    PedidoResponse pagar(Long idPedido, PagoRequest pagoReq);
    PedidoResponse iniciarEnvio(Long idPedido);
    PedidoResponse marcarEntregado(Long idPedido);
    PedidoResponse cancelar(Long idPedido, String motivo);
    PagedModel<PedidoResponse> findAllByUser(Long idUser, Pageable pageable);
    PagedModel<PedidoResponse> findAll(Pageable pageable);
    PedidoResponse getById(Long id);

    // --- NUEVOS MÉTODOS PARA EL DASHBOARD ---
    BigDecimal getGananciasTotales();
    BigDecimal getGananciasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);
    Map<String, Long> getProductosMasVendidos(int limit);

    List<PedidoItemDto> getItemsByPedidoId(Long id);

}

