package cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.business.EnvioException;
import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.business.EnvioService;
import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.dto.EnvioRequestDTO;
import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.dto.CambioEstadoDTO;
import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.dto.BitacoraResponseDTO;
import jakarta.validation.Valid;
import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.dto.EnvioResponseDTO;
import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.domain.*;

@RestController
@RequestMapping("api/envios")
@Tag(name = "Envios", description = "Endpoints para la gestion de envios")
@CrossOrigin(origins = "*")
public class EnvioController {

    private final EnvioService service;

    public EnvioController(EnvioService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo envio")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Envio creado"),
            @ApiResponse(responseCode = "400", description = "Error en validación")
    })
    public ResponseEntity<?> save(
            @Valid @RequestBody EnvioRequestDTO dto) {
        Envio saved = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertirRespuesta(saved));
    }

    @GetMapping("/optimizados")
    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> enviosOptimizados() {
        List<Envio> envios = service.findAllOptimized();
        List<EnvioResponseDTO> list = new ArrayList<>();

        for (Envio envio : envios) {
            list.add(convertirRespuesta(envio));
        }

        return list;
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar el estado de un envio")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "El envio se actualizo con exito"),
            @ApiResponse(responseCode = "400", description = "El envio no existe")
    })
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable("id") Integer envioId,
            @Valid @RequestBody CambioEstadoDTO cambio) {
        service.updatedState(envioId, cambio.nuevoEstado(), cambio.observaciones());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/bitacora")
    public List<BitacoraResponseDTO> bitacora(@PathVariable Integer id) {
        return service.findBitacora(id);
    }

    private EnvioResponseDTO convertirRespuesta(Envio envio) {
        return new EnvioResponseDTO(
                envio.getId(),
                envio.getCodigoRastreo(),
                envio.getDireccionDestino(),
                envio.getPesoKg(),
                envio.getCosto(),
                envio.getEstadoEnvio(),
                envio.getVehiculo().getId(),
                envio.getConductor().getId(),
                envio.getVehiculo().getEmpresa().getId(),
                envio.getVehiculo().getEmpresa().getNombre());
    }

}
