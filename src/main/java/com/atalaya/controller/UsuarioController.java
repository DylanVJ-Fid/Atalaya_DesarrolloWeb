package com.atalaya.controller;

import com.atalaya.domain.Usuario;
import com.atalaya.service.RolService;
import com.atalaya.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @GetMapping("/login")
    public String login() {
        return "usuario/login";
    }

    @PostMapping("/validar")
    public String validarLogin(@RequestParam String correo,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        Usuario usuario = usuarioService.buscarPorCorreo(correo);

        if (usuario != null
                && usuario.getPassword().equals(password)
                && Boolean.TRUE.equals(usuario.getActivo())) {

            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/producto/";
        }

        model.addAttribute("error", "Correo o contraseña incorrectos");
        return "usuario/login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.listar());
        return "usuario/registro";
    }

    @PostMapping("/guardar")
    public String guardar(Usuario usuario) {
        usuarioService.guardar(usuario);
        return "redirect:/usuario/listado";
    }

    @GetMapping("/listado")
    public String listado(Model model, HttpSession session) {

        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/usuario/login";
        }
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("roles", rolService.listar());
        return "usuario/listado";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable Integer idUsuario, Model model, HttpSession session) {

        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/usuario/login";
        }

        Usuario usuario = usuarioService.buscarPorId(idUsuario).orElse(null);

        if (usuario == null) {
            return "redirect:/usuario/listado";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.listar());
        return "usuario/modificar";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/usuario/login";
    }

    @GetMapping("/eliminar/{idUsuario}")
    public String eliminar(@PathVariable Integer idUsuario, HttpSession session) {

        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/usuario/login";
        }

        usuarioService.eliminar(idUsuario);
        return "redirect:/usuario/listado";
    }
}
