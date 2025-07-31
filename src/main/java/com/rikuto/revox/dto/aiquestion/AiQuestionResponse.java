package com.rikuto.revox.dto.aiquestion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiQuestionResponse {

	private Integer id;
	private Integer userId;
	private Integer bikeId;
	private Integer categoryId;
	private String question;
	private String answer;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
