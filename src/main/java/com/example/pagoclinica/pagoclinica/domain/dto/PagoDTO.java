package com.example.pagoclinica.pagoclinica.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoDTO {
    private Long id;
    private Long citaId;
    private Long pacienteId;
    private LocalDate fechaPago;
    private String metodoPago;
    private BigDecimal monto;
    private String estado;
    private String referencia;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getCitaId() {
        return citaId;
    }
    public void setCitaId(Long citaId) {
        this.citaId = citaId;
    }
    public Long getPacienteId() {
        return pacienteId;
    }
    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }
    public LocalDate getFechaPago() {
        return fechaPago;
    }
    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }
    public String getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    public BigDecimal getMonto() {
        return monto;
    }
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getReferencia() {
        return referencia;
    }
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

}
