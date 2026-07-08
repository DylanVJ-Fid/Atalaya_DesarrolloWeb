package com.atalaya.controller;

import com.atalaya.domain.Producto;
import com.atalaya.service.CategoriaService;
import com.atalaya.service.ProductoService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(@RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Long categoriaId,
            Model model, HttpSession session) {

        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/usuario/login";
        }

        var productos = productoService.filtrar(descripcion, categoriaId);

        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        model.addAttribute("producto", new Producto());

        return "producto/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Producto producto) {
        productoService.guardar(producto);
        return "redirect:/producto/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer id) {
        productoService.eliminar(id);
        return "redirect:/producto/listado";
    }

    @GetMapping("/modifica/{id}")
    public String modifica(@PathVariable Integer id, Model model) {

        Optional<Producto> producto = productoService.buscarPorId(id);

        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());

            var categorias = categoriaService.getCategorias(true);
            model.addAttribute("categorias", categorias);

            return "producto/modifica";
        }

        return "redirect:/producto/listado";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        Optional<Producto> producto = productoService.buscarPorId(id);

        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            return "producto/detalle";
        }

        return "redirect:/producto/listado";
    }
}
