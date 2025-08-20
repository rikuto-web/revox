package com.rikuto.revox.dto.bike;

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
public class BikeCreateRequest {

	@NotBlank(message = "メーカー名は必須です。")
	@Size(max = 50, message = "メーカー名は50文字以内で入力してください。")
	private String manufacturer;

	@NotBlank(message = "車両名は必須です。")
	@Size(max = 100, message = "車両名は100文字以内で入力してください。")
	private String modelName;

	@Size(max = 20, message = "型式は20文字以内で入力してください。")
	private String modelCode;

	private Integer modelYear;

	@Min(value = 0, message = "走行距離は0以上である必要があります。")
	private Integer currentMileage;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate purchaseDate;

	@Size(max = 2048)
	private String imageUrl;
}