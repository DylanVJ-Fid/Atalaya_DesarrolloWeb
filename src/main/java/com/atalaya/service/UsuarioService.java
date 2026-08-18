package com.atalaya.service;

import com.atalaya.domain.Usuario;
import com.atalaya.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional
    public void guardar(Usuario usuario) {

        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }

        if (usuario.getPassword() != null
                && !usuario.getPassword().isBlank()
                && !usuario.getPassword().startsWith("$2a$")
                && !usuario.getPassword().startsWith("$2b$")
                && !usuario.getPassword().startsWith("$2y$")
                && !usuario.getPassword().startsWith("{noop}")
                && !usuario.getPassword().startsWith("{bcrypt}")) {

            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            throw new UsernameNotFoundException(
                    "Usuario no encontrado: " + correo);
        }

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new UsernameNotFoundException(
                    "El usuario está inactivo: " + correo);
        }

        var roles = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .collect(Collectors.toSet());

        return new User(
                usuario.getCorreo(),
                usuario.getPassword(),
                roles
        );
    }
}
