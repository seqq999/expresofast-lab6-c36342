package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.ConductorService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.EmpresaLogisticaService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.VehiculoService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Conductor;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.EmpresaLogistica;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Vehiculo;

@RestController
@RequestMapping("/api")
public class CatalogController {

    private final VehiculoService vehiculoService;
    private final ConductorService conductorService;
    private final EmpresaLogisticaService empresaService;

    public CatalogController(VehiculoService vehiculoService,
            ConductorService conductorService, EmpresaLogisticaService empresaService) {
        this.vehiculoService = vehiculoService;
        this.conductorService = conductorService;
        this.empresaService = empresaService;
    }

    @GetMapping("/vehiculos")
    public List<Vehiculo> listarVehiculos() {
        return vehiculoService.listar();
    }

    @GetMapping("/conductores")
    public List<Conductor> listarConductores() {
        return conductorService.findAll();
    }

    @GetMapping("/empresas")
    public List<EmpresaLogistica> listarEmpresas() {
        return empresaService.findAll();
    }
}