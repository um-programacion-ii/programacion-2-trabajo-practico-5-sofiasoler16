package com.um.tp5.controllers;

import com.um.tp5.domain.Departamento;
import com.um.tp5.service.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService s) {
        this.departamentoService = s;
    }
}