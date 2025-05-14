// src/main/java/sv/edu/udb/InvestigacionDwf/controller/PedidoController.java
// src/main/java/sv/edu/udb/InvestigacionDwf/controller/PedidoController.java
package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.PagoRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.service.PedidoService;

import java.util.List;

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
    public List<PedidoResponse> getByUser(@PathVariable Long idUser) {
        return pedidoService.findAllByUser(idUser);
    }

    @GetMapping("/all")
    public List<PedidoResponse> getAllPedidos() {
        return pedidoService.findAll();
    }
}






