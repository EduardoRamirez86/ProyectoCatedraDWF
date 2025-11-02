# Proyecto de Cátedra DWF - Ecommerce

Este repositorio contiene el código fuente de la API REST desarrollada en Spring Boot. Esta API gestiona la lógica de negocio, la persistencia de datos y la autenticación para la aplicación de E-commerce "NOVA-e y una pagina WEB "Ecommerce" con react js".

## ✨ Características Principales

- **Framework:** Spring Boot 3
- **Seguridad:** Spring Security con autenticación basada en JSON Web Tokens (JWT).
- **Base de Datos:** MySQL.
- **ORM:** Spring Data JPA (Hibernate).
- **Validación:** `spring-boot-starter-validation` para DTOs.
- **Mapeo de Objetos:** MapStruct para una conversión eficiente y limpia entre Entidades y DTOs.

---

## 🚀 Guía de Inicio Rápido: Pasos para Ejecutar el Proyecto

Para poner en marcha la API en tu entorno local, sigue estos pasos sagrados al pie de la letra.

### 1. Prerrequisitos

Asegúrate de tener instalado el siguiente software en tu sistema:

- **Java Development Kit (JDK):** Versión 17 o superior.
- **Apache Maven:** Versión 3.6 o superior (para la gestión de dependencias y construcción del proyecto).
- **MySQL Server:** Versión 8 o superior.
- **Ngrok:** Una cuenta (incluso la gratuita) y el ejecutable de [Ngrok](https://ngrok.com/download) descargado.
- **Un IDE:** Como IntelliJ IDEA o Visual Studio Code con las extensiones de Java.

### 2. Configuración de la Base de Datos MySQL

La API necesita una base de datos para funcionar.

1.  **Crear la Base de Datos:** Abre tu cliente de MySQL (MySQL Workbench, DBeaver, o la línea de comandos) y ejecuta la siguiente instrucción para crear una base de datos vacía.

    
2.  **Configurar la Conexión:** Abre el archivo `application.properties` que se encuentra en `src/main/resources/`. Busca la sección de `spring.datasource` y **asegúrate de que las credenciales coincidan con tu configuración local de MySQL**.

    
### 3. Inyección de Datos Iniciales (¡Importante!)

Este proyecto utiliza un sistema para inyectar datos esenciales al arrancar la aplicación por primera vez (o cuando la base de datos está vacía). Estos datos incluyen:

- Roles de usuario (`ROLE_USER`, `ROLE_ADMIN`, `ROLE_EMPLOYEE`).
- Tipos de producto iniciales.
- Un usuario administrador por defecto.

No necesitas hacer nada para que esto funcione, pero **tenlo en mente**. Al ejecutar la aplicación, tus tablas `roles`, `tipo_producto` y `users` se poblarán automáticamente. Si vacías la base de datos, estos datos se reinsertarán en el siguiente arranque.

### 4. Construir y Ejecutar la API

1.  **Clona el Repositorio:**    
2.  **Construye el Proyecto con Maven:** Abre una terminal en la raíz del proyecto y ejecuta:

    Esto descargará todas las dependencias y compilará el código.
3.  **Ejecuta la Aplicación:** Puedes hacerlo desde tu IDE (buscando la clase principal con el método `main` y ejecutándola) o directamente desde la terminal con Maven:
    
