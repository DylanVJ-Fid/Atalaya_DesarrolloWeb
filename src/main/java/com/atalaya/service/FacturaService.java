package com.atalaya.service;

import com.atalaya.domain.DetalleCarrito;
import com.atalaya.domain.Factura;
import com.atalaya.domain.Producto;
import com.atalaya.domain.Venta;
import com.atalaya.repository.FacturaRepository;
import com.atalaya.repository.VentaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;
    private final ProductoService productoService;

    public FacturaService(FacturaRepository facturaRepository,
            VentaRepository ventaRepository,
            ProductoService productoService) {
        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
        this.productoService = productoService;
    }

    @Transactional(readOnly = true)
    public List<Factura> listar() {
        return facturaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Factura> buscarPorId(Long id) {
        return facturaRepository.findById(id);
    }

    @Transactional
    public Factura guardar(Factura factura) {
        return facturaRepository.save(factura);
    }

    @Transactional
    public Factura comprarProducto(Producto producto) {
        validarProductoDisponible(producto, 1);

        Factura factura = crearFactura(producto.getPrecio());
        guardarVenta(factura, producto, 1);
        descontarExistencias(producto, 1);
        return factura;
    }

    @Transactional
    public Factura comprarCarrito(List<DetalleCarrito> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El carrito esta vacio");
        }

        detalles.forEach(detalle -> validarProductoDisponible(detalle.getProducto(), detalle.getCantidad()));

        BigDecimal subtotal = detalles.stream()
                .map(DetalleCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Factura factura = crearFactura(subtotal);

        for (DetalleCarrito detalle : detalles) {
            Producto producto = detalle.getProducto();
            Integer cantidad = detalle.getCantidad();
            guardarVenta(factura, producto, cantidad);
            descontarExistencias(producto, cantidad);
        }

        return factura;
    }

    @Transactional
    public void eliminar(Long id) {
        facturaRepository.deleteById(id);
    }

    @Transactional
    public void eliminarTodo() {
        facturaRepository.deleteAll();
    }

    private Factura crearFactura(BigDecimal subtotal) {
        BigDecimal iva = subtotal.multiply(new BigDecimal("0.13"));
        BigDecimal total = subtotal.add(iva);
        LocalDateTime ahora = LocalDateTime.now();

        Factura factura = new Factura();
        factura.setFecha(ahora);
        factura.setEstado("Completado");
        factura.setTotal(total);
        factura.setFechaCreacion(ahora);
        factura.setFechaModificacion(ahora);
        return facturaRepository.save(factura);
    }

    private void guardarVenta(Factura factura, Producto producto, Integer cantidad) {
        LocalDateTime ahora = LocalDateTime.now();

        Venta venta = new Venta();
        venta.setFactura(factura);
        venta.setProducto(producto);
        venta.setCantidad(cantidad);
        venta.setPrecioHistorico(producto.getPrecio());
        venta.setFechaCreacion(ahora);
        venta.setFechaModificacion(ahora);
        ventaRepository.save(venta);
    }

    private void descontarExistencias(Producto producto, Integer cantidad) {
        producto.setExistencias(producto.getExistencias() - cantidad);
        productoService.guardar(producto);
    }

    private void validarProductoDisponible(Producto producto, Integer cantidad) {
        if (producto == null || !Boolean.TRUE.equals(producto.getActivo())) {
            throw new IllegalArgumentException("Hay productos no disponibles en el carrito");
        }

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de compra no es valida");
        }

        if (producto.getExistencias() == null || producto.getExistencias() < cantidad) {
            throw new IllegalArgumentException("No hay suficientes existencias para " + producto.getDescripcion());
        }
    }
}
