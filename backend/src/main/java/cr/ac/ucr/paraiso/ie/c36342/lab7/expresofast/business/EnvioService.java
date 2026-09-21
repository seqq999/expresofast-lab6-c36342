package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.ConductorRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.EnvioRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.VehiculoRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.BitacoraEnvioRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.UsuarioRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Conductor;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Envio;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Vehiculo;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.BitacoraEnvio;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Usuario;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto.EnvioRequestDTO;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto.BitacoraResponseDTO;

@Service
public class EnvioService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "PENDIENTE", "EN_TRANSITO", "ENTREGADO", "CANCELADO");

    private final EnvioRepository repo;
    private final VehiculoRepository vehiculoRepo;
    private final ConductorRepository conductorRepo;
    private final BitacoraEnvioRepository bitacoraRepo;
    private final UsuarioRepository usuarioRepo;

    public EnvioService(EnvioRepository repo, VehiculoRepository vehiculoRepo,
            ConductorRepository conductorRepo, BitacoraEnvioRepository bitacoraRepo,
            UsuarioRepository usuarioRepo) {
        this.repo = repo;
        this.vehiculoRepo = vehiculoRepo;
        this.conductorRepo = conductorRepo;
        this.bitacoraRepo = bitacoraRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Transactional(readOnly = true)
    public List<Envio> findAllOptimized() {
        return repo.findAllOptimized();
    }

    @Transactional(readOnly = true)
    public List<Envio> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Envio> findById(Integer id) {
        return repo.findById(id);
    }

    @Transactional
    public Envio save(EnvioRequestDTO dto) {
        Vehiculo vehiculo = getVehicle(dto.vehiculoId());
        Conductor conductor = getDriver(dto.conductorId());
        validteWeigth(dto.pesoKg(), vehiculo.getCapacidadKg());

        Envio envio = new Envio();
        envio.setCodigoRastreo(dto.codigoRastreo());
        envio.setDireccionDestino(dto.direccionDestino());
        envio.setPesoKg(dto.pesoKg());
        envio.setCosto(dto.costo());
        envio.setEstadoEnvio("PENDIENTE");
        envio.setVehiculo(vehiculo);
        envio.setConductor(conductor);
        return repo.save(envio);
    }

    @Transactional
    public Envio updatedState(Integer id, String estado, String observaciones) {
        validateState(estado);
        Envio envio = repo.findById(id)
            .orElseThrow(() -> new EnvioException("No existe el envío con id " + id));
        String previousState = envio.getEstadoEnvio();
        if (("ENTREGADO".equals(previousState) || "CANCELADO".equals(previousState))
                && ("PENDIENTE".equals(estado) || "EN_TRANSITO".equals(estado))) {
            throw new InvalidStateTransitionException("Transición de estado no permitida para el envío " + envio.getCodigoRastreo());
        }
        if ("EN_TRANSITO".equals(previousState) && "CANCELADO".equals(estado)) {
            throw new InvalidStateTransitionException("No se puede cancelar un envío en tránsito");
        }
        envio.setEstadoEnvio(estado);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        BitacoraEnvio bitacora = new BitacoraEnvio();
        bitacora.setEnvio(envio);
        bitacora.setEstadoAnterior(previousState);
        bitacora.setEstadoNuevo(estado);
        bitacora.setFechaCambio(LocalDateTime.now());
        bitacora.setUsuario(usuario);
        bitacora.setObservaciones(observaciones);
        bitacoraRepo.save(bitacora);
        return envio;
    }

    @Transactional
    public Envio cancelarEnvio(Integer id) {
        return updatedState(id, "CANCELADO", "Cancelación solicitada");
    }

    @Transactional(readOnly = true)
    public List<BitacoraResponseDTO> findBitacora(Integer envioId) {
        if (!repo.existsById(envioId)) {
            throw new ResourceNotFoundException("No existe el envío con id " + envioId);
        }
        return bitacoraRepo.findByEnvioIdOrderByFechaCambioDesc(envioId).stream()
                .map(item -> new BitacoraResponseDTO(item.getId(), item.getEstadoAnterior(), item.getEstadoNuevo(),
                        item.getFechaCambio(), item.getUsuario().getUsername(), item.getObservaciones()))
                .toList();
    }

    @Transactional
    public int updateStatePerVehicle(Integer vehiculoId, String estado) {
        return repo.updateEstadoByVehiculoId(vehiculoId, estado);
    }

    @Transactional
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    private Vehiculo getVehicle(Integer id) {
        if (id == null) {
            throw new EnvioException("El vehículo es obligatorio");
        }
        return vehiculoRepo.findById(id)
                .orElseThrow(() -> new EnvioException("No existe el vehículo con id " + id));
    }

    private Conductor getDriver(Integer id) {
        if (id == null) {
            throw new EnvioException("El conductor es obligatorio");
        }
        return conductorRepo.findById(id)
                .orElseThrow(() -> new EnvioException("No existe el conductor con id " + id));
    }

    private void validteWeigth(BigDecimal peso, BigDecimal capacidad) {
        if (peso == null || capacidad == null || peso.signum() < 0) {
            throw new EnvioException("El peso del envío debe ser válido");
        }
        if (peso.compareTo(capacidad) > 0) {
            throw new CapacidadExcedidaException("El peso del envío supera la capacidad del vehículo");
        }
    }

    private void validateState(String estado) {
        if (estado == null || !ESTADOS_VALIDOS.contains(estado)) {
            throw new EnvioException("Estado de envío no válido");
        }
    }

    /**
     * Calcula la tarifa del flete según una matriz de peso (kg) y distancia (km).
     *
     * Matriz de tarifas:
     * - Peso <= 10 kg (Ligero):
     *     - Distancia <= 15 km: 2500.0
     *     - Distancia <= 50 km: 4000.0
     *     - Distancia > 50 km:  6000.0
     * - Peso <= 30 kg (Mediano):
     *     - Distancia <= 15 km: 4500.0
     *     - Distancia <= 50 km: 7500.0
     *     - Distancia > 50 km:  9500.0
     * - Peso > 30 kg (Pesado):
     *     - Distancia <= 15 km: 12000.0
     *     - Distancia <= 50 km: 15000.0
     *     - Distancia > 50 km:  20000.0
     *
     * @param pesoKg peso en kilogramos
     * @param distanciaKm distancia en kilómetros
     * @return tarifa calculada en colones
     */
    public double calcularTarifa(double pesoKg, double distanciaKm) {
        if (pesoKg <= 0 || distanciaKm <= 0) {
            throw new IllegalArgumentException("El peso y la distancia deben ser mayores a cero");
        }

        if (pesoKg <= 10.0) {
            if (distanciaKm <= 15.0) {
                return 2500.0;
            } else if (distanciaKm <= 50.0) {
                return 4000.0;
            } else {
                return 6000.0;
            }
        } else if (pesoKg <= 30.0) {
            if (distanciaKm <= 15.0) {
                return 4500.0;
            } else if (distanciaKm <= 50.0) {
                return 7500.0;
            } else {
                return 9500.0;
            }
        } else {
            if (distanciaKm <= 15.0) {
                return 12000.0;
            } else if (distanciaKm <= 50.0) {
                return 15000.0;
            } else {
                return 20000.0;
            }
        }
    }
}
