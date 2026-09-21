package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.VehiculoRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Vehiculo;

@Service
public class VehiculoService {

    private final VehiculoRepository repo;

    public VehiculoService(VehiculoRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Vehiculo> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    @Transactional
    public Vehiculo guardar(Vehiculo vehiculo) {
        if (vehiculo.getId() == null && repo.existsByPlaca(vehiculo.getPlaca())) {
            throw new DuplicateResourceException("Ya existe un vehículo con la placa " + vehiculo.getPlaca());
        }
        return repo.save(vehiculo);
    }

    @Transactional
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
