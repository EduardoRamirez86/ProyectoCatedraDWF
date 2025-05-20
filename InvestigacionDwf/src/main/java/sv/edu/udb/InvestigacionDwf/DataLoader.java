package sv.edu.udb.InvestigacionDwf;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.InvestigacionDwf.model.entity.Producto;
import sv.edu.udb.InvestigacionDwf.model.entity.Role;
import sv.edu.udb.InvestigacionDwf.model.entity.TipoProducto;
import sv.edu.udb.InvestigacionDwf.repository.ProductoRepository;
import sv.edu.udb.InvestigacionDwf.repository.RoleRepository;
import sv.edu.udb.InvestigacionDwf.repository.TipoProductoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final TipoProductoRepository tipoProductoRepository;
    private final ProductoRepository productoRepository;

    public DataLoader(RoleRepository roleRepository,
                      TipoProductoRepository tipoProductoRepository,
                      ProductoRepository productoRepository) {
        this.roleRepository = roleRepository;
        this.tipoProductoRepository = tipoProductoRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Seed Roles
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role("ROLE_USER"));
        }

        // Seed Tipos de Producto
        TipoProducto camisa = tipoProductoRepository.findByTipo("Camisa")
                .orElseGet(() -> {
                    TipoProducto t = new TipoProducto();
                    t.setTipo("Camisa");
                    t.setDescripcion("Prenda superior para torso");
                    return tipoProductoRepository.save(t);
                });

        TipoProducto pantalon = tipoProductoRepository.findByTipo("Pantalón")
                .orElseGet(() -> {
                    TipoProducto t = new TipoProducto();
                    t.setTipo("Pantalón");
                    t.setDescripcion("Prenda inferior para piernas");
                    return tipoProductoRepository.save(t);
                });

        TipoProducto calzado = tipoProductoRepository.findByTipo("Calzado")
                .orElseGet(() -> {
                    TipoProducto t = new TipoProducto();
                    t.setTipo("Calzado");
                    t.setDescripcion("Zapatos y sandalias");
                    return tipoProductoRepository.save(t);
                });

        // Seed Productos
        createProductoIfNotExists("Camisa Blanca", "Camisa de algodón blanca, talla M.", "19.99", "10.00", 50, "https://images.pexels.com/photos/769733/pexels-photo-769733.jpeg", 5, camisa);
        createProductoIfNotExists("Pantalón Jeans", "Pantalón de mezclilla azul, talla 32.", "39.99", "20.00", 30, "https://images.unsplash.com/photo-1541099649105-f69ad21f3246", 8, pantalon);
        createProductoIfNotExists("Tenis Deportivos", "Calzado deportivo unisex, talla 42.", "59.99", "30.00", 20, "https://images.unsplash.com/photo-1460353581641-37baddab0fa2", 10, calzado);
        createProductoIfNotExists("Camisa Azul", "Camisa de algodón azul, talla L.", "24.99", "12.00", 40, "https://images.pexels.com/photos/991509/pexels-photo-991509.jpeg", 6, camisa);
        createProductoIfNotExists("Camisa a Cuadros", "Camisa de franela a cuadros, talla M.", "29.99", "15.00", 35, "https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg", 7, camisa);
        createProductoIfNotExists("Camisa de Lino", "Camisa de lino ligera, talla S.", "34.99", "18.00", 25, "https://images.pexels.com/photos/769749/pexels-photo-769749.jpeg", 8, camisa);
        createProductoIfNotExists("Pantalón Chino", "Pantalón chino color caqui, talla 34.", "39.99", "20.00", 30, "https://images.pexels.com/photos/1598507/pexels-photo-1598507.jpeg", 8, pantalon);
        createProductoIfNotExists("Pantalón Cargo", "Pantalón cargo con bolsillos laterales, talla 32.", "44.99", "22.00", 20, "https://images.pexels.com/photos/1082529/pexels-photo-1082529.jpeg", 9, pantalon);
        createProductoIfNotExists("Pantalón de Vestir", "Pantalón de vestir negro, talla 36.", "49.99", "25.00", 15, "https://images.pexels.com/photos/1082528/pexels-photo-1082528.jpeg", 10, pantalon);
        createProductoIfNotExists("Botas de Cuero", "Botas de cuero marrón, talla 40.", "79.99", "40.00", 10, "https://images.pexels.com/photos/293250/pexels-photo-293250.jpeg", 12, calzado);
        createProductoIfNotExists("Sandalias", "Sandalias de cuero, talla 38.", "29.99", "15.00", 25, "https://images.pexels.com/photos/19090/pexels-photo.jpg", 5, calzado);
        createProductoIfNotExists("Zapatos de Vestir", "Zapatos de vestir negros, talla 42.", "69.99", "35.00", 15, "https://images.pexels.com/photos/298863/pexels-photo-298863.jpeg", 10, calzado);
    }

    private void createProductoIfNotExists(String nombre, String descripcion, String precio, String costo,
                                           int cantidad, String imagen, int puntos, TipoProducto tipo) {
        Optional<Producto> existing = productoRepository.findByNombre(nombre);
        if (existing.isEmpty()) {
            Producto p = new Producto();
            p.setNombre(nombre);
            p.setDescripcion(descripcion);
            p.setPrecio(new BigDecimal(precio));
            p.setCosto(new BigDecimal(costo));
            p.setCantidad(cantidad);
            p.setImagen(imagen);
            p.setCantidadPuntos(puntos);
            p.setTipoProducto(tipo);
            p.setFechaCreacion(LocalDateTime.now());
            p.setFechaActualizacion(LocalDateTime.now());
            productoRepository.save(p);
        }
    }

}