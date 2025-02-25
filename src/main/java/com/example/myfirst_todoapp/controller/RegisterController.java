package com.example.myfirst_todoapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.myfirst_todoapp.entity.UserEntity;
import com.example.myfirst_todoapp.form.RegisterForm;
import com.example.myfirst_todoapp.service.RegisterService;

import jakarta.servlet.http.HttpSession;

@Controller
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @GetMapping("/register")
    public String viewRegister(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(Model model, @Validated RegisterForm registerForm, HttpSession session,
            BindingResult result) {

        if (result.hasErrors()) {
            model.addAttribute("registerForm", registerForm);
            return "register";
        }
        // ユーザー名の重複チェック
        if (registerService.checkUserName(registerForm.getUserName())) {
            model.addAttribute("registerForm", registerForm);
            model.addAttribute("errorMessage", "⚠️このユーザー名は既に使われています。");
            return "register";
        } else {
            registerService.registUserInfo(registerForm);
            UserEntity userEntity = registerService.findByUserName(registerForm.getUserName());
            if (userEntity != null && userEntity.getUserPassword().equals(registerForm.getUserPassword())) {
                session.setAttribute("userEntity", userEntity);
                return "redirect:/taskList/" + userEntity.getUserId();
            }
            model.addAttribute("errorMessage", "ログインに失敗しました。");
            return "login";
        }
    }

}
