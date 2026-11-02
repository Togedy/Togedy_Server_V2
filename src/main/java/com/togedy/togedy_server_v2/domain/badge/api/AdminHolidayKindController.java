package com.togedy.togedy_server_v2.domain.badge.api;

import com.togedy.togedy_server_v2.domain.badge.application.HolidayKindService;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayKindDto;
import com.togedy.togedy_server_v2.domain.badge.dto.HolidayKindRequest;
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
@RequestMapping("/admin/holiday-kinds")
public class AdminHolidayKindController {

    private final HolidayKindService holidayKindService;

    @GetMapping
    public String holidayKindList(Model model) {
        model.addAttribute("holidayKinds", holidayKindService.findHolidayKinds());
        return "admin/holiday-kind/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new HolidayKindRequest());
        return "admin/holiday-kind/form";
    }

    @PostMapping
    public String createHolidayKind(@Valid @ModelAttribute("form") HolidayKindRequest request,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/holiday-kind/form";
        }

        try {
            holidayKindService.generateHolidayKind(request);
        } catch (CustomException e) {
            bindingResult.reject(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
            return "admin/holiday-kind/form";
        }
        return "redirect:/admin/holiday-kinds";
    }

    @GetMapping("/{holidayKindId}/edit")
    public String editForm(@PathVariable Long holidayKindId, Model model) {
        HolidayKindDto holidayKind = holidayKindService.findHolidayKind(holidayKindId);

        HolidayKindRequest form = new HolidayKindRequest();
        form.setName(holidayKind.getName());
        form.setBadgeEnabled(holidayKind.isBadgeEnabled());

        model.addAttribute("holidayKindId", holidayKindId);
        model.addAttribute("badgeImageUrl", holidayKind.getBadgeImageUrl());
        model.addAttribute("form", form);
        return "admin/holiday-kind/form";
    }

    @PostMapping("/{holidayKindId}/edit")
    public String modifyHolidayKind(@PathVariable Long holidayKindId,
                                    @Valid @ModelAttribute("form") HolidayKindRequest request,
                                    BindingResult bindingResult,
                                    Model model) {
        model.addAttribute("holidayKindId", holidayKindId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("badgeImageUrl", holidayKindService.findHolidayKind(holidayKindId).getBadgeImageUrl());
            return "admin/holiday-kind/form";
        }

        try {
            holidayKindService.modifyHolidayKind(request, holidayKindId);
        } catch (CustomException e) {
            bindingResult.reject(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
            model.addAttribute("badgeImageUrl", holidayKindService.findHolidayKind(holidayKindId).getBadgeImageUrl());
            return "admin/holiday-kind/form";
        }
        return "redirect:/admin/holiday-kinds";
    }

    @PostMapping("/{holidayKindId}/delete")
    public String removeHolidayKind(@PathVariable Long holidayKindId, RedirectAttributes redirectAttributes) {
        try {
            holidayKindService.removeHolidayKind(holidayKindId);
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getErrorCode().getMessage());
        }
        return "redirect:/admin/holiday-kinds";
    }
}
