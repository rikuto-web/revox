package com.rikuto.revox.repository;

import com.rikuto.revox.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * カテゴリーに関するリポジトリです。
 * JpaRepositoryを継承しています。
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
