package com.rikuto.revox.repository;

import com.rikuto.revox.domain.AiQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AI質問・回答に関するリポジトリです。
 * JpaRepositoryを継承しています。
 */
@Repository
public interface AiQuestionRepository extends JpaRepository<AiQuestion, Integer> {

	/**
	 * ユーザーIDに紐づいたAI質問履歴を取得します。論理削除された情報は取得しません。
	 * @param userId ユーザーID
	 * @return ユーザーのAI質問履歴リスト
	 */
	List<AiQuestion> findByUserIdAndIsDeletedFalse(Integer userId);

	/**
	 * バイクIDに紐づいたAI質問履歴を取得します。論理削除された情報は取得しません。
	 * @param bikeId バイクID
	 * @return バイクに関するAI質問履歴リスト
	 */
	List<AiQuestion> findByBikeIdAndIsDeletedFalse(Integer bikeId);
}