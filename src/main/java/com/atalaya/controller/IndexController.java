package com.atalaya.controller;

import com.atalaya.service.CategoriaService;
import com.atalaya.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = {"", "/producto"})
public class IndexController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public IndexController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/")
    public String cargarIndex(@RequestParam(required = false) String descripcion, Model model) {

        var productos = productoService.listar();

        if (descripcion != null && !descripcion.isEmpty()) {
            productos = productoService.filtrar(descripcion, null);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("descripcion", descripcion);

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        return "index";
    }

    @GetMapping("/consultas/{idCategoria}")
    public String listado(@PathVariable("idCategoria") Integer idCategoria, Model model) {

        var productos = productoService.filtrar(null, idCategoria);
        model.addAttribute("productos", productos);

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        return "index";
    }
}