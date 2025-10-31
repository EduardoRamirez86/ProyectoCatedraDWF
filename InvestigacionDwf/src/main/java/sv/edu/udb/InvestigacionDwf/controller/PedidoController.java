package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.dto.request.PagoRequest;
import sv.edu.udb.InvestigacionDwf.dto.request.PedidoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoItemDto;
import sv.edu.udb.InvestigacionDwf.dto.response.PedidoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.service.PedidoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<Pedido> assembler
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return pedidoService.findAllByUser(idUser, pageable);
    }

    @GetMapping("/all")
    public PagedModel<PedidoResponse> getAllPedidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<Pedido> assembler
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return pedidoService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public PedidoResponse getById(@PathVariable Long id) {
        return pedidoService.getById(id);
    }

    // --- NUEVOS ENDPOINTS PARA EL DASHBOARD DE GANANCIAS ---

    @GetMapping("/dashboard/ganancias/totales")
    public BigDecimal getGananciasTotales() {
        return pedidoService.getGananciasTotales();
    }

    @GetMapping("/dashboard/ganancias/periodo")
    public BigDecimal getGananciasPorPeriodo(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return pedidoService.getGananciasPorPeriodo(fechaInicio, fechaFin);
    }

    @GetMapping("/dashboard/productos-mas-vendidos")
    public Map<String, Long> getProductosMasVendidos(@RequestParam(defaultValue = "5") int limit) {
        return pedidoService.getProductosMasVendidos(limit);
    }

    /** * Endpoint para obtener todos los items (productos) de un pedido específico.
     * ESTE ES EL ENDPOINT QUE NECESITAMOS EN EL FRONTEND.
     *
     * @param id El ID del pedido.
     * @return Una lista de PedidoItemDto con los detalles de cada producto en el pedido.
     */
    @GetMapping("/{id}/items")
    public ResponseEntity<List<PedidoItemDto>> getItemsByPedidoId(@PathVariable Long id) {
        // Llama a un nuevo método en tu PedidoService para obtener los items.
        List<PedidoItemDto> items = pedidoService.getItemsByPedidoId(id);
        return ResponseEntity.ok(items);
    }

}




