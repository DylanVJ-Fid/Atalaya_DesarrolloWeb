package com.atalaya.controller;

import com.atalaya.domain.Usuario;
import com.atalaya.service.RolService;
import com.atalaya.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    // Página de login
    @GetMapping("/login")
    public String login() {
        return "usuario/login";
    }

    // Página de registro
    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.listar());
        return "usuario/registro";
    }

    // Guardar usuario
    @PostMapping("/guardar")
    public String guardar(Usuario usuario, Model model) {

        Usuario existente = usuarioService.buscarPorCorreo(usuario.getCorreo());

        if (existente != null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", rolService.listar());
            model.addAttribute("error", "Ya existe una cuenta registrada con ese correo.");
            return "usuario/registro";
        }

        usuario.setActivo(true);

        // USER representa al cliente normal
        usuario.setRol(rolService.buscarPorNombre("USER"));

        usuarioService.guardar(usuario);

        return "redirect:/usuario/login?registro=true";
    }

    // Listado de usuarios - Spring Security controla que sea ADMIN
    @GetMapping("/listado")
    public String listado(Model model) {

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("roles", rolService.listar());

        return "usuario/listado";
    }

    // Modificar usuario
    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable Integer idUsuario, Model model) {

        Usuario usuario = usuarioService.buscarPorId(idUsuario).orElse(null);

        if (usuario == null) {
            return "redirect:/usuario/listado";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.listar());

        return "usuario/modificar";
    }

    //Actualizar usuario
    @PostMapping("/actualizar")
    public String actualizar(Usuario usuario) {

        Usuario usuarioActual
                = usuarioService.buscarPorId(usuario.getIdUsuario()).orElse(null);

        if (usuarioActual == null) {
            return "redirect:/usuario/listado";
        }

        // Si no escribió una nueva contraseña,
        // mantenemos la contraseña cifrada que ya tenía
        if (usuario.getPassword() == null
                || usuario.getPassword().isBlank()) {

            usuario.setPassword(usuarioActual.getPassword());
        }

        usuarioService.guardar(usuario);

        return "redirect:/usuario/listado";
    }

    // Eliminar usuario
    @GetMapping("/eliminar/{idUsuario}")
    public String eliminar(@PathVariable Integer idUsuario) {

        usuarioService.eliminar(idUsuario);

        return "redirect:/usuario/listado";
    }
}
