package com.project.patient.service;

import com.project.patient.dto.PatientRequestDTO;
import com.project.patient.exception.EmailAlreadyExistsException;
import com.project.patient.exception.PatientNotFoundException;
import com.project.patient.grpc.BillingServiceGrpcClient;
import com.project.patient.kafka.KafkaProducer;
import com.project.patient.mapper.PatientMapper;
import com.project.patient.model.Patient;
import com.project.patient.repository.PatientRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import com.project.patient.dto.PatientResponseDTO;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;
    public PatientService(PatientRepository patientRepository,BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer){

        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;

    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        List<PatientResponseDTO> patientResponseDTOS = patients.stream()
                .map(PatientMapper::toDTO).toList();
        return patientResponseDTOS;
    }
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("email already there"+patientRequestDTO.getEmail());
        }

        Patient newPaitent = patientRepository.save(
                PatientMapper.toModel(patientRequestDTO));
        billingServiceGrpcClient.createBillingAccount(newPaitent.getId().toString(), newPaitent.getName(), newPaitent.getEmail());

        kafkaProducer.sendEvent(newPaitent);

        return PatientMapper.toDTO(newPaitent);
    }

    public PatientResponseDTO updatePatient(PatientRequestDTO patientRequestDTO,UUID id){
        Patient patient = patientRepository.findById(id).orElseThrow(()->new PatientNotFoundException("patient not found" + id));
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)){
            throw new EmailAlreadyExistsException("email already there"+patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress((patientRequestDTO.getAddress()));
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }


    public void deletePatient(UUID id){
        Patient patient = patientRepository.findById(id).orElseThrow(()->new PatientNotFoundException("patient not found"));
        patientRepository.delete(patient);

    }



}
