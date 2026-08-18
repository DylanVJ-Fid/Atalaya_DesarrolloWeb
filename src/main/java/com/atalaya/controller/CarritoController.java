package com.atalaya.controller;

import com.atalaya.domain.Carrito;
import com.atalaya.service.CarritoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private static final String CARRITO_COOKIE = "atalayaCarrito";
    private static final int COOKIE_DIAS = 60 * 60 * 24 * 30;

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/listado")
    public String listado(@CookieValue(name = CARRITO_COOKIE, required = false) Integer idCarrito,
            HttpServletResponse response,
            Model model) {
        Carrito carrito = carritoService.getOrCreateCarrito(idCarrito);
        guardarCookie(response, carrito);

        model.addAttribute("detalles", carritoService.getDetalles(carrito.getIdCarrito()));
        model.addAttribute("total", carritoService.getTotal(carrito.getIdCarrito()));
        model.addAttribute("cantidadProductos", carritoService.getCantidadProductos(carrito.getIdCarrito()));
        model.addAttribute("carritoVacio", carritoService.isVacio(carrito.getIdCarrito()));
        return "carrito/listado";
    }

    @PostMapping("/agregar/{idProducto}")
    public String agregar(@CookieValue(name = CARRITO_COOKIE, required = false) Integer idCarrito,
            @PathVariable Integer idProducto,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpServletResponse response) {
        Carrito carrito = carritoService.agregar(idCarrito, idProducto);
        guardarCookie(response, carrito);
        redirectAttributes.addFlashAttribute("todoOk", "Producto agregado al carrito");

        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }

        return "redirect:/producto/listado";
    }

    @PostMapping("/actualizar")
    public String actualizar(@CookieValue(name = CARRITO_COOKIE, required = false) Integer idCarrito,
            @RequestParam Integer idProducto,
            @RequestParam Integer cantidad) {
        carritoService.actualizar(idCarrito, idProducto, cantidad);
        return "redirect:/carrito/listado";
    }

    @PostMapping("/sumar/{idProducto}")
    public String sumar(@CookieValue(name = CARRITO_COOKIE, required = false) Integer idCarrito,
            @PathVariable Integer idProducto) {
        carritoService.sumar(idCarrito, idProducto);
        return "redirect:/carrito/listado";
    }

    @PostMapping("/restar/{idProducto}")
    public String restar(@CookieValue(name = CARRITO_COOKIE, required = false) Integer idCarrito,
            @PathVariable Integer idProducto) {
        carritoService.restar(idCarrito, idProducto);
        return "redirect:/carrito/listado";
    }

    @PostMapping("/eliminar/{idProducto}")
    public String eliminar(@CookieValue(name = CARRITO_COOKIE, required = false) Integer idCarrito,
            @PathVariable Integer idProducto) {
        carritoService.eliminar(idCarrito, idProducto);
        return "redirect:/carrito/listado";
    }

    @PostMapping("/vaciar")
    public String vaciar(@CookieValue(name = CARRITO_COOKIE, required = false) Integer idCarrito) {
        carritoService.vaciar(idCarrito);
        return "redirect:/carrito/listado";
    }

    private void guardarCookie(HttpServletResponse response, Carrito carrito) {
        Cookie cookie = new Cookie(CARRITO_COOKIE, carrito.getIdCarrito().toString());
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_DIAS);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
