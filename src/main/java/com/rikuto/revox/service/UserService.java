package com.rikuto.revox.service;

import com.rikuto.revox.entity.User;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// IDで直接取得
	public User findById(Integer userId) {
		return userRepository.findByIdAndIsDeletedFalse(userId)
				.orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));
	}
}
