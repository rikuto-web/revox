package com.rikuto.revox.service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.Part;
import com.rikuto.revox.dto.ai.AiCreatePrompt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Google Gemini AI（Gen AI SDK）との外部API連携サービス
 */
@Slf4j
@Service
public class GeminiService {

	private final Client client;

	/**
	 * Gemini　AIへの認証およびクライアントの初期化を行います。
	 */
	public GeminiService() {
		try {
			log.info("Gemini　APIへアクセスします。環境変数を確認します。");

			String projectId = Optional.ofNullable(System.getenv("GOOGLE_CLOUD_PROJECT"))
					.orElseThrow(() -> new IllegalArgumentException("GOOGLE_CLOUD_PROJECT is not set."));
			log.info("GOOGLE_CLOUD_PROJECTが正常に読み込まれました。");

			String location = Optional.ofNullable(System.getenv("GOOGLE_CLOUD_LOCATION"))
					.orElseThrow(() -> new IllegalArgumentException("GOOGLE_CLOUD_LOCATION is not set."));
			log.info("GOOGLE_CLOUD_LOCATIONが正常に読み込まれました。");

			this.client = Client.builder()
					.project(projectId)
					.location(location)
					.vertexAI(true)
					.httpOptions(HttpOptions.builder().apiVersion("v1").timeout(90_000).build())
					.build();
		} catch(Exception e) {
			throw new RuntimeException("クライアントの初期化に失敗しました。", e);
		}
	}

	/**
	 * AIからの回答を生成するためのビジネスロジックです。
	 * プロンプトの生成およびAIの詳細設定を行った後、回答を生成します。
	 *
	 * @param userQuestion ユーザーからの質問と車両情報
	 * @return AIが生成した回答
	 */
	public String generateContent(AiCreatePrompt userQuestion) {
		try {
			log.info("質問内容の生成を開始します。");
			String prompt = String.format(
					"""
							あなたは %s %s (%s年式) の整備士です。
							以下の質問に回答してください。質問が作業手順に関するものであれば、以下の形式で回答してください。
							それ以外の質問（例：部品の型番のみ）であれば、質問に直接的に、簡潔に回答してください。
							
							## 作業手順に関する回答形式
							【必要な工具・部品】
							- 工具の名称・サイズ（例: ソケットレンチ 14mm）
							- 部品の品番・規格（例: エンジンオイル 10W-40 SN以上）
							
							【作業手順（簡潔に）】
							1. 手順1
							2. 手順2
							
							【注意事項(安全関連のみ)】
							- 注意1
							- 注意2
							
							【トルク値】
							- 〇〇: 〇〇 Nm
							
							## 質問
							%s
							
							※不明点は「車両の仕様書を確認してください」と明記し、推測は避けること。
							""",
					userQuestion.getManufacturer(),
					userQuestion.getModelName(),
					userQuestion.getModelYear(),
					userQuestion.getQuestion()
			);

			Content content = Content.builder()
					.role("user")
					.parts(List.of(Part.builder()
							.text(prompt)
							.build()))
					.build();
			log.info("質問内容の生成が完了しました。");

			log.info("詳細の設定を開始します。");
			GenerateContentConfig contentParameter = GenerateContentConfig.builder()
					.temperature(0.4F)
					.maxOutputTokens(3000)
					.topP(0.8F)
					.topK(20F)
					.build();
			log.info("詳細の設定が完了しました。");

			GenerateContentResponse createAnswer = client.models.generateContent(
					"gemini-2.5-flash",
					List.of(content),
					contentParameter);

			String answer = createAnswer.text();
			if (answer != null && ! answer.isEmpty()) {
				log.info("回答を正常に取得しました。");
				return answer;
			} else {
				log.warn("Geminiからnullまたは空の回答が返されました。");
				return "回答を取得できませんでした。";
			}

		} catch(Exception e) {
			log.error("呼び出しに失敗しました", e);
			return "技術的な問題により回答できませんでした。";
		}
	}
}
