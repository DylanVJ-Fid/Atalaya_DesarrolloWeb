package com.atalaya.repository;

import com.atalaya.domain.Carrito;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    Optional<Carrito> findByIdCarritoAndActivoTrue(Integer idCarrito);
}
