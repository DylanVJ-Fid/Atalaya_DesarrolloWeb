package com.atalaya.service;

import com.atalaya.domain.Factura;
import com.atalaya.repository.FacturaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    // Listar facturas
    @Transactional(readOnly = true)
    public List<Factura> listar() {
        return facturaRepository.findAll();
    }

    // Buscar por ID
    @Transactional(readOnly = true)
    public Optional<Factura> buscarPorId(Long id) {
        return facturaRepository.findById(id);
    }

    // Guardar factura
    @Transactional
    public void guardar(Factura factura) {
        facturaRepository.save(factura);
    }

    // Eliminar una factura
    @Transactional
    public void eliminar(Long id) {
        facturaRepository.deleteById(id);
    }

    // Limpiar historial completo
    @Transactional
    public void eliminarTodo() {
        facturaRepository.deleteAll();
    }
}