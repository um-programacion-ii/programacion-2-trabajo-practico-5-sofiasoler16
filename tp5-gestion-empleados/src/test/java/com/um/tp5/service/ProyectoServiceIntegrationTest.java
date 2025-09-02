package com.um.tp5.service;

import com.um.tp5.domain.Departamento;
import com.um.tp5.domain.Empleado;
import com.um.tp5.domain.Proyecto;
import com.um.tp5.repository.DepartamentoRepository;
import com.um.tp5.repository.EmpleadoRepository;
import com.um.tp5.repository.ProyectoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ProyectoServiceIntegrationTest {

    @Autowired private ProyectoService proyectoService;
    @Autowired private DepartamentoRepository departamentoRepository;
    @Autowired private EmpleadoRepository empleadoRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private DepartamentoService departamentoService;
    @Autowired private EmpleadoService empleadoService;

    private Proyecto proyecto(String nombre, LocalDate ini, LocalDate fin) {
        Proyecto p = new Proyecto();
        p.setNombre(nombre);
        p.setDescripcion("Desc " + nombre);
        p.setFechaInicio(ini);
        p.setFechaFin(fin);
        return proyectoRepository.save(p);
    }

    @Test
    void obtenerActivos_deberiaRetornarSoloActivos() {
        Proyecto p1 = new Proyecto();
        p1.setNombre("App1");
        p1.setDescripcion("Descripcion App1");
        p1.setFechaInicio(LocalDate.now());
        p1.setFechaFin(LocalDate.now().plusDays(7));

        Proyecto p2 = new Proyecto();
        p2.setNombre("App2");
        p2.setDescripcion("Descripcion App2");
        p2.setFechaInicio(LocalDate.now().plusDays(1));
        p2.setFechaFin(LocalDate.now().plusDays(8));

        proyectoService.guardar(p1);
        proyectoService.guardar(p2);

        List<Proyecto> activos = proyectoService.obtenerActivos();

        assertEquals(2, activos.size());
        List<String> nombres = activos.stream().map(Proyecto::getNombre).toList();
        assertTrue(nombres.contains("App1"));
        assertTrue(nombres.contains("App2"));
    }

    // Repository
    @Test
    void cuandoBuscarProyectosActivos_retornarProyecto() {
        Proyecto p1 = new Proyecto();
        p1.setNombre("App1");
        p1.setDescripcion("Descripcion App1");
        p1.setFechaInicio(LocalDate.now());
        p1.setFechaFin(LocalDate.now().plusDays(7));

        proyectoRepository.save(p1);

        List<Proyecto> proyecto = proyectoRepository.findActivos(LocalDate.now());
        assertEquals("App1", proyecto.getFirst().getNombre());
    }

    @Test
    void eliminar_ok_service() {
        Proyecto p = proyecto("Borrar", LocalDate.now(), LocalDate.now().plusDays(1));
        proyectoService.eliminar(p.getId());
        assertFalse(proyectoRepository.existsById(p.getId()));
    }

    @Test
    void actualizar_ok_service() {
        Proyecto p = proyecto("Old", LocalDate.now(), LocalDate.now().plusDays(7));

        Proyecto cambios = new Proyecto();
        cambios.setNombre("New");
        cambios.setDescripcion("Nueva desc");
        cambios.setFechaInicio(LocalDate.now().plusDays(1));
        cambios.setFechaFin(LocalDate.now().plusDays(8));

        Proyecto actualizado = proyectoService.actualizar(p.getId(), cambios);
        assertEquals("New", actualizado.getNombre());
        assertEquals(LocalDate.now().plusDays(1), actualizado.getFechaInicio());
    }

}