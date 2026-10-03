package com.togedy.togedy_server_v2.domain.config.api;

import com.togedy.togedy_server_v2.domain.config.application.CalendarAnnouncementService;
import com.togedy.togedy_server_v2.domain.config.dto.CalendarAnnouncementDto;
import com.togedy.togedy_server_v2.domain.config.dto.CalendarAnnouncementRequest;
import com.togedy.togedy_server_v2.global.security.AdminUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/calendar-announcements")
public class AdminCalendarAnnouncementController {

    private final CalendarAnnouncementService calendarAnnouncementService;

    @GetMapping
    public String calendarAnnouncementList(Model model) {
        model.addAttribute("announcements", calendarAnnouncementService.findCalendarAnnouncements());
        return "admin/calendar-announcement/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new CalendarAnnouncementRequest());
        return "admin/calendar-announcement/form";
    }

    @PostMapping
    public String createCalendarAnnouncement(@Valid @ModelAttribute("form") CalendarAnnouncementRequest request,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal AdminUser adminUser) {
        if (bindingResult.hasErrors()) {
            return "admin/calendar-announcement/form";
        }

        calendarAnnouncementService.generateCalendarAnnouncement(request, adminUser.getId());
        return "redirect:/admin/calendar-announcements";
    }

    @GetMapping("/{calendarAnnouncementId}/edit")
    public String editForm(@PathVariable Long calendarAnnouncementId, Model model) {
        CalendarAnnouncementDto announcement = calendarAnnouncementService.findCalendarAnnouncement(
                calendarAnnouncementId);

        CalendarAnnouncementRequest form = new CalendarAnnouncementRequest();
        form.setContent(announcement.getContent());

        model.addAttribute("calendarAnnouncementId", calendarAnnouncementId);
        model.addAttribute("form", form);
        return "admin/calendar-announcement/form";
    }

    @PostMapping("/{calendarAnnouncementId}/edit")
    public String modifyCalendarAnnouncement(@PathVariable Long calendarAnnouncementId,
                                             @Valid @ModelAttribute("form") CalendarAnnouncementRequest request,
                                             BindingResult bindingResult,
                                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("calendarAnnouncementId", calendarAnnouncementId);
            return "admin/calendar-announcement/form";
        }

        calendarAnnouncementService.modifyCalendarAnnouncement(request, calendarAnnouncementId);
        return "redirect:/admin/calendar-announcements";
    }

    @PostMapping("/{calendarAnnouncementId}/delete")
    public String removeCalendarAnnouncement(@PathVariable Long calendarAnnouncementId) {
        calendarAnnouncementService.removeCalendarAnnouncement(calendarAnnouncementId);
        return "redirect:/admin/calendar-announcements";
    }
}
