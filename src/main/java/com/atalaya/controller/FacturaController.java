package com.atalaya.controller;

import com.atalaya.domain.Factura;
import com.atalaya.domain.Producto;
import com.atalaya.service.FacturaService;
import com.atalaya.service.ProductoService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/factura")
public class FacturaController {

    private final FacturaService facturaService;
    private final ProductoService productoService;

    public FacturaController(FacturaService facturaService,
                             ProductoService productoService) {
        this.facturaService = facturaService;
        this.productoService = productoService;
    }

    // Checkout
    @GetMapping("/checkout/{id}")
    public String checkout(@PathVariable Integer id, Model model) {

        Optional<Producto> producto = productoService.buscarPorId(id);

        if (producto.isPresent()) {
            Producto p = producto.get();

            BigDecimal subtotal = p.getPrecio();
            BigDecimal iva = subtotal.multiply(new BigDecimal("0.13"));
            BigDecimal total = subtotal.add(iva);

            model.addAttribute("producto", p);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("iva", iva);
            model.addAttribute("total", total);
        }

        return "factura/checkout";
    }

    // Historial
    @GetMapping("/historial")
    public String historial(Model model) {
        model.addAttribute("facturas", facturaService.listar());
        return "factura/historial";
    }

    // Guardar factura
    @PostMapping("/guardar")
    public String guardar(Factura factura,
                          @RequestParam Integer idProducto) {

        Optional<Producto> producto = productoService.buscarPorId(idProducto);

        if (producto.isPresent()) {

            Producto p = producto.get();

            BigDecimal subtotal = p.getPrecio();
            BigDecimal iva = subtotal.multiply(new BigDecimal("0.13"));
            BigDecimal total = subtotal.add(iva);

            factura.setFecha(LocalDateTime.now());
            factura.setEstado("Completado");
            factura.setTotal(total);

            facturaService.guardar(factura);
        }

        return "redirect:/factura/historial";
    }

    // Detalle
    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, Model model) {

        Optional<Factura> factura = facturaService.buscarPorId(id);

        factura.ifPresent(f -> model.addAttribute("factura", f));

        return "factura/checkout";
    }

    // Limpiar historial
    @PostMapping("/limpiar")
    public String limpiarHistorial() {
        facturaService.eliminarTodo();
        return "redirect:/factura/historial";
    }
}