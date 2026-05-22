package com.medicalrecords.controller;

import com.medicalrecords.config.CurrentUserHelper;
import com.medicalrecords.model.*;
import com.medicalrecords.service.ExaminationService;
import com.medicalrecords.service.SickLeaveService;
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
@RequestMapping("/sickleaves")
@RequiredArgsConstructor
public class SickLeaveController {

    private final SickLeaveService sickLeaveService;
    private final ExaminationService examinationService;
    private final CurrentUserHelper currentUserHelper;

    @GetMapping
    public String list(Model model) {
        User currentUser = currentUserHelper.getCurrentUser();
        List<SickLeave> sickLeaves;

        if (currentUser.getRole() == Role.PATIENT) {
            sickLeaves = sickLeaveService.findByPatientId(currentUser.getPatient().getId());
        } else {
            sickLeaves = sickLeaveService.findAll();
        }

        model.addAttribute("sickLeaves", sickLeaves);
        model.addAttribute("currentUser", currentUser);
        return "sickleaves/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String newForm(Model model) {
        User currentUser = currentUserHelper.getCurrentUser();
        List<Examination> exams = currentUser.getRole() == Role.DOCTOR
                ? examinationService.findByDoctor(currentUser.getDoctor())
                : examinationService.findAll();
        model.addAttribute("sickLeave", new SickLeave());
        model.addAttribute("examinations", exams);
        return "sickleaves/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String create(@Valid @ModelAttribute SickLeave sickLeave, BindingResult result,
                         Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            User currentUser = currentUserHelper.getCurrentUser();
            model.addAttribute("examinations", currentUser.getRole() == Role.DOCTOR
                    ? examinationService.findByDoctor(currentUser.getDoctor())
                    : examinationService.findAll());
            return "sickleaves/form";
        }

        User currentUser = currentUserHelper.getCurrentUser();
        if (currentUser.getRole() == Role.DOCTOR) {
            Examination exam = examinationService.findById(sickLeave.getExamination().getId());
            if (!exam.getDoctor().getId().equals(currentUser.getDoctor().getId())) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "You can only issue sick leaves for your own examinations.");
                return "redirect:/sickleaves";
            }
        }

        sickLeaveService.save(sickLeave);
        redirectAttributes.addFlashAttribute("successMessage", "Sick leave issued successfully.");
        return "redirect:/sickleaves";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = currentUserHelper.getCurrentUser();
        SickLeave sickLeave = sickLeaveService.findById(id);

        if (currentUser.getRole() == Role.DOCTOR
                && !sickLeave.getExamination().getDoctor().getId().equals(currentUser.getDoctor().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You can only edit sick leaves from your own examinations.");
            return "redirect:/sickleaves";
        }

        List<Examination> exams = currentUser.getRole() == Role.DOCTOR
                ? examinationService.findByDoctor(currentUser.getDoctor())
                : examinationService.findAll();
        model.addAttribute("sickLeave", sickLeave);
        model.addAttribute("examinations", exams);
        return "sickleaves/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String update(@PathVariable Long id, @Valid @ModelAttribute SickLeave sickLeave,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = currentUserHelper.getCurrentUser();
        SickLeave existing = sickLeaveService.findById(id);

        if (currentUser.getRole() == Role.DOCTOR
                && !existing.getExamination().getDoctor().getId().equals(currentUser.getDoctor().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You can only edit sick leaves from your own examinations.");
            return "redirect:/sickleaves";
        }

        if (result.hasErrors()) {
            model.addAttribute("examinations", currentUser.getRole() == Role.DOCTOR
                    ? examinationService.findByDoctor(currentUser.getDoctor())
                    : examinationService.findAll());
            return "sickleaves/form";
        }

        // Doctors cannot reassign a sick leave to a different examination
        if (currentUser.getRole() == Role.DOCTOR) {
            sickLeave.setExamination(existing.getExamination());
        }

        sickLeaveService.update(id, sickLeave);
        redirectAttributes.addFlashAttribute("successMessage", "Sick leave updated.");
        return "redirect:/sickleaves";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = currentUserHelper.getCurrentUser();
        SickLeave sickLeave = sickLeaveService.findById(id);

        if (currentUser.getRole() == Role.DOCTOR
                && !sickLeave.getExamination().getDoctor().getId().equals(currentUser.getDoctor().getId())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "You can only delete sick leaves from your own examinations.");
            return "redirect:/sickleaves";
        }

        sickLeaveService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Sick leave deleted.");
        return "redirect:/sickleaves";
    }
}
