package com.rikuto.revox.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.Part;
import com.rikuto.revox.dto.aiquestion.AiQuestionPrompt;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Google Gemini AIとの連携を担うサービスです。
 */
@Service
public class GeminiService {

	private final Client client;

	/**
	 * GeminiServiceのコンストラクタです。
	 * 環境変数からGoogle CloudのプロジェクトIDとロケーションを取得し、Gemini AIクライアントを初期化します。
	 * 初期化に失敗した場合は、RuntimeExceptionをスローします。
	 */
	public GeminiService() {
		try {
			String projectId = Optional.ofNullable(System.getenv("GOOGLE_CLOUD_PROJECT"))
					.orElseThrow(() -> new IllegalArgumentException("GOOGLE_CLOUD_PROJECT environment variable not set."));
			String location = Optional.ofNullable(System.getenv("GOOGLE_CLOUD_LOCATION"))
					.orElseThrow(() -> new IllegalArgumentException("GOOGLE_CLOUD_LOCATION environment variable not set."));

			this.client = Client.builder()
					.project(projectId)
					.location(location)
					.vertexAI(true)
					.httpOptions(HttpOptions.builder().apiVersion("v1").build())
					.build();
		} catch (Exception e) {
			throw new RuntimeException("Gemini AIクライアントの作成に失敗しました。原因:", e);
		}
	}

	/**
	 * 指定されたプロンプトからGemini AIにコンテンツを生成させます。
	 *
	 * @param prompt AIへのプロンプト生成に必要な情報を持つDTO
	 * @return AIが生成した回答の文字列。生成中にエラーが発生した場合は、エラーメッセージを返します。
	 */
	public String generateContent(AiQuestionPrompt prompt) {
		// AIに役割を与えるシステム命令を設定（キャラ設定など）
		String systemInstructionText = "あなたはバイクの専門家です。提供されたバイクの情報とユーザーの質問に基づき、正確・具体的かつ整備初心者にわかりやすく回答してください。";
		//AIが理解できるオブジェクトに変換
		Content systemInstruction = Content.fromParts(Part.fromText(systemInstructionText));

		StringBuilder promptBuilder = getStringBuilder(prompt);

		// プロンプトをContentオブジェクトにラップ
		Content userContent = Content.fromParts(Part.fromText(promptBuilder.toString()));

		// GenerateContentConfigを作成し、システム命令を設定
		GenerateContentConfig config = GenerateContentConfig.builder()
				.systemInstruction(systemInstruction)
				.build();

		try {
			// client.models.generateContent()を使ってAPIを呼び出す
			GenerateContentResponse response = client.models.generateContent(
					"gemini-2.5-flash",
					userContent,//ラップしたプロンプト
					config
			);
			return response.text();
		} catch (Exception e) {
			return "AIからの回答生成中にエラーが発生しました。";
		}
	}

	/**
	 * AiQuestionPrompt DTOから、AIに送信するプロンプト文字列を構築します。
	 *
	 * @param prompt プロンプト生成に必要な情報を持つAiQuestionPrompt DTO
	 * @return 構築されたプロンプト文字列を保持するStringBuilder
	 */
	@NotNull
	private static StringBuilder getStringBuilder(AiQuestionPrompt prompt) {
		StringBuilder promptBuilder = new StringBuilder();
		promptBuilder.append("バイク情報:\n");

		promptBuilder.append(String.format("- メーカー: %s\n", prompt.getManufacturer()));
		promptBuilder.append(String.format("- モデル名: %s\n", prompt.getModelName()));

		if (prompt.getModelCode() != null) {
			promptBuilder.append(String.format("- モデルコード: %s\n", prompt.getModelCode()));
		}
		if (prompt.getModelYear() != null) {
			promptBuilder.append(String.format("- 年式: %s\n", prompt.getModelYear()));
		}
		if (prompt.getCurrentMileage() != null) {
			promptBuilder.append(String.format("- 現在の走行距離: %s km\n", prompt.getCurrentMileage()));
		}
		if (prompt.getPurchaseDate() != null) {
			promptBuilder.append(String.format("- 購入日: %s\n", prompt.getPurchaseDate()));
		}

		promptBuilder.append(String.format("\n質問内容: %s", prompt.getQuestion()));
		return promptBuilder;
	}
}