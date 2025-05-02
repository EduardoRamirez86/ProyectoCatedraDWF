package sv.edu.udb.InvestigacionDwf.service;

import org.springframework.stereotype.Service;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;

import java.util.List;

@Service
public interface PedidoService {
    PedidoResponse checkout(PedidoRequest req);
    List<PedidoResponse> findAllByUser(Long idUser);
}
