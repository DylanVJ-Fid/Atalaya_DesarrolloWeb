package com.atalaya.service;

import com.atalaya.domain.Carrito;
import com.atalaya.domain.DetalleCarrito;
import com.atalaya.domain.Producto;
import com.atalaya.repository.CarritoRepository;
import com.atalaya.repository.DetalleCarritoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final DetalleCarritoRepository detalleCarritoRepository;
    private final ProductoService productoService;

    public CarritoService(CarritoRepository carritoRepository,
            DetalleCarritoRepository detalleCarritoRepository,
            ProductoService productoService) {
        this.carritoRepository = carritoRepository;
        this.detalleCarritoRepository = detalleCarritoRepository;
        this.productoService = productoService;
    }

    public Carrito getOrCreateCarrito(Integer idCarrito) {
        if (idCarrito != null) {
            var carrito = carritoRepository.findByIdCarritoAndActivoTrue(idCarrito);
            if (carrito.isPresent()) {
                return carrito.get();
            }
        }

        Carrito carrito = new Carrito();
        carrito.setFechaCreacion(LocalDateTime.now());
        carrito.setActivo(true);
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito agregar(Integer idCarrito, Integer idProducto) {
        Carrito carrito = getOrCreateCarrito(idCarrito);
        productoService.buscarPorId(idProducto)
                .filter(this::tieneExistencias)
                .ifPresent(producto -> agregarDetalle(carrito, producto));
        return carrito;
    }

    @Transactional
    public void actualizar(Integer idCarrito, Integer idProducto, Integer cantidad) {
        if (idCarrito == null || cantidad == null) {
            return;
        }

        detalleCarritoRepository.findByCarritoIdCarritoAndProductoIdProducto(idCarrito, idProducto)
                .ifPresent(detalle -> actualizarDetalle(detalle, cantidad));
    }

    @Transactional
    public void sumar(Integer idCarrito, Integer idProducto) {
        if (idCarrito == null) {
            return;
        }

        detalleCarritoRepository.findByCarritoIdCarritoAndProductoIdProducto(idCarrito, idProducto)
                .ifPresent(detalle -> actualizarDetalle(detalle, detalle.getCantidad() + 1));
    }

    @Transactional
    public void restar(Integer idCarrito, Integer idProducto) {
        if (idCarrito == null) {
            return;
        }

        detalleCarritoRepository.findByCarritoIdCarritoAndProductoIdProducto(idCarrito, idProducto)
                .ifPresent(detalle -> actualizarDetalle(detalle, detalle.getCantidad() - 1));
    }

    @Transactional
    public void eliminar(Integer idCarrito, Integer idProducto) {
        if (idCarrito == null) {
            return;
        }

        detalleCarritoRepository.findByCarritoIdCarritoAndProductoIdProducto(idCarrito, idProducto)
                .ifPresent(detalleCarritoRepository::delete);
    }

    @Transactional
    public void vaciar(Integer idCarrito) {
        if (idCarrito != null) {
            detalleCarritoRepository.deleteByCarritoIdCarrito(idCarrito);
        }
    }

    public List<DetalleCarrito> getDetalles(Integer idCarrito) {
        if (idCarrito == null) {
            return List.of();
        }
        return detalleCarritoRepository.findByCarritoIdCarrito(idCarrito);
    }

    public Integer getCantidadProductos(Integer idCarrito) {
        return getDetalles(idCarrito)
                .stream()
                .mapToInt(DetalleCarrito::getCantidad)
                .sum();
    }

    public BigDecimal getTotal(Integer idCarrito) {
        return getDetalles(idCarrito)
                .stream()
                .map(DetalleCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isVacio(Integer idCarrito) {
        return getDetalles(idCarrito).isEmpty();
    }

    private void agregarDetalle(Carrito carrito, Producto producto) {
        var detalle = detalleCarritoRepository
                .findByCarritoIdCarritoAndProductoIdProducto(carrito.getIdCarrito(), producto.getIdProducto());

        if (detalle.isPresent()) {
            actualizarDetalle(detalle.get(), detalle.get().getCantidad() + 1);
            return;
        }

        detalleCarritoRepository.save(new DetalleCarrito(carrito, producto, 1));
    }

    private void actualizarDetalle(DetalleCarrito detalle, Integer cantidad) {
        if (cantidad <= 0) {
            detalleCarritoRepository.delete(detalle);
            return;
        }

        Integer existencias = detalle.getProducto().getExistencias();
        if (existencias == null || existencias <= 0) {
            detalleCarritoRepository.delete(detalle);
            return;
        }

        detalle.setCantidad(Math.min(cantidad, existencias));
        detalleCarritoRepository.save(detalle);
    }

    private boolean tieneExistencias(Producto producto) {
        return Boolean.TRUE.equals(producto.getActivo())
                && producto.getExistencias() != null
                && producto.getExistencias() > 0;
    }
}
