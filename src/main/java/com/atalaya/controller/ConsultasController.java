package com.atalaya.controller;

import com.atalaya.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/consultas")
public class ConsultasController {
    
    private final ProductoService productoService;

    public ConsultasController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var lista = productoService.listar();
        model.addAttribute("productos", lista);
        return "consultas/listado";
    }

    @PostMapping("/consultaDerivada")
    public String consultaDerivada(@RequestParam double precioInf,
            @RequestParam double precioSup,
            Model model) {

        var lista = productoService.consultaDerivada(precioInf, precioSup);

        model.addAttribute("productos", lista);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);

        return "consultas/listado";
    }
}
    

