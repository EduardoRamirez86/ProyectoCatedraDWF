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
import java.util.Objects;

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
                .orElseGet(() -> tipoProductoRepository.save(
                        TipoProducto.builder()
                                .tipo("Camisa")
                                .descripcion("Prenda superior para torso")
                                .build()
                ));

        TipoProducto pantalon = tipoProductoRepository.findByTipo("Pantal\u00f3n")
                .orElseGet(() -> tipoProductoRepository.save(
                        TipoProducto.builder()
                                .tipo("Pantal\u00f3n")
                                .descripcion("Prenda inferior para piernas")
                                .build()
                ));

        TipoProducto calzado = tipoProductoRepository.findByTipo("Calzado")
                .orElseGet(() -> tipoProductoRepository.save(
                        TipoProducto.builder()
                                .tipo("Calzado")
                                .descripcion("Zapatos y sandalias")
                                .build()
                ));

        // Seed Productos
        createProductoIfNotExists("Camisa Blanca", "Camisa de algod\u00f3n blanca, talla M.", "19.99", "10.00", 50, "https://images.pexels.com/photos/769733/pexels-photo-769733.jpeg", 5, camisa);
        createProductoIfNotExists("Pantal\u00f3n Jeans", "Pantal\u00f3n de mezclilla azul, talla 32.", "39.99", "20.00", 30, "https://images.unsplash.com/photo-1541099649105-f69ad21f3246", 8, pantalon);
        createProductoIfNotExists("Tenis Deportivos", "Calzado deportivo unisex, talla 42.", "59.99", "30.00", 20, "https://images.unsplash.com/photo-1460353581641-37baddab0fa2", 10, calzado);
        createProductoIfNotExists("Camisa Azul", "Camisa de algod\u00f3n azul, talla L.", "24.99", "12.00", 40, "https://images.pexels.com/photos/991509/pexels-photo-991509.jpeg", 6, camisa);
        createProductoIfNotExists("Camisa a Cuadros", "Camisa de franela a cuadros, talla M.", "29.99", "15.00", 35, "https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg", 7, camisa);
        createProductoIfNotExists("Camisa de Lino", "Camisa de lino ligera, talla S.", "34.99", "18.00", 25, "https://images.pexels.com/photos/769749/pexels-photo-769749.jpeg", 8, camisa);
        createProductoIfNotExists("Pantal\u00f3n Chino", "Pantal\u00f3n chino color caqui, talla 34.", "39.99", "20.00", 30, "https://images.pexels.com/photos/1598507/pexels-photo-1598507.jpeg", 8, pantalon);
        createProductoIfNotExists("Pantal\u00f3n Cargo", "Pantal\u00f3n cargo con bolsillos laterales, talla 32.", "44.99", "22.00", 20, "https://images.pexels.com/photos/1082529/pexels-photo-1082529.jpeg", 9, pantalon);
        createProductoIfNotExists("Pantal\u00f3n de Vestir", "Pantal\u00f3n de vestir negro, talla 36.", "49.99", "25.00", 15, "https://images.pexels.com/photos/1082528/pexels-photo-1082528.jpeg", 10, pantalon);
        createProductoIfNotExists("Botas de Cuero", "Botas de cuero marr\u00f3n, talla 40.", "79.99", "40.00", 10, "https://images.pexels.com/photos/293250/pexels-photo-293250.jpeg", 12, calzado);
        createProductoIfNotExists("Sandalias", "Sandalias de cuero, talla 38.", "29.99", "15.00", 25, "https://images.pexels.com/photos/19090/pexels-photo.jpg", 5, calzado);
        createProductoIfNotExists("Zapatos de Vestir", "Zapatos de vestir negros, talla 42.", "69.99", "35.00", 15, "https://images.pexels.com/photos/298863/pexels-photo-298863.jpeg", 10, calzado);

        // Nuevos 15 productos
        createProductoIfNotExists("Camisa Negra", "Camisa casual negra, talla M.", "21.99", "11.00", 45, "https://images.pexels.com/photos/4041683/pexels-photo-4041683.jpeg", 6, camisa);
        createProductoIfNotExists("Pantal\u00f3n Slim", "Pantal\u00f3n slim fit gris, talla 30.", "42.99", "22.00", 25, "https://images.pexels.com/photos/4041684/pexels-photo-4041684.jpeg", 9, pantalon);
        createProductoIfNotExists("Zapatillas Running", "Zapatillas para correr, talla 41.", "64.99", "33.00", 20, "https://images.pexels.com/photos/19090/pexels-photo.jpg", 11, calzado);
        createProductoIfNotExists("Camisa Manga Larga", "Camisa manga larga blanca, talla L.", "26.99", "13.00", 30, "https://images.pexels.com/photos/298863/pexels-photo-298863.jpeg", 7, camisa);
        createProductoIfNotExists("Camisa Polo", "Camisa tipo polo, talla M.", "28.99", "14.00", 28, "https://images.pexels.com/photos/769733/pexels-photo-769733.jpeg", 6, camisa);
        createProductoIfNotExists("Pantal\u00f3n Deportivo", "Pantal\u00f3n para hacer ejercicio, talla L.", "35.99", "17.00", 22, "https://images.pexels.com/photos/769749/pexels-photo-769749.jpeg", 8, pantalon);
        createProductoIfNotExists("Zapatillas Urbanas", "Zapatillas urbanas modernas, talla 43.", "54.99", "27.00", 18, "https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg", 9, calzado);
        createProductoIfNotExists("Zapatos de Cuero", "Zapatos formales de cuero negro, talla 40.", "74.99", "37.00", 12, "https://images.pexels.com/photos/1598507/pexels-photo-1598507.jpeg", 11, calzado);
        createProductoIfNotExists("Camisa Estampada", "Camisa con estampado floral, talla M.", "33.99", "17.00", 26, "https://images.pexels.com/photos/1082529/pexels-photo-1082529.jpeg", 7, camisa);
        createProductoIfNotExists("Pantal\u00f3n Recto", "Pantal\u00f3n corte recto, talla 32.", "38.99", "19.00", 19, "https://images.pexels.com/photos/1082528/pexels-photo-1082528.jpeg", 8, pantalon);
        createProductoIfNotExists("Zapatos Casual", "Zapatos casuales marrones, talla 42.", "66.99", "33.00", 16, "https://images.pexels.com/photos/298863/pexels-photo-298863.jpeg", 10, calzado);
        createProductoIfNotExists("Camisa Formal", "Camisa formal celeste, talla M.", "31.99", "16.00", 24, "https://images.pexels.com/photos/19090/pexels-photo.jpg", 7, camisa);
        createProductoIfNotExists("Botines", "Botines negros, talla 41.", "72.99", "36.00", 14, "https://images.pexels.com/photos/293250/pexels-photo-293250.jpeg", 10, calzado);
        createProductoIfNotExists("Sandalias Playeras", "Sandalias para playa, talla 39.", "24.99", "12.00", 20, "https://images.pexels.com/photos/19090/pexels-photo.jpg", 6, calzado);
        createProductoIfNotExists("Camisa Verde", "Camisa verde oliva, talla L.", "27.99", "13.50", 33, "https://images.pexels.com/photos/991509/pexels-photo-991509.jpeg", 7, camisa);
    }

    private void createProductoIfNotExists(String nombre, String descripcion, String precio, String costo,
                                           int cantidad, String imagen, int puntos, TipoProducto tipo) {
        if (Objects.isNull(productoRepository.findByNombre(nombre).orElse(null))) {
            productoRepository.save(
                    Producto.builder()
                            .nombre(nombre)
                            .descripcion(descripcion)
                            .precio(new BigDecimal(precio))
                            .costo(new BigDecimal(costo))
                            .cantidad(cantidad)
                            .imagen(imagen)
                            .cantidadPuntos(puntos)
                            .tipoProducto(tipo)
                            .fechaCreacion(LocalDateTime.now())
                            .fechaActualizacion(LocalDateTime.now())
                            .build()
            );
        }
    }
}