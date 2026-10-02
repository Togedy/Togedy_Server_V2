package com.togedy.togedy_server_v2.domain.support.api;

import com.togedy.togedy_server_v2.domain.support.application.InquiryService;
import com.togedy.togedy_server_v2.domain.support.enums.InquiryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/inquiries")
public class AdminInquiryController {

    private static final int PAGE_SIZE = 20;

    private final InquiryService inquiryService;

    @GetMapping
    public String inquiryList(@RequestParam(defaultValue = "1") int page, Model model) {
        model.addAttribute("page", page);
        model.addAttribute("result", inquiryService.findInquiries(page, PAGE_SIZE));
        model.addAttribute("statuses", InquiryStatus.values());
        return "admin/inquiry/list";
    }

    @PostMapping("/{inquiryId}/status")
    public String modifyInquiryStatus(@PathVariable Long inquiryId,
                                      @RequestParam InquiryStatus status,
                                      @RequestParam(defaultValue = "1") int page) {
        inquiryService.modifyInquiryStatus(inquiryId, status);
        return "redirect:/admin/inquiries?page=" + page;
    }
}
