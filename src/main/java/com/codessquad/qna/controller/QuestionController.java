package com.codessquad.qna.controller;

import com.codessquad.qna.domain.*;
import com.codessquad.qna.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpSession;
import static com.codessquad.qna.controller.HttpSessionUtils.*;


@Controller
@RequestMapping("/questions")
public class QuestionController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private QuestionRepository questionRepository;

    @Autowired
    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @GetMapping("/form")
    public String form(HttpSession session) {
        if (!isLoginUser(session)) {
            return "/users/loginForm";
        }
        return "/qna/form";
    }

    @PostMapping("")
    public String create(String title, String contents, HttpSession session) {
        if (!isLoginUser(session)) {
            logger.info("새 글 작성에 실패했습니다.");
            return "/users/loginForm";
        }
        User sessionUser = getSessionUser(session);
        Question question = new Question(sessionUser, title, contents);
        questionRepository.save(question);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        model.addAttribute("question", questionRepository.getOne(id));
        return "/qna/show";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        if (!isLoginUser(session)) {
            return "/users/loginForm";
        }
        User loginUser = getSessionUser(session);
        Question question = questionRepository.getOne(id);
        if (question.isNotSameAuthor(loginUser)) {
            return "/users/loginForm";
        }
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, String title, String contents, HttpSession session) {
        if (!isLoginUser(session)) {
            return "/users/loginForm";
        }
        User loginUser = getSessionUser(session);
        Question question = questionRepository.getOne(id);
        if (question.isNotSameAuthor(loginUser)) {
            return "/users/loginForm";
        }
        question.update(title, contents);
        questionRepository.save(question);
        return String.format("redirect:/questions/%d", id);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (!isLoginUser(session)) {
            return "/users/loginForm";
        }
        User loginUser = getSessionUser(session);
        Question question = questionRepository.getOne(id);
        if (question.isNotSameAuthor(loginUser)) {
            return "/users/loginForm";
        }
        questionRepository.delete(question);
        return "redirect:/";
    }
}