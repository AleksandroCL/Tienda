package com.proyecto.tienda.service;

import com.proyecto.tienda.exception.RecursoNoEncontradoException;

import com.proyecto.tienda.dto.CarritoItemDTO;
import com.proyecto.tienda.model.Juego;
import com.proyecto.tienda.repository.JuegoRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CarritoService {

    private static final String CARRITO_SESSION = "CARRITO";

    private final JuegoRepository juegoRepository;

    public CarritoService(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    /**
     * Obtiene el carrito almacenado en sesión.
     *
     * Estructura:
     * ID del juego -> cantidad
     */
    @SuppressWarnings("unchecked")
    private Map<Long, Integer> obtenerCarrito(HttpSession session) {

        Map<Long, Integer> carrito = (Map<Long, Integer>) session.getAttribute(CARRITO_SESSION);

        if (carrito == null) {
            carrito = new LinkedHashMap<>();
            session.setAttribute(CARRITO_SESSION, carrito);
        }

        return carrito;
    }

    /**
     * Agrega un juego al carrito.
     */
    public void agregar(Long juegoId, HttpSession session) {

        Juego juego = juegoRepository.findById(juegoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El juego solicitado no existe."));

        if (juego.getStock() == null || juego.getStock() <= 0) {
            throw new IllegalStateException(
                    "El juego no tiene stock disponible.");
        }

        Map<Long, Integer> carrito = obtenerCarrito(session);

        int cantidadActual = carrito.getOrDefault(juegoId, 0);

        if (cantidadActual >= juego.getStock()) {
            throw new IllegalStateException(
                    "No puedes agregar más unidades. Stock disponible: "
                            + juego.getStock());
        }

        carrito.put(juegoId, cantidadActual + 1);

        session.setAttribute(CARRITO_SESSION, carrito);
    }

    /**
     * Aumenta la cantidad de un juego.
     */
    public void aumentar(Long juegoId, HttpSession session) {

        Juego juego = juegoRepository.findById(juegoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El juego solicitado no existe."));

        Map<Long, Integer> carrito = obtenerCarrito(session);

        if (!carrito.containsKey(juegoId)) {
            return;
        }

        int cantidadActual = carrito.get(juegoId);

        if (cantidadActual >= juego.getStock()) {
            throw new IllegalStateException(
                    "No puedes agregar más unidades. Stock disponible: "
                            + juego.getStock());
        }

        carrito.put(juegoId, cantidadActual + 1);

        session.setAttribute(CARRITO_SESSION, carrito);
    }

    /**
     * Disminuye la cantidad de un juego.
     */
    public void disminuir(Long juegoId, HttpSession session) {

        Map<Long, Integer> carrito = obtenerCarrito(session);

        if (!carrito.containsKey(juegoId)) {
            return;
        }

        int cantidadActual = carrito.get(juegoId);

        if (cantidadActual <= 1) {
            carrito.remove(juegoId);
        } else {
            carrito.put(juegoId, cantidadActual - 1);
        }

        session.setAttribute(CARRITO_SESSION, carrito);
    }

    /**
     * Elimina completamente un juego del carrito.
     */
    public void eliminar(Long juegoId, HttpSession session) {

        Map<Long, Integer> carrito = obtenerCarrito(session);

        carrito.remove(juegoId);

        session.setAttribute(CARRITO_SESSION, carrito);
    }

    /**
     * Vacía completamente el carrito.
     */
    public void vaciar(HttpSession session) {

        session.removeAttribute(CARRITO_SESSION);
    }

    /**
     * Obtiene los productos completos del carrito.
     */
    public List<CarritoItemDTO> obtenerItems(HttpSession session) {

        Map<Long, Integer> carrito = obtenerCarrito(session);

        List<CarritoItemDTO> items = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : carrito.entrySet()) {

            Long juegoId = entry.getKey();
            Integer cantidad = entry.getValue();

            Juego juego = juegoRepository.findById(juegoId)
                    .orElse(null);

            // Si el juego fue eliminado de la BD,
            // lo quitamos del carrito.
            if (juego == null) {
                continue;
            }

            items.add(new CarritoItemDTO(
                    juego.getId(),
                    juego.getTitulo(),
                    juego.getImagenUrl(),
                    juego.getPrecio(),
                    cantidad,
                    juego.getStock()));
        }

        return items;
    }

    /**
     * Calcula el total del carrito.
     */
    public BigDecimal calcularTotal(HttpSession session) {

        return obtenerItems(session)
                .stream()
                .map(CarritoItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int obtenerCantidadTotal(HttpSession session) {

        return obtenerCarrito(session)
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * Obtiene la cantidad de productos diferentes.
     *
     * Ejemplo:
     *
     * GTA V -> 2
     * FIFA -> 1
     *
     * Resultado -> 2 productos.
     */
    public int obtenerCantidadProductos(HttpSession session) {

        return obtenerCarrito(session).size();
    }
}