package com.medicalrecords.controller;

import com.medicalrecords.exception.DuplicateResourceException;
import com.medicalrecords.model.Doctor;
import com.medicalrecords.model.Patient;
import com.medicalrecords.service.DoctorService;
import com.medicalrecords.service.PatientService;
import com.medicalrecords.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAccountController {

    private final UserService userService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @GetMapping("/doctors/{id}/assign-user")
    public String doctorAssignForm(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.findById(id);
        model.addAttribute("entityName", doctor.getName());
        model.addAttribute("backUrl", "/doctors/" + id);
        model.addAttribute("submitUrl", "/doctors/" + id + "/assign-user");
        userService.findByDoctor(doctor).ifPresent(u -> model.addAttribute("existingUsername", u.getUsername()));
        return "users/assign-form";
    }

    @PostMapping("/doctors/{id}/assign-user")
    public String doctorAssign(@PathVariable Long id,
                               @RequestParam String username,
                               @RequestParam(required = false) String password,
                               RedirectAttributes redirectAttributes, Model model) {
        Doctor doctor = doctorService.findById(id);
        boolean isNew = userService.findByDoctor(doctor).isEmpty();
        if (isNew && (password == null || password.isBlank())) {
            model.addAttribute("entityName", doctor.getName());
            model.addAttribute("backUrl", "/doctors/" + id);
            model.addAttribute("submitUrl", "/doctors/" + id + "/assign-user");
            model.addAttribute("errorMessage", "Password is required when creating a new account.");
            model.addAttribute("existingUsername", "");
            return "users/assign-form";
        }
        try {
            userService.assignToDoctor(doctor, username, password);
            redirectAttributes.addFlashAttribute("successMessage", "User account assigned successfully.");
        } catch (DuplicateResourceException e) {
            model.addAttribute("entityName", doctor.getName());
            model.addAttribute("backUrl", "/doctors/" + id);
            model.addAttribute("submitUrl", "/doctors/" + id + "/assign-user");
            model.addAttribute("errorMessage", e.getMessage());
            userService.findByDoctor(doctor).ifPresent(u -> model.addAttribute("existingUsername", u.getUsername()));
            return "users/assign-form";
        }
        return "redirect:/doctors/" + id;
    }

    @GetMapping("/patients/{id}/assign-user")
    public String patientAssignForm(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id);
        model.addAttribute("entityName", patient.getName());
        model.addAttribute("backUrl", "/patients/" + id);
        model.addAttribute("submitUrl", "/patients/" + id + "/assign-user");
        userService.findByPatient(patient).ifPresent(u -> model.addAttribute("existingUsername", u.getUsername()));
        return "users/assign-form";
    }

    @PostMapping("/patients/{id}/assign-user")
    public String patientAssign(@PathVariable Long id,
                                @RequestParam String username,
                                @RequestParam(required = false) String password,
                                RedirectAttributes redirectAttributes, Model model) {
        Patient patient = patientService.findById(id);
        boolean isNew = userService.findByPatient(patient).isEmpty();
        if (isNew && (password == null || password.isBlank())) {
            model.addAttribute("entityName", patient.getName());
            model.addAttribute("backUrl", "/patients/" + id);
            model.addAttribute("submitUrl", "/patients/" + id + "/assign-user");
            model.addAttribute("errorMessage", "Password is required when creating a new account.");
            model.addAttribute("existingUsername", "");
            return "users/assign-form";
        }
        try {
            userService.assignToPatient(patient, username, password);
            redirectAttributes.addFlashAttribute("successMessage", "User account assigned successfully.");
        } catch (DuplicateResourceException e) {
            model.addAttribute("entityName", patient.getName());
            model.addAttribute("backUrl", "/patients/" + id);
            model.addAttribute("submitUrl", "/patients/" + id + "/assign-user");
            model.addAttribute("errorMessage", e.getMessage());
            userService.findByPatient(patient).ifPresent(u -> model.addAttribute("existingUsername", u.getUsername()));
            return "users/assign-form";
        }
        return "redirect:/patients/" + id;
    }
}
