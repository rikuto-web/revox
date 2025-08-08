package com.rikuto.revox.controller;

import com.rikuto.revox.dto.aiquestion.AiQuestionCreateRequest;
import com.rikuto.revox.dto.aiquestion.AiQuestionResponse;
import com.rikuto.revox.service.AiQuestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI_APIに関するcontrollerです。
 */
@RestController
@RequestMapping("/api/ai-questions/user/{userId}")
public class AiQuestionController {

	private final AiQuestionService aiQuestionService;

	public AiQuestionController(AiQuestionService aiQuestionService) {
		this.aiQuestionService = aiQuestionService;
	}

	/**
	 * 指定されたユーザーIDに紐づくAI質問履歴を取得します。
	 * get /api/ai-questions/user/{userId}
	 *
	 * @param userId ユーザーID
	 * @return AI質問履歴リストとHTTPステータス200 OK
	 */
	@GetMapping
	public ResponseEntity<List<AiQuestionResponse>> getAiQuestionsByUserId(@PathVariable @Positive Integer userId){
		List<AiQuestionResponse> responses = aiQuestionService.getAiQuestionByUserId(userId);

		return ResponseEntity.ok(responses);
	}

	/**
	 * ユーザーからの質問を取得し、それに対するAIの回答を返します。
	 * post /api/ai-questions/user/{userId}/bike/{bikeId}/category/{categoryId}
	 *
	 * @param request 質問リクエスト
	 * @return 作成されたAI回答情報とHTTPステータス201 Created
	 */
	@PostMapping("/bike/{bikeId}/category/{categoryId}")
	public ResponseEntity<AiQuestionResponse> createAiQuestion(@RequestBody @Valid AiQuestionCreateRequest request,
	                                                           @PathVariable @Positive Integer userId,
	                                                           @PathVariable @Positive Integer bikeId,
	                                                           @PathVariable @Positive Integer categoryId) {
		AiQuestionResponse response
				= aiQuestionService.createAiQuestion(request, userId, bikeId, categoryId);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
