package com.example.pagoclinica.pagoclinica.infraestructura.mapper;

import com.example.pagoclinica.pagoclinica.domain.dto.PagoDTO;
import com.example.pagoclinica.pagoclinica.infraestructura.entity.PagoClinica;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PagoClinicaMapper {
    PagoDTO toPayClinicalDTO(PagoClinica pagoClinica);
    PagoClinica toPagoClinica(PagoDTO payClinicalDTO);
}