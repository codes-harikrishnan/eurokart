package com.harikrishnan.eurokart.user.dto;


import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AuthResponseDto {
    private String token;

}
