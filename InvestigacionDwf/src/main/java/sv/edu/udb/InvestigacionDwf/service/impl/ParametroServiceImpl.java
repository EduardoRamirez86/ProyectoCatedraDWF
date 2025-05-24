package sv.edu.udb.InvestigacionDwf.service.impl;

import sv.edu.udb.InvestigacionDwf.model.entity.Parametro;
import sv.edu.udb.InvestigacionDwf.repository.ParametroRepository;
import sv.edu.udb.InvestigacionDwf.service.ParametroService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ParametroServiceImpl implements ParametroService {

    private final ParametroRepository parametroRepository;

    public ParametroServiceImpl(ParametroRepository parametroRepository) {
        this.parametroRepository = parametroRepository;
    }

    @Override
    public BigDecimal obtenerBigDecimal(String clave, BigDecimal valorPorDefecto) {
        return parametroRepository.findByClave(clave)
                .map(parametro -> {
                    try {
                        return new BigDecimal(parametro.getValor());
                    } catch (NumberFormatException e) {
                        return valorPorDefecto;
                    }
                })
                .orElse(valorPorDefecto);
    }

    @Override
    public String obtenerString(String clave, String valorPorDefecto) {
        return parametroRepository.findByClave(clave)
                .map(Parametro::getValor)
                .orElse(valorPorDefecto);
    }
}

