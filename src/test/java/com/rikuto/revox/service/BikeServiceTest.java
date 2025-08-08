package com.rikuto.revox.service;

import com.rikuto.revox.domain.User;
import com.rikuto.revox.domain.bike.Bike;
import com.rikuto.revox.domain.bike.BikeUpdateData;
import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.dto.bike.BikeUpdateRequest;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.BikeMapper;
import com.rikuto.revox.repository.BikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BikeServiceTest {

	@Mock
	private BikeRepository bikeRepository;

	@Mock
	private UserService userService;

	@Mock
	private BikeMapper bikeMapper;

	@InjectMocks
	private BikeService bikeService;

	private User testUser;
	private Bike testBike;
	private BikeCreateRequest commonBikeCreateRequest;

	@BeforeEach
	void setUp() {
		testUser = User.builder().id(1).nickname("testUser").build();
		testBike = Bike.builder().id(101).user(testUser).manufacturer("Honda").modelName("CBR250RR").build();

		commonBikeCreateRequest = BikeCreateRequest.builder()
				.userId(testUser.getId())
				.manufacturer("Honda")
				.modelName("CBR250RR")
				.modelCode("MC51")
				.modelYear(2023)
				.currentMileage(1000)
				.purchaseDate(LocalDate.of(2023, 1, 1))
				.imageUrl("http://example.com/bike.jpg")
				.build();
	}

	// 共通スタブ補助メソッド
	private void stubUserFound() {
		when(userService.findById(testUser.getId())).thenReturn(testUser);
	}

	private void stubUserNotFound() {
		when(userService.findById(testUser.getId()))
				.thenThrow(new ResourceNotFoundException("ユーザーID " + testUser.getId() + " が見つかりません。"));
	}

	private void stubBikeFound() {
		when(bikeRepository.findByIdAndUserIdAndIsDeletedFalse(testUser.getId(), testBike.getId()))
				.thenReturn(Optional.of(testBike));
	}

	private void stubBikeNotFound() {
		when(bikeRepository.findByIdAndUserIdAndIsDeletedFalse(testUser.getId(), testBike.getId()))
				.thenReturn(Optional.empty());
	}

	@Nested
	class FindByIdAndUserIdTests {

		@Test
		void ユーザーIDとバイクIDに紐づくバイクを正しく取得できること() {
			stubBikeFound();
			BikeResponse expectedResponse = BikeResponse.builder().id(testBike.getId()).modelName(testBike.getModelName()).build();
			when(bikeMapper.toResponse(testBike)).thenReturn(expectedResponse);

			BikeResponse result = bikeService.findByIdAndUserId(testUser.getId(), testBike.getId());

			assertThat(result).isEqualTo(expectedResponse);
			verify(bikeRepository).findByIdAndUserIdAndIsDeletedFalse(testUser.getId(), testBike.getId());
			verify(bikeMapper).toResponse(testBike);
		}

		@Test
		void バイクが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubBikeNotFound();

			assertThatThrownBy(() -> bikeService.findByIdAndUserId(testUser.getId(), testBike.getId()))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("ユーザーID " + testUser.getId() + " に紐づくバイクID " + testBike.getId() + " が見つかりません。");

			verify(bikeMapper, never()).toResponse(any());
		}
	}

	@Nested
	class RegisterBikeTests {

		@Test
		void 新しいバイク情報が正常に登録され登録されたバイク情報が返されること() {
			stubUserFound();
			when(bikeMapper.toEntity(testUser, commonBikeCreateRequest)).thenReturn(testBike);
			when(bikeRepository.save(testBike)).thenReturn(testBike);
			BikeResponse expectedResponse = BikeResponse.builder().id(testBike.getId()).build();
			when(bikeMapper.toResponse(testBike)).thenReturn(expectedResponse);

			BikeResponse result = bikeService.registerBike(commonBikeCreateRequest);

			assertThat(result).isEqualTo(expectedResponse);
			verify(userService).findById(testUser.getId());
			verify(bikeMapper).toEntity(testUser, commonBikeCreateRequest);
			verify(bikeRepository).save(testBike);
			verify(bikeMapper).toResponse(testBike);
		}

		@Test
		void ユーザーが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubUserNotFound();

			assertThatThrownBy(() -> bikeService.registerBike(commonBikeCreateRequest))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessage("ユーザーID " + testUser.getId() + " が見つかりません。");

			verify(bikeMapper, never()).toEntity(any(), any());
			verify(bikeRepository, never()).save(any());
		}
	}

	@Nested
	class UpdateBikeTests {

		@Test
		void 既存のバイク情報が正常に更新され更新されたバイク情報が返されること() {
			stubBikeFound();
			BikeUpdateRequest updateRequest = BikeUpdateRequest.builder()
					.manufacturer("Honda")
					.modelName("Ninja 400")
					.build();

			Bike updatedBike = testBike;
			updatedBike.updateFrom(
					BikeUpdateData.builder()
							.manufacturer("Honda")
							.modelName("Ninja 400")
							.build()
			);

			BikeResponse expectedResponse = BikeResponse.builder()
					.id(testBike.getId())
					.modelName("Ninja 400")
					.manufacturer("Honda")
					.build();

			when(bikeRepository.save(any(Bike.class))).thenReturn(updatedBike);
			when(bikeMapper.toResponse(any(Bike.class))).thenReturn(expectedResponse);

			BikeResponse result = bikeService.updateBike(updateRequest, testBike.getId(), testUser.getId());

			assertThat(result).isEqualTo(expectedResponse);
			verify(bikeRepository).save(any(Bike.class));
			verify(bikeMapper).toResponse(any(Bike.class));
		}

		@Test
		void バイクが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubBikeNotFound();
			BikeUpdateRequest updateRequest = BikeUpdateRequest.builder()
					.manufacturer("Honda")
					.build();

			assertThatThrownBy(() -> bikeService.updateBike(updateRequest, testBike.getId(), testUser.getId()))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("ユーザーID " + testUser.getId() + " に紐づくバイクID " + testBike.getId() + " が見つかりません。");

			verify(bikeRepository, never()).save(any());
		}
	}

	@Nested
	class SoftDeleteBikeTests {

		@Test
		void 登録されているバイクが正常に論理削除されること() {
			stubBikeFound();
			when(bikeRepository.save(testBike)).thenReturn(testBike);

			bikeService.softDeleteBike(testUser.getId(), testBike.getId());

			assertThat(testBike.isDeleted()).isTrue();
			verify(bikeRepository).save(testBike);
		}

		@Test
		void バイクが見つからない場合にResourceNotFoundExceptionをスローすること() {
			stubBikeNotFound();

			assertThatThrownBy(() -> bikeService.softDeleteBike(testUser.getId(), testBike.getId()))
					.isInstanceOf(ResourceNotFoundException.class)
					.hasMessageContaining("ユーザー ID " + testUser.getId() + " に紐づくバイクID " + testBike.getId() + "が見つかりません。");

			verify(bikeRepository, never()).save(any());
		}
	}

	@Nested
	class FindBikeByUserIdTests {

		@Test
		void ユーザーIDに紐づく全てのバイク情報を正しく取得できること() {
			List<Bike> bikeList = List.of(testBike);
			when(bikeRepository.findByUserIdAndIsDeletedFalse(testUser.getId())).thenReturn(bikeList);

			List<BikeResponse> expectedResponse = List.of(BikeResponse.builder().id(testBike.getId()).build());
			when(bikeMapper.toResponseList(bikeList)).thenReturn(expectedResponse);

			List<BikeResponse> result = bikeService.findBikeByUserId(testUser.getId());

			assertThat(result).isEqualTo(expectedResponse);
			verify(bikeRepository, times(1)).findByUserIdAndIsDeletedFalse(testUser.getId());
			verify(bikeMapper, times(1)).toResponseList(bikeList);
		}
	}
}