package sv.edu.udb.InvestigacionDwf.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private Long savedRoleId;

    @BeforeEach
    void init() {
        Role role = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
        savedRoleId = role.getIdRol();
    }

    @AfterEach
    void clean() {
        roleRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindByName() {
        Role role = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        Optional<Role> found = roleRepository.findByName("ROLE_ADMIN");
        assertTrue(found.isPresent());
        assertEquals("ROLE_ADMIN", found.get().getName());
    }

    @Test
    void shouldReturnEmpty_When_NameNotExists() {
        Optional<Role> found = roleRepository.findByName("NO_ROLE");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindById() {
        Optional<Role> role = roleRepository.findById(savedRoleId);
        assertTrue(role.isPresent());
        assertEquals("ROLE_ADMIN", role.get().getName());
    }

    @Test
    void testFindAll() {
        List<Role> roles = roleRepository.findAll();
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
    }

    @Test
    void testSave() {
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        Role saved = roleRepository.save(role);
        assertNotNull(saved.getIdRol());
        assertEquals("ROLE_USER", saved.getName());
    }

    @Test
    void testUpdate() {
        Optional<Role> roleOpt = roleRepository.findById(savedRoleId);
        assertTrue(roleOpt.isPresent());
        Role role = roleOpt.get();
        role.setName("ROLE_SUPERADMIN");
        roleRepository.save(role);

        Optional<Role> updated = roleRepository.findById(savedRoleId);
        assertTrue(updated.isPresent());
        assertEquals("ROLE_SUPERADMIN", updated.get().getName());
    }

    @Test
    void testDelete() {
        Optional<Role> roleOpt = roleRepository.findById(savedRoleId);
        assertTrue(roleOpt.isPresent());
        roleRepository.delete(roleOpt.get());
        Optional<Role> deleted = roleRepository.findById(savedRoleId);
        assertFalse(deleted.isPresent());
    }
}
