package com.example.pagoclinica.pagoclinica.domain.repository;

import com.example.pagoclinica.pagoclinica.domain.dto.PagoDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IPayClinical {
    PagoDTO registrarPago(PagoDTO payClinicalDTO);
    PagoDTO obtenerPagoPorId(Long id);
    List<PagoDTO> obtenerTodosLosPagos();
    List<PagoDTO> obtenerPagosPorCitaId(Long citaId);
    List<PagoDTO> obtenerPagosPendientes();
    BigDecimal obtenerTotalIngresosPorFecha(LocalDate fecha);
    Map<String, Long> obtenerMetodosPagoMasUsados();
    String generarReporteIngresos();
    PagoDTO actualizarPago(Long id, PagoDTO payClinicalDTO);
    void eliminarPago(Long id);
    PagoDTO procesarPago(Long id, String metodoPago);
}