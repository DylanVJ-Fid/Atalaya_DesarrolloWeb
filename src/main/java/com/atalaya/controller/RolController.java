package com.atalaya.controller;

import com.atalaya.domain.Rol;
import com.atalaya.service.RolService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rol")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        if (!model.containsAttribute("rol")) {
            model.addAttribute("rol", new Rol());
        }
        model.addAttribute("roles", rolService.listar());
        return "rol/listado";
    }

    @GetMapping("/modificar/{idRol}")
    public String modificar(@PathVariable Integer idRol,
            Model model,
            RedirectAttributes redirectAttributes) {

        return rolService.buscarPorId(idRol)
                .map(rol -> {
                    model.addAttribute("rol", rol);
                    model.addAttribute("roles", rolService.listar());
                    return "rol/listado";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "El rol no existe.");
                    return "redirect:/rol/listado";
                });
    }

    @PostMapping("/guardar")
    public String guardar(Rol rol, RedirectAttributes redirectAttributes) {
        try {
            rolService.guardar(rol);
            redirectAttributes.addFlashAttribute("todoOk", "Rol guardado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rol/listado";
    }

    @PostMapping("/eliminar/{idRol}")
    public String eliminar(@PathVariable Integer idRol,
            RedirectAttributes redirectAttributes) {
        try {
            rolService.eliminar(idRol);
            redirectAttributes.addFlashAttribute("todoOk", "Rol eliminado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rol/listado";
    }
}
