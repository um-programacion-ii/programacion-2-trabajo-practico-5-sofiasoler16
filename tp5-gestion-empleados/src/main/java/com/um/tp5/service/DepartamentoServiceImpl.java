package com.um.tp5.service;

import com.um.tp5.domain.Departamento;
import com.um.tp5.exceptions.RecursoNoEncontradoException;
import com.um.tp5.repository.DepartamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DepartamentoServiceImpl implements DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoServiceImpl(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    @Override
    public Departamento guardar(Departamento departamento) {
        return departamentoRepository.save(departamento);
    }

    @Override
    public Departamento buscarPorId(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado con ID: " + id));
    }

    @Override
    public Departamento buscarPorNombre(String nombre) {
        return departamentoRepository.findByNombre(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Departamento no encontrado: " + nombre));
    }

    @Override
    public List<Departamento> obtenerTodos() {
        return departamentoRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
        if (!departamentoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Departamento no encontrado con ID: " + id);
        }
        departamentoRepository.deleteById(id);
    }
}