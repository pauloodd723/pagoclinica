package com.example.pagoclinica.pagoclinica.domain.dto;



import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PayClinicalDTO {
    private Long id;
    private Long citaId;
    private Long pacienteId;
    private LocalDate fechaPago;
    private String metodoPago;
    private BigDecimal monto;
    private String estadoPago;
}