package com.rikuto.revox.repository;

import com.rikuto.revox.domain.Ai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AIへの質問および回答に関するリポジトリです。
 * JpaRepositoryを継承しています。
 */
@Repository
public interface AiRepository extends JpaRepository<Ai, Integer> {

	/**
	 * ユーザーIDに紐づいたAI質問履歴を取得します。
	 *
	 * @param userId ユーザーID
	 * @return ユーザーのAI質問履歴リスト
	 */
	List<Ai> findByUserId(Integer userId);
}