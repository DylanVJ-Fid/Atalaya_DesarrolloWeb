package com.atalaya.controller;

import com.atalaya.domain.DetalleCarrito;
import com.atalaya.domain.Factura;
import com.atalaya.domain.Producto;
import com.atalaya.service.CarritoService;
import com.atalaya.service.FacturaService;
import com.atalaya.service.ProductoService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/factura")
public class FacturaController {

    private static final String CARRITO_COOKIE = "atalayaCarrito";
    private static final BigDecimal IVA = new BigDecimal("0.13");

    private final FacturaService facturaService;
    private final ProductoService productoService;
    private final CarritoService carritoService;

    public FacturaController(FacturaService facturaService,
            ProductoService productoService,
            CarritoService carritoService) {
        this.facturaService = facturaService;
        this.productoService = productoService;
        this.carritoService = carritoService;
    }

    @GetMapping("/checkout/{id}")
    public String checkout(@PathVariable Integer id, Model model) {
        Optional<Producto> producto = productoService.buscarPorId(id);

        if (producto.isEmpty()) {
            return "redirect:/producto";
        }

        BigDecimal subtotal = producto.get().getPrecio();
        cargarTotales(model, subtotal);
        model.addAttribute("producto", producto.get());

        return "factura/checkout";
    }

    @GetMapping("/checkout-carrito")
    public String checkoutCarrito(@CookieValue(name = CARRITO_COOKIE, required = false) Integer idCarrito,
            Model model,
            RedirectAttributes redirectAttributes) {
        List<DetalleCarrito> detalles = carritoService.getDetalles(idCarrito);

        if (detalles.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El carrito esta vacio");
            return "redirect:/carrito/listado";
        }

        BigDecimal subtotal = carritoService.getTotal(idCarrito);
        cargarTotales(model, subtotal);
        model.addAttribute("detalles", detalles);
        model.addAttribute("cantidadProductos", carritoService.getCantidadProductos(idCarrito));

        return "factura/checkout-carrito";
    }

    @GetMapping("/historial")
    public String historial(Model model) {
        model.addAttribute("facturas", facturaService.listar());
        return "factura/historial";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idProducto,
            RedirectAttributes redirectAttributes) {
        Optional<Producto> producto = productoService.buscarPorId(idProducto);

        if (producto.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El producto no existe");
            return "redirect:/producto";
        }

        try {
            facturaService.comprarProducto(producto.get());
            redirectAttributes.addFlashAttribute("todoOk", "Compra realizada correctamente");
            return "redirect:/factura/historial";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/producto";
        }
    }

    @PostMapping("/guardar-carrito")
    public String guardarCarrito(@CookieValue(name = CARRITO_COOKIE, required = false) Integer idCarrito,
            RedirectAttributes redirectAttributes) {
        List<DetalleCarrito> detalles = carritoService.getDetalles(idCarrito);

        try {
            facturaService.comprarCarrito(detalles);
            carritoService.vaciar(idCarrito);
            redirectAttributes.addFlashAttribute("todoOk", "Compra del carrito realizada correctamente");
            return "redirect:/factura/historial";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/carrito/listado";
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Optional<Factura> factura = facturaService.buscarPorId(id);

        factura.ifPresent(f -> model.addAttribute("factura", f));

        return "factura/checkout";
    }

    @PostMapping("/limpiar")
    public String limpiarHistorial() {
        facturaService.eliminarTodo();
        return "redirect:/factura/historial";
    }

    private void cargarTotales(Model model, BigDecimal subtotal) {
        BigDecimal iva = subtotal.multiply(IVA);
        BigDecimal total = subtotal.add(iva);

        model.addAttribute("subtotal", subtotal);
        model.addAttribute("iva", iva);
        model.addAttribute("total", total);
    }
}
