package com.medicalrecords.controller;

import com.medicalrecords.config.CurrentUserHelper;
import com.medicalrecords.model.Patient;
import com.medicalrecords.model.Role;
import com.medicalrecords.model.User;
import com.medicalrecords.service.DoctorService;
import com.medicalrecords.service.ExaminationService;
import com.medicalrecords.service.PatientService;
import com.medicalrecords.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ExaminationService examinationService;
    private final CurrentUserHelper currentUserHelper;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String list(Model model) {
        model.addAttribute("patients", patientService.findAll());
        return "patients/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        User currentUser = currentUserHelper.getCurrentUser();
        if (currentUser != null && currentUser.getRole() == Role.PATIENT) {
            if (!currentUser.getPatient().getId().equals(id)) {
                return "redirect:/patients/" + currentUser.getPatient().getId();
            }
        }
        Patient patient = patientService.findById(id);
        model.addAttribute("patient", patient);
        model.addAttribute("examinations", examinationService.findPatientHistory(patient));
        model.addAttribute("linkedUser", userService.findByPatient(patient).orElse(null));
        return "patients/view";
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public String myProfile(Model model) {
        User currentUser = currentUserHelper.getCurrentUser();
        Patient patient = currentUser.getPatient();
        model.addAttribute("patient", patient);
        model.addAttribute("examinations", examinationService.findPatientHistory(patient));
        model.addAttribute("linkedUser", null);
        return "patients/view";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("gps", doctorService.findAllGps());
        return "patients/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute Patient patient, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {
        if (patient.getPersonalDoctor() == null || patient.getPersonalDoctor().getId() == null) {
            result.rejectValue("personalDoctor", "required", "Personal doctor is required");
        }
        if (result.hasErrors()) {
            model.addAttribute("gps", doctorService.findAllGps());
            return "patients/form";
        }
        patientService.save(patient);
        redirectAttributes.addFlashAttribute("successMessage", "Patient registered successfully.");
        return "redirect:/patients";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.findById(id));
        model.addAttribute("gps", doctorService.findAllGps());
        return "patients/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Patient patient,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (patient.getPersonalDoctor() == null || patient.getPersonalDoctor().getId() == null) {
            result.rejectValue("personalDoctor", "required", "Personal doctor is required");
        }
        if (result.hasErrors()) {
            model.addAttribute("gps", doctorService.findAllGps());
            return "patients/form";
        }
        patientService.update(id, patient);
        redirectAttributes.addFlashAttribute("successMessage", "Patient updated successfully.");
        return "redirect:/patients";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        patientService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Patient deleted.");
        return "redirect:/patients";
    }
}
