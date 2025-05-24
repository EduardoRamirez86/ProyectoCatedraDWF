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
import java.util.Objects; // Importa Objects para las validaciones de nulidad

@Service
@RequiredArgsConstructor // Genera un constructor con todos los campos 'final', inyectando dependencias.
public class CuponServiceImpl implements CuponService {

    private final CuponRepository cuponRepository;
    private final ParametroService parametroService;

    @Override
    @Transactional // Marca el método como transaccional para asegurar la persistencia del cupón.
    public Cupon generateCouponForUser(User user) {
        // Valida que el usuario no sea nulo, ya que es esencial para el cupón.
        if (Objects.isNull(user) || Objects.isNull(user.getIdUser())) {
            throw new IllegalArgumentException("El usuario para generar el cupón no puede ser nulo o no tener ID.");
        }

        // Genera un código único para el cupón usando UUID.
        String codigo = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Obtener porcentaje de descuento desde parámetros (BigDecimal)
        // Usa parametroService para obtener el valor dinámicamente, con un valor por defecto.
        BigDecimal descuentoBD = parametroService.obtenerBigDecimal("descuento_cupon", new BigDecimal("15.0"));
        double porcentaje = descuentoBD.doubleValue();

        // Construye la entidad Cupon utilizando el Lombok Builder, mejorando la legibilidad.
        Cupon cupon = Cupon.builder()
                .user(user) // Asocia el cupón al usuario proporcionado.
                .codigo(codigo)
                .porcentajeDescuento(porcentaje)
                .usado(false) // Los cupones recién generados no están usados.
                .fechaCreacion(LocalDateTime.now()) // Establece la fecha de creación actual.
                // La fecha de expiración se calcula a 30 días a partir de la fecha de creación.
                .fechaExpiracion(LocalDateTime.now().plusDays(30))
                .build();

        return cuponRepository.save(cupon); // Guarda el nuevo cupón en la base de datos.
    }

    @Override
    @Transactional(readOnly = true) // Marca el método como transaccional de solo lectura para optimización.
    public boolean validateCoupon(String codigo, User user) {
        // Valida que el código y el usuario no sean nulos para evitar NPEs.
        if (Objects.isNull(codigo) || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del cupón no puede ser nulo o vacío para la validación.");
        }
        if (Objects.isNull(user) || Objects.isNull(user.getIdUser())) {
            throw new IllegalArgumentException("El usuario para validar el cupón no puede ser nulo o no tener ID.");
        }

        // Busca el cupón por su código.
        // Si se encuentra, verifica múltiples condiciones para su validez:
        // 1. Que el cupón pertenezca al usuario especificado.
        // 2. Que el cupón no haya sido usado.
        // 3. Que el cupón no haya expirado.
        return cuponRepository.findByCodigo(codigo)
                .map(c -> Objects.nonNull(c.getUser()) && c.getUser().getIdUser().equals(user.getIdUser()) // Asegura que el usuario del cupón no sea nulo antes de comparar ID
                        && !c.isUsado()
                        && c.getFechaExpiracion().isAfter(LocalDateTime.now()))
                .orElse(false); // Si el cupón no se encuentra, se considera inválido.
    }

    @Override
    @Transactional // Marca el método como transaccional para asegurar la actualización del cupón.
    public void redeemCoupon(String codigo, User user) {
        // Valida que el código y el usuario no sean nulos.
        if (Objects.isNull(codigo) || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del cupón no puede ser nulo o vacío para la redención.");
        }
        if (Objects.isNull(user) || Objects.isNull(user.getIdUser())) {
            throw new IllegalArgumentException("El usuario para redimir el cupón no puede ser nulo o no tener ID.");
        }

        // Busca el cupón por su código; si no se encuentra, lanza ResourceNotFoundException.
        Cupon cupon = cuponRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado con código: " + codigo));

        // Realiza múltiples validaciones de estado del cupón antes de redimirlo.
        if (Objects.isNull(cupon.getUser()) || !cupon.getUser().getIdUser().equals(user.getIdUser())) { // Vuelve a verificar que el usuario del cupón no sea nulo
            throw new IllegalStateException("El cupón no pertenece a este usuario.");
        }
        if (cupon.isUsado()) {
            throw new IllegalStateException("El cupón ya ha sido usado.");
        }
        if (cupon.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("El cupón ha expirado.");
        }

        cupon.setUsado(true); // Marca el cupón como usado.
        cuponRepository.save(cupon); // Guarda el estado actualizado del cupón en la base de datos.
    }
}


