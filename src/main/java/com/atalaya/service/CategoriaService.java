package com.atalaya.service;

import com.atalaya.domain.Categoria;
import com.atalaya.repository.CategoriaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // Constructor 
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Listar (sin Firebase por ahora despues lo incorporo)
    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }

    // Obtener
    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }

    // Guardar (Sin imagen hasta que agregue Firebase)
    @Transactional
    public void save(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    // Eliminar con validación
    @Transactional
    public void delete(Integer idCategoria) {

        if (!categoriaRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException(
                "La categoria con ID " + idCategoria + " no existe!");
        }

        try {
            categoriaRepository.deleteById(idCategoria);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                "No se puede eliminar la categoria, tiene productos asociados");
        }
    }
}
