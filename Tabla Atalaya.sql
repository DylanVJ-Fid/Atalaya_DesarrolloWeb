/*
  Script de base de datos para ATALAYA
*/

-- CREACIÓN DE BASE DE DATOS
DROP DATABASE IF EXISTS atalaya;
CREATE DATABASE atalaya;
USE atalaya;

-- TABLA: CATEGORIA
CREATE TABLE categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(100) NOT NULL,
    ruta_imagen VARCHAR(1024),
    activo BOOLEAN
);

-- TABLA: PRODUCTO
CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    id_categoria INT,
    descripcion VARCHAR(200) NOT NULL,
    detalle TEXT,
    precio DECIMAL(10,2),
    existencias INT,
    ruta_imagen VARCHAR(1024),
    activo BOOLEAN,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

-- TABLA: CARRITO
CREATE TABLE carrito (
    id_carrito INT AUTO_INCREMENT PRIMARY KEY,
    fecha_creacion DATETIME,
    activo BOOLEAN
);

-- TABLA: DETALLE_CARRITO
CREATE TABLE detalle_carrito (
    id_detalle_carrito INT AUTO_INCREMENT PRIMARY KEY,
    id_carrito INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    FOREIGN KEY (id_carrito) REFERENCES carrito(id_carrito),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    UNIQUE KEY uk_carrito_producto (id_carrito, id_producto)
);

-- TABLA: USUARIO
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(512),
    nombre VARCHAR(100),
    apellidos VARCHAR(100),
    correo VARCHAR(100),
    telefono VARCHAR(20),
    activo BOOLEAN
);

-- TABLA: ROL
CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    rol VARCHAR(50)
);

-- TABLA: USUARIO_ROL
CREATE TABLE usuario_rol (
    id_usuario INT,
    id_rol INT,
    PRIMARY KEY (id_usuario, id_rol),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);


-- TABLA: RUTA
CREATE TABLE ruta (
    id_ruta INT AUTO_INCREMENT PRIMARY KEY,
    ruta VARCHAR(255),
    requiere_rol BOOLEAN
);

-- INSERT: CATEGORÍAS
INSERT INTO categoria (descripcion, ruta_imagen, activo) VALUES 
('Monitores', 'https://cyberteamcr.com/wp-content/uploads/2023/11/MONITOR-SAMSUNG-LF27T350FHNXZA.jpg', true),
('Teclados', 'https://cdn.shopify.com/s/files/1/1161/3498/products/logitech-teclado-para-gaming-prodigy-g213-1.jpg?v=1571445731', true),
('Componentes', 'https://compubetel.com/wp-content/uploads/2025/12/AC14008-700x700.png', true),
('Mouse','https://www.officedepot.co.cr/medias/1200ftw-1304000009.jpg?context=bWFzdGVyfHJvb3R8ODQ1MjJ8aW1hZ2UvanBlZ3xhREJqTDJneVlTOHhNalF6T0RFMk1EazVPRFF6TUM4eE1qQXdablIzWHpFek1EUXdNREF3TURrdWFuQm58MjE3ZGY2YjU5NjhjMGNkZThmNzhlM2VlNWUwNWNiYmQ0YWM3YTBmNTUwZGNiMTQ5MGJiYTRlODgwMWViNWU1Ng',true),
('Laptop','https://www.intelec.co.cr/wp-content/uploads/2026/05/82X700GCGJ-LENOVO-1200x1200-01.-700x700-1.jpg',true),
('Impresoras','https://www.tiendamonge.com/media/catalog/product/i/m/impresora-epson-l3250-negro-175067-1-frontal_2__1.jpg?optimize=medium&bg-color=255,255,255&fit=bounds&height=700&width=700&canvas=700:700',true),
('Audífonos','https://m.media-amazon.com/images/I/61U11HyIY0L._AC_UF894,1000_QL80_.jpg',true);

-- INSERT: PRODUCTOS
INSERT INTO producto (id_categoria, descripcion, detalle, precio, existencias, ruta_imagen, activo) VALUES
(1, 'Monitor Samsung', 'Curvo 27 pulgadas', 180000, 3, 'https://cyberteamcr.com/wp-content/uploads/2023/11/MONITOR-SAMSUNG-LF27T350FHNXZA.jpg', true),
(1, 'Monitor HP 24', 'Monitor Full HD', 120000, 5, 'https://www.cqnetcr.com/112630-large_default/monitor-hp-m24fw-24-led-ips-hdmi-vga-10001.jpg', true),
(1, 'Monitor LG UltraGear', '27 pulgadas Gamer', 210000, 4, 'https://img.pacifiko.com/PROD/resize/1/500x500/NzM4MjhjZm.jpg', true),
(1, 'Monitor ASUS TUF', '24 pulgadas 165Hz', 195000, 6, 'https://edropcr.com/wp-content/uploads/2025/03/Monitor-Asus-TUF-Gaming-VG27VQ-27_-MT027ASU05-1.jpg', true),
(2, 'Teclado Logitech', 'Mecánico RGB', 45000, 10, 'https://cdn.shopify.com/s/files/1/1161/3498/products/logitech-teclado-para-gaming-prodigy-g213-1.jpg?v=1571445731', true),
(2, 'Teclado Redragon K552', 'Switch Blue', 39000, 8, 'https://hftecnologia.com.ar/img/Public/1123-producto-xccx-9128.jpg', true),
(2, 'Teclado HyperX Alloy', 'Switch Red', 69000, 4, 'https://ddtech.mx/assets/uploads/4c85e38940740aec239d5ad6b506e986.jpg', true),
(2, 'Teclado Razer BlackWidow', 'RGB Mecánico', 85000, 5, 'https://walmartcr.vtexassets.com/arquivos/ids/1016470/image-09fd893d62e947fdab41a41a60c9804b.jpg?v=638972606164030000', true),
(3, 'Procesador Intel i5', '12va generación', 150000, 4, 'https://compubetel.com/wp-content/uploads/2025/12/AC14008-700x700.png', true),
(3, 'Procesador Ryzen 5 5600X', '6 núcleos', 165000, 5, 'https://jdgaming.es/wp-content/uploads/2025/11/AMD-Ryzen-5-5600x-tray.webp', true),
(3, 'Memoria RAM Kingston', '16GB DDR4', 45000, 12, 'https://media.kingston.com/kingston/product/FURY_Beast_Black_DDR4_1-sm.jpg', true),
(3, 'SSD Kingston NV2', '1TB NVMe', 62000, 9, 'https://extremetechcr.com/wp-content/uploads/2024/11/22143.jpg', true),
(4, 'Mouse Logitech G203', 'Mouse para gaming', 15000, 8, 'https://www.officedepot.co.cr/medias/1200ftw-1304000009.jpg?context=bWFzdGVyfHJvb3R8ODQ1MjJ8aW1hZ2UvanBlZ3xhREJqTDJneVlTOHhNalF6T0RFMk1EazVPRFF6TUM4eE1qQXdablIzWHpFek1EUXdNREF3TURrdWFuQm58MjE3ZGY2YjU5NjhjMGNkZThmNzhlM2VlNWUwNWNiYmQ0YWM3YTBmNTUwZGNiMTQ5MGJiYTRlODgwMWViNWU1Ng', true),
(4, 'Mouse Razer DeathAdder', 'Ergonómico', 35000, 6, 'https://holacompras.com/wp-content/uploads/2023/02/RZ01-03210300-R3M1-1.jpg', true),
(4, 'Mouse Logitech G502', '11 botones', 48000, 7, 'https://holacompras.com/wp-content/uploads/2023/01/910-006144-1.jpg', true),
(4, 'Mouse HyperX Pulsefire', 'RGB', 32000, 5, 'https://walmartcr.vtexassets.com/arquivos/ids/821195/83285-01.jpg?v=638704042704730000', true),
(5, 'Laptop Lenovo IdeaPad', 'Laptop Ryzen 5', 450000, 6, 'https://www.intelec.co.cr/wp-content/uploads/2026/05/82X700GCGJ-LENOVO-1200x1200-01.-700x700-1.jpg', true),
(5, 'Laptop HP Pavilion', 'Intel Core i5', 520000, 4, 'https://www.cqnetcr.com/113963-thickbox_default/laptop-hp-pavilion-gaming-15-ec2500la-156-ryzen5.jpg', true),
(5, 'Laptop ASUS VivoBook', '16GB RAM', 610000, 3, 'https://innovacellcr.com/cdn/shop/files/asus-x1502va-azul-frontal.jpg?v=1779824162', true),
(5, 'Laptop Acer Aspire 5', 'Ryzen 7', 575000, 5, 'https://www.officedepot.co.cr/medias/515ftw-1301000545.jpg?context=bWFzdGVyfHJvb3R8MTA2NzI5fGltYWdlL2pwZWd8YURSbUwyaGtNaTh4TWpVNE1UZ3pOelF6T1RBd05pODFNVFZtZEhkZk1UTXdNVEF3TURVME5TNXFjR2N8MzU4ZmM0NWYxYzA3Y2IxMDg2MmRmNDNhN2RlZmVjMDMzYjY4NmEyYmVkYjM1NTc0MDMyOTUxMmI5ODkwMTkzZA', true),
(6, 'Impresora Epson L3250', 'Impresora multifuncional', 98000, 5, 'https://www.tiendamonge.com/media/catalog/product/i/m/impresora-epson-l3250-negro-175067-1-frontal_2__1.jpg?optimize=medium&bg-color=255,255,255&fit=bounds&height=700&width=700&canvas=700:700', true),
(6, 'Impresora Canon G3110', 'Sistema continuo', 115000, 3, 'https://www.3ases.com/wp-content/uploads/2023/02/021313-MULTIFUNCIONAL-INALAMBRICA-PIXMA-G3110-INKJET-2.jpg', true),
(6, 'Impresora HP Smart Tank', 'WiFi', 145000, 4, 'https://www.officedepot.co.cr/medias/38393.jpg-1200ftw?context=bWFzdGVyfHJvb3R8MzcwMjMwfGltYWdlL2pwZWd8YURrNUwyaGtNUzh4TURjNU9UZzRNRFV6TmpBNU5DOHpPRE01TXk1cWNHZGZNVEl3TUdaMGR3fGVlMThjMjI5ODM5ZDI3YTgwMDgzN2UxZGM1NWZiODA3ZWE3NzBjYjBlMTk5YWFiZGM0YjNkN2IyYzk3OWMwZmY', true),
(6, 'Impresora Brother DCP', 'Láser', 165000, 2, 'https://walmartcr.vtexassets.com/arquivos/ids/1046863/image-ee1bebcb2ba04cf4af809fb041d0a03c.jpg?v=639039975360770000', true),
(7, 'Audífonos JBL Tune 510BT', 'Audífonos Bluetooth', 35000, 12, 'https://m.media-amazon.com/images/I/61U11HyIY0L._AC_UF894,1000_QL80_.jpg', true),
(7, 'Sony WH-CH520', 'Inalámbricos', 48000, 8, 'https://www.tiendauniversal.com/cdn/shop/files/027242932425_1_600x.jpg?v=1782738491', true),
(7, 'HyperX Cloud Stinger', 'Gaming', 52000, 6, 'https://www.intelec.co.cr/wp-content/uploads/2026/03/676A2AA-3.webp', true),
(7, 'Logitech G435', 'Lightspeed', 65000, 4, 'https://www.intelec.co.cr/wp-content/uploads/2022/02/Headset-Logitech-G435-Gaming-Inalambrico-Lightspeed-Azul-con-Fucsia-1000x1000.png.jpg', true);

-- INSERT: USUARIOS 
INSERT INTO usuario (username,password,nombre,apellidos,correo,telefono,activo)
VALUES
('maria','{noop}123','Maria','Gonzales','maria@gmail.com','8900-8878',true),
('juan','{noop}123','Juan','Perez','juan@gmail.com','6450-3689',true),
('carlos','{noop}123','Carlos','Mora','carlos@gmail.com','7898-8936',true),
('ana','{noop}123','Ana','Rodriguez','ana@gmail.com','6123-0166',true);

-- INSERT: ROLES
INSERT INTO rol (rol) VALUES 
('ADMIN'),
('VENDEDOR'),
('USER');


-- INSERT: USUARIO_ROL
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
(1,1),(1,2),(1,3),
(2,2),(2,3),
(3,3),
(4,3);

--  INSERT: RUTAS
INSERT INTO ruta (ruta, requiere_rol) VALUES 
('/',false),
('/index',false),
('/producto',false),
('/producto/listado',false),
('/producto/nuevo',true),
('/producto/guardar',true),
('/producto/eliminar/**',true),
('/producto/modificar/**',true),
('/carrito/listado',false),
('/carrito/agregar/**',false),
('/carrito/actualizar',false),
('/carrito/sumar/**',false),
('/carrito/restar/**',false),
('/carrito/eliminar/**',false),
('/carrito/vaciar',false),
('/css/**',false),
('/js/**',false),
('/webjars/**',false);
