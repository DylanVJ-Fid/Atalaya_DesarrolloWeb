package com.atalaya.controller;

import com.atalaya.domain.Usuario;
import com.atalaya.service.RegistroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//cambio final agregado
@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    //cambio final agregado
    @GetMapping("/activacion/{correo}/{claveActivacion}")
    public String activar(
            @PathVariable String correo,
            @PathVariable String claveActivacion,
            Model model) {

        Usuario usuario = registroService.buscarUsuario(
                correo,
                claveActivacion
        );

        if (usuario == null) {
            model.addAttribute("error",
                    "El enlace de activación no es válido.");

            return "usuario/activacion";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("claveActivacion", claveActivacion);

        return "usuario/activacion";
    }

    //cambio final agregado
    @PostMapping("/activar")
    public String activarCuenta(
            Usuario usuario,
            @RequestParam String claveActivacion,
            @RequestParam String nuevaPassword,
            Model model) {

        boolean activado = registroService.activar(
                usuario,
                claveActivacion,
                nuevaPassword
        );

        if (activado) {
            return "redirect:/usuario/login?activado=true";
        }

        model.addAttribute("error",
                "No se pudo activar la cuenta.");

        return "usuario/activacion";
    }
}