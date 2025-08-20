package com.rikuto.revox.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AIへの質問および回答に対するレスポンス内容のDTOです。
 */
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
}
