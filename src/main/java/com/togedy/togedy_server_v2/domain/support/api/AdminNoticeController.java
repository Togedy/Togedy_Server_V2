package com.togedy.togedy_server_v2.domain.support.api;

import com.togedy.togedy_server_v2.domain.support.application.NoticeService;
import com.togedy.togedy_server_v2.domain.support.dto.GetNoticeResponse;
import com.togedy.togedy_server_v2.domain.support.dto.PatchNoticeRequest;
import com.togedy.togedy_server_v2.domain.support.dto.PostNoticeRequest;
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
@RequestMapping("/admin/notices")
public class AdminNoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public String noticeList(Model model) {
        model.addAttribute("notices", noticeService.findNotices());
        return "admin/notice/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new PostNoticeRequest());
        return "admin/notice/form";
    }

    @PostMapping
    public String createNotice(@Valid @ModelAttribute("form") PostNoticeRequest request,
                               BindingResult bindingResult,
                               @AuthenticationPrincipal AdminUser adminUser) {
        if (bindingResult.hasErrors()) {
            return "admin/notice/form";
        }

        noticeService.generateNotice(request, adminUser.getId());
        return "redirect:/admin/notices";
    }

    @GetMapping("/{noticeId}/edit")
    public String editForm(@PathVariable Long noticeId, Model model) {
        GetNoticeResponse notice = noticeService.findNotice(noticeId);

        PatchNoticeRequest form = new PatchNoticeRequest();
        form.setNoticeTitle(notice.getNoticeTitle());
        form.setNoticeContent(notice.getNoticeContent());

        model.addAttribute("noticeId", noticeId);
        model.addAttribute("form", form);
        return "admin/notice/form";
    }

    @PostMapping("/{noticeId}/edit")
    public String modifyNotice(@PathVariable Long noticeId,
                               @Valid @ModelAttribute("form") PatchNoticeRequest request,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("noticeId", noticeId);
            return "admin/notice/form";
        }

        noticeService.modifyNotice(request, noticeId);
        return "redirect:/admin/notices";
    }

    @PostMapping("/{noticeId}/delete")
    public String removeNotice(@PathVariable Long noticeId) {
        noticeService.removeNotice(noticeId);
        return "redirect:/admin/notices";
    }
}
