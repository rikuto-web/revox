package com.rikuto.revox.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateRequest {

	@NotBlank
	@Size(max = 50)
	private String nickname;
}
