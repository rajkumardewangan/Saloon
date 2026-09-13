package com.example.salon.controller;

import com.example.salon.entity.Appointment;
import com.example.salon.entity.AppointmentStatus;
import com.example.salon.service.AppointmentService;
import com.example.salon.service.BarberService;
import com.example.salon.service.CustomerService;
import com.example.salon.service.SalonServiceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CustomerService customerService;
    private final BarberService barberService;
    private final SalonServiceService salonServiceService;

    public AppointmentController(AppointmentService appointmentService,
                                  CustomerService customerService,
                                  BarberService barberService,
                                  SalonServiceService salonServiceService) {
        this.appointmentService = appointmentService;
        this.customerService = customerService;
        this.barberService = barberService;
        this.salonServiceService = salonServiceService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("appointments", appointmentService.findAll());
        return "appointments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        addFormLookups(model);
        model.addAttribute("statuses", AppointmentStatus.values());
        return "appointments/form";
    }

    @PostMapping
    public String save(@RequestParam Long customerId,
                        @RequestParam Long barberId,
                        @RequestParam Long serviceId,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentDateTime,
                        @RequestParam(required = false) AppointmentStatus status,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        try {
            Appointment appointment = new Appointment();
            appointment.setCustomer(customerService.findById(customerId));
            appointment.setBarber(barberService.findById(barberId));
            appointment.setService(salonServiceService.findById(serviceId));
            appointment.setAppointmentDateTime(appointmentDateTime);
            appointment.setStatus(status != null ? status : AppointmentStatus.SCHEDULED);

            appointmentService.save(appointment);
            redirectAttributes.addFlashAttribute("message", "Appointment booked successfully.");
            return "redirect:/appointments";
        } catch (IllegalStateException | IllegalArgumentException ex) {
            addFormLookups(model);
            model.addAttribute("statuses", AppointmentStatus.values());
            model.addAttribute("errorMessage", ex.getMessage());
            return "appointments/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Appointment cancelled.");
        return "redirect:/appointments";
    }

    private void addFormLookups(Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("barbers", barberService.findAll());
        model.addAttribute("services", salonServiceService.findAll());
    }
}
