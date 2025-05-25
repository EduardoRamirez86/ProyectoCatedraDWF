package sv.edu.udb.InvestigacionDwf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sv.edu.udb.InvestigacionDwf.model.entity.Parametro;
import sv.edu.udb.InvestigacionDwf.repository.ParametroRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth/parametros")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ParametroController {

    private final ParametroRepository parametroRepository;

    // Obtener todos los parámetros
    @GetMapping("/all")
    public List<Parametro> getAllParametros() {
        return parametroRepository.findAll();
    }

    // Obtener parámetro por clave
    @GetMapping("/clave/{clave}")
    public Parametro getByClave(@PathVariable String clave) {
        return parametroRepository.findByClave(clave)
                .orElse(null); // O puedes lanzar una excepción personalizada si lo prefieres
    }

    // Obtener parámetro por ID
    @GetMapping("/{id}")
    public Parametro getById(@PathVariable Long id) {
        return parametroRepository.findById(id)
                .orElse(null);
    }

    // Actualizar un parámetro (solo el valor)
    @PutMapping("/{id}")
    public Parametro updateParametro(@PathVariable Long id, @RequestBody Parametro request) {
        Optional<Parametro> parametroOptional = parametroRepository.findById(id);
        if (parametroOptional.isPresent()) {
            Parametro parametro = parametroOptional.get();
            parametro.setValor(request.getValor());
            return parametroRepository.save(parametro);
        }
        return null; // También puedes manejar el caso no encontrado con una excepción si lo deseas
    }
}
