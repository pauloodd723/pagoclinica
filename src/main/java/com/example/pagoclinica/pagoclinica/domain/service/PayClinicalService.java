package com.example.pagoclinica.pagoclinica.domain.service;

import com.example.pagoclinica.pagoclinica.domain.dto.CitaDTO;
import com.example.pagoclinica.pagoclinica.domain.dto.EstadoCitaRequestDTO;
import com.example.pagoclinica.pagoclinica.domain.dto.PagoDTO;
import com.example.pagoclinica.pagoclinica.domain.dto.PacienteDTO;
import com.example.pagoclinica.pagoclinica.infraestructura.client.CitaCliente;
import com.example.pagoclinica.pagoclinica.infraestructura.client.PacienteCliente;

import com.example.pagoclinica.pagoclinica.infraestructura.repositories.PayClinicalImpl; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PayClinicalService {

    @Autowired
    private PayClinicalImpl pagoClinicaImpl;

    @Autowired
    private CitaCliente citaCliente;

    @Autowired
    private PacienteCliente pacienteCliente;

    public PagoDTO registrarPago(PagoDTO payClinicalDTO) {

        try {
            CitaDTO cita = citaCliente.getAppointmentById(payClinicalDTO.getCitaId());
            if (cita == null) { 
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La cita con ID " + payClinicalDTO.getCitaId() + " no existe.");
            }

            if (payClinicalDTO.getPacienteId() != null) {
                PacienteDTO paciente = pacienteCliente.getPacienteById(payClinicalDTO.getPacienteId());
                if (paciente == null) { 
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El paciente con ID " + payClinicalDTO.getPacienteId() + " asociado al pago no existe.");
                }

                 if (!cita.getPacienteId().equals(payClinicalDTO.getPacienteId())) {
                    System.err.println("ADVERTENCIA: El pacienteId del pago (" + payClinicalDTO.getPacienteId() +
                                       ") no coincide con el pacienteId (" + cita.getPacienteId() + ") de la cita (" + cita.getId() + ").");
                                    }
            } else {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pacienteId es requerido en el pago.");
            }

        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (feign.FeignException fe) {
            String serviceName = "";
            if (fe.request() != null && fe.request().url().contains("citaspoo")) {
                serviceName = "Citas";
            } else if (fe.request() != null && fe.request().url().contains("pacientes-production")) {
                serviceName = "Pacientes";
            }

            if (fe.status() == 404) {
                 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo verificar el recurso en el servicio de " + serviceName + ". ID: " + (serviceName.equals("Citas") ? payClinicalDTO.getCitaId() : payClinicalDTO.getPacienteId()) + ". El recurso no fue encontrado (404).", fe);
            } else {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error de comunicación con el servicio de " + serviceName + ". Detalles: " + fe.getMessage(), fe);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al verificar dependencias para el pago. Detalles: " + e.getMessage(), e);
        }
        return pagoClinicaImpl.registrarPago(payClinicalDTO);
    }

    public PagoDTO obtenerPagoPorId(Long id) {
        return pagoClinicaImpl.obtenerPagoPorId(id);
    }

    public List<PagoDTO> obtenerTodosLosPagos() {
        return pagoClinicaImpl.obtenerTodosLosPagos();
    }

    public List<PagoDTO> obtenerPagosPorCitaId(Long citaId) {
        return pagoClinicaImpl.obtenerPagosPorCitaId(citaId);
    }

    public List<PagoDTO> obtenerPagosPendientes() {
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

    public PagoDTO actualizarPago(Long id, PagoDTO payClinicalDTO) {

        try {
            if (payClinicalDTO.getCitaId() != null) {
                CitaDTO cita = citaCliente.getAppointmentById(payClinicalDTO.getCitaId());
                if (cita == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La cita con ID " + payClinicalDTO.getCitaId() + " para actualizar no existe.");
            }
            if (payClinicalDTO.getPacienteId() != null) {
                PacienteDTO paciente = pacienteCliente.getPacienteById(payClinicalDTO.getPacienteId());
                if (paciente == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El paciente con ID " + payClinicalDTO.getPacienteId() + " para actualizar no existe.");
            }
        } catch (feign.FeignException.NotFound fe) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo verificar la cita o paciente para la actualización. El recurso no fue encontrado (404).", fe);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error de comunicación con servicios externos durante la actualización del pago.", e);
        }
        return pagoClinicaImpl.actualizarPago(id, payClinicalDTO);
    }

    public boolean eliminarPago(Long id) {

        try {
            pagoClinicaImpl.eliminarPago(id);  
            return true;
        } catch (Exception e) {

            System.err.println("Error al intentar eliminar el pago ID " + id + ": " + e.getMessage());
            return false;
        }
    }

    public PagoDTO procesarPago(Long id, String metodoPago) {
        PagoDTO pagoProcesado = pagoClinicaImpl.procesarPago(id, metodoPago);
        if (pagoProcesado != null && "Pagado".equalsIgnoreCase(pagoProcesado.getEstado())) {
            try {
                EstadoCitaRequestDTO estadoRequest = new EstadoCitaRequestDTO("Pagado");
                citaCliente.updateAppointmentStatus(pagoProcesado.getCitaId(), estadoRequest);
            } catch (Exception e) {
                System.err.println("Pago procesado (ID: " + pagoProcesado.getId() + ") pero falló la actualización del estado de la cita (ID: " + pagoProcesado.getCitaId() + "): " + e.getMessage());
            }
        }
        return pagoProcesado;
    }


    public List<CitaDTO> obtenerTodasLasCitasExternas() {
        try {
            return citaCliente.getAllAppointments();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error al obtener todas las citas del servicio externo: " + e.getMessage(), e);
        }
    }

    public CitaDTO obtenerCitaExternaPorId(Long id) {
        try {
            CitaDTO cita = citaCliente.getAppointmentById(id);
            if (cita == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita externa no encontrada con ID: " + id);
            }
            return cita;
        } catch (feign.FeignException.NotFound fe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita externa no encontrada con ID: " + id + ". Detalles: " + fe.getMessage(), fe);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error al obtener la cita externa con ID " + id + ": " + e.getMessage(), e);
        }
    }

    public CitaDTO actualizarEstadoCitaExterna(Long citaId, EstadoCitaRequestDTO estadoRequest) {
        String nuevoEstado = estadoRequest.getEstado();
        if (!"Pagado".equalsIgnoreCase(nuevoEstado) && !"Anulado".equalsIgnoreCase(nuevoEstado) && !"Pendiente".equalsIgnoreCase(nuevoEstado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado '" + nuevoEstado + "' no válido. Los estados permitidos son 'Pagado', 'Anulado', 'Pendiente'.");
        }
        try {
            CitaDTO citaExistente = citaCliente.getAppointmentById(citaId); 
             if (citaExistente == null) { 
                 throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la cita con ID: " + citaId + " para actualizar.");
            }
            return citaCliente.updateAppointmentStatus(citaId, estadoRequest);
        } catch (feign.FeignException.NotFound fe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró la cita con ID: " + citaId + " para actualizar. Detalles: " + fe.getMessage(), fe);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error al actualizar el estado de la cita externa con ID " + citaId + ": " + e.getMessage(), e);
        }
    }


    public List<PacienteDTO> obtenerTodosLosPacientesExternos() {
        try {
            return pacienteCliente.getAllPacientes();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error al obtener todos los pacientes del servicio externo: " + e.getMessage(), e);
        }
    }

    public PacienteDTO obtenerPacienteExternoPorId(Long id) {
        try {
            PacienteDTO paciente = pacienteCliente.getPacienteById(id);
            if (paciente == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente externo no encontrado con ID: " + id);
            }
            return paciente;
        } catch (feign.FeignException.NotFound fe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente externo no encontrado con ID: " + id + ". Detalles: " + fe.getMessage(), fe);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error al obtener el paciente externo con ID " + id + ": " + e.getMessage(), e);
        }
    }
}