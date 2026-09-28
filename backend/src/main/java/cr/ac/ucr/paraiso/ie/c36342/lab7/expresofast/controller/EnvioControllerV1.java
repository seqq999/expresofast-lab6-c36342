package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.EnvioService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto.EnvioDTO;

@RestController
@RequestMapping("/api/v1/envios")
public class EnvioControllerV1 {

    private final EnvioService envioService;

    public EnvioControllerV1(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public Page<EnvioDTO> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado) {

        return envioService.listarPaginado(page, size, sortBy, direction, busqueda, estado);
    }

    @GetMapping("/procedimiento/{estado}")
    public List<EnvioDTO> listarViaStoredProcedure(@PathVariable String estado) {
        return envioService.listarViaStoredProcedure(estado);
    }
}