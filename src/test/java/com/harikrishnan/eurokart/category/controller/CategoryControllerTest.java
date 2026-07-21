package com.harikrishnan.eurokart.category.controller;
import com.harikrishnan.eurokart.category.dto.CategoryRequestDto;
import com.harikrishnan.eurokart.category.dto.CategoryResponseDto;
import com.harikrishnan.eurokart.category.service.CategoryService;
import com.harikrishnan.eurokart.configuration.JWTFilter;
import com.harikrishnan.eurokart.exception.ResourceNotFoundException;
import com.harikrishnan.eurokart.util.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = CategoryController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTFilter.class)
})
public class CategoryControllerTest {


    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

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

    @Test
    @WithMockUser
    void addCategory_WithInvalidRequest_ShouldReturnMethodArgumentExceptionResponse () throws Exception {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .description("TestDescription")
                .build();

        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequestDto))
        ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request parameters"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.name").value("must not be blank"));

    }

    @Test
    @WithMockUser
    void getCategory_ShouldReturnValidCategoryResponseList () throws Exception{
        List<CategoryResponseDto> categoryResponseDtoList = List.of(CategoryResponseDto.builder()
                        .name("TestName")
                        .description("TestDescription")
                .build());
        when(categoryService.getCategories()).thenReturn(categoryResponseDtoList);

        mockMvc.perform(get("/category")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("TestName"))
                .andExpect(jsonPath("$[0].description").value("TestDescription"));
    }

    @Test
    @WithMockUser
    void getCategoryById_WithAnInValidId_ShouldReturn404 () throws  Exception{
        when(categoryService.getCategoryById(99L)).thenThrow( new ResourceNotFoundException("Unable to find category with id:" + 99L));

        mockMvc.perform(get("/category/99").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void updateCategoryById_WithAValidRequest_ShouldReturnUpdatedCategoryResponse () throws Exception {
        CategoryRequestDto updatedCategoryRequestDto = CategoryRequestDto.builder()
                .name("UpdatedTestName")
                .description("UpdatedTestDescription")
                .build();

        CategoryResponseDto categoryResponseDto = CategoryResponseDto.builder()
                .id(1L)
                .name("UpdatedTestName")
                .description("UpdatedTestDescription")
                .build();

        when(categoryService.updateCategoryById(1L,updatedCategoryRequestDto)).thenReturn(categoryResponseDto);
        mockMvc.perform(put("/category/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategoryRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(categoryResponseDto.getName()))
                .andExpect(jsonPath("$.description").value(categoryResponseDto.getDescription()));

    }

    @Test
    @WithMockUser
    void deleteCategory_WithAValidId_ShouldDeleteCategory ()  throws Exception {
       doNothing().when(categoryService).deleteCategoryById(1L);
        mockMvc.perform(delete("/category/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
