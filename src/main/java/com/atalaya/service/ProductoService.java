package com.atalaya.service;

import com.atalaya.domain.Producto;
import com.atalaya.repository.ProductoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

     // Se hace uso del repositorio ProductoRepository
    private final ProductoRepository productoRepository;

    // Se hace uso del servicio FirebaseStorageService
    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(ProductoRepository productoRepository,
            FirebaseStorageService firebaseStorageService) {

        this.productoRepository = productoRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    // Recupera un registro de producto si existe
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    // Recupera los productos activos
    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findByActivoTrue();
    }

    // Filtra productos por descripción o categoría
    @Transactional(readOnly = true)
    public List<Producto> filtrar(String descripcion, Integer categoriaId) {
        if (descripcion != null && !descripcion.isEmpty()) {
            return productoRepository.findByDescripcionContainingIgnoreCaseAndActivoTrue(descripcion);
        } else if (categoriaId != null) {
            return productoRepository.findByCategoriaIdCategoriaAndActivoTrue(categoriaId);
        } else {
            return productoRepository.findByActivoTrue();
        }
    }

    // Si Producto trae un idProducto... se actualiza el registro, sino se crea
    @Transactional
    public void guardar(Producto producto, MultipartFile imagenFile) {
        //se "salva" el producto
        productoRepository.save(producto);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String ruta = firebaseStorageService.uploadImage(imagenFile, "producto", producto.getIdProducto());

                producto.setRutaImagen(ruta);
                productoRepository.save(producto);

            } catch (IOException e) {

            }
        }
    }

    // Si idProducto existe, se elimina
    @Transactional
    public void eliminar(Integer idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException("El producto con ID " + idProducto + " no existe!");
        }
        try {
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el producto, tiene datos asociados");
        }
    }

    // Consulta derivada que filtra productos de un rango de precios y lo ordena por precio
    @Transactional(readOnly = true)
    public List<Producto> consultaDerivada(double precioInf, double precioSup) {
        return productoRepository.findByPrecioBetweenOrderByPrecioAsc(precioInf, precioSup);
    }
}