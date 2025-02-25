package com.example.myfirst_todoapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.myfirst_todoapp.entity.UserEntity;
import com.example.myfirst_todoapp.service.LoginService;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        testUserEntity = new UserEntity();
        testUserEntity.setUserId(1);
        testUserEntity.setUserName("testUser");
        testUserEntity.setUserPassword("testPassword");
    }

    // ログイン画面表示成功
    @Test
    void viewLoginSuccess() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginForm"));
    }

    // ログイン処理成功
    @Test
    void loginSuccessful() throws Exception {
        when(loginService.findByUserName("testUser")).thenReturn(testUserEntity);

        mockMvc.perform(post("/login")
                .param("userName", "testUser")
                .param("userPassword", "testPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/taskList/" + testUserEntity.getUserId()));

        verify(loginService, times(1)).findByUserName("testUser");
    }

    // ログイン処理失敗 ユーザー名空文字
    @Test
    void loginFailure001() throws Exception {
        mockMvc.perform(post("/login")
                .param("userName", "")
                .param("userPassword", "testPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        verify(loginService, never()).findByUserName(any());
    }

    // ログイン失敗（パスワードが空文字）
    @Test
    void loginFailure002() throws Exception {
        when(loginService.findByUserName("testUser")).thenReturn(testUserEntity);

        mockMvc.perform(post("/login")
                .param("userName", "testUser")
                .param("userPassword", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(loginService, never()).findByUserName(any());
    }

    // ログイン失敗（パスワード違い）
    @Test
    void loginFailure003() throws Exception {
        when(loginService.findByUserName("testUser")).thenReturn(testUserEntity);

        mockMvc.perform(post("/login")
                .param("userName", "testUser")
                .param("userPassword", "wrongPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginForm"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "⚠︎ ユーザー名またはパスワードに誤りがあります。"));

        verify(loginService, times(1)).findByUserName("testUser");
    }

    // ログイン失敗（ユーザが存在しない）
    @Test
    void loginFailure004() throws Exception {
        when(loginService.findByUserName("unknownUser")).thenReturn(null);

        mockMvc.perform(post("/login")
                .param("userName", "unknownUser")
                .param("userPassword", "password"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("loginForm"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "⚠︎ ユーザー名またはパスワードに誤りがあります。"));

        verify(loginService, times(1)).findByUserName("unknownUser");
    }

}
