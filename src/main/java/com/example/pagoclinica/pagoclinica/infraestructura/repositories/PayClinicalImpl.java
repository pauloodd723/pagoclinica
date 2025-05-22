package com.example.pagoclinica.pagoclinica.infraestructura.repositories;

import com.example.pagoclinica.pagoclinica.domain.dto.PagoDTO;
import com.example.pagoclinica.pagoclinica.domain.repository.IPayClinical;
import com.example.pagoclinica.pagoclinica.infraestructura.crud.PagoRepository;
import com.example.pagoclinica.pagoclinica.infraestructura.entity.PagoClinica;
import com.example.pagoclinica.pagoclinica.infraestructura.mapper.PagoClinicaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository; 


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository 
public class PayClinicalImpl implements IPayClinical {

    private final PagoRepository pagoRepository;
    private final PagoClinicaMapper pagoClinicaMapper;

    @Autowired
    public PayClinicalImpl(PagoRepository pagoRepository, PagoClinicaMapper pagoClinicaMapper) {
        this.pagoRepository = pagoRepository;
        this.pagoClinicaMapper = pagoClinicaMapper;
    }

    @Override
    public PagoDTO registrarPago(PagoDTO pagoDTO) {
        PagoClinica pagoClinica = pagoClinicaMapper.toPagoClinica(pagoDTO);

        PagoClinica pagoGuardado = pagoRepository.save(pagoClinica);
        return pagoClinicaMapper.toPayClinicalDTO(pagoGuardado);
    }

    @Override
    public PagoDTO obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id)
                .map(pagoClinicaMapper::toPayClinicalDTO)
                .orElse(null);
    }

    @Override
    public List<PagoDTO> obtenerTodosLosPagos() {
        return pagoRepository.findAll().stream()
                .map(pagoClinicaMapper::toPayClinicalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagoDTO> obtenerPagosPorCitaId(Long citaId) {
        return pagoRepository.findByCitaId(citaId).stream()
                .map(pagoClinicaMapper::toPayClinicalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagoDTO> obtenerPagosPendientes() {
        return pagoRepository.findByEstado("Pendiente").stream() 
                .map(pagoClinicaMapper::toPayClinicalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal obtenerTotalIngresosPorFecha(LocalDate fecha) {
        return pagoRepository.sumMontoByFechaPago(fecha);
    }

    @Override
    public Map<String, Long> obtenerMetodosPagoMasUsados() {
        return pagoRepository.findMetodosPagoMasUsados().stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }

    @Override
    public String generarReporteIngresos() {
        StringBuilder reporte = new StringBuilder("--- Reporte de Ingresos ---\n\n");
        LocalDate hoy = LocalDate.now();
        BigDecimal totalHoy = obtenerTotalIngresosPorFecha(hoy);
        if (totalHoy == null) totalHoy = BigDecimal.ZERO; 
        reporte.append("Total de ingresos el ").append(hoy).append(": $").append(totalHoy).append("\n");

        Map<String, Long> metodosMasUsados = obtenerMetodosPagoMasUsados();
        reporte.append("\nMétodos de pago más usados:\n");
        if (metodosMasUsados.isEmpty()) {
            reporte.append("No hay transacciones registradas.\n");
        } else {
            metodosMasUsados.forEach((metodo, cantidad) ->
                    reporte.append("- ").append(metodo).append(": ").append(cantidad).append(" transacciones\n")
            );
        }
        return reporte.toString();
    }


    @Override
    public PagoDTO actualizarPago(Long id, PagoDTO pagoDTO) {
        Optional<PagoClinica> pagoExistenteOptional = pagoRepository.findById(id);
        if (pagoExistenteOptional.isPresent()) {
            PagoClinica pagoExistente = pagoExistenteOptional.get();
            pagoExistente.setCitaId(pagoDTO.getCitaId());
            pagoExistente.setPacienteId(pagoDTO.getPacienteId());
            pagoExistente.setFechaPago(pagoDTO.getFechaPago());
            pagoExistente.setMetodoPago(pagoDTO.getMetodoPago());
            pagoExistente.setMonto(pagoDTO.getMonto());
            pagoExistente.setEstado(pagoDTO.getEstado());

            PagoClinica pagoActualizado = pagoRepository.save(pagoExistente);
            return pagoClinicaMapper.toPayClinicalDTO(pagoActualizado);
        } else {
            return null; 
        }
    }

    @Override
    public void eliminarPago(Long id) {
        if (pagoRepository.existsById(id)) {
            pagoRepository.deleteById(id);
        } else {
        }
    }

    @Override
    public PagoDTO procesarPago(Long id, String metodoPago) {
        Optional<PagoClinica> pagoOptional = pagoRepository.findById(id);
        if (pagoOptional.isPresent()) {
            PagoClinica pagoAProcesar = pagoOptional.get();
            pagoAProcesar.setMetodoPago(metodoPago);
            pagoAProcesar.setEstado("Pagado");
            pagoAProcesar.setFechaPago(LocalDate.now());
            PagoClinica pagoProcesado = pagoRepository.save(pagoAProcesar);
            return pagoClinicaMapper.toPayClinicalDTO(pagoProcesado);
        } else {
            return null;
        }
    }
}