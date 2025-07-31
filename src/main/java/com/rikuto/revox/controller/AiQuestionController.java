package com.rikuto.revox.controller;

import com.rikuto.revox.dto.aiquestion.AiQuestionCreateRequest;
import com.rikuto.revox.dto.aiquestion.AiQuestionResponse;
import com.rikuto.revox.service.AiQuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai-questions")
public class AiQuestionController {

	private final AiQuestionService aiQuestionService;

	public AiQuestionController(AiQuestionService aiQuestionService) {
		this.aiQuestionService = aiQuestionService;
	}

	/**
	 * AI質問を作成し、回答を生成します。
	 *
	 * @param request AI質問作成リクエスト
	 * @return 作成されたAI質問・回答情報とHTTPステータス201 Created
	 */
	@PostMapping
	public ResponseEntity<AiQuestionResponse> createAiQuestion(@RequestBody @Valid AiQuestionCreateRequest request) {
		AiQuestionResponse response = aiQuestionService.createAiQuestion(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * 指定されたユーザーのAI質問履歴を取得します。
	 *
	 * @param userId ユーザーID
	 * @return AI質問履歴リストとHTTPステータス200 OK
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<AiQuestionResponse>> getAiQuestionsByUserId(@PathVariable Integer userId){
		List<AiQuestionResponse> responses = aiQuestionService.getAiQuestionByUserId(userId);
		return ResponseEntity.ok(responses);
	}
}
