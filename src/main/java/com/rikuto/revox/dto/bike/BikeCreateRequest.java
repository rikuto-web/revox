package com.rikuto.revox.dto.bike;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * ユーザーから受け取るバイク情報リクエストです。
 * 各フィールドにはバリデーションがあります。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "新しいバイクを登録するためのリクエストDTOです。")
public class BikeCreateRequest {

	@NotBlank(message = "メーカー名は必須です。")
	@Size(max = 50, message = "メーカー名は50文字以内で入力してください。")
	@Schema(description = "バイクのメーカー名。必須項目。", example = "ホンダ")
	private String manufacturer;

	@NotBlank(message = "車両名は必須です。")
	@Size(max = 100, message = "車両名は100文字以内で入力してください。")
	@Schema(description = "バイクの車両名。必須項目。", example = "Rebel 250")
	private String modelName;

	@Size(max = 20, message = "型式は20文字以内で入力してください。")
	@Schema(description = "バイクの型式。")
	private String modelCode;

	@Schema(description = "バイクの年式。", example = "2023")
	private Integer modelYear;

	@Min(value = 0, message = "走行距離は0以上である必要があります。")
	@Schema(description = "現在の走行距離（km）。", example = "5000")
	private Integer currentMileage;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Schema(description = "購入日。", example = "2023-04-01")
	private LocalDate purchaseDate;

	@Size(max = 2048)
	@Schema(description = "バイクの画像URL。")
	private String imageUrl;
}