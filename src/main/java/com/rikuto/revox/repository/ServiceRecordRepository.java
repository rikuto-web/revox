package com.rikuto.revox.repository;

import com.rikuto.revox.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザーのバイク整備に関するサービス記録、およびAIアドバイスの情報に関するリポジトリです。
 * JpaRepositoryを継承しています。
 * MVPではサービス記録のIDでのCRUD操作で十分なため他メソッドはありません。
 */
@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Integer> {
}
