INSERT INTO roles (idRole, name) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_USER');

INSERT INTO ropa (id, nombre, precio) VALUES (1,'Camiseta', 15.99);
INSERT INTO ropa (nombre, precio) VALUES ('Pantalón', 29.99);
INSERT INTO ropa (nombre, precio) VALUES ('Chaqueta', 49.99);
INSERT INTO ropa (nombre, precio) VALUES ('Falda', 25.50);
INSERT INTO ropa (nombre, precio) VALUES ('Vestido', 39.99);

-- Example user with ROLE_USER
INSERT INTO users (idUser, username, password, email, token) VALUES (1, 'testuser', 'hashedpassword', 'testuser@example.com', null);

-- Assign ROLE_USER to the user
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);