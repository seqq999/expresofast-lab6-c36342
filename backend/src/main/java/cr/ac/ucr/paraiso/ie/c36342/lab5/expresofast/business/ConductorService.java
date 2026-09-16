package cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.business;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.data.ConductorRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.domain.Conductor;

@Service
public class ConductorService {

    private final ConductorRepository repo;

    public ConductorService(ConductorRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Conductor> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Conductor> findById(Integer id) {
        return repo.findById(id);
    }

    @Transactional
    public Conductor save(Conductor conductor) {
        return repo.save(conductor);
    }

    @Transactional
    public void deleteById(Integer id) {
        repo.deleteById(id);
    }
}
