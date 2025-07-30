
package com.rikuto.revox.dto.bike;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeCreateRequest {

	@NotNull(message = "ユーザーIDは必須です。")
	@Min(value = 1, message = "ユーザーIDは1以上である必要があります。")
	private Integer userId;

	@NotBlank(message = "メーカー名は必須です。")
	@Size(max = 50, message = "メーカー名は50文字以内で入力してください。")
	private String manufacturer;

	@NotBlank(message = "車両名は必須です。")
	@Size(max = 100, message = "車両名は100文字以内で入力してください。")
	private String modelName;

	//型式はnull許容、入力する場合は文字数の制限があります。
	@Size(max = 20, message = "型式は20文字以内で入力してください。")
	private String modelCode;

	// 年式はnull許容
	private Integer modelYear;

	// 走行距離はnull許容
	@Min(value = 0, message = "走行距離は0以上である必要があります。")
	private Integer currentMileage;

	// 購入日はnull許容
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate purchaseDate;

	@Size(max = 2048, message = "画像URLは2048文字以内で入力してください。")
	private String imageUrl;
}