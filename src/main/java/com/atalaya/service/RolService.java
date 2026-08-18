package com.atalaya.service;

import com.atalaya.domain.Rol;
import com.atalaya.repository.RolRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Transactional(readOnly = true)
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rol> buscarPorId(Integer idRol) {
        return rolRepository.findById(idRol);
    }

    @Transactional
    public void guardar(Rol rol) {
        String nombre = rol.getNombre() == null
                ? ""
                : rol.getNombre().trim().toUpperCase();

        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio.");
        }

        Rol existente = rolRepository.findByNombre(nombre);

        if (existente != null
                && !existente.getIdRol().equals(rol.getIdRol())) {
            throw new IllegalArgumentException("Ya existe un rol con ese nombre.");
        }

        rol.setNombre(nombre);
        rolRepository.save(rol);
    }

    @Transactional
    public void eliminar(Integer idRol) {
        if (!rolRepository.existsById(idRol)) {
            throw new IllegalArgumentException("El rol no existe.");
        }

        try {
            rolRepository.deleteById(idRol);
            rolRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar el rol porque está asignado a uno o más usuarios.",
                    e
            );
        }
    }

    @Transactional(readOnly = true)
    public Rol buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }
}
