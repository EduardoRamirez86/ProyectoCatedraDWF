--- 1. Tablas sin dependencias (independientes)
CREATE TABLE role (
                      id_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE tipo_producto (
                               id_tipo_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
                               tipo VARCHAR(255) NOT NULL,
                               descripcion VARCHAR(255)
);

CREATE TABLE parametros (
                            id_parametro BIGINT AUTO_INCREMENT PRIMARY KEY,
                            clave VARCHAR(255) NOT NULL UNIQUE,
                            valor VARCHAR(255) NOT NULL,
                            descripcion VARCHAR(255) NOT NULL
);

CREATE TABLE ropa (
                      id_ropa BIGINT AUTO_INCREMENT PRIMARY KEY,
                      nombre VARCHAR(50) NOT NULL,
                      precio DOUBLE NOT NULL
);

-- 2. Tablas que dependen de `role`
CREATE TABLE users (
                       id_user BIGINT AUTO_INCREMENT PRIMARY KEY,
                       id_rol BIGINT NOT NULL,
                       primer_nombre VARCHAR(255) NOT NULL,
                       segundo_nombre VARCHAR(255),
                       primer_apellido VARCHAR(255) NOT NULL,
                       segundo_apellido VARCHAR(255),
                       fecha_nacimiento DATE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       telefono VARCHAR(255),
                       dui VARCHAR(255),
                       direccion VARCHAR(255),
                       puntos INT NOT NULL DEFAULT 0,
                       CONSTRAINT fk_users_role FOREIGN KEY (id_rol) REFERENCES role(id_rol)
);

-- 3. Tablas que dependen de `users` o `tipo_producto`
CREATE TABLE producto (
                          id_producto BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre VARCHAR(100) NOT NULL,
                          descripcion VARCHAR(500),
                          precio DECIMAL(10,2) NOT NULL,
                          costo DECIMAL(10,2) NOT NULL,
                          cantidad INT NOT NULL,
                          imagen VARCHAR(255),
                          cantidad_puntos INT NOT NULL,
                          id_tipo_producto BIGINT NOT NULL,
                          fecha_creacion DATETIME,
                          fecha_actualizacion DATETIME,
                          CONSTRAINT fk_producto_tipo FOREIGN KEY (id_tipo_producto) REFERENCES tipo_producto(id_tipo_producto)
);

CREATE TABLE carrito (
                         id_carrito BIGINT AUTO_INCREMENT PRIMARY KEY,
                         id_user BIGINT NOT NULL UNIQUE,
                         fecha_creacion DATETIME,
                         CONSTRAINT fk_carrito_user FOREIGN KEY (id_user) REFERENCES users(id_user)
);

CREATE TABLE direccion (
                           id_direccion BIGINT AUTO_INCREMENT PRIMARY KEY,
                           id_user BIGINT NOT NULL,
                           alias VARCHAR(255),
                           calle VARCHAR(255),
                           ciudad VARCHAR(255),
                           departamento VARCHAR(255),
                           latitud DOUBLE,
                           longitud DOUBLE,
                           CONSTRAINT fk_direccion_user FOREIGN KEY (id_user) REFERENCES users(id_user)
);

CREATE TABLE cupones (
                         id_cupon BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         codigo VARCHAR(20) NOT NULL UNIQUE,
                         porcentaje_descuento DOUBLE NOT NULL,
                         usado BOOLEAN NOT NULL,
                         fecha_creacion DATETIME,
                         fecha_expiracion DATETIME NOT NULL,
                         CONSTRAINT fk_cupon_user FOREIGN KEY (user_id) REFERENCES users(id_user)
);

-- 4. Tablas que dependen de `producto` o `carrito`
CREATE TABLE carrito_item (
                              id_carrito_item BIGINT AUTO_INCREMENT PRIMARY KEY,
                              id_carrito BIGINT,
                              id_producto BIGINT,
                              cantidad INT,
                              CONSTRAINT fk_carritoitem_carrito FOREIGN KEY (id_carrito) REFERENCES carrito(id_carrito),
                              CONSTRAINT fk_carritoitem_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- 5. Tabla que depende de `users` y `producto`
CREATE TABLE resena (
                        id_resena BIGINT AUTO_INCREMENT PRIMARY KEY,
                        id_user BIGINT NOT NULL,
                        id_producto BIGINT NOT NULL,
                        comentario VARCHAR(255),
                        fecha DATETIME,
                        rating VARCHAR(20),
                        CONSTRAINT fk_resena_user FOREIGN KEY (id_user) REFERENCES users(id_user),
                        CONSTRAINT fk_resena_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

-- 6. Tabla que depende de `carrito` y `direccion`
CREATE TABLE pedidos (
                         id_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
                         fecha_inicio DATETIME NOT NULL,
                         fecha_final DATETIME,
                         total DECIMAL(10,2) NOT NULL,
                         puntos_totales INT NOT NULL,
                         estado VARCHAR(20) NOT NULL,
                         tipo_pago VARCHAR(30) NOT NULL,
                         carrito_id BIGINT,
                         id_direccion BIGINT,
                         CONSTRAINT fk_pedido_carrito FOREIGN KEY (carrito_id) REFERENCES carrito(id_carrito),
                         CONSTRAINT fk_pedido_direccion FOREIGN KEY (id_direccion) REFERENCES direccion(id_direccion)
);

-- 7. Tablas que dependen de `pedidos`, `users`, etc.
CREATE TABLE notificaciones (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                mensaje VARCHAR(500) NOT NULL,
                                fecha_envio DATETIME NOT NULL,
                                estado VARCHAR(15) NOT NULL,
                                pedido_id BIGINT NOT NULL,
                                CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id_user),
                                CONSTRAINT fk_notif_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id_pedido)
);

CREATE TABLE historial_pedido (
                                  id_historial_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  id_pedido BIGINT,
                                  id_user BIGINT,
                                  estado VARCHAR(20) NOT NULL,
                                  fecha DATETIME,
                                  descripcion VARCHAR(255),
                                  CONSTRAINT fk_histpedido_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
                                  CONSTRAINT fk_histpedido_user FOREIGN KEY (id_user) REFERENCES users(id_user)
);

CREATE TABLE historial_puntos (
                                  id_historial_puntos BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  id_user BIGINT,
                                  id_pedido BIGINT,
                                  fecha DATETIME,
                                  cantidad_anterior INT,
                                  cantidad_nueva INT,
                                  CONSTRAINT fk_histpuntos_user FOREIGN KEY (id_user) REFERENCES users(id_user),
                                  CONSTRAINT fk_histpuntos_pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
);