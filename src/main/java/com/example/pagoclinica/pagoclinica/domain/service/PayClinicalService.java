package com.example.pagoclinica.pagoclinica.domain.service;

import com.example.pagoclinica.pagoclinica.domain.dto.PayClinicalDTO;
// Importamos la interfaz del repositorio en lugar de la implementación directa si seguimos patrones de DIP
// Aunque tu estructura actual inyecta la implementación, lo mantendré así por consistencia con tu código.
import com.example.pagoclinica.pagoclinica.infraestructura.repositories.PayClinicalImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PayClinicalService {

    @Autowired
    private PayClinicalImpl pagoClinicaImpl; // Inyectamos la implementación concreta

    public PayClinicalDTO registrarPago(PayClinicalDTO payClinicalDTO) {
        return pagoClinicaImpl.registrarPago(payClinicalDTO);
    }

    public PayClinicalDTO obtenerPagoPorId(Long id) {
        return pagoClinicaImpl.obtenerPagoPorId(id);
    }

    public List<PayClinicalDTO> obtenerTodosLosPagos() {
        return pagoClinicaImpl.obtenerTodosLosPagos();
    }

    public List<PayClinicalDTO> obtenerPagosPorCitaId(Long citaId) {
        return pagoClinicaImpl.obtenerPagosPorCitaId(citaId);
    }

    public List<PayClinicalDTO> obtenerPagosPendientes() {
        return pagoClinicaImpl.obtenerPagosPendientes();
    }

    public BigDecimal obtenerTotalIngresosPorFecha(LocalDate fecha) {
        return pagoClinicaImpl.obtenerTotalIngresosPorFecha(fecha);
    }

    public Map<String, Long> obtenerMetodosPagoMasUsados() {
        return pagoClinicaImpl.obtenerMetodosPagoMasUsados();
    }

    public String generarReporteIngresos() {
        return pagoClinicaImpl.generarReporteIngresos();
    }

    // Nuevos métodos de servicio
    public PayClinicalDTO actualizarPago(Long id, PayClinicalDTO payClinicalDTO) {
        return pagoClinicaImpl.actualizarPago(id, payClinicalDTO);
    }

    public boolean eliminarPago(Long id) {
        // La implementación del repositorio podría devolver void o boolean.
        // Ajustar según la firma en IPayClinical y PayClinicalImpl.
        try {
            pagoClinicaImpl.eliminarPago(id);
            return true; // Eliminación exitosa
        } catch (Exception e) {
            // Manejar excepción si el pago no se encuentra o hay otro error
            return false; // Eliminación fallida
        }
    }

    public PayClinicalDTO procesarPago(Long id, String metodoPago) {
        return pagoClinicaImpl.procesarPago(id, metodoPago);
    }
}