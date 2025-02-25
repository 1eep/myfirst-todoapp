package com.example.myfirst_todoapp.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.myfirst_todoapp.entity.TaskEntity;
import com.example.myfirst_todoapp.entity.UserEntity;
import com.example.myfirst_todoapp.form.TaskForm;
import com.example.myfirst_todoapp.service.TaskListService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TaskListController {

    @Autowired
    private TaskListService taskListService;

    /**
     * 登録されているタスクを全件表示
     * 
     * @param model
     * @param userId
     * @param session
     * @return
     */
    @GetMapping("/taskList/{userId}")
    public String viewTaskList(Model model, @PathVariable("userId") int userId, HttpSession session) {

        // セッションからユーザー情報を取得
        UserEntity userEntity = (UserEntity) session.getAttribute("userEntity");
        if (userEntity == null) {
            return "redirect:/login";
        } else if (userEntity.getUserId() != userId) {
            return "redirect:/login";
        } else {
            model.addAttribute("userEntity", userEntity);
            List<TaskEntity> taskList = taskListService.searchAllTask(userEntity.getUserId());
            if (taskList == null || taskList.size() == 0) {
                taskList = Collections.emptyList();
                model.addAttribute("Msg", "⚠️ 登録されているタスクはありません。");
            }
            model.addAttribute("taskList", taskList);
            return "taskList";
        }
    }

    /**
     * タスクの編集ページに遷移
     * 
     * @param model
     * @param id
     * @param session
     * @return
     */
    @PostMapping("/task/edit/{id}")
    public String viewEditTask(Model model, @PathVariable("id") int id, HttpSession session) {
        model.addAttribute("task", taskListService.getTaskById(id));
        return "taskEdit";
    }

    /**
     * タスクの編集内容を適用
     * 
     * @param model
     * @param taskForm
     * @param session
     * @return
     */
    @PostMapping("/tasks/update")
    public String taskEdit(Model model, @Validated TaskForm taskForm, BindingResult result, HttpSession session) {

        if (result.hasErrors()) {
            return "login";
        }
        taskListService.updateTask(taskForm);
        UserEntity userEntity = (UserEntity) session.getAttribute("userEntity");
        model.addAttribute("userEntity", userEntity);
        List<TaskEntity> taskList = taskListService.searchAllTask(userEntity.getUserId());
        if (taskList == null || taskList.size() == 0) {
            taskList = Collections.emptyList();
            model.addAttribute("Msg", "⚠️ 登録されているタスクはありません。");
        }
        model.addAttribute("taskList", taskList);
        return "redirect:/taskList/" + userEntity.getUserId();
    }

    /**
     * TOPページ(taskList.html)に戻る
     * 
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/cancel")
    public String cancelEdit(Model model, HttpSession session) {
        UserEntity userEntity = (UserEntity) session.getAttribute("userEntity");
        model.addAttribute("userEntity", userEntity);
        List<TaskEntity> taskList = taskListService.searchAllTask(userEntity.getUserId());
        model.addAttribute("taskList", taskList);
        return "redirect:/taskList/" + userEntity.getUserId();
    }

    /**
     * タスクの削除ページに遷移
     * 
     * @param model
     * @param id
     * @param session
     * @return
     */
    @PostMapping("/task/delete/{id}")
    public String viewDeleteTask(Model model, @PathVariable("id") int id, HttpSession session) {
        model.addAttribute("task", taskListService.getTaskById(id));
        return "taskDelete";
    }

    /**
     * タスクの削除を実行
     * 
     * @param model
     * @param taskForm
     * @param session
     * @return
     */
    @PostMapping("/tasks/delete")
    public String taskDelete(Model model, TaskForm taskForm, HttpSession session) {
        taskListService.deleteTask(taskForm);
        UserEntity userEntity = (UserEntity) session.getAttribute("userEntity");
        model.addAttribute("userEntity", userEntity);
        return "redirect:/taskList/" + userEntity.getUserId();
    }

    /**
     * タスクの新規登録ページに遷移
     * 
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/task/new")
    public String viewAddTask(Model model, HttpSession session) {
        model.addAttribute("taskForm", new TaskForm());
        return "taskAdd";
    }

    /**
     * タスクの新規登録を実行
     * 
     * @param model
     * @param session
     * @param taskForm
     * @return
     */
    @PostMapping("/task/add")
    public String postMethodName(Model model, HttpSession session, @Validated TaskForm taskForm, BindingResult result) {
        if (result.hasErrors()) {
            return "login";
        }
        UserEntity userEntity = (UserEntity) session.getAttribute("userEntity");
        taskForm.setUserId(userEntity.getUserId());
        taskListService.addNewTask(taskForm);
        return "redirect:/taskList/" + userEntity.getUserId();
    }

    /**
     * ログアウト
     * 
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}