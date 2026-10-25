package com.seneyu.service;

import com.seneyu.Patient;
import com.seneyu.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public void insertPatient(Patient patient) {
        patientRepository.save(patient);
    }
}
