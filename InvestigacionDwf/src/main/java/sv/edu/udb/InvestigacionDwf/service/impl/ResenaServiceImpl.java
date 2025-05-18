package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sv.edu.udb.InvestigacionDwf.dto.request.ResenaRequest;
import sv.edu.udb.InvestigacionDwf.dto.response.ResenaResponse;
import sv.edu.udb.InvestigacionDwf.service.mapper.ResenaMapper;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.Resena;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.repository.ResenaRepository;
import sv.edu.udb.InvestigacionDwf.repository.UserRepository;
import sv.edu.udb.InvestigacionDwf.service.ResenaService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResenaServiceImpl implements ResenaService {

    private final ResenaRepository resenaRepository;
    private final UserRepository userRepository;
    private final ProductoRepository productoRepository;

    @Override
    public ResenaResponse crearResena(ResenaRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getIdUser()) || Objects.isNull(request.getIdProducto())) {
            throw new IllegalArgumentException("Datos de reseña inválidos");
        }

        User user = userRepository.findById(request.getIdUser())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Resena resena = Resena.builder()
                .user(user)
                .producto(producto)
                .comentario(request.getComentario())
                .rating(request.getRating())
                .build();

        resenaRepository.save(resena);

        return ResenaMapper.toDto(resena);
    }

    @Override
    public List<ResenaResponse> obtenerResenasPorProducto(Long idProducto) {
        if (Objects.isNull(idProducto)) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }

        return resenaRepository.findByProducto_IdProducto(idProducto)
                .stream()
                .map(ResenaMapper::toDto)
                .collect(Collectors.toList());
    }
}

