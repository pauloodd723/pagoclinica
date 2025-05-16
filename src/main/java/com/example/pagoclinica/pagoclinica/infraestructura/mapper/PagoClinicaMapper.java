package com.example.pagoclinica.pagoclinica.infraestructura.mapper;




import com.example.pagoclinica.pagoclinica.domain.dto.PayClinicalDTO;
import com.example.pagoclinica.pagoclinica.infraestructura.entity.PagoClinica;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PagoClinicaMapper {
    PayClinicalDTO toPayClinicalDTO(PagoClinica pagoClinica);
    PagoClinica toPagoClinica(PayClinicalDTO payClinicalDTO);
}