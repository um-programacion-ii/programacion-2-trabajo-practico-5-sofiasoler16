package com.um.tp5.controllers;

import com.um.tp5.domain.Proyecto;
import com.um.tp5.service.ProyectoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService s) {
        this.proyectoService = s;
    }
}
