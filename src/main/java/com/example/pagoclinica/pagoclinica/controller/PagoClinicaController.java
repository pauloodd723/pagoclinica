package com.example.pagoclinica.pagoclinica.controller;

import com.example.pagoclinica.pagoclinica.domain.dto.PayClinicalDTO;
import com.example.pagoclinica.pagoclinica.domain.service.PayClinicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Asegúrate de tener esta importación para el payload de procesarPago

@RestController
@RequestMapping("/pagos")
public class PagoClinicaController {

    @Autowired
    private PayClinicalService payClinicalService;

    @GetMapping
    public ResponseEntity<List<PayClinicalDTO>> obtenerTodosLosPagos() {
        return ResponseEntity.ok(payClinicalService.obtenerTodosLosPagos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayClinicalDTO> obtenerPagoPorId(@PathVariable Long id) {
        PayClinicalDTO pago = payClinicalService.obtenerPagoPorId(id);
        if (pago != null) {
            return ResponseEntity.ok(pago);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<PayClinicalDTO> registrarPago(@RequestBody PayClinicalDTO payClinicalDTO) {
        PayClinicalDTO nuevoPago = payClinicalService.registrarPago(payClinicalDTO);
        return new ResponseEntity<>(nuevoPago, HttpStatus.CREATED);
    }

    @GetMapping("/cita/{citaId}")
    public ResponseEntity<List<PayClinicalDTO>> obtenerPagosPorCitaId(@PathVariable Long citaId) {
        return ResponseEntity.ok(payClinicalService.obtenerPagosPorCitaId(citaId));
    }

    @GetMapping("/reporte/ingresos")
    public ResponseEntity<String> generarReporteIngresos() {
        return ResponseEntity.ok(payClinicalService.generarReporteIngresos());
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<PayClinicalDTO>> obtenerPagosPendientes() {
        return ResponseEntity.ok(payClinicalService.obtenerPagosPendientes());
    }

    // NUEVOS ENDPOINTS:

    /**
     * Actualiza un pago existente.
     * PUT /pagos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<PayClinicalDTO> actualizarPago(
            @PathVariable Long id,
            @RequestBody PayClinicalDTO payClinicalDTO) {
        // Es buena práctica asegurarse que el ID del DTO coincida con el path variable,
        // o ignorar el ID del DTO y usar solo el del path.
        // Por simplicidad, aquí se asume que el servicio maneja la lógica de encontrar por 'id'
        // y actualizar con 'payClinicalDTO'.
        PayClinicalDTO pagoActualizado = payClinicalService.actualizarPago(id, payClinicalDTO);
        if (pagoActualizado != null) {
            return ResponseEntity.ok(pagoActualizado);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Elimina un pago por su ID.
     * DELETE /pagos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        boolean eliminado = payClinicalService.eliminarPago(id);
        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204 No Content si fue exitoso
        }
        return ResponseEntity.notFound().build(); // 404 Not Found si no se encontró para eliminar
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<PayClinicalDTO> procesarPago(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) { // Usamos PayClinicalDTO para el DTO

        String metodoPago = payload.getOrDefault("metodoPago", "EFECTIVO");

        PayClinicalDTO pagoActualizado = payClinicalService.procesarPago(id, metodoPago);
        if (pagoActualizado != null) {
            return ResponseEntity.ok(pagoActualizado);
        }
        return ResponseEntity.notFound().build();
    }
}