# Atalaya - Sistema de Comercio Electronico

Atalaya es una aplicacion web de comercio electronico desarrollada con Spring Boot. El proyecto permite administrar productos, categorias, usuarios, carrito de compras y pedidos desde una interfaz web con Thymeleaf y Bootstrap.

Este README es preliminar y resume el estado actual del avance del proyecto.

## Historias de usuario por persona

### Persona 1 - Autenticacion y usuarios

Responsable de:

- Registro de usuario (HU01).
- Login (HU02).
- Roles (HU19).
- CRUD basico de usuarios.

Debe hacer:

- `Usuario` y `Rol` como entidades.
- `UsuarioRepository`.
- `UsuarioService`.
- `UsuarioController`.
- Vistas de login y registro.

Estado actual:

- El proyecto ya cuenta con entidades `Usuario` y `Rol`.
- Existe repositorio, servicio y controlador para usuarios.
- Existen vistas de login, registro, listado y modificacion de usuarios.
- El login es basico y valida correo/contrasena contra la base de datos.

Importancia:

- Es critico para todo el sistema porque permite identificar usuarios y preparar la proteccion de las secciones administrativas.

### Persona 2 - Productos y categorias

Responsable de:

- Ver productos (HU04).
- Detalle de producto (HU07).
- CRUD de productos (HU14, HU15, HU16).
- Categorias (HU17).

Debe hacer:

- `Producto` y `Categoria` como entidades.
- CRUD completo de productos.
- CRUD de categorias.
- Listado con Bootstrap.
- Filtros basicos.

Estado actual:

- El proyecto ya cuenta con entidades `Producto` y `Categoria`.
- Existe listado publico de productos.
- Existe detalle de producto.
- Existe CRUD de productos y categorias.
- Hay filtros por categoria y consulta por rango de precio.

Importancia:

- Es el nucleo visual del sistema porque representa el catalogo que ve el usuario.

### Persona 3 - Carrito de compras

Responsable de:

- Agregar producto al carrito (HU08).
- Modificar cantidad (HU09).
- Eliminar producto del carrito (HU10).

Debe hacer:

- `Carrito`.
- `DetalleCarrito`.
- Logica para sumar y restar productos.
- Vista del carrito.
- Calculo del total.

Estado actual:

- El proyecto ya cuenta con `Carrito` y `DetalleCarrito`.
- Se pueden agregar productos al carrito.
- Se pueden sumar, restar, actualizar y eliminar productos.
- Se calcula el total del carrito.
- Existe vista del carrito.

Importancia:

- Este modulo tiene logica importante porque conecta el catalogo con el proceso de compra.

### Persona 4 - Pedidos y pagos

Responsable de:

- Confirmar compra (HU11).
- Historial de pedidos (HU12).
- Pago basico opcional.

Debe hacer:

- Pedido o factura.
- Detalle del pedido o venta.
- Pago basico.
- Generar pedido desde carrito.
- Guardar en base de datos.
- Mostrar historial.

Estado actual:

- El proyecto usa `Factura` para representar pedidos.
- El proyecto usa `Venta` para representar el detalle de productos comprados.
- Existe checkout individual.
- Existe checkout desde carrito.
- Se guarda la factura en base de datos.
- Se guarda el detalle de ventas por factura.
- Existe historial de pedidos con detalle.
- El pago es basico y funciona como formulario visual.

Importancia:

- Este modulo conecta todo el sistema: productos, carrito, compras e historial.

## Tecnologias

- Java 21
- Spring Boot 3.5.16
- Spring Web
- Spring Data JPA / Hibernate
- Thymeleaf
- Bootstrap 5
- Font Awesome
- jQuery
- MySQL
- Maven

## Modulos implementados

### Productos y categorias

- CRUD de productos.
- CRUD de categorias.
- Listado publico de productos activos.
- Filtro por categoria.
- Consulta de productos por rango de precio.
- Manejo de imagen, precio, existencias y estado activo.

### Carrito de compras

- Agregar productos al carrito.
- Aumentar, reducir y actualizar cantidades.
- Eliminar productos del carrito.
- Vaciar carrito.
- Calculo de subtotal por producto y total del carrito.
- Compra completa desde el carrito.

### Pedidos e historial

- Checkout individual desde un producto.
- Checkout de carrito completo.
- Calculo de subtotal, IVA 13% y total.
- Registro de factura.
- Registro de ventas por producto dentro de cada factura.
- Descuento de existencias al confirmar la compra.
- Historial de pedidos con detalle de productos comprados.

### Usuarios y roles

- Entidades `Usuario` y `Rol`.
- Repositorios, servicios y controlador de usuarios.
- Registro de usuarios.
- Login basico por correo y contrasena.
- CRUD basico de usuarios.
- Accesos de usuarios desde el menu principal.

> Nota: el login actual es funcional pero basico. Todavia no usa Spring Security, sesiones reales, logout ni contrasenas encriptadas.

## Requisitos de ejecucion

- JDK 21 o superior.
- Maven 3.9 o superior.
- MySQL 8 o compatible.
- Una base de datos llamada `atalaya` para ejecucion local, o una base externa compatible como Aiven MySQL.

## Configuracion de base de datos

La aplicacion usa variables de entorno para permitir ejecucion local y despliegue en Render/Aiven.

Valores principales:

```properties
DB_URL=jdbc:mysql://localhost:3306/atalaya
DB_USERNAME=root
DB_PASSWORD=tu_password
```

Si no se configuran variables, la aplicacion intenta usar:

```properties
jdbc:mysql://localhost:3306/atalaya
usuario: root
password: vacio
```

Para Aiven, el `DB_URL` debe tener formato similar a:

```properties
jdbc:mysql://HOST_AIVEN:PUERTO/defaultdb?ssl-mode=REQUIRED&allowPublicKeyRetrieval=true
```

## Ejecucion local

Desde la raiz del proyecto:

```bash
mvn spring-boot:run
```

O para compilar sin ejecutar pruebas:

```bash
mvn -DskipTests compile
```

La aplicacion queda disponible por defecto en:

```text
http://localhost:8080/producto
```

## Despliegue en Render

El proyecto incluye `Dockerfile`, por lo que puede desplegarse en Render como Web Service usando Docker.

Variables recomendadas en Render:

```text
PORT=<asignado por Render>
DB_URL=jdbc:mysql://HOST_AIVEN:PUERTO/defaultdb?ssl-mode=REQUIRED&allowPublicKeyRetrieval=true
DB_USERNAME=avnadmin
DB_PASSWORD=<password de Aiven>
```

La aplicacion usa `server.port=${PORT:8080}`, por lo que respeta el puerto asignado por Render.

## Estructura principal

```text
src/main/java/com/atalaya
├── controller
├── domain
├── repository
└── service

src/main/resources/templates
├── carrito
├── categoria
├── consultas
├── factura
├── general
├── producto
└── usuario
```

## Estado del avance

Completado o funcional a nivel preliminar:

- Catalogo de productos.
- Administracion de productos y categorias.
- Carrito de compras.
- Compra individual y compra desde carrito.
- Historial de pedidos con detalle.
- Registro, login basico y CRUD de usuarios.
- Configuracion preparada para MySQL local o Aiven.
- Dockerfile para despliegue.

Pendiente o recomendado para siguientes iteraciones:

- Integrar Spring Security.
- Encriptar contrasenas.
- Manejar sesiones de usuario y logout.
- Asociar pedidos a usuarios autenticados.
- Validar roles para proteger pantallas administrativas.
- Mejorar manejo de errores y mensajes al usuario.
- Agregar pruebas automatizadas.
- Revisar datos iniciales y script SQL segun el modelo actual de JPA.
