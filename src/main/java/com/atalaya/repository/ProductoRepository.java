package com.atalaya.repository;

import com.atalaya.domain.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    public List<Producto> findByActivoTrue();

    public List<Producto> findByDescripcionContainingIgnoreCaseAndActivoTrue(String descripcion);

    public List<Producto> findByCategoriaIdCategoriaAndActivoTrue(Integer categoriaId);

    public List<Producto> findByPrecioBetweenOrderByPrecioAsc(double precioInf, double precioSup);
}