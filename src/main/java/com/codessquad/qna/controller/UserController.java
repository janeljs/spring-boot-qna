package com.codessquad.qna.controller;

import com.codessquad.qna.domain.User;
import com.codessquad.qna.exception.IllegalUserAccessException;
import com.codessquad.qna.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;


import static com.codessquad.qna.controller.HttpSessionUtils.*;

@RequestMapping("/users")
@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public String login(String userId, String password, HttpSession session) {
        User user = userService.findByUserId(userId);
        if (!user.hasMatchingPassword(password)) {
            throw new IllegalUserAccessException("비밀번호가 틀렸습니다.");
        }
        session.setAttribute(USER_SESSION_KEY, user);
        logger.info("user : {}님이 로그인하셨습니다.", user.getUserId());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(USER_SESSION_KEY);
        return "redirect:/";
    }

    @PostMapping
    public String create(User user) {
        userService.join(user);
        return "redirect:/users";
    }

    @GetMapping
    public String list(Model model) {
        List<User> users = userService.findUsers();
        model.addAttribute("users", users);
        return "user/list";
    }

    @GetMapping("/{id}")
    public String profile(@PathVariable Long id, Model model) {
        User user = userService.findUser(id);
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/{id}/form")
    public String updateForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = userService.findVerifiedUser(id, session);
        model.addAttribute("user", user);
        return "user/updateForm";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @Valid User updatedUser, HttpSession session, @RequestParam String password) {
        User user = userService.findVerifiedUser(id, session);
        if (!user.hasMatchingPassword(password)) {
            return "redirect:/users/" + id + "/form";
        }
        user.update(updatedUser);
        userService.update(user);
        return "redirect:/users";
    }

}

