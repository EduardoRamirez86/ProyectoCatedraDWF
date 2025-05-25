// src/main/java/sv/edu/udb/InvestigacionDwf/controller/PedidoController.java
package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.PagoRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.service.PedidoService;

@RestController
@RequestMapping("/auth/pedido")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse checkout(@RequestBody PedidoRequest req) {
        return pedidoService.checkout(req);
    }

    @PostMapping("/{id}/confirmar")
    public PedidoResponse confirmar(@PathVariable Long id) {
        return pedidoService.confirmar(id);
    }

    @PostMapping("/{id}/pagar")
    public PedidoResponse pagar(@PathVariable Long id, @RequestBody PagoRequest req) {
        return pedidoService.pagar(id, req);
    }

    @PostMapping("/{id}/envio")
    public PedidoResponse inicioEnvio(@PathVariable Long id) {
        return pedidoService.iniciarEnvio(id);
    }

    @PostMapping("/{id}/entregar")
    public PedidoResponse entregar(@PathVariable Long id) {
        return pedidoService.marcarEntregado(id);
    }

    @PostMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id, @RequestParam String motivo) {
        return pedidoService.cancelar(id, motivo);
    }

    @GetMapping("/user/{idUser}")
    public PagedModel<PedidoResponse> getByUser(
            @PathVariable Long idUser,
            Pageable pageable,
            PagedResourcesAssembler<Pedido> assembler
    ) {
        // NOTA: aquí el assembler debe coincidir en genérico con el ServiceImpl
        return pedidoService.findAllByUser(idUser, pageable);
    }

    @GetMapping("/all")
    public PagedModel<PedidoResponse> getAllPedidos(
            Pageable pageable,
            PagedResourcesAssembler<Pedido> assembler
    ) {
        return pedidoService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public PedidoResponse getById(@PathVariable Long id) {
        return pedidoService.getById(id);
    }
}






