package com.atalaya.controller;

import com.atalaya.service.CarritoService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CarritoModelAdvice {

    private static final String CARRITO_COOKIE = "atalayaCarrito";

    private final CarritoService carritoService;

    public CarritoModelAdvice(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @ModelAttribute("carritoCantidad")
    public Integer carritoCantidad(@CookieValue(name = CARRITO_COOKIE, required = false) Integer idCarrito) {
        return carritoService.getCantidadProductos(idCarrito);
    }
}
