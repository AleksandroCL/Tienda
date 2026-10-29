package com.proyecto.tienda.service;

import com.proyecto.tienda.dto.DashboardJuegoDTO;
import com.proyecto.tienda.dto.DashboardResumenDTO;
import com.proyecto.tienda.model.Juego;
import com.proyecto.tienda.model.Plataforma;
import com.proyecto.tienda.repository.CategoriaRepository;
import com.proyecto.tienda.repository.JuegoRepository;
import com.proyecto.tienda.repository.PlataformaRepository;
import com.proyecto.tienda.repository.UsuarioRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private static final int UMBRAL_STOCK_BAJO = 3;
    private static final int LIMITE_JUEGOS = 5;

    private final JuegoRepository juegos;
    private final UsuarioRepository usuarios;
    private final CategoriaRepository categorias;
    private final PlataformaRepository plataformas;

    public DashboardService(JuegoRepository juegos, UsuarioRepository usuarios,
            CategoriaRepository categorias, PlataformaRepository plataformas) {
        this.juegos = juegos;
        this.usuarios = usuarios;
        this.categorias = categorias;
        this.plataformas = plataformas;
    }

    @Transactional(readOnly = true)
    public DashboardResumenDTO obtenerResumen() {
        var inventario = juegos.resumirInventario(UMBRAL_STOCK_BAJO);
        var atencion = juegos.buscarConPocoStock(UMBRAL_STOCK_BAJO, PageRequest.of(0, LIMITE_JUEGOS))
                .stream().map(juego -> resumirJuego(juego, false)).toList();
        var idsRecientes = juegos.findTop5ByOrderByIdDesc().stream().map(Juego::getId).toList();
        // La carga de plataformas se limita a los cinco IDs, sin paginar un JOIN de colección.
        List<DashboardJuegoDTO> recientes = idsRecientes.isEmpty() ? List.of()
                : juegos.buscarParaDashboard(idsRecientes).stream().map(juego -> resumirJuego(juego, true)).toList();
        return new DashboardResumenDTO(inventario.getTotalJuegos(), usuarios.count(),
                categorias.count(), plataformas.count(), inventario.getJuegosAgotados(),
                inventario.getJuegosStockBajo(), inventario.getUnidadesDisponibles(),
                UMBRAL_STOCK_BAJO, atencion, recientes);
    }

    private DashboardJuegoDTO resumirJuego(Juego juego, boolean incluirPlataformas) {
        String plataformasTexto = incluirPlataformas
                ? juego.getPlataformas().stream().map(Plataforma::getNombre).sorted().collect(Collectors.joining(" · "))
                : "";
        String imagen = juego.getImagenUrl();
        return new DashboardJuegoDTO(juego.getId(), juego.getTitulo(),
                imagen == null || imagen.isBlank() ? "/img/placeholder-juego.svg" : imagen,
                juego.getPrecio(), juego.getStock() == null ? 0 : juego.getStock(),
                plataformasTexto.isBlank() ? "Sin plataforma" : plataformasTexto);
    }
}
