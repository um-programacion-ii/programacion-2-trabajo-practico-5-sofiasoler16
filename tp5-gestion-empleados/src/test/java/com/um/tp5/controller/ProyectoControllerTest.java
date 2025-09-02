package com.um.tp5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.um.tp5.controllers.ProyectoController;
import com.um.tp5.domain.Proyecto;
import com.um.tp5.service.ProyectoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // o @MockitoBean si prefieres
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProyectoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ProyectoControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @SuppressWarnings("removal")
    @MockBean private ProyectoService proyectoService;

    private Proyecto prj(Long id, String nombre, String desc, LocalDate ini, LocalDate fin) {
        Proyecto p = new Proyecto();
        p.setId(id);
        p.setNombre(nombre);
        p.setDescripcion(desc);
        p.setFechaInicio(ini);
        p.setFechaFin(fin);
        return p;
    }

    @Test
    void obtenerTodos() throws Exception {
        given(proyectoService.obtenerTodos())
                .willReturn(List.of(
                        prj(1L,"App1","Desc1", LocalDate.now(), LocalDate.now().plusDays(10)),
                        prj(2L,"App2","Desc2", LocalDate.now().minusDays(5), LocalDate.now().plusDays(2))
                ));

        mvc.perform(get("/api/proyectos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("App1")));
    }

    @Test
    void obtenerPorId() throws Exception {
        given(proyectoService.buscarPorId(7L))
                .willReturn(prj(7L,"Core","Core Desc", LocalDate.now(), LocalDate.now().plusDays(5)));

        mvc.perform(get("/api/proyectos/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Core")));
    }

    @Test
    void obtenerPorActivos() throws Exception {
        given(proyectoService.obtenerActivos())
                .willReturn(List.of(
                        prj(10L,"Activo1","A1", LocalDate.now().minusDays(1), LocalDate.now().plusDays(3))
                ));

        mvc.perform(get("/api/proyectos/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Activo1")));
    }

    @Test
    void PostProyecto() throws Exception {
        Proyecto entrada = prj(null,"Nuevo","N", LocalDate.now(), LocalDate.now().plusDays(7));
        Proyecto salida  = prj(100L,"Nuevo","N", entrada.getFechaInicio(), entrada.getFechaFin());
        given(proyectoService.guardar(any(Proyecto.class))).willReturn(salida);

        mvc.perform(post("/api/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.nombre", is("Nuevo")));
    }

    @Test
    void actualizarProyecto() throws Exception {
        Proyecto cambios = prj(null,"Renombrado","Nueva desc", LocalDate.now(), LocalDate.now().plusDays(9));
        Proyecto actualizado = prj(5L,"Renombrado","Nueva desc", cambios.getFechaInicio(), cambios.getFechaFin());
        given(proyectoService.actualizar(eq(5L), any(Proyecto.class))).willReturn(actualizado);

        mvc.perform(put("/api/proyectos/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(cambios)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.nombre", is("Renombrado")));
    }

    @Test
    void deleteProyecto() throws Exception {
        mvc.perform(delete("/api/proyectos/3"))
                .andExpect(status().isNoContent());
    }
}
