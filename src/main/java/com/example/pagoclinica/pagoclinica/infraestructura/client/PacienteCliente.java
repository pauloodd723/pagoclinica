package com.example.pagoclinica.pagoclinica.infraestructura.client;

import com.example.pagoclinica.pagoclinica.domain.dto.PacienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "pacientes-client", url = "https://microserviciospacientes-production.up.railway.app")
public interface PacienteCliente {

    @GetMapping("/pacientes") 
    List<PacienteDTO> getAllPacientes();

    @GetMapping("/pacientes/{id}") 
    PacienteDTO getPacienteById(@PathVariable("id") Long id);
}