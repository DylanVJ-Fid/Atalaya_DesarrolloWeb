# Atalaya — Sistema de comercio electrónico

Atalaya es una tienda virtual desarrollada con Java y Spring Boot. Permite administrar categorías, productos, usuarios y roles; consultar el catálogo; utilizar un carrito de compras; confirmar pedidos y consultar el historial de facturas.

## Estado del proyecto

- Catálogo y detalle de productos.
- CRUD de productos, categorías y usuarios.
- Registro, activación por correo, autenticación y autorización con Spring Security.
- Carrito con control de cantidades, existencias, subtotales y total.
- Checkout individual y desde el carrito.
- Registro transaccional mediante facturas y ventas.
- Historial y detalle de pedidos.
- Persistencia MySQL local o en Aiven.
- Despliegue mediante Docker en Render.
- Configuración preparada para Firebase Storage.

## Tecnologías

- Java 21.
- Spring Boot 3.5.16.
- Spring Web, Spring Data JPA, Spring Security y Spring Mail.
- Hibernate y MySQL.
- Thymeleaf, Bootstrap 5, Font Awesome y jQuery.
- Maven y Docker.

## Requisitos

- JDK 21 o superior.
- Maven 3.9 o superior.
- MySQL 8 o una instancia compatible como Aiven MySQL.
- Git.

## Instalación rápida

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/DylanVJ-Fid/Atalaya_DesarrolloWeb.git
   cd Atalaya_DesarrolloWeb
   ```

2. Crear y poblar la base de datos siguiendo la sección siguiente.

3. Definir las variables de entorno:

   ```text
   DB_URL=jdbc:mysql://localhost:3306/atalaya
   DB_USERNAME=root
   DB_PASSWORD=contraseña_mysql
   MAILERSEND_API_TOKEN=token_de_mailersend
   MAIL_FROM=correo_de_dominio_verificado
   ```

4. Ejecutar la aplicación:

   ```bash
   mvn spring-boot:run
   ```

5. Abrir `https://atalaya-desarrolloweb-gljc.onrender.com/`.

Las credenciales nunca deben guardarse en Git. Para desarrollo local se puede crear `src/main/resources/application-local.properties`; este archivo está excluido mediante `.gitignore`.

## Creación y población de la base de datos

El archivo [`Tabla Atalaya.sql`](./Tabla%20Atalaya.sql) crea la base `atalaya`, genera sus tablas, relaciones y datos iniciales. Incluye categorías, productos, usuarios, roles y permisos de ejemplo.

> **Advertencia:** el script comienza con `DROP DATABASE IF EXISTS atalaya`; al ejecutarlo elimina cualquier base local anterior con ese nombre y vuelve a crearla.

### Opción A: MySQL Workbench

1. Iniciar MySQL y abrir MySQL Workbench.
2. Conectarse al servidor local.
3. Seleccionar **File > Open SQL Script**.
4. Abrir `Tabla Atalaya.sql`.
5. Ejecutar todo el script con el botón del rayo.
6. Actualizar la lista de esquemas y verificar la base `atalaya`.

### Opción B: terminal de MySQL

Desde la raíz del proyecto:

```bash
mysql -u root -p < "Tabla Atalaya.sql"
```

Después se puede validar la instalación:

```sql
USE atalaya;
SHOW TABLES;
SELECT COUNT(*) AS categorias FROM categoria;
SELECT COUNT(*) AS productos FROM producto;
SELECT COUNT(*) AS usuarios FROM usuario;
```

### Uso con Aiven

La instancia administrada normalmente utiliza una base existente como `defaultdb`. En ese caso no se deben ejecutar las instrucciones `DROP DATABASE`, `CREATE DATABASE` y `USE atalaya`. Se debe seleccionar `defaultdb` en el cliente SQL y ejecutar desde la primera sentencia `CREATE TABLE`.

La conexión de Spring se configura así:

```text
DB_URL=jdbc:mysql://HOST:PUERTO/defaultdb?sslMode=REQUIRED&allowPublicKeyRetrieval=true
DB_USERNAME=avnadmin
DB_PASSWORD=contraseña_de_Aiven
```

## Modelo de datos

El script contiene 11 tablas; por lo tanto, supera el mínimo solicitado de 8:

| Tabla | Propósito |
| --- | --- |
| `categoria` | Clasificación de productos. |
| `producto` | Catálogo, precios y existencias. |
| `carrito` | Encabezado del carrito activo. |
| `detalle_carrito` | Productos y cantidades del carrito. |
| `usuario` | Cuentas registradas. |
| `rol` | Roles de autorización. |
| `usuario_rol` | Relación de muchos a muchos entre usuarios y roles. |
| `ruta` | Rutas y requisito de autorización. |
| `constante` | Valores públicos configurables de la aplicación. |
| `factura` | Encabezado de una compra confirmada. |
| `venta` | Detalle transaccional de productos comprados. |

`factura` y `venta` registran transacciones reales del sistema. Cada compra conserva fecha, total, estado, datos de facturación, producto, cantidad y precio histórico. De esta forma, un cambio posterior en el precio del catálogo no altera la compra registrada.

## Funcionalidades por módulo

### Usuarios y seguridad

- Registro de usuarios (HU01).
- Inicio y cierre de sesión (HU02).
- Roles y protección de pantallas administrativas (HU19).
- Activación de cuenta mediante correo.
- Contraseñas codificadas mediante Spring Security.

### Productos y categorías

- Catálogo y filtros (HU04).
- Detalle de producto (HU07).
- CRUD de productos (HU14, HU15 y HU16).
- Administración de categorías (HU17).

### Carrito

- Agregar productos (HU08).
- Modificar cantidades sin superar existencias (HU09).
- Eliminar productos (HU10).
- Vaciar carrito y calcular subtotales y total.

### Pedidos

- Confirmar compra (HU11).
- Consultar historial y detalle (HU12).
- Calcular subtotal, IVA del 13 % y total.
- Descontar existencias al confirmar la compra.

## Código adicional no visto en clase

El proyecto incorpora procesamiento asíncrono para el correo de activación mediante `@EnableAsync`, `@Async` y un `ThreadPoolTaskExecutor` dedicado.

En el flujo anterior, la petición HTTP esperaba a que Gmail terminara DNS, conexión, TLS, autenticación y envío SMTP. En Render esa operación podía congelar visualmente el registro. Ahora el flujo es:

1. Guardar el usuario inactivo y generar su token.
2. Enviar la tarea de correo a `correoExecutor`.
3. Redirigir inmediatamente a la pantalla de verificación.
4. Procesar el correo en segundo plano con timeouts de 5 segundos.

El ejecutor utiliza entre uno y dos hilos y una cola de 50 tareas. Esto evita crear hilos sin límite y mantiene aislado el trabajo SMTP del hilo que responde al navegador.

## Despliegue en Render

El `Dockerfile` compila con Maven y ejecuta la aplicación sobre Java 21. En el Web Service se deben configurar:

```text
DB_URL=jdbc:mysql://HOST_AIVEN:PUERTO/defaultdb?sslMode=REQUIRED&allowPublicKeyRetrieval=true
DB_USERNAME=avnadmin
DB_PASSWORD=contraseña_de_Aiven
MAILERSEND_API_TOKEN=token_de_mailersend
MAIL_FROM=correo_de_dominio_verificado
SPRING_PROFILES_ACTIVE=default
```

El enlace de activación se construye con el valor `servidor.http` de la tabla `constante`. El valor configurado para producción es `https://atalaya-desarrolloweb-gljc.onrender.com`.

```sql
UPDATE constante
SET valor = 'https://atalaya-desarrolloweb-gljc.onrender.com'
WHERE atributo = 'servidor.http';
```

Atalaya utiliza Gmail mediante TLS y el puerto 587. Esto funciona localmente con una contraseña de aplicación. Las instancias gratuitas de Render bloquean la salida por el puerto 587, por lo que Gmail requiere una instancia pagada o un proveedor accesible mediante HTTPS/otro puerto.

## Firebase Storage

La configuración utiliza:

```properties
firebase.bucket.name=atalaya-65141.firebasestorage.app
firebase.storage.path=atalaya
firebase.json.path=firebase
firebase.json.file=atalaya-65141-firebase-adminsdk-fbsvc-5c3535b580.json
```

El archivo JSON contiene una llave privada y está excluido del repositorio. Debe suministrarse de forma segura en cada ambiente. Las propiedades por sí solas no inicializan Firebase; se requiere la dependencia Firebase Admin y una clase de configuración antes de utilizar Storage desde el código.

Para Render, el contenido completo del JSON debe convertirse a Base64 y guardarse
en la variable secreta `FIREBASE_CREDENTIALS_BASE64`. En desarrollo local se usa
el archivo JSON ignorado dentro de `src/main/resources/firebase`.

## Internacionalización

La interfaz actual está redactada en español. Si el requisito académico exige internacionalización técnica con cambio de idioma, todavía se deben añadir archivos `messages_es.properties`, `messages_en.properties`, un `LocaleResolver` y un selector de idioma. No se marca como finalizada hasta comprobar ese comportamiento.

## Artículo científico IEEE

Documento colaborativo para edición del equipo:

- **Enlace compartido:** _pendiente de agregar por el equipo_.
- **Estado:** en elaboración.
- **Entrega final:** exportar y entregar en PDF cuando contenido, referencias y formato IEEE hayan sido revisados.

### Análisis de oferta de mercado

Atalaya participa en el mercado de comercio electrónico de equipo tecnológico. La oferta existente puede agruparse en tres tipos:

| Tipo de competidor | Fortalezas | Oportunidad para Atalaya |
| --- | --- | --- |
| Grandes marketplaces | Catálogo amplio, reconocimiento y logística. | Experiencia más simple y catálogo especializado. |
| Tiendas tecnológicas nacionales | Soporte local, garantía y disponibilidad inmediata. | Comparación clara, compra digital y seguimiento centralizado. |
| Venta por redes sociales | Atención directa y costos operativos bajos. | Mayor formalidad mediante cuentas, inventario, carrito y comprobante. |

La propuesta de Atalaya se diferencia por integrar catálogo especializado, existencias, carrito, seguridad por roles, activación de cuentas e historial transaccional en una sola aplicación. Para defender el análisis ante el profesor conviene explicar que el proyecto no busca competir por tamaño con un marketplace global; busca digitalizar de manera trazable el proceso de una tienda tecnológica local.

## Estructura principal

```text
src/main/java/com/atalaya
|-- config
|-- controller
|-- domain
|-- repository
`-- service

src/main/resources
|-- templates
|-- application.properties
`-- firebase (credencial local no publicada)
```
