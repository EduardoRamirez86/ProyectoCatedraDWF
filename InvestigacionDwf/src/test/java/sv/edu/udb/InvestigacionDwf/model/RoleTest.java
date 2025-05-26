package sv.edu.udb.InvestigacionDwf.model;

import org.junit.jupiter.api.Test;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;
import sv.edu.udb.InvestigacionDwf.model.entity.User;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void testConstructorAndGetters() {
        Role role = new Role("ADMIN");
        role.setIdRol(1L);

        assertEquals(1L, role.getIdRol());
        assertEquals("ADMIN", role.getName());
    }

    @Test
    void testSetters() {
        Role role = new Role();
        role.setIdRol(2L);
        role.setName("USER");

        assertEquals(2L, role.getIdRol());
        assertEquals("USER", role.getName());
    }

    @Test
    void testRelacionesBidireccionales() {
        Role role = new Role();
        role.setIdRol(1L);
        role.setName("ADMIN");
        User user = User.builder().idUser(1L).build();
        Set<User> users = new HashSet<>();
        users.add(user);
        role.setUsers(users);

        assertNotNull(role.getUsers());
        assertTrue(role.getUsers().contains(user));
    }
}
