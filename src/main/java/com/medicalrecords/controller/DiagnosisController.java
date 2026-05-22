package com.medicalrecords.controller;

import com.medicalrecords.model.Diagnosis;
import com.medicalrecords.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/diagnoses")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("diagnoses", diagnosisService.findAll());
        return "diagnoses/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("diagnosis", new Diagnosis());
        return "diagnoses/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute Diagnosis diagnosis, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "diagnoses/form";
        diagnosisService.save(diagnosis);
        redirectAttributes.addFlashAttribute("successMessage", "Diagnosis created successfully.");
        return "redirect:/diagnoses";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("diagnosis", diagnosisService.findById(id));
        return "diagnoses/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Diagnosis diagnosis,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "diagnoses/form";
        diagnosisService.update(id, diagnosis);
        redirectAttributes.addFlashAttribute("successMessage", "Diagnosis updated successfully.");
        return "redirect:/diagnoses";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        diagnosisService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Diagnosis deleted.");
        return "redirect:/diagnoses";
    }
}
