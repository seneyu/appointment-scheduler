package com.seneyu.service;

import com.seneyu.entity.Patient;
import com.seneyu.exception.ResourceNotFoundException;
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

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + id + " not found."));
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient updatedPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + id + " not found."));

        updatedPatient.setFirstName(patientDetails.getFirstName());
        updatedPatient.setLastName(patientDetails.getLastName());
        updatedPatient.setDateOfBirth(patientDetails.getDateOfBirth());
        updatedPatient.setPhone(patientDetails.getPhone());
        updatedPatient.setEmail(patientDetails.getEmail());
        return patientRepository.save(updatedPatient);
    }

    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient " + id + " not found."));
        patientRepository.delete(patient);
    }
}
