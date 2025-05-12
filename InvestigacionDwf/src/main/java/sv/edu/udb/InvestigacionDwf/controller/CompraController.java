package sv.edu.udb.InvestigacionDwf.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.edu.udb.InvestigacionDwf.model.entity.Compra;
import sv.edu.udb.InvestigacionDwf.repository.CompraRepository;

@RestController
@RequestMapping("/auth/compras") // Base path remains the same
public class CompraController {

    private final CompraRepository compraRepository;

    public CompraController(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    @GetMapping
    public List<Compra> getAllCompras() {
        return compraRepository.findAll();
    }

    @PostMapping
    public Compra createCompra(@RequestBody Compra compra) {
        return compraRepository.save(compra);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Compra> updateCompra(@PathVariable Long id, @RequestBody Compra compraDetails) {
        return compraRepository.findById(id)
                .map(compra -> {
                    compra.setFechaCompra(compraDetails.getFechaCompra());
                    compra.setCantidad(compraDetails.getCantidad());
                    compra.setTotal(compraDetails.getTotal());
                    return ResponseEntity.ok(compraRepository.save(compra));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompra(@PathVariable Long id) {
        return compraRepository.findById(id)
                .map(compra -> {
                    compraRepository.delete(compra);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
