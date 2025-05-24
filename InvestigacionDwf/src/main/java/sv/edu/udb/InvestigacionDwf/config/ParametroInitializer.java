package sv.edu.udb.InvestigacionDwf.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sv.edu.udb.InvestigacionDwf.model.entity.Parametro;
import sv.edu.udb.InvestigacionDwf.repository.ParametroRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ParametroInitializer {

    private final ParametroRepository parametroRepository;

    @Value("${app.shipping.cost}")
    private BigDecimal defaultShippingCost;

    @Value("${app.coupon.discount}")
    private BigDecimal defaultCouponDiscount;

    public ParametroInitializer(ParametroRepository parametroRepository) {
        this.parametroRepository = parametroRepository;
    }

    @PostConstruct
    public void init() {
        // Definir parámetros y valores por defecto
        List<Parametro> defaults = List.of(
                Parametro.builder()
                        .clave("costo_envio")
                        .valor(defaultShippingCost.toString())
                        .descripcion("Costo fijo de envío para pedidos (USD)")
                        .build(),
                Parametro.builder()
                        .clave("descuento_cupon")
                        .valor(defaultCouponDiscount.toString())
                        .descripcion("Descuento por defecto para cupones (%)")
                        .build()
        );

        for (Parametro def : defaults) {
            parametroRepository.findByClave(def.getClave())
                    .ifPresentOrElse(existing -> {
                        // Actualiza el valor si ha cambiado
                        existing.setValor(def.getValor());
                        parametroRepository.save(existing);
                    }, () -> {
                        // Inserta nuevo parámetro
                        parametroRepository.save(def);
                    });
        }
    }
}
