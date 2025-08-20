package com.rikuto.revox.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 使用クラウドに依存せず画像の操作を行うためのインターフェースです。
 * 今後実装予定となります。
 */
public interface ImageUploadService {

	/**
	 * 画像ファイルのアップロードを行います。
	 *
	 * @param file   画像ファイル
	 * @param folder 保存場所
	 * @return アップロードが成功した画像の公開URL
	 */
	String uploadImage(MultipartFile file, String folder);

	/**
	 * 画像ファイルの削除を行います。
	 *
	 * @param imageUrl 削除したい画像のURL
	 */
	void deleteImage(String imageUrl);
}