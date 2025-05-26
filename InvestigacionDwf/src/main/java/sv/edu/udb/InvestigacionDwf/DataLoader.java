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
        if (roleRepository.findByName("ROLE_EMPLOYEE").isEmpty()) {
            roleRepository.save(new Role("ROLE_EMPLOYEE"));
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
        createProductoIfNotExists("Camisa Blanca", "Camisa de algod\u00f3n blanca, talla M.", "19.99", "10.00", 50, "https://images.unsplash.com/photo-1671438118097-479e63198629?q=80&w=2077&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 5, camisa);
        createProductoIfNotExists("Pantal\u00f3n Jeans", "Pantal\u00f3n de mezclilla azul, talla 32.", "39.99", "20.00", 30, "https://images.unsplash.com/photo-1602293589930-45aad59ba3ab?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 8, pantalon);
        createProductoIfNotExists("Tenis Deportivos", "Calzado deportivo unisex, talla 42.", "59.99", "30.00", 20, "https://plus.unsplash.com/premium_photo-1682435561654-20d84cef00eb?q=80&w=2036&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 10, calzado);
        createProductoIfNotExists("Camisa Azul", "Camisa de algod\u00f3n azul, talla L.", "24.99", "12.00", 40, "https://images.unsplash.com/photo-1740711152088-88a009e877bb?q=80&w=1160&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 6, camisa);
        createProductoIfNotExists("Camisa a Cuadros", "Camisa de franela a cuadros, talla M.", "29.99", "15.00", 35, "https://www.devre.la/media/catalog/product/d/e/devre-camisa-sport_42d000267-001_001.jpg?optimize=high&bg-color=255,255,255&fit=bounds&height=1350&width=900&canvas=900:1350", 7, camisa);
        createProductoIfNotExists("Camisa de Lino", "Camisa de lino ligera, talla S.", "34.99", "18.00", 25, "https://images.unsplash.com/photo-1713881842156-3d9ef36418cc?q=80&w=988&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 8, camisa);
        createProductoIfNotExists("Pantal\u00f3n Chino", "Pantal\u00f3n chino color caqui, talla 34.", "39.99", "20.00", 30, "https://holstone.com.mx/cdn/shop/files/60.png?v=1731608812", 8, pantalon);
        createProductoIfNotExists("Pantal\u00f3n Cargo", "Pantal\u00f3n cargo con bolsillos laterales, talla 32.", "44.99", "22.00", 20, "https://dcdn-us.mitiendanube.com/stores/001/122/213/products/3aed032c-e251-405f-acad-9dde3e9a58ab-11e5d1e83e75645a7717171022894980-1024-1024.jpeg", 9, pantalon);
        createProductoIfNotExists("Pantal\u00f3n de Vestir", "Pantal\u00f3n de vestir negro, talla 36.", "49.99", "25.00", 15, "https://siman.vtexassets.com/arquivos/ids/2458467-800-800?v=637745124247600000&width=800&height=800&aspect=true", 10, pantalon);
        createProductoIfNotExists("Botas de Cuero", "Botas de cuero marr\u00f3n, talla 40.", "79.99", "40.00", 10, "https://images.unsplash.com/photo-1698234912698-0cb48c13eee4?q=80&w=1160&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 12, calzado);
        createProductoIfNotExists("Sandalias", "Sandalias de cuero, talla 38.", "29.99", "15.00", 25, "https://m.media-amazon.com/images/I/812GOCRwg4L._AC_UY900_.jpg", 5, calzado);
        createProductoIfNotExists("Zapatos de Vestir", "Zapatos de vestir negros, talla 42.", "69.99", "35.00", 15, "https://carloronaldi.com.mx/cdn/shop/products/1101MANTANEGRO.jpg?v=1678754726&width=3713", 10, calzado);

        // Nuevos 15 productos
        createProductoIfNotExists("Camisa Negra", "Camisa casual negra, talla M.", "21.99", "11.00", 45, "https://img.kwcdn.com/product/fancy/cf4dd863-8b4a-4569-b29b-1b6a65b1410c.jpg?imageMogr2/auto-orient%7CimageView2/2/w/800/q/70/format/webp", 6, camisa);
        createProductoIfNotExists("Pantal\u00f3n Slim", "Pantal\u00f3n slim fit gris, talla 30.", "42.99", "22.00", 25, "https://www.devre.la/media/catalog/product/d/e/devre-pantalon_16d000082-005_002.jpg?optimize=high&bg-color=255,255,255&fit=bounds&height=1350&width=900&canvas=900:1350", 9, pantalon);
        createProductoIfNotExists("Zapatillas Running", "Zapatillas para correr, talla 41.", "64.99", "33.00", 20, "https://images.unsplash.com/photo-1562183241-b937e95585b6?q=80&w=1065&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 11, calzado);
        createProductoIfNotExists("Camisa Manga Larga", "Camisa manga larga blanca, talla L.", "26.99", "13.00", 30, "https://www.gef.co/cdn/shop/files/new-alton-blanco-900-743037_000900-1.jpg?v=1736279746", 7, camisa);
        createProductoIfNotExists("Camisa Polo", "Camisa tipo polo, talla M.", "28.99", "14.00", 28, "https://images.unsplash.com/photo-1720514496161-914011a9ee02?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 6, camisa);
        createProductoIfNotExists("Pantal\u00f3n Deportivo", "Pantal\u00f3n para hacer ejercicio, talla L.", "35.99", "17.00", 22, "https://assets.adidas.com/images/h_840,f_auto,q_auto,fl_lossy,c_fill,g_auto/b86de2c5a6184a50aee2b5b42281a882_9366/Pantalon_de_Entrenamiento_Gym_Heat_Negro_IS1286_21_model.jpg", 8, pantalon);
        createProductoIfNotExists("Zapatillas Urbanas", "Zapatillas urbanas modernas, talla 43.", "54.99", "27.00", 18, "https://images.unsplash.com/photo-1691436785976-c9da7b0051c8?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 9, calzado);
        createProductoIfNotExists("Zapatos de Cuero", "Zapatos formales de cuero negro, talla 40.", "74.99", "37.00", 12, "https://images.unsplash.com/photo-1677203006929-fd0d9f4f350d?q=80&w=1227&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 11, calzado);
        createProductoIfNotExists("Camisa Estampada", "Camisa con estampado floral, talla M.", "33.99", "17.00", 26, "https://images.unsplash.com/photo-1733395700989-febbc2d31ed8?q=80&w=987&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 7, camisa);
        createProductoIfNotExists("Pantal\u00f3n Recto", "Pantal\u00f3n corte recto, talla 32.", "38.99", "19.00", 19, "https://www.kiabi.es/images/pantalon-de-traje-corte-recto-azul-marino-ti549_2_hd1.jpg", 8, pantalon);
        createProductoIfNotExists("Zapatos Casual", "Zapatos casuales marrones, talla 42.", "66.99", "33.00", 16, "https://luisatoledo.es/1213/zapatos-marrones-cordones-amaia.jpg", 10, calzado);
        createProductoIfNotExists("Camisa Formal", "Camisa formal celeste, talla M.", "31.99", "16.00", 24, "https://www.guapa.eu/1530-large_default/camisa-mujer-en-sarga-celeste-con-bolsillo-y-coordinados-de-flores.jpg", 7, camisa);
        createProductoIfNotExists("Botines", "Botines negros, talla 41.", "72.99", "36.00", 14, "https://cdn0.uncomo.com/es/posts/3/5/6/como_combinar_botines_marrones_32653_600.webp", 10, calzado);
        createProductoIfNotExists("Sandalias Playeras", "Sandalias para playa, talla 39.", "24.99", "12.00", 20, "https://images.unsplash.com/photo-1569397693026-b29fe469c0fb?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", 6, calzado);
        createProductoIfNotExists("Camisa Verde", "Camisa verde oliva, talla L.", "27.99", "13.50", 33, "https://m.media-amazon.com/images/I/71wMqEkqVjL._AC_UY1000_.jpg", 7, camisa);
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