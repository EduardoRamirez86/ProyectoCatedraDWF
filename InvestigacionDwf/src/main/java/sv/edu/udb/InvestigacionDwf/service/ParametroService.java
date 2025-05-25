package sv.edu.udb.InvestigacionDwf.service;

import java.math.BigDecimal;

public interface ParametroService {
    BigDecimal obtenerBigDecimal(String clave, BigDecimal valorPorDefecto);
    String obtenerString(String clave, String valorPorDefecto);
}


