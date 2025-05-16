package com.example.pagoclinica.pagoclinica.infraestructura.repositories;

import com.example.pagoclinica.pagoclinica.domain.dto.PayClinicalDTO;
import com.example.pagoclinica.pagoclinica.domain.repository.IPayClinical;
import com.example.pagoclinica.pagoclinica.infraestructura.crud.PagoRepository;
import com.example.pagoclinica.pagoclinica.infraestructura.entity.PagoClinica;
import com.example.pagoclinica.pagoclinica.infraestructura.mapper.PagoClinicaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository; // Cambiado a @Repository ya que es una implementación de repositorio
// Aunque @Service también funciona para la inyección de dependencias.

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository // Es más semántico usar @Repository para clases de acceso a datos
public class PayClinicalImpl implements IPayClinical {

    private final PagoRepository pagoRepository;
    private final PagoClinicaMapper pagoClinicaMapper;

    @Autowired
    public PayClinicalImpl(PagoRepository pagoRepository, PagoClinicaMapper pagoClinicaMapper) {
        this.pagoRepository = pagoRepository;
        this.pagoClinicaMapper = pagoClinicaMapper;
    }

    @Override
    public PayClinicalDTO registrarPago(PayClinicalDTO payClinicalDTO) {
        PagoClinica pagoClinica = pagoClinicaMapper.toPagoClinica(payClinicalDTO);
        // Se podría establecer la fecha de pago aquí si no viene en el DTO
        // pagoClinica.setFechaPago(LocalDate.now());
        PagoClinica pagoGuardado = pagoRepository.save(pagoClinica);
        return pagoClinicaMapper.toPayClinicalDTO(pagoGuardado);
    }

    @Override
    public PayClinicalDTO obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id)
                .map(pagoClinicaMapper::toPayClinicalDTO)
                .orElse(null);
    }

    @Override
    public List<PayClinicalDTO> obtenerTodosLosPagos() {
        return pagoRepository.findAll().stream()
                .map(pagoClinicaMapper::toPayClinicalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PayClinicalDTO> obtenerPagosPorCitaId(Long citaId) {
        return pagoRepository.findByCitaId(citaId).stream()
                .map(pagoClinicaMapper::toPayClinicalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PayClinicalDTO> obtenerPagosPendientes() {
        return pagoRepository.findByEstadoPago("Pendiente").stream() // Asumiendo que "Pendiente" es el estado
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
        if (totalHoy == null) totalHoy = BigDecimal.ZERO; // Manejar nulos
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

    // Implementación de los nuevos métodos
    @Override
    public PayClinicalDTO actualizarPago(Long id, PayClinicalDTO payClinicalDTO) {
        Optional<PagoClinica> pagoExistenteOptional = pagoRepository.findById(id);
        if (pagoExistenteOptional.isPresent()) {
            PagoClinica pagoExistente = pagoExistenteOptional.get();
            // Actualiza los campos necesarios. Evita actualizar el ID.
            // Puedes usar el mapper para esto o hacerlo manualmente.
            // Aquí un ejemplo manual para control granular:
            pagoExistente.setCitaId(payClinicalDTO.getCitaId());
            pagoExistente.setPacienteId(payClinicalDTO.getPacienteId());
            pagoExistente.setFechaPago(payClinicalDTO.getFechaPago());
            pagoExistente.setMetodoPago(payClinicalDTO.getMetodoPago());
            pagoExistente.setMonto(payClinicalDTO.getMonto());
            pagoExistente.setEstadoPago(payClinicalDTO.getEstadoPago());

            PagoClinica pagoActualizado = pagoRepository.save(pagoExistente);
            return pagoClinicaMapper.toPayClinicalDTO(pagoActualizado);
        } else {
            return null; // O lanzar una excepción personalizada
        }
    }

    @Override
    public void eliminarPago(Long id) {
        if (pagoRepository.existsById(id)) {
            pagoRepository.deleteById(id);
        } else {
            // Opcional: lanzar una excepción si el elemento no existe
            // throw new RuntimeException("Pago no encontrado con id: " + id);
        }
    }

    @Override
    public PayClinicalDTO procesarPago(Long id, String metodoPago) {
        Optional<PagoClinica> pagoOptional = pagoRepository.findById(id);
        if (pagoOptional.isPresent()) {
            PagoClinica pagoAProcesar = pagoOptional.get();
            pagoAProcesar.setMetodoPago(metodoPago);
            pagoAProcesar.setEstadoPago("Pagado"); // O el estado que corresponda
            pagoAProcesar.setFechaPago(LocalDate.now()); // Actualizar fecha al momento del pago
            PagoClinica pagoProcesado = pagoRepository.save(pagoAProcesar);
            return pagoClinicaMapper.toPayClinicalDTO(pagoProcesado);
        } else {
            return null; // O lanzar una excepción
        }
    }
}