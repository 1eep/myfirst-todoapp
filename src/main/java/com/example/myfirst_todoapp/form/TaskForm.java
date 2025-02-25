package com.example.myfirst_todoapp.form;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskForm {

    private int taskId;

    @NotBlank
    private String taskTitle;

    private int userId;

    @NotBlank
    private String taskDescription;

    @NotBlank
    private Date taskDeadline;

    @NotBlank
    private String taskStatus;

    private LocalDateTime createdAt;

    private int deleteFlg;
}
