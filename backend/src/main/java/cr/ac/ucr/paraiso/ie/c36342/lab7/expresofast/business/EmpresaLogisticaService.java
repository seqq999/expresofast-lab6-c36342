package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.EmpresaLogisticaRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.EmpresaLogistica;

@Service
public class EmpresaLogisticaService {

    private final EmpresaLogisticaRepository repo;

    public EmpresaLogisticaService(EmpresaLogisticaRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<EmpresaLogistica> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<EmpresaLogistica> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    @Transactional
    public EmpresaLogistica guardar(EmpresaLogistica empresa) {
        return repo.save(empresa);
    }

    @Transactional
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
