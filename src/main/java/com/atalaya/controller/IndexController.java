package com.atalaya.controller;

import com.atalaya.domain.Categoria;
import com.atalaya.service.CategoriaService;
import com.atalaya.service.ProductoService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/producto")
public class IndexController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public IndexController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping({"", "/"})
    public String cargarIndex(Model model) {
        var productos = productoService.listar();
        model.addAttribute("productos", productos);

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        return "index";
    }

    @GetMapping("/consultas/{idCategoria}")
    public String listado(@PathVariable("idCategoria") Integer idCategoria, Model model) {
        Optional<Categoria> categoriaOpt = categoriaService.getCategoria(idCategoria);

        if (categoriaOpt.isEmpty()) {
            model.addAttribute("productos", java.util.Collections.EMPTY_LIST);
        } else {
            var categoria = categoriaOpt.get();
            var productos = categoria.getProductos();
            model.addAttribute("productos", productos);
        }

        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);

        return "index";
    }
}
