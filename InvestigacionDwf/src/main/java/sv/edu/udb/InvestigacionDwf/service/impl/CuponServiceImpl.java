package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.Cupon;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.repository.CuponRepository;
import sv.edu.udb.InvestigacionDwf.service.CuponService;
import sv.edu.udb.InvestigacionDwf.service.ParametroService;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CuponServiceImpl implements CuponService {

    private final CuponRepository cuponRepository;
    private final ParametroService parametroService;

    @Override
    @Transactional
    public Cupon generateCouponForUser(User user) {
        String codigo = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Obtener porcentaje de descuento desde parámetros (BigDecimal)
        BigDecimal descuentoBD = parametroService.obtenerBigDecimal("descuento_cupon", new BigDecimal("15.0"));
        double porcentaje = descuentoBD.doubleValue();

        Cupon cupon = Cupon.builder()
                .user(user)
                .codigo(codigo)
                .porcentajeDescuento(porcentaje)
                .usado(false)
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusDays(30))
                .build();
        return cuponRepository.save(cupon);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateCoupon(String codigo, User user) {
        return cuponRepository.findByCodigo(codigo)
                .map(c -> c.getUser().getIdUser().equals(user.getIdUser())
                        && !c.isUsado()
                        && c.getFechaExpiracion().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Override
    @Transactional
    public void redeemCoupon(String codigo, User user) {
        Cupon cupon = cuponRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado: " + codigo));

        if (!cupon.getUser().getIdUser().equals(user.getIdUser())) {
            throw new IllegalStateException("El cupón no pertenece a este usuario");
        }
        if (cupon.isUsado()) {
            throw new IllegalStateException("El cupón ya ha sido usado");
        }
        if (cupon.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("El cupón ha expirado");
        }

        cupon.setUsado(true);
        cuponRepository.save(cupon);
    }
}


