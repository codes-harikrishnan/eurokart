package com.harikrishnan.eurokart.user.dto;

import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private String email;

    private String message;

}
