package sv.edu.udb.InvestigacionDwf.service.impl;

import lombok.RequiredArgsConstructor; // Importar RequiredArgsConstructor de Lombok
import org.slf4j.Logger; // Importar Logger para el log
import org.slf4j.LoggerFactory; // Importar LoggerFactory para el log
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import sv.edu.udb.InvestigacionDwf.model.entity.Parametro;
import sv.edu.udb.InvestigacionDwf.repository.ParametroRepository;
import sv.edu.udb.InvestigacionDwf.service.ParametroService;

import java.math.BigDecimal;
import java.util.Objects; // Importar Objects para validaciones

@Service
@RequiredArgsConstructor // Genera un constructor con los campos 'final' para la inyección de dependencias
public class ParametroServiceImpl implements ParametroService {

    private static final Logger logger = LoggerFactory.getLogger(ParametroServiceImpl.class);

    private final ParametroRepository parametroRepository;

    // El constructor se genera automáticamente por @RequiredArgsConstructor, así que no es necesario escribirlo.
    // public ParametroServiceImpl(ParametroRepository parametroRepository) {
    //     this.parametroRepository = parametroRepository;
    // }

    /**
     * Obtiene un valor de parámetro de tipo BigDecimal por su clave.
     * Si la clave no se encuentra o el valor no es un número válido, devuelve un valor por defecto.
     *
     * @param clave La clave del parámetro a buscar.
     * @param valorPorDefecto El valor BigDecimal a devolver si el parámetro no se encuentra o no es válido.
     * @return El valor del parámetro como BigDecimal, o el valor por defecto.
     * @throws IllegalArgumentException Si la clave es nula o vacía.
     */
    @Override
    @Transactional(readOnly = true) // Este método solo lee datos
    public BigDecimal obtenerBigDecimal(String clave, BigDecimal valorPorDefecto) {
        // Validar la clave de entrada
        if (Objects.isNull(clave) || clave.isBlank()) {
            throw new IllegalArgumentException("La clave del parámetro no puede ser nula o vacía.");
        }
        // Validar que el valor por defecto no sea nulo
        if (Objects.isNull(valorPorDefecto)) {
            logger.warn("Se ha proporcionado un valorPorDefecto nulo para la clave '{}'.", clave);
            // Podrías lanzar una excepción o asignar un valor por defecto interno si lo consideras un error grave.
            // Por ahora, el .orElse(valorPorDefecto) lo manejaría si fuera necesario.
        }


        return parametroRepository.findByClave(clave)
                .map(parametro -> {
                    try {
                        // Intentar convertir el valor String del parámetro a BigDecimal
                        return new BigDecimal(parametro.getValor());
                    } catch (NumberFormatException e) {
                        // Si la conversión falla, registrar la advertencia y devolver el valor por defecto
                        logger.warn("El valor del parámetro para la clave '{}' ('{}') no es un número válido. Se usará el valor por defecto: {}",
                                clave, parametro.getValor(), valorPorDefecto, e);
                        return valorPorDefecto;
                    }
                })
                .orElseGet(() -> {
                    // Si el parámetro no se encuentra, registrar la advertencia y devolver el valor por defecto
                    logger.warn("Parámetro no encontrado para la clave '{}'. Se usará el valor por defecto: {}", clave, valorPorDefecto);
                    return valorPorDefecto;
                });
    }

    /**
     * Obtiene un valor de parámetro de tipo String por su clave.
     * Si la clave no se encuentra, devuelve un valor por defecto.
     *
     * @param clave La clave del parámetro a buscar.
     * @param valorPorDefecto El valor String a devolver si el parámetro no se encuentra.
     * @return El valor del parámetro como String, o el valor por defecto.
     * @throws IllegalArgumentException Si la clave es nula o vacía.
     */
    @Override
    @Transactional(readOnly = true) // Este método solo lee datos
    public String obtenerString(String clave, String valorPorDefecto) {
        // Validar la clave de entrada
        if (Objects.isNull(clave) || clave.isBlank()) {
            throw new IllegalArgumentException("La clave del parámetro no puede ser nula o vacía.");
        }
        // Validar que el valor por defecto no sea nulo (aunque un String nulo es más común como valor por defecto)
        if (Objects.isNull(valorPorDefecto)) {
            logger.debug("Se ha proporcionado un valorPorDefecto nulo para la clave '{}'.", clave);
        }

        return parametroRepository.findByClave(clave)
                .map(Parametro::getValor) // Si el parámetro se encuentra, mapear a su valor
                .orElseGet(() -> {
                    // Si el parámetro no se encuentra, registrar la advertencia y devolver el valor por defecto
                    logger.warn("Parámetro no encontrado para la clave '{}'. Se usará el valor por defecto: {}", clave, valorPorDefecto);
                    return valorPorDefecto;
                });
    }
}

