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

@Service
public class GeminiService {

	private final Client client;

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
			throw new RuntimeException("GenaiClientの作成に失敗しました。", e);
		}
	}

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