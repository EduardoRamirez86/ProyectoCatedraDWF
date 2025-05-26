package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.InvestigacionDwf.model.entity.Notificacion;
import sv.edu.udb.InvestigacionDwf.model.entity.User;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;
import sv.edu.udb.InvestigacionDwf.model.entity.Pedido;
import sv.edu.udb.InvestigacionDwf.model.enums.EstadoNotificacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static sv.edu.udb.InvestigacionDwf.model.enums.EstadoPedido.PENDIENTE;
import static sv.edu.udb.InvestigacionDwf.model.enums.TipoPago.EFECTIVO;

@DataJpaTest
@ActiveProfiles("test")
class NotificacionRepositoryTest {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Long savedNotificacionId;
    private User user;
    private Pedido pedido;

    @BeforeEach
    void init() {
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        user = userRepository.save(User.builder().username("notifuser").email("n@n.com").password("pass").primerNombre("a").primerApellido("b").role(role).build());
        pedido = Pedido.builder()
                .carrito(null)
                .fechaInicio(LocalDateTime.now())
                .total(BigDecimal.ONE)
                .puntosTotales(1)
                .estado(PENDIENTE)
                .tipoPago(EFECTIVO)
                .build();
        pedido = pedidoRepository.save(pedido);

        Notificacion notif = Notificacion.builder()
                .user(user)
                .pedido(pedido)
                .mensaje("Hola")
                .fechaEnvio(LocalDateTime.now())
                .estado(EstadoNotificacion.ENVIADA)
                .build();
        notificacionRepository.save(notif);
        savedNotificacionId = notif.getId();
    }

    @AfterEach
    void clean() {
        notificacionRepository.deleteAll();
        pedidoRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Optional<Notificacion> notif = notificacionRepository.findById(savedNotificacionId);
        assertTrue(notif.isPresent());
        assertEquals("Hola", notif.get().getMensaje());
    }

    @Test
    void testFindAll() {
        List<Notificacion> notifs = notificacionRepository.findAll();
        assertNotNull(notifs);
        assertFalse(notifs.isEmpty());
    }

    @Test
    void testSave() {
        Notificacion notif = Notificacion.builder()
                .user(user)
                .pedido(pedido) // <--- Asigna el pedido aquí
                .mensaje("Nuevo mensaje")
                .fechaEnvio(LocalDateTime.now())
                .estado(EstadoNotificacion.LEIDA)
                .build();
        Notificacion saved = notificacionRepository.save(notif);
        assertNotNull(saved.getId());
        assertEquals("Nuevo mensaje", saved.getMensaje());
    }

    @Test
    void testUpdate() {
        Optional<Notificacion> notifOpt = notificacionRepository.findById(savedNotificacionId);
        assertTrue(notifOpt.isPresent());
        Notificacion notif = notifOpt.get();
        notif.setMensaje("Mensaje actualizado");
        notificacionRepository.save(notif);

        Optional<Notificacion> updated = notificacionRepository.findById(savedNotificacionId);
        assertTrue(updated.isPresent());
        assertEquals("Mensaje actualizado", updated.get().getMensaje());
    }

    @Test
    void testDelete() {
        Optional<Notificacion> notifOpt = notificacionRepository.findById(savedNotificacionId);
        assertTrue(notifOpt.isPresent());
        notificacionRepository.delete(notifOpt.get());
        Optional<Notificacion> deleted = notificacionRepository.findById(savedNotificacionId);
        assertFalse(deleted.isPresent());
    }

    @Test
    void shouldSaveAndFindByUserAndEstado() {
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        User user = userRepository.save(User.builder().username("notifuser2").email("n2@n.com").password("pass").primerNombre("a").primerApellido("b").role(role).build());
        Pedido pedido = Pedido.builder()
                .carrito(null)
                .fechaInicio(LocalDateTime.now())
                .total(BigDecimal.ONE)
                .puntosTotales(1)
                .estado(PENDIENTE)
                .tipoPago(EFECTIVO)
                .build();
        pedido = pedidoRepository.save(pedido);

        Notificacion notif = Notificacion.builder()
                .user(user)
                .pedido(pedido)
                .mensaje("Hola")
                .fechaEnvio(LocalDateTime.now())
                .estado(EstadoNotificacion.ENVIADA)
                .build();
        notificacionRepository.save(notif);

        List<Notificacion> found = notificacionRepository.findByUserAndEstado(user, EstadoNotificacion.ENVIADA);
        assertFalse(found.isEmpty());
        assertEquals(user.getIdUser(), found.get(0).getUser().getIdUser());
    }
}
