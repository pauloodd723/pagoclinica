package com.example.pagoclinica.pagoclinica.infraestructura.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagos_clinica")
@Data
public class PagoClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "citaid" ,nullable = false)
    private Long citaId;

    @Column(name = "pacienteid" ,nullable = false)
    private Long pacienteId;

    @Column(name = "fechapago" ,nullable = false)
    private LocalDate fechaPago;

    @Column(name = "metodopago" ,nullable = false)
    private String metodoPago;

    @Column(name = "monto" ,nullable = false)
    private BigDecimal monto;

    @Column(name = "estado" ,nullable = false)
    private String estado;

    @Column(name = "referencia" ,nullable = false)
    private String referencia;

}