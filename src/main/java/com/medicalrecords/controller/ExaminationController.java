package com.medicalrecords.controller;

import com.medicalrecords.config.CurrentUserHelper;
import com.medicalrecords.model.*;
import com.medicalrecords.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/examinations")
@RequiredArgsConstructor
public class ExaminationController {

    private final ExaminationService examinationService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final CurrentUserHelper currentUserHelper;

    @GetMapping
    public String list(Model model) {
        User currentUser = currentUserHelper.getCurrentUser();
        List<Examination> examinations;

        if (currentUser.getRole() == Role.PATIENT) {
            examinations = examinationService.findPatientHistory(currentUser.getPatient());
        } else if (currentUser.getRole() == Role.DOCTOR) {
            examinations = examinationService.findByDoctor(currentUser.getDoctor());
        } else {
            examinations = examinationService.findAll();
        }

        model.addAttribute("examinations", examinations);
        return "examinations/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String newForm(Model model) {
        User currentUser = currentUserHelper.getCurrentUser();
        model.addAttribute("examination", new Examination());
        model.addAttribute("doctors", currentUser.getRole() == Role.ADMIN
                ? doctorService.findAll()
                : List.of(currentUser.getDoctor()));
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("diagnoses", diagnosisService.findAll());
        return "examinations/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String create(@Valid @ModelAttribute Examination examination, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnosisService.findAll());
            return "examinations/form";
        }
        User currentUser = currentUserHelper.getCurrentUser();
        if (currentUser.getRole() == Role.DOCTOR) {
            examination.setDoctor(currentUser.getDoctor());
        }
        examinationService.save(examination);
        redirectAttributes.addFlashAttribute("successMessage", "Examination added successfully.");
        return "redirect:/examinations";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        User currentUser = currentUserHelper.getCurrentUser();
        Examination examination = examinationService.findById(id);
        if (currentUser.getRole() == Role.DOCTOR
                && !examination.getDoctor().getId().equals(currentUser.getDoctor().getId())) {
            return "redirect:/examinations?error=forbidden";
        }
        model.addAttribute("examination", examination);
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("diagnoses", diagnosisService.findAll());
        return "examinations/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Examination examination,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("doctors", doctorService.findAll());
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("diagnoses", diagnosisService.findAll());
            return "examinations/form";
        }
        User currentUser = currentUserHelper.getCurrentUser();
        Doctor doctorCtx = currentUser.getRole() == Role.DOCTOR ? currentUser.getDoctor() : null;
        examinationService.update(id, examination, doctorCtx);
        redirectAttributes.addFlashAttribute("successMessage", "Examination updated successfully.");
        return "redirect:/examinations";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = currentUserHelper.getCurrentUser();
        Examination examination = examinationService.findById(id);
        if (currentUser.getRole() == Role.DOCTOR
                && !examination.getDoctor().getId().equals(currentUser.getDoctor().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only delete your own examinations.");
            return "redirect:/examinations";
        }
        examinationService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Examination deleted.");
        return "redirect:/examinations";
    }
}
