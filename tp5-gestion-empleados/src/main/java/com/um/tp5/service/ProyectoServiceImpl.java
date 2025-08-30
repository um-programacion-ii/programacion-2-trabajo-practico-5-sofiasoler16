package com.um.tp5.service;

import com.um.tp5.domain.Proyecto;
import com.um.tp5.exceptions.RecursoNoEncontradoException;
import com.um.tp5.repository.ProyectoRepository;
import com.um.tp5.service.ProyectoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProyectoServiceImpl implements ProyectoService {

    private final ProyectoRepository proyectoRepository;

    public ProyectoServiceImpl(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    @Override
    public Proyecto guardar(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    @Override
    @Transactional(readOnly = true)
    public Proyecto buscarPorId(Long id) {
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerTodos() {
        return proyectoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerActivos() {
        return proyectoRepository.findActivos(LocalDate.now());
    }

    @Override
    public Proyecto actualizar(Long id, Proyecto p) {
        if (!proyectoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Proyecto no encontrado con ID: " + id);
        }
        p.setId(id);
        return proyectoRepository.save(p);
    }

    @Override
    public void eliminar(Long id) {
        if (!proyectoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Proyecto no encontrado con ID: " + id);
        }
        proyectoRepository.deleteById(id);
    }
}
