package com.example.start.controller.objective;

import com.example.start.dto.objective.KeyResultForm;
import com.example.start.dto.objective.ObjectiveForm;
import com.example.start.dto.objective.ObjectiveResponse;
import com.example.start.entity.objective.KeyResult;
import com.example.start.entity.objective.Objective;
import com.example.start.entity.post.User;
import com.example.start.enums.ObjectiveStatus;
import com.example.start.service.objective.DailyCheckService;
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
    private final DailyCheckService dailyCheckService;

    @GetMapping({"", "/"})
    public String listObjectives(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

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

        Objective objective = new Objective();
        objective.setTitle(form.getTitle());
        objective.setDescription(form.getDescription());

        if (form.getKeyResults() != null) {
            for (KeyResultForm krForm : form.getKeyResults()) {
                if (krForm.getContent() != null && !krForm.getContent().isBlank()) {
                    KeyResult kr = new KeyResult();
                    kr.setContent(krForm.getContent());
                    kr.setProgress(0);
                    kr.setWeight(1);
                    kr.setObjective(objective);
                    objective.getKeyResults().add(kr);
                }
            }
        }

        objectiveService.saveObjective(objective, loginUser);
        return "redirect:/okr";
    }

    @GetMapping("/edit/{id}")
    public String editObjectiveForm(@PathVariable Long id,
                                    Model model,
                                    HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        Objective objective = objectiveService.findById(id);

        // 컨트롤러에서 1차 방어
        if (!objective.getUser().getId().equals(loginUser.getId())) {
            return "redirect:/okr";
        }

        model.addAttribute("objective", objective);
        return "okr/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateObjective(@PathVariable Long id,
                                  @ModelAttribute ObjectiveForm form,
                                  HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        objectiveService.updateObjective(id, form, loginUser);
        return "redirect:/okr";
    }

    @PostMapping("/{id}/delete")
    public String deleteObjective(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        objectiveService.deleteObjective(id, loginUser);
        return "redirect:/okr";
    }

    @PostMapping("/check")
    public String toggleKeyResultCheck(@RequestParam Long keyResultId, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        dailyCheckService.toggleToday(keyResultId, loginUser);
        return "redirect:/okr";
    }

    @GetMapping("/archive")
    public String archiveList(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        List<ObjectiveResponse> archived =
                objectiveService.findObjectiveResponsesByUserAndStatus(loginUser, ObjectiveStatus.ARCHIVED);

        model.addAttribute("objectives", archived);
        return "okr/archive";
    }

    @PostMapping("/{id}/archive")
    public String archive(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        objectiveService.archiveObjective(id, loginUser);
        return "redirect:/okr";
    }

    @PostMapping("/{id}/restore")
    public String restore(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        objectiveService.restoreObjective(id, loginUser);
        return "redirect:/okr/archive";
    }
}