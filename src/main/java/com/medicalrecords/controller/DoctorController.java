package com.medicalrecords.controller;

import com.medicalrecords.model.Doctor;
import com.medicalrecords.service.DoctorService;
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
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final UserService userService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        return "doctors/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.findById(id);
        model.addAttribute("doctor", doctor);
        model.addAttribute("linkedUser", userService.findByDoctor(doctor).orElse(null));
        return "doctors/view";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        return "doctors/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute Doctor doctor, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "doctors/form";
        doctorService.save(doctor);
        redirectAttributes.addFlashAttribute("successMessage", "Doctor created successfully.");
        return "redirect:/doctors";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.findById(id));
        return "doctors/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Doctor doctor,
                         BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) return "doctors/form";
        doctorService.update(id, doctor);
        redirectAttributes.addFlashAttribute("successMessage", "Doctor updated successfully.");
        return "redirect:/doctors";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        doctorService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Doctor deleted.");
        return "redirect:/doctors";
    }
}
