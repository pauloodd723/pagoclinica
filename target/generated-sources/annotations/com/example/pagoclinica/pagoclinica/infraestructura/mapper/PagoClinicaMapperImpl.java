package com.example.pagoclinica.pagoclinica.infraestructura.mapper;

import com.example.pagoclinica.pagoclinica.domain.dto.PayClinicalDTO;
import com.example.pagoclinica.pagoclinica.infraestructura.entity.PagoClinica;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-16T11:09:43-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class PagoClinicaMapperImpl implements PagoClinicaMapper {

    @Override
    public PayClinicalDTO toPayClinicalDTO(PagoClinica pagoClinica) {
        if ( pagoClinica == null ) {
            return null;
        }

        PayClinicalDTO payClinicalDTO = new PayClinicalDTO();

        payClinicalDTO.setCitaId( pagoClinica.getCitaId() );
        payClinicalDTO.setEstadoPago( pagoClinica.getEstadoPago() );
        payClinicalDTO.setFechaPago( pagoClinica.getFechaPago() );
        payClinicalDTO.setId( pagoClinica.getId() );
        payClinicalDTO.setMetodoPago( pagoClinica.getMetodoPago() );
        payClinicalDTO.setMonto( pagoClinica.getMonto() );
        payClinicalDTO.setPacienteId( pagoClinica.getPacienteId() );

        return payClinicalDTO;
    }

    @Override
    public PagoClinica toPagoClinica(PayClinicalDTO payClinicalDTO) {
        if ( payClinicalDTO == null ) {
            return null;
        }

        PagoClinica pagoClinica = new PagoClinica();

        pagoClinica.setCitaId( payClinicalDTO.getCitaId() );
        pagoClinica.setEstadoPago( payClinicalDTO.getEstadoPago() );
        pagoClinica.setFechaPago( payClinicalDTO.getFechaPago() );
        pagoClinica.setId( payClinicalDTO.getId() );
        pagoClinica.setMetodoPago( payClinicalDTO.getMetodoPago() );
        pagoClinica.setMonto( payClinicalDTO.getMonto() );
        pagoClinica.setPacienteId( payClinicalDTO.getPacienteId() );

        return pagoClinica;
    }
}
