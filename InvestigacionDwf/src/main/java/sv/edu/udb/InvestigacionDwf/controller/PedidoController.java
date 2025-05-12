// src/main/java/sv/edu/udb/InvestigacionDwf/controller/PedidoController.java
package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PedidoResponse> checkout(@RequestBody PedidoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.checkout(req));
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<PedidoResponse> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.confirmar(id));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<PedidoResponse> pagar(@PathVariable Long id, @RequestBody PagoRequest req) {
        return ResponseEntity.ok(pedidoService.pagar(id, req));
    }

    @PostMapping("/{id}/envio")
    public ResponseEntity<PedidoResponse> inicioEnvio(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.iniciarEnvio(id));
    }

    @PostMapping("/{id}/entregar")
    public ResponseEntity<PedidoResponse> entregar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.marcarEntregado(id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id, @RequestParam String motivo) {
        return ResponseEntity.ok(pedidoService.cancelar(id, motivo));
    }

    @GetMapping("/user/{idUser}")
    public ResponseEntity<List<PedidoResponse>> getByUser(@PathVariable Long idUser) {
        return ResponseEntity.ok(pedidoService.findAllByUser(idUser));
    }
    @GetMapping("/all")
    public ResponseEntity<List<PedidoResponse>> getAllPedidos() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

}


