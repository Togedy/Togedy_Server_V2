package com.togedy.togedy_server_v2.domain.badge.api;

import com.togedy.togedy_server_v2.domain.badge.application.HolidayService;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayDto;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayRequest;
import com.togedy.togedy_server_v2.global.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/holidays")
public class AdminHolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public String holidayList(Model model) {
        model.addAttribute("holidays", holidayService.findHolidays());
        return "admin/holiday/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new HolidayRequest());
        return "admin/holiday/form";
    }

    @PostMapping
    public String createHoliday(@Valid @ModelAttribute("form") HolidayRequest request,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/holiday/form";
        }

        try {
            holidayService.generateHoliday(request);
        } catch (CustomException e) {
            bindingResult.reject(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
            return "admin/holiday/form";
        }
        return "redirect:/admin/holidays";
    }

    @GetMapping("/{holidayId}/edit")
    public String editForm(@PathVariable Long holidayId, Model model) {
        HolidayDto holiday = holidayService.findHoliday(holidayId);

        HolidayRequest form = new HolidayRequest();
        form.setName(holiday.getName());
        form.setStartDate(holiday.getStartDate());
        form.setEndDate(holiday.getEndDate());

        model.addAttribute("holidayId", holidayId);
        model.addAttribute("form", form);
        return "admin/holiday/form";
    }

    @PostMapping("/{holidayId}/edit")
    public String modifyHoliday(@PathVariable Long holidayId,
                                @Valid @ModelAttribute("form") HolidayRequest request,
                                BindingResult bindingResult,
                                Model model) {
        model.addAttribute("holidayId", holidayId);
        if (bindingResult.hasErrors()) {
            return "admin/holiday/form";
        }

        try {
            holidayService.modifyHoliday(request, holidayId);
        } catch (CustomException e) {
            bindingResult.reject(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
            return "admin/holiday/form";
        }
        return "redirect:/admin/holidays";
    }

    @PostMapping("/{holidayId}/delete")
    public String removeHoliday(@PathVariable Long holidayId, RedirectAttributes redirectAttributes) {
        try {
            holidayService.removeHoliday(holidayId);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorCode().getMessage());
        }
        return "redirect:/admin/holidays";
    }
}
