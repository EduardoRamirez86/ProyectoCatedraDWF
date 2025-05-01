DROP DATABASE IF EXISTS pruebaseguridad;
CREATE DATABASE IF NOT EXISTS pruebaseguridad;
USE pruebaseguridad;

DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS compra;
DROP TABLE IF EXISTS ropa;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
    idRole BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
    idUser BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(idRole) ON DELETE CASCADE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(idUser) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(idRole) ON DELETE CASCADE
);

CREATE TABLE ropa (
    idRopa BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    precio DOUBLE NOT NULL
);

CREATE TABLE compra (
    idCompra BIGINT AUTO_INCREMENT PRIMARY KEY,
    fechaCompra DATETIME NOT NULL,
    cantidad INT NOT NULL,
    total DOUBLE NOT NULL,
    user_id BIGINT,
    ropa_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(idUser) ON DELETE CASCADE,
    FOREIGN KEY (ropa_id) REFERENCES ropa(idRopa) ON DELETE CASCADE
);