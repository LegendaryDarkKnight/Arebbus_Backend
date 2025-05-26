package com.project.arebbus.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserPostCreateRequest {
    private String content;
    private List<String> tags;
}
