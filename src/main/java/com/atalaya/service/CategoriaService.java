package com.atalaya.service;

import com.atalaya.domain.Categoria;
import com.atalaya.repository.CategoriaRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoriaService {

    // Se hace uso del repositorio CategoriaRepository
    private final CategoriaRepository categoriaRepository;

    // Se hace uso del servicio FirebaseStorageService
    private final FirebaseStorageService firebaseStorageService;

    public CategoriaService(CategoriaRepository categoriaRepository,
            FirebaseStorageService firebaseStorageService) {

        this.categoriaRepository = categoriaRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    // Recupera en un ArrayList todos los registros de categoria o sólo activos
    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }

    // Recupera un registro de categoria si existe
    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }

    // Si Categoria trae un idCategoria... se actualiza el registro, sino se crea
    @Transactional
    public void save(Categoria categoria, MultipartFile imagenFile) {
        //se "salva" la categoria
        categoriaRepository.save(categoria);

        if (!imagenFile.isEmpty()) {  //Nos pasan una imagen...
            try {
                String ruta = firebaseStorageService.uploadImage(
                        imagenFile,
                        "categoria",
                        categoria.getIdCategoria());

                categoria.setRutaImagen(ruta);
                categoriaRepository.save(categoria);

            } catch (IOException e) {

            }
        }
    }

    //Si idCategoria existe, se elimina... si no tiene productos asociados
    @Transactional
    public void delete(Integer idCategoria) {
        //Se valida que la categoria exista...
        if (!categoriaRepository.existsById(idCategoria)) {
            //Se lanza una excepción para indicarle al usuario que no se eliminó
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

