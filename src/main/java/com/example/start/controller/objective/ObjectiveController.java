package com.example.start.controller.objective;

import com.example.start.dto.objective.KeyResultForm;
import com.example.start.dto.objective.ObjectiveForm;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.service.objective.ObjectiveService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/okr")
public class ObjectiveController {

    private final ObjectiveService objectiveService;

    // ✅ OKR 목록 조회
    @GetMapping
    public String listObjectives(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        List<Objective> objectives = objectiveService.findByUser(loginUser);
        model.addAttribute("objectives", objectives);
        return "okr/list";
    }

    // ✅ OKR 생성 폼
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("objectiveForm", new ObjectiveForm());
        return "okr/create";
    }

    // ✅ OKR 저장 처리 (목표 + 핵심 결과)
    @PostMapping("/create")
    public String createObjective(@ModelAttribute ObjectiveForm form, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // Objective 생성
        Objective objective = new Objective();
        objective.setTitle(form.getTitle());
        objective.setDescription(form.getDescription());

        // KeyResults 생성
        for (KeyResultForm krForm : form.getKeyResults()) {
            if (krForm.getContent() != null && !krForm.getContent().isBlank()) {
                KeyResult kr = new KeyResult();
                kr.setContent(krForm.getContent());
                kr.setProgress(0); // 초기값
                kr.setObjective(objective);
                objective.getKeyResults().add(kr);
            }
        }

        objectiveService.saveObjective(objective, loginUser);
        return "redirect:/okr";
    }

    // 수정 페이지 이동
    @GetMapping("/edit/{id}")
    public String editObjectiveForm(@PathVariable Long id, Model model) {
        Objective objective = objectiveService.findById(id);
        model.addAttribute("objective", objective);
        return "okr/edit";
    }

    // 수정 처리
    @PostMapping("/edit/{id}")
    public String updateObjective(@PathVariable Long id,
                                  @ModelAttribute ObjectiveForm form,
                                  HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        objectiveService.updateObjective(id, form, loginUser);
        return "redirect:/okr";
    }

    // 삭제 처리
    @PostMapping("/delete/{id}")
    public String deleteObjective(@PathVariable Long id) {
        objectiveService.deleteById(id);
        return "redirect:/okr";
    }


}
