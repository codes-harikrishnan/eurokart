package com.harikrishnan.eurokart.category.controller;

import com.harikrishnan.eurokart.category.dto.CategoryRequestDto;
import com.harikrishnan.eurokart.category.dto.CategoryResponseDto;
import com.harikrishnan.eurokart.category.service.CategoryService;
import com.harikrishnan.eurokart.configuration.AppConfiguration;
import com.harikrishnan.eurokart.configuration.LogFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {


    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser
    void addCategory_WithValidRequest_ShouldReturnValidResponse () throws Exception {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .name("TestName")
                .description("TestDescription")
                .build();

        CategoryResponseDto categoryResponseDto = CategoryResponseDto.builder()
                .id(1L)
                .name("TestName")
                .description("TestDescription")
                .build();

        when(categoryService.addCategory(categoryRequestDto)).thenReturn(categoryResponseDto);
        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(categoryRequestDto.getName()))
                .andExpect(jsonPath("$.description").value(categoryRequestDto.getDescription()));

    }

}
