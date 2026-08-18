package com.atalaya.controller;

import com.atalaya.domain.Usuario;
import com.atalaya.service.RolService;
import com.atalaya.service.RegistroService;
import com.atalaya.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final RegistroService registroService;

    public UsuarioController(UsuarioService usuarioService,
            RolService rolService,
            RegistroService registroService) {

        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.registroService = registroService;
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
        return "usuario/registro";
    }

    // Guardar usuario
    @PostMapping("/guardar")
    public String guardar(Usuario usuario, Model model) {

        Usuario existente = usuarioService.buscarPorCorreo(usuario.getCorreo());

        if (existente != null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("error",
                    "Ya existe una cuenta registrada con ese correo.");
            return "usuario/registro";
        }

        // USER representa al cliente normal
        usuario.getRoles().add(
                rolService.buscarPorNombre("USER")
        );

        registroService.registrar(usuario);

        return "redirect:/usuario/verificar?correo=" + usuario.getCorreo();
    }

    // Página para indicar que debe verificar el correo
    //cambio final agregado
    @GetMapping("/verificar")
    public String verificarCorreo(
            @RequestParam String correo,
            Model model) {

        model.addAttribute("correo", correo);

        return "usuario/verificar";
    }

    // Listado de usuarios
    @GetMapping("/listado")
    public String listado(Model model) {

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("roles", rolService.listar());

        return "usuario/listado";
    }

    // Modificar usuario
    @GetMapping("/modificar/{idUsuario}")
    public String modificar(
            @PathVariable Integer idUsuario,
            Model model) {

        Usuario usuario =
                usuarioService.buscarPorId(idUsuario).orElse(null);

        if (usuario == null) {
            return "redirect:/usuario/listado";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.listar());

        return "usuario/modificar";
    }

    // Actualizar usuario
    @PostMapping("/actualizar")
    public String actualizar(Usuario usuario) {

        Usuario usuarioActual =
                usuarioService.buscarPorId(
                        usuario.getIdUsuario()
                ).orElse(null);

        if (usuarioActual == null) {
            return "redirect:/usuario/listado";
        }

        // Si no escribió una nueva contraseña,
        // mantenemos la contraseña actual
        if (usuario.getPassword() == null
                || usuario.getPassword().isBlank()) {

            usuario.setPassword(usuarioActual.getPassword());
        }

        // Si no se enviaron roles, conservamos los actuales
        if (usuario.getRoles() == null
                || usuario.getRoles().isEmpty()) {

            usuario.setRoles(usuarioActual.getRoles());
        }

        usuarioService.guardar(usuario);

        return "redirect:/usuario/listado";
    }

    // Eliminar usuario
    @GetMapping("/eliminar/{idUsuario}")
    public String eliminar(
            @PathVariable Integer idUsuario) {

        usuarioService.eliminar(idUsuario);

        return "redirect:/usuario/listado";
    }
}
