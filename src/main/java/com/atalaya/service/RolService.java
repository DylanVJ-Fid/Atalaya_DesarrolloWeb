package com.atalaya.service;

import com.atalaya.domain.Rol;
import com.atalaya.repository.RolRepository;
import java.util.List;
import java.util.Optional;
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
        rolRepository.save(rol);
    }

    @Transactional
    public void eliminar(Integer idRol) {
        rolRepository.deleteById(idRol);
    }

    @Transactional(readOnly = true)
    public Rol buscarPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }
}