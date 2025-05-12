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

        // Seed Productos - Existing Products with Updated Image URLs
        if (productoRepository.findByNombre("Camisa Blanca").isEmpty()) {
            Producto p1 = new Producto();
            p1.setNombre("Camisa Blanca");
            p1.setDescripcion("Camisa de algodón blanca, talla M.");
            p1.setPrecio(new BigDecimal("19.99"));
            p1.setCosto(new BigDecimal("10.00"));
            p1.setCantidad(50);
            p1.setImagen("https://images.pexels.com/photos/769733/pexels-photo-769733.jpeg");
            p1.setCantidadPuntos(5);
            p1.setTipoProducto(camisa);
            productoRepository.save(p1);
        }

        if (productoRepository.findByNombre("Pantalón Jeans").isEmpty()) {
            Producto p2 = new Producto();
            p2.setNombre("Pantalón Jeans");
            p2.setDescripcion("Pantalón de mezclilla azul, talla 32.");
            p2.setPrecio(new BigDecimal("39.99"));
            p2.setCosto(new BigDecimal("20.00"));
            p2.setCantidad(30);
            p2.setImagen("https://images.unsplash.com/photo-1541099649105-f69ad21f3246");
            p2.setCantidadPuntos(8);
            p2.setTipoProducto(pantalon);
            productoRepository.save(p2);
        }

        if (productoRepository.findByNombre("Tenis Deportivos").isEmpty()) {
            Producto p3 = new Producto();
            p3.setNombre("Tenis Deportivos");
            p3.setDescripcion("Calzado deportivo unisex, talla 42.");
            p3.setPrecio(new BigDecimal("59.99"));
            p3.setCosto(new BigDecimal("30.00"));
            p3.setCantidad(20);
            p3.setImagen("https://images.unsplash.com/photo-1460353581641-37baddab0fa2");
            p3.setCantidadPuntos(10);
            p3.setTipoProducto(calzado);
            productoRepository.save(p3);
        }

        // Additional Camisas
        if (productoRepository.findByNombre("Camisa Azul").isEmpty()) {
            Producto p4 = new Producto();
            p4.setNombre("Camisa Azul");
            p4.setDescripcion("Camisa de algodón azul, talla L.");
            p4.setPrecio(new BigDecimal("24.99"));
            p4.setCosto(new BigDecimal("12.00"));
            p4.setCantidad(40);
            p4.setImagen("https://images.pexels.com/photos/991509/pexels-photo-991509.jpeg");
            p4.setCantidadPuntos(6);
            p4.setTipoProducto(camisa);
            productoRepository.save(p4);
        }

        if (productoRepository.findByNombre("Camisa a Cuadros").isEmpty()) {
            Producto p5 = new Producto();
            p5.setNombre("Camisa a Cuadros");
            p5.setDescripcion("Camisa de franela a cuadros, talla M.");
            p5.setPrecio(new BigDecimal("29.99"));
            p5.setCosto(new BigDecimal("15.00"));
            p5.setCantidad(35);
            p5.setImagen("https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg");
            p5.setCantidadPuntos(7);
            p5.setTipoProducto(camisa);
            productoRepository.save(p5);
        }

        if (productoRepository.findByNombre("Camisa de Lino").isEmpty()) {
            Producto p6 = new Producto();
            p6.setNombre("Camisa de Lino");
            p6.setDescripcion("Camisa de lino ligera, talla S.");
            p6.setPrecio(new BigDecimal("34.99"));
            p6.setCosto(new BigDecimal("18.00"));
            p6.setCantidad(25);
            p6.setImagen("https://images.pexels.com/photos/769749/pexels-photo-769749.jpeg");
            p6.setCantidadPuntos(8);
            p6.setTipoProducto(camisa);
            productoRepository.save(p6);
        }

        // Additional Pantalones
        if (productoRepository.findByNombre("Pantalón Chino").isEmpty()) {
            Producto p7 = new Producto();
            p7.setNombre("Pantalón Chino");
            p7.setDescripcion("Pantalón chino color caqui, talla 34.");
            p7.setPrecio(new BigDecimal("39.99"));
            p7.setCosto(new BigDecimal("20.00"));
            p7.setCantidad(30);
            p7.setImagen("https://images.pexels.com/photos/1598507/pexels-photo-1598507.jpeg");
            p7.setCantidadPuntos(8);
            p7.setTipoProducto(pantalon);
            productoRepository.save(p7);
        }

        if (productoRepository.findByNombre("Pantalón Cargo").isEmpty()) {
            Producto p8 = new Producto();
            p8.setNombre("Pantalón Cargo");
            p8.setDescripcion("Pantalón cargo con bolsillos laterales, talla 32.");
            p8.setPrecio(new BigDecimal("44.99"));
            p8.setCosto(new BigDecimal("22.00"));
            p8.setCantidad(20);
            p8.setImagen("https://images.pexels.com/photos/1082529/pexels-photo-1082529.jpeg");
            p8.setCantidadPuntos(9);
            p8.setTipoProducto(pantalon);
            productoRepository.save(p8);
        }

        if (productoRepository.findByNombre("Pantalón de Vestir").isEmpty()) {
            Producto p9 = new Producto();
            p9.setNombre("Pantalón de Vestir");
            p9.setDescripcion("Pantalón de vestir negro, talla 36.");
            p9.setPrecio(new BigDecimal("49.99"));
            p9.setCosto(new BigDecimal("25.00"));
            p9.setCantidad(15);
            p9.setImagen("https://images.pexels.com/photos/1082528/pexels-photo-1082528.jpeg");
            p9.setCantidadPuntos(10);
            p9.setTipoProducto(pantalon);
            productoRepository.save(p9);
        }

        // Additional Calzado
        if (productoRepository.findByNombre("Botas de Cuero").isEmpty()) {
            Producto p10 = new Producto();
            p10.setNombre("Botas de Cuero");
            p10.setDescripcion("Botas de cuero marrón, talla 40.");
            p10.setPrecio(new BigDecimal("79.99"));
            p10.setCosto(new BigDecimal("40.00"));
            p10.setCantidad(10);
            p10.setImagen("https://images.pexels.com/photos/293250/pexels-photo-293250.jpeg");
            p10.setCantidadPuntos(12);
            p10.setTipoProducto(calzado);
            productoRepository.save(p10);
        }

        if (productoRepository.findByNombre("Sandalias").isEmpty()) {
            Producto p11 = new Producto();
            p11.setNombre("Sandalias");
            p11.setDescripcion("Sandalias de cuero, talla 38.");
            p11.setPrecio(new BigDecimal("29.99"));
            p11.setCosto(new BigDecimal("15.00"));
            p11.setCantidad(25);
            p11.setImagen("https://images.pexels.com/photos/19090/pexels-photo.jpg");
            p11.setCantidadPuntos(5);
            p11.setTipoProducto(calzado);
            productoRepository.save(p11);
        }

        if (productoRepository.findByNombre("Zapatos de Vestir").isEmpty()) {
            Producto p12 = new Producto();
            p12.setNombre("Zapatos de Vestir");
            p12.setDescripcion("Zapatos de vestir negros, talla 42.");
            p12.setPrecio(new BigDecimal("69.99"));
            p12.setCosto(new BigDecimal("35.00"));
            p12.setCantidad(15);
            p12.setImagen("https://images.pexels.com/photos/298863/pexels-photo-298863.jpeg");
            p12.setCantidadPuntos(10);
            p12.setTipoProducto(calzado);
            productoRepository.save(p12);
        }
    }
}
