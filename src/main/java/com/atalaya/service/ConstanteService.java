package com.atalaya.service;

import com.atalaya.repository.ConstanteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConstanteService {

    private final ConstanteRepository constanteRepository;

    public ConstanteService(ConstanteRepository constanteRepository) {
        this.constanteRepository = constanteRepository;
    }

    @Transactional(readOnly = true)
    public String getValor(String atributo, String valorPredeterminado) {
        return constanteRepository.findByAtributo(atributo)
                .map(constante -> constante.getValor())
                .filter(valor -> !valor.isBlank())
                .orElse(valorPredeterminado);
    }
}
