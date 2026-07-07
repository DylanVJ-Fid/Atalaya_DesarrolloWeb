package com.atalaya.controller;

import com.atalaya.domain.Categoria;
import com.atalaya.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        model.addAttribute("categoria", new Categoria());
        return "categoria/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Categoria categoria) {
        return "categoria/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Categoria categoria,
            RedirectAttributes redirectAttributes) {

        categoriaService.save(categoria);
        redirectAttributes.addFlashAttribute("todoOk", "Guardado correctamente");

        return "redirect:/categoria/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idCategoria) {
        categoriaService.delete(idCategoria);
        return "redirect:/categoria/listado";
    }

    @GetMapping("/modifica/{idCategoria}")
    public String modifica(@PathVariable Integer idCategoria,
            Model model,
            RedirectAttributes redirectAttributes) {

        Optional<Categoria> categoria = categoriaService.getCategoria(idCategoria);

        if (categoria.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No existe");
            return "redirect:/categoria/listado";
        }

        model.addAttribute("categoria", categoria.get());
        return "categoria/modifica";
    }
}
