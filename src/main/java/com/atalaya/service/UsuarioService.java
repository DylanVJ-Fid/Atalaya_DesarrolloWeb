package com.atalaya.service;

import com.atalaya.domain.Usuario;
import com.atalaya.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    @Transactional(readOnly = true)
    public boolean validarLogin(String correo, String password) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            return false;
        }

        return usuario.getPassword().equals(password) && Boolean.TRUE.equals(usuario.getActivo());
    }
}