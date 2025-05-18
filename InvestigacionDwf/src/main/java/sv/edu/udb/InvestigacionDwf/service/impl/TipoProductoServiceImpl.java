package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.edu.udb.InvestigacionDwf.dto.request.TipoProductoRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.TipoProductoResponse;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;
import sv.edu.udb.InvestigacionDwf.repository.TipoProductoRepository;
import sv.edu.udb.InvestigacionDwf.service.TipoProductoService;
import sv.edu.udb.InvestigacionDwf.service.mapper.TipoProductoMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoProductoServiceImpl implements TipoProductoService {

    private final TipoProductoRepository repository;
    private final TipoProductoMapper mapper;

    @Override
    public TipoProductoResponse crearTipoProducto(TipoProductoRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getTipo()) || Objects.isNull(request.getDescripcion())) {
            throw new IllegalArgumentException("Datos del tipo de producto inválidos");
        }

        TipoProducto tipoProducto = mapper.toEntity(request);
        return mapper.toResponse(repository.save(tipoProducto));
    }

    @Override
    public List<TipoProductoResponse> listarTipoProductos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TipoProductoResponse obtenerTipoProductoPorId(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID del tipo de producto no puede ser nulo");
        }

        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("TipoProducto no encontrado"));
    }

    @Override
    public TipoProductoResponse actualizarTipoProducto(Long id, TipoProductoRequest request) {
        if (Objects.isNull(id) || Objects.isNull(request) ||
                Objects.isNull(request.getTipo()) || Objects.isNull(request.getDescripcion())) {
            throw new IllegalArgumentException("Datos inválidos para la actualización del tipo de producto");
        }

        TipoProducto existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoProducto no encontrado"));

        existente.setTipo(request.getTipo());
        existente.setDescripcion(request.getDescripcion());

        return mapper.toResponse(repository.save(existente));
    }

    @Override
    public void eliminarTipoProducto(Long id) {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }

        if (!repository.existsById(id)) {
            throw new RuntimeException("TipoProducto no encontrado");
        }

        repository.deleteById(id);
    }
}



