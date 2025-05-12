// RopaController.java
package sv.edu.udb.InvestigacionDwf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.model.entity.Ropa;
import sv.edu.udb.InvestigacionDwf.service.RopaService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("auth/ropa")
@CrossOrigin(origins = "http://localhost:3000")
public class RopaController {

    private final RopaService ropaService;

    @Autowired
    public RopaController(RopaService ropaService) {
        this.ropaService = ropaService;
    }

    @GetMapping
    public List<Ropa> getAllRopa() {
        return ropaService.getAllRopa();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ropa> getRopaById(@PathVariable Long id) {
        Optional<Ropa> ropa = ropaService.getRopaById(id);
        return ropa.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ropa createRopa(@RequestBody Ropa ropa) {
        return ropaService.createRopa(ropa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ropa> updateRopa(@PathVariable Long id, @RequestBody Ropa ropaDetails) {
        try {
            Ropa updatedRopa = ropaService.updateRopa(id, ropaDetails);
            return ResponseEntity.ok(updatedRopa);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRopa(@PathVariable Long id) {
        try {
            ropaService.deleteRopa(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
