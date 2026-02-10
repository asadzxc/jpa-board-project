package com.example.start.controller.objective;

import com.example.start.dto.objective.KeyResultForm;
import com.example.start.dto.objective.ObjectiveForm;
import com.example.start.dto.objective.ObjectiveResponse;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.service.objective.DailyCheckService;
import com.example.start.service.objective.ObjectiveService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.start.enums.ObjectiveStatus;


import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/okr")
public class ObjectiveController {

    private final ObjectiveService objectiveService;
    private final DailyCheckService dailyCheckService;

    @GetMapping({"","/"})
    public String listObjectives(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        List<ObjectiveResponse> objectives =
                objectiveService.findObjectiveResponsesByUserAndStatus(loginUser, ObjectiveStatus.ACTIVE);
        model.addAttribute("objectives", objectives);
        return "okr/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("objectiveForm", new ObjectiveForm());
        return "okr/create";
    }

    @PostMapping("/create")
    public String createObjective(@ModelAttribute ObjectiveForm form, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        Objective objective = new Objective();
        objective.setTitle(form.getTitle());
        objective.setDescription(form.getDescription());

        if (form.getKeyResults() != null) {
            for (KeyResultForm krForm : form.getKeyResults()) {
                if (krForm.getContent() != null && !krForm.getContent().isBlank()) {
                    KeyResult kr = new KeyResult();
                    kr.setContent(krForm.getContent());
                    kr.setProgress(0);
                    kr.setWeight(1);              // ✅ weight 명시
                    kr.setObjective(objective);
                    objective.getKeyResults().add(kr);
                }
            }
        }

        objectiveService.saveObjective(objective, loginUser);
        return "redirect:/okr";
    }

    @GetMapping("/edit/{id}")
    public String editObjectiveForm(@PathVariable Long id, Model model) {
        Objective objective = objectiveService.findById(id);
        model.addAttribute("objective", objective);
        return "okr/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateObjective(@PathVariable Long id,
                                  @ModelAttribute ObjectiveForm form,
                                  HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        objectiveService.updateObjective(id, form, loginUser);
        return "redirect:/okr";
    }

    // ❌ 기존: @DeleteMapping("/delete/{id}")
    // ✅ 변경: 프로토타입 + 숨은 메서드 미사용 기준으로 POST로 맞춤 (템플릿의 method="post" 와 일치)
    @PostMapping("/{id}/delete")
    public String deleteObjective(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // (선택) 소유자 검증이 필요하면 서비스 시그니처 바꿔 loginUser 전달
        objectiveService.deleteObjective(id, loginUser);
        return "redirect:/okr";
    }

    @PostMapping("/check")
    public String toggleKeyResultCheck(@RequestParam Long keyResultId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        dailyCheckService.toggleToday(keyResultId, loginUser);

        // 상세 페이지가 있으면 그쪽으로 보내고, 없으면 목록으로
        // return "redirect:/okr/kr/" + keyResultId;
        return "redirect:/okr";
    }

    // ✅ 보관함 목록
    @GetMapping("/archive")
    public String archiveList(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // 방법 A(추천): 서비스에 status 필터 메서드 만들고 사용
        List<ObjectiveResponse> archived = objectiveService
                .findObjectiveResponsesByUserAndStatus(loginUser, ObjectiveStatus.ARCHIVED);

        model.addAttribute("objectives", archived);
        return "okr/archive";
    }

    // ✅ 보관하기
    @PostMapping("/{id}/archive")
    public String archive(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        objectiveService.archiveObjective(id, loginUser);
        return "redirect:/okr";
    }

    // ✅ 복원하기
    @PostMapping("/{id}/restore")
    public String restore(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        objectiveService.restoreObjective(id, loginUser);
        return "redirect:/okr/archive";
    }

}
