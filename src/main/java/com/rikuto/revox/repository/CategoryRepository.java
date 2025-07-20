package com.rikuto.revox.repository;

import com.rikuto.revox.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * カテゴリーに関するリポジトリです。
 * JpaRepositoryを継承しています。
 * MVPではカテゴリーIDでのCRUD操作で十分なため他メソッドはありません。
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
