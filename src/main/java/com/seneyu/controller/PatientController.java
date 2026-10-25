package com.seneyu.controller;

import com.seneyu.Patient;
import com.seneyu.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<Patient> getPatients() {
        return patientService.getAllPatients();
    }

    @PostMapping
    public void addNewPatient(
            @RequestBody Patient patient) {
        patientService.insertPatient(patient);
    }
}
