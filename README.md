# 🛒 Proyecto de Cátedra DWF - Ecosistema E-commerce

Este repositorio contiene el código fuente para un **ecosistema de e-commerce completo**, compuesto por dos proyectos principales desacoplados:

1.  **API REST (Backend):** Desarrollada en **Spring Boot**, gestiona toda la lógica de negocio, la persistencia de datos con MySQL y la autenticación. Es el cerebro de la operación.
2.  **Cliente Web (Frontend):** Una completa aplicación web desarrollada en **React JS**, que consume la API para ofrecer una experiencia de e-commerce completa, desde la landing page hasta el ciclo de compra.

Este sistema dual da servicio tanto a la aplicación móvil **NOVA-e** como a la página web, compartiendo la misma lógica de negocio centralizada en la API.

---

## ✨ Arquitectura y Características

### Backend (API REST - Puerto 8080)

| Característica | Detalle |
| :--- | :--- |
| **Framework** | Spring Boot 3 |
| **Seguridad** | Spring Security con autenticación basada en **JSON Web Tokens (JWT)**. |
| **Base de Datos** | MySQL. |
| **ORM** | Spring Data JPA (Hibernate). |
| **Validación** | `spring-boot-starter-validation` para DTOs. |
| **Mapeo de Objetos** | MapStruct para una conversión eficiente entre Entidades y DTOs. |

### Frontend (Cliente Web - Puerto 3000)

* **Framework:** React JS (React App).
* **Gestión de Estado:** (API).
* **Enrutamiento:** React Router.
* **Librería de Componentes:** (Bootstrap).
* **Comunicación HTTP:** Fetch API.

---

## 🚀 Guía de Ejecución Completa del Ecosistema

Para poner en marcha **todo el sistema** (Backend y Frontend) en tu entorno local, sigue esta guía al pie de la letra.

### 1. Prerrequisitos

Asegúrate de tener instalado el siguiente software:

* **Java Development Kit (JDK):** Versión 17 o superior.
* **Apache Maven:** Versión 3.6 o superior.
* **Node.js y npm:** Versión 16 o superior.
* **MySQL Server:** Versión 8 o superior.
* **Ngrok:** El ejecutable y una cuenta (gratuita es suficiente) de [Ngrok](https://ngrok.com/download).
* **Un IDE:** Como IntelliJ IDEA y/o Visual Studio Code.

### 2. Configuración de la Base de Datos MySQL

La API necesita una base de datos para funcionar.

1.  **Crear la Base de Datos:** Abre tu cliente de MySQL (MySQL Workbench, DBeaver, etc.) y ejecuta:
    ```sql
    CREATE DATABASE ecommerce_3;
    ```

2.  **Configurar la Conexión de la API:** Abre el archivo **`application.properties`** en el proyecto de backend (`src/main/resources/`). Asegúrate de que las credenciales (`url`, `username`, `password`) coincidan con tu configuración local de MySQL.

### 3. Inyección de Datos Iniciales (¡Importante!)

Este proyecto inyecta datos esenciales al arrancar la API, incluyendo roles (`ROLE_USER`, `ROLE_ADMIN`, `ROLE_EMPLOYEE`), tipos de producto y un usuario administrador. No necesitas hacer nada, ya que **las tablas se poblarán automáticamente** al arrancar la API por primera vez.

### 4. Ejecución del Backend (API REST - Puerto 8080)

1.  **Navega al Directorio del Backend:** Abre una terminal y sitúate en la carpeta raíz del proyecto Spring Boot.
2.  **Construye el Proyecto con Maven:**
    ```bash
    mvn clean install
    ```
3.  **Ejecuta el Backend:**
    ```bash
    mvn spring-boot:run
    ```
    > 💡 **Deja esta terminal abierta.** Tu API ahora está viva en `http://localhost:8080`.

### 5. Exponer la API al Mundo con Ngrok

Para que los clientes (la app móvil y el frontend de React) puedan comunicarse con tu API local, necesitas un túnel.

1.  **Abre una NUEVA terminal.**
2.  **Inicia el Túnel de Ngrok:** Apunta Ngrok al puerto de tu API.
    ```bash
    ngrok http 8080
    ```
    > 💡 La terminal de Ngrok te mostrará una URL temporal (ej: `https://abcd1234.ngrok-free.app`). **Copia esta URL.**

### 6. Ejecución del Frontend (Cliente Web - Puerto 3000)

1.  **Navega al Directorio del Frontend:** Abre una **TERCERA terminal** y sitúate en la carpeta raíz del proyecto React.
2.  **Instala las dependencias:**
    ```bash
    npm install
    ```
3.  **Actualiza la URL de la API:** En el archivo de configuración del frontend (ej. un archivo `.env` o una constante de configuración), **reemplaza** la URL base de la API con la URL de Ngrok que copiaste en el paso 5.
4.  **Ejecuta el Frontend:**
    ```bash
    npm start 
    # o npm run dev si usas Vite
    ```
    Tu navegador se abrirá automáticamente en `http://localhost:3000`, mostrando la página web.

---

## ✅ ¡Ecosistema Operativo!

Si has seguido todos los pasos, tu entorno de desarrollo está **completo y funcional**:

* **Terminal 1:** Ejecutando la API de Spring Boot en el puerto `8080`.
* **Terminal 2:** Ejecutando Ngrok, que expone tu API al mundo.
* **Terminal 3:** Ejecutando la aplicación de React en el puerto `3000`.

Ahora puedes interactuar con la página web, y esta se comunicará con tu API local a través del túnel de Ngrok.

> **Nota Importante sobre Ngrok:** La URL de la versión gratuita es temporal. Si reinicias Ngrok, **deberás repetir el Paso 6.3** (actualizar la URL en el frontend) y, si aplica, en la app móvil.
