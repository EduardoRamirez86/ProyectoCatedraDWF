package sv.edu.udb.InvestigacionDwf.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import sv.edu.udb.InvestigacionDwf.exception.ResourceNotFoundException;
import sv.edu.udb.InvestigacionDwf.model.entity.Cupon;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.repository.CuponRepository;
import sv.edu.udb.InvestigacionDwf.service.ParametroService;
import sv.edu.udb.InvestigacionDwf.service.impl.CuponServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CuponServiceImplTest {

    @InjectMocks
    private CuponServiceImpl service;

    @Mock
    private CuponRepository cuponRepository;
    @Mock
    private ParametroService parametroService;

    @Test
    void generateCouponForUser_shouldSaveAndReturn() {
        User user = new User();
        user.setIdUser(1L);
        when(parametroService.obtenerBigDecimal(anyString(), any())).thenReturn(new BigDecimal("10.0"));
        Cupon cupon = new Cupon();
        when(cuponRepository.save(any(Cupon.class))).thenReturn(cupon);

        Cupon result = service.generateCouponForUser(user);

        assertEquals(cupon, result);
        verify(cuponRepository).save(any(Cupon.class));
    }

    @Test
    void generateCouponForUser_shouldThrowIfUserNull() {
        assertThrows(IllegalArgumentException.class, () -> service.generateCouponForUser(null));
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> service.generateCouponForUser(user));
    }

    @Test
    void validateCoupon_shouldReturnTrueIfValid() {
        User user = new User();
        user.setIdUser(1L);
        Cupon cupon = new Cupon();
        cupon.setUser(user);
        cupon.setUsado(false);
        cupon.setFechaExpiracion(LocalDateTime.now().plusDays(1));
        when(cuponRepository.findByCodigo("CODE")).thenReturn(Optional.of(cupon));

        assertTrue(service.validateCoupon("CODE", user));
    }

    @Test
    void validateCoupon_shouldReturnFalseIfNotFound() {
        User user = new User();
        user.setIdUser(1L);
        when(cuponRepository.findByCodigo("CODE")).thenReturn(Optional.empty());

        assertFalse(service.validateCoupon("CODE", user));
    }

    @Test
    void validateCoupon_shouldThrowIfInvalidArgs() {
        User user = new User();
        user.setIdUser(1L);
        assertThrows(IllegalArgumentException.class, () -> service.validateCoupon(null, user));
        assertThrows(IllegalArgumentException.class, () -> service.validateCoupon(" ", user));
        assertThrows(IllegalArgumentException.class, () -> service.validateCoupon("CODE", null));
        assertThrows(IllegalArgumentException.class, () -> service.validateCoupon("CODE", new User()));
    }

    @Test
    void redeemCoupon_shouldSetUsadoTrue() {
        User user = new User();
        user.setIdUser(1L);
        Cupon cupon = new Cupon();
        cupon.setUser(user);
        cupon.setUsado(false);
        cupon.setFechaExpiracion(LocalDateTime.now().plusDays(1));
        when(cuponRepository.findByCodigo("CODE")).thenReturn(Optional.of(cupon));

        service.redeemCoupon("CODE", user);

        assertTrue(cupon.isUsado());
        verify(cuponRepository).save(cupon);
    }

    @Test
    void redeemCoupon_shouldThrowIfInvalidArgs() {
        User user = new User();
        user.setIdUser(1L);
        assertThrows(IllegalArgumentException.class, () -> service.redeemCoupon(null, user));
        assertThrows(IllegalArgumentException.class, () -> service.redeemCoupon(" ", user));
        assertThrows(IllegalArgumentException.class, () -> service.redeemCoupon("CODE", null));
        assertThrows(IllegalArgumentException.class, () -> service.redeemCoupon("CODE", new User()));
    }

    @Test
    void redeemCoupon_shouldThrowIfNotFound() {
        User user = new User();
        user.setIdUser(1L);
        when(cuponRepository.findByCodigo("CODE")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.redeemCoupon("CODE", user));
    }

    @Test
    void redeemCoupon_shouldThrowIfNotUserOwner() {
        User user = new User();
        user.setIdUser(1L);
        Cupon cupon = new Cupon();
        User anotherUser = new User();
        anotherUser.setIdUser(2L);
        cupon.setUser(anotherUser);
        cupon.setUsado(false);
        cupon.setFechaExpiracion(LocalDateTime.now().plusDays(1));
        when(cuponRepository.findByCodigo("CODE")).thenReturn(Optional.of(cupon));
        assertThrows(IllegalStateException.class, () -> service.redeemCoupon("CODE", user));
    }

    @Test
    void redeemCoupon_shouldThrowIfAlreadyUsed() {
        User user = new User();
        user.setIdUser(1L);
        Cupon cupon = new Cupon();
        cupon.setUser(user);
        cupon.setUsado(true);
        cupon.setFechaExpiracion(LocalDateTime.now().plusDays(1));
        when(cuponRepository.findByCodigo("CODE")).thenReturn(Optional.of(cupon));
        assertThrows(IllegalStateException.class, () -> service.redeemCoupon("CODE", user));
    }

    @Test
    void redeemCoupon_shouldThrowIfExpired() {
        User user = new User();
        user.setIdUser(1L);
        Cupon cupon = new Cupon();
        cupon.setUser(user);
        cupon.setUsado(false);
        cupon.setFechaExpiracion(LocalDateTime.now().minusDays(1));
        when(cuponRepository.findByCodigo("CODE")).thenReturn(Optional.of(cupon));
        assertThrows(IllegalStateException.class, () -> service.redeemCoupon("CODE", user));
    }
}
