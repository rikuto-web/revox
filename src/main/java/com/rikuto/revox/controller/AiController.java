package com.rikuto.revox.controller;

import com.rikuto.revox.dto.ai.AiQuestionCreateRequest;
import com.rikuto.revox.dto.ai.AiQuestionResponse;
import com.rikuto.revox.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "AIに関する管理", description = "AI（Gemini API）とのやり取りを管理するエンドポイント群です。")
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
	 * ユーザーからの質問を取得し、AIからの回答を返します。
	 */
	@Operation(summary = "AIに質問を送信する", description = "ユーザーからの質問をAIに送信し、回答を受け取ります。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "質問が成功し、AIからの回答を返却",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = AiQuestionResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（バリデーションエラーなど）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない")
	})
	@PostMapping("/bike/{bikeId}/category/{categoryId}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<AiQuestionResponse> createAiQuestion(
			@RequestBody @Valid AiQuestionCreateRequest request,
			@Parameter(description = "質問を投稿するユーザーの一意の識別子。", required = true)
			@PathVariable @Positive Integer userId,
			@Parameter(description = "質問が関連する自転車の一意の識別子。", required = true)
			@PathVariable @Positive Integer bikeId,
			@Parameter(description = "質問が関連するカテゴリの一意の識別子。", required = true)
			@PathVariable @Positive Integer categoryId
	) {
		AiQuestionResponse response = aiService.createAiQuestion(request, userId, bikeId, categoryId);

		return ResponseEntity.ok(response);
	}

	// READ
	//------------------------------------------------------------------------------------------------------------------

	/**
	 * 指定されたユーザーIDに紐づくAI質問履歴を取得します。
	 */
	@Operation(summary = "AI質問履歴を取得する", description = "指定されたユーザーのAIとのやり取り履歴をリストで取得します。")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "質問履歴の取得に成功",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = AiQuestionResponse.class))),
			@ApiResponse(responseCode = "400", description = "不正なリクエスト（ユーザーIDが不正など）"),
			@ApiResponse(responseCode = "403", description = "アクセス権限がない"),
			@ApiResponse(responseCode = "404", description = "ユーザーが見つからない")
	})
	@GetMapping
	@PreAuthorize("hasAnyRole('GUEST', 'USER')")
	public ResponseEntity<List<AiQuestionResponse>> getAiQuestionsByUserId(
			@Parameter(description = "AI質問履歴を取得したいユーザーの一意の識別子。", required = true)
			@PathVariable @Positive Integer userId
	) {
		List<AiQuestionResponse> responses = aiService.getAiQuestionByUserId(userId);

		return ResponseEntity.ok(responses);
	}
}