package com.seneyu.controller;

import com.seneyu.Appointment;
import com.seneyu.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @PostMapping
    public void addNewAppointment(@RequestBody Appointment appointment) {
        appointmentService.insertAppointment(appointment);
    }
}
