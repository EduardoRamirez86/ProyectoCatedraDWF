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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoProductoServiceImpl implements TipoProductoService {

    private final TipoProductoRepository repository;
    private final TipoProductoMapper mapper;

    @Override
    public TipoProductoResponse crearTipoProducto(TipoProductoRequest request) {
        TipoProducto tipoProducto = mapper.toEntity(request);
        return mapper.toResponse(tipoProducto);
    }

    @Override
    public List<TipoProductoResponse> listarTipoProductos() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TipoProductoResponse obtenerTipoProductoPorId(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RuntimeException("TipoProducto no encontrado"));
    }

    @Override
    public TipoProductoResponse actualizarTipoProducto(Long id, TipoProductoRequest request) {
        TipoProducto existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TipoProducto no encontrado"));

        existente.setTipo(request.getTipo());
        existente.setDescripcion(request.getDescripcion());

        return mapper.toResponse(repository.save(existente));
    }

    @Override
    public void eliminarTipoProducto(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("TipoProducto no encontrado");
        }
        repository.deleteById(id);
    }
}


