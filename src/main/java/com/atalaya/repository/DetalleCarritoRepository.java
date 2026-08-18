package com.atalaya.repository;

import com.atalaya.domain.DetalleCarrito;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Integer> {

    List<DetalleCarrito> findByCarritoIdCarrito(Integer idCarrito);

    Optional<DetalleCarrito> findByCarritoIdCarritoAndProductoIdProducto(Integer idCarrito, Integer idProducto);

    void deleteByCarritoIdCarrito(Integer idCarrito);
}
