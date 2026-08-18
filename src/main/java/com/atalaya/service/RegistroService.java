package com.atalaya.service;

import com.atalaya.domain.Usuario;
import com.atalaya.repository.UsuarioRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//cambio final agregado
@Service
public class RegistroService {

    private final UsuarioRepository usuarioRepository;
    private final CorreoService correoService;
    private final UsuarioService usuarioService;

    public RegistroService(UsuarioRepository usuarioRepository,
            CorreoService correoService,
            UsuarioService usuarioService) {

        this.usuarioRepository = usuarioRepository;
        this.correoService = correoService;
        this.usuarioService = usuarioService;
    }

    //cambio final agregado
    @Transactional
    public void registrar(Usuario usuario) {

        String claveActivacion = UUID.randomUUID().toString();

        usuario.setPassword(claveActivacion);
        usuario.setActivo(false);

        usuarioRepository.save(usuario);

        String enlace = "http://localhost:8080/registro/activacion/"
                + usuario.getCorreo() + "/" + claveActivacion;

        String contenido = """
                <h2>Bienvenido a Atalaya</h2>
                <p>Gracias por registrarte.</p>
                <p>Para configurar y activar tu cuenta, haz clic en el siguiente enlace:</p>
                <p>
                    <a href="%s">Configurar mi cuenta</a>
                </p>
                """.formatted(enlace);

        try {
            correoService.enviarCorreoHtml(
                    usuario.getCorreo(),
                    "Activación de cuenta - Atalaya",
                    contenido
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "No se pudo enviar el correo de activación", e);
        }
    }

    //cambio final agregado
    public Usuario buscarUsuario(String correo, String claveActivacion) {

        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario != null
                && usuario.getPassword().equals(claveActivacion)
                && Boolean.FALSE.equals(usuario.getActivo())) {

            return usuario;
        }

        return null;
    }

    //cambio final agregado
    @Transactional
    public boolean activar(
            Usuario usuario,
            String claveActivacion,
            String nuevaPassword) {

        Usuario usuarioActual = usuarioRepository.findByCorreo(
                usuario.getCorreo()
        );

        if (usuarioActual == null
                || !usuarioActual.getPassword().equals(claveActivacion)
                || Boolean.TRUE.equals(usuarioActual.getActivo())) {

            return false;
        }

        usuarioActual.setNombre(usuario.getNombre());
        usuarioActual.setPassword(nuevaPassword);
        usuarioActual.setActivo(true);

        usuarioService.guardar(usuarioActual);

        return true;
    }
}