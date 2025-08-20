package com.rikuto.revox.controller;

import com.rikuto.revox.dto.ai.AiQuestionCreateRequest;
import com.rikuto.revox.dto.ai.AiQuestionResponse;
import com.rikuto.revox.service.AiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI(Gemini API)に関するコントローラーです。
 */
@RestController
@RequestMapping("/api/ai/user/{userId}")
public class AiController {

	private final AiService aiService;

	public AiController(AiService aiService) {
		this.aiService = aiService;
	}

	// CREATE
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * ユーザーからの質問を取得し、200ステータスを返します。
	 * post /api/ai/user/{userId}/bike/{bikeId}/category/{categoryId}
	 *
	 * @param request 質問リクエスト
	 * @return HTTPステータス200 OK
	 */
	@PostMapping("/bike/{bikeId}/category/{categoryId}")
	public ResponseEntity<AiQuestionResponse> createAiQuestion(@RequestBody @Valid AiQuestionCreateRequest request,
	                                                           @PathVariable @Positive Integer userId,
	                                                           @PathVariable @Positive Integer bikeId,
	                                                           @PathVariable @Positive Integer categoryId) {
		AiQuestionResponse response = aiService.createAiQuestion(request, userId, bikeId, categoryId);

		return ResponseEntity.ok(response);
	}

	// READ
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 指定されたユーザーIDに紐づくAI質問履歴を取得します。
	 * get /api/ai/user/{userId}
	 *
	 * @param userId ユーザーID
	 * @return AI質問履歴リストとHTTPステータス200 OK
	 */
	@GetMapping
	public ResponseEntity<List<AiQuestionResponse>> getAiQuestionsByUserId(@PathVariable @Positive Integer userId) {
		List<AiQuestionResponse> responses = aiService.getAiQuestionByUserId(userId);

		return ResponseEntity.ok(responses);
	}
}
