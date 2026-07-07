package com.atalaya.service;

import com.atalaya.domain.Producto;
import com.atalaya.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public void guardar(Producto producto) {
        productoRepository.save(producto);
    }

    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public List<Producto> listar() {
        return productoRepository.findByActivoTrue();
    }

    public List<Producto> filtrar(String descripcion, Long categoriaId) {
        if (descripcion != null && !descripcion.isEmpty()) {
            return productoRepository.findByDescripcionContainingIgnoreCase(descripcion);
        } else if (categoriaId != null) {
            return productoRepository.findByCategoriaIdCategoria(categoriaId);
        } else {
            return productoRepository.findByActivoTrue();
        }
    }
     // Consulta derivada
    public List<Producto> consultaDerivada(double precioInf, double precioSup) {
        return productoRepository.findByPrecioBetweenOrderByPrecioAsc(precioInf, precioSup);
    }
}
