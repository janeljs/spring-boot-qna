package com.codessquad.qna.controller;

import com.codessquad.qna.domain.*;
import com.codessquad.qna.exception.*;
import com.codessquad.qna.service.QuestionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

import static com.codessquad.qna.controller.HttpSessionUtils.*;


@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public String list(Model model, @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        questionService.addPages(model, pageable);
        return "index";
    }

    @GetMapping("/form")
    public String form(HttpSession session) {
        if (!isLoginUser(session)) {
            throw new IllegalUserAccessException("로그인이 필요합니다.");
        }
        return "/qna/form";
    }

    @PostMapping
    public String create(String title, String contents, HttpSession session) {
        if (!isLoginUser(session)) {
            throw new IllegalUserAccessException("로그인이 필요합니다.");
        }
        User sessionUser = getSessionUser(session);
        Question question = new Question(sessionUser, title, contents);
        questionService.update(question);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("question", questionService.findQuestion(id));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        Question question = questionService.findVerifiedQuestion(id, session);
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @Valid Question updatedQuestion, Errors errors, Model model, HttpSession session) {
        Question question = questionService.findVerifiedQuestion(id, session);
        updatedQuestion.setId(id);
        
        if (errors.hasErrors()) {
            model.addAttribute("question", updatedQuestion);
            model.addAttribute("errorMessage", Objects.requireNonNull(errors.getFieldError()).getDefaultMessage());
            return "/qna/updateForm";
        }
        questionService.update(question, updatedQuestion);
        return "redirect:/questions/" + id;
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        Question question = questionService.findVerifiedQuestion(id, session);
        questionService.delete(question);
        return "redirect:/";
    }
}
