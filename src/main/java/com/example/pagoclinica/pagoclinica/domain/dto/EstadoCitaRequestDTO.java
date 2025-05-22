package com.example.pagoclinica.pagoclinica.domain.dto;

public class EstadoCitaRequestDTO {
    private String estado;


    public EstadoCitaRequestDTO() {
    }

    public EstadoCitaRequestDTO(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}