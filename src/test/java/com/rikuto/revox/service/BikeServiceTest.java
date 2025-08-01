package com.rikuto.revox.service;

import com.rikuto.revox.dto.bike.BikeCreateRequest;
import com.rikuto.revox.dto.bike.BikeResponse;
import com.rikuto.revox.domain.Bike;
import com.rikuto.revox.domain.User;
import com.rikuto.revox.exception.ResourceNotFoundException;
import com.rikuto.revox.mapper.BikeMapper;
import com.rikuto.revox.repository.BikeRepository;
import com.rikuto.revox.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * BikeServiceのテストクラスです。
 * サービス層のビジネスロジックが正しく動作するかを検証するため、
 * 依存するリポジトリやマッパーはモック化して単体テストを行っています。
 */
@ExtendWith(MockitoExtension.class)
class BikeServiceTest {

	@Mock
	private BikeRepository bikeRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BikeMapper bikeMapper;

	@InjectMocks
	private BikeService bikeService;

	@Mock
	private User testUser;

	@Mock
	private Bike testBike;

	private BikeCreateRequest commonBikeCreateRequest;

	@BeforeEach
	void setUp() {
		// BikeCreateRequestの準備（これは各テストで共通して使えるテンプレートとして定義）
		commonBikeCreateRequest = BikeCreateRequest.builder()
				.userId(1)
				.manufacturer("Honda")
				.modelName("CBR250RR")
				.modelCode("MC51")
				.modelYear(2023)
				.currentMileage(1000)
				.purchaseDate(LocalDate.of(2023, 1, 1))
				.imageUrl("http://example.com/bike.jpg")
				.build();
	}

	@Test
	void ユーザーIDに紐づく論理削除されていないバイク情報を正しく取得できること() {
		// testBikeモックの作成
		Bike testBike = mock(Bike.class);
		//BikeResponseとして返す情報をbuilderで準備
		BikeResponse expectedBikeResponse = BikeResponse.builder()
				.id(101)
				.manufacturer("Honda")
				.modelName("CBR250RR")
				.modelCode("MC51")
				.modelYear(2023)
				.currentMileage(1000)
				.purchaseDate(LocalDate.of(2023, 1, 1))
				.imageUrl("http://example.com/bike.jpg")
				.createdAt(LocalDateTime.now().minusDays(1))
				.updatedAt(LocalDateTime.now().minusDays(1))
				.userId(commonBikeCreateRequest.getUserId())
				.build();

		// bikeRepositoryが論理削除されていない（isDeleted=false）バイクを返すように設定
		when(bikeRepository.findByUserIdAndIsDeletedFalse(commonBikeCreateRequest.getUserId())).thenReturn(Optional.of(testBike));
		// bikeMapperがBikeエンティティをBikeResponseに変換するように設定
		when(bikeMapper.toResponse(testBike)).thenReturn(expectedBikeResponse);

		// When: findBikeByUserIdメソッドを呼び出し
		BikeResponse result = bikeService.findBikeByUserId(commonBikeCreateRequest.getUserId());

		// Then: 期待されるBikeResponseが返されることをAssertJで確認
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedBikeResponse.getId());
		assertThat(result.getModelName()).isEqualTo(expectedBikeResponse.getModelName());

		// モックのメソッドが適切に呼び出されたことを検証
		verify(bikeRepository, times(1)).findByUserIdAndIsDeletedFalse(commonBikeCreateRequest.getUserId());
		verify(bikeMapper, times(1)).toResponse(testBike);
	}

	@Test
	void ユーザーIDに紐づく論理削除されていないバイクが見つからない場合にResourceNotFoundExceptionをスローすること() {
		// Given: bikeRepositoryが空のOptionalを返すように設定
		when(bikeRepository.findByUserIdAndIsDeletedFalse(commonBikeCreateRequest.getUserId())).thenReturn(Optional.empty());

		// When & Then: ResourceNotFoundExceptionがスローされることをAssertJで確認
		assertThatThrownBy(() -> bikeService.findBikeByUserId(commonBikeCreateRequest.getUserId()))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("User ID " + commonBikeCreateRequest.getUserId() + " に紐づくバイクが見つかりません。");

		// bikeRepositoryの呼び出しのみが行われたことを検証
		verify(bikeRepository, times(1)).findByUserIdAndIsDeletedFalse(commonBikeCreateRequest.getUserId());
	}

	@Test
	void 新しいバイク情報が正常に登録され登録されたバイク情報が返されること() {
		// 登録用の新しいBikeエンティティのモックを作成
		Bike newBikeEntity = mock(Bike.class);

		// 依存関係の振る舞いを設定
		when(userRepository.findById(commonBikeCreateRequest.getUserId())).thenReturn(Optional.of(testUser));
		when(bikeMapper.toEntity(commonBikeCreateRequest, testUser)).thenReturn(newBikeEntity);
		when(bikeRepository.save(newBikeEntity)).thenReturn(newBikeEntity);

		BikeResponse expectedBikeResponse = BikeResponse.builder()
				.id(102)
				.manufacturer(commonBikeCreateRequest.getManufacturer())
				.modelName(commonBikeCreateRequest.getModelName())
				.modelCode(commonBikeCreateRequest.getModelCode())
				.modelYear(commonBikeCreateRequest.getModelYear())
				.currentMileage(commonBikeCreateRequest.getCurrentMileage())
				.purchaseDate(commonBikeCreateRequest.getPurchaseDate())
				.imageUrl(commonBikeCreateRequest.getImageUrl())
				.userId(commonBikeCreateRequest.getUserId())
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();
		when(bikeMapper.toResponse(newBikeEntity)).thenReturn(expectedBikeResponse);

		// When: registerBikeメソッドを呼び出し
		BikeResponse result = bikeService.registerBike(commonBikeCreateRequest);

		// Then: 期待されるBikeResponseが返され、関連メソッドが呼び出されたことをAssertJで確認
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(expectedBikeResponse.getId());
		assertThat(result.getModelName()).isEqualTo(expectedBikeResponse.getModelName());

		// モックのメソッドが適切に呼び出されたことの検証
		verify(userRepository, times(1)).findById(commonBikeCreateRequest.getUserId());
		verify(bikeMapper, times(1)).toEntity(eq(commonBikeCreateRequest), eq(testUser));
		verify(bikeRepository, times(1)).save(eq(newBikeEntity));
		verify(bikeMapper, times(1)).toResponse(eq(newBikeEntity));
	}

	@Test
	void バイク登録時に指定されたユーザーが見つからない場合にResourceNotFoundExceptionをスローすること() {
		// Given: userRepositoryが空のOptionalを返すように設定
		when(userRepository.findById(commonBikeCreateRequest.getUserId())).thenReturn(Optional.empty());

		// When & Then: ResourceNotFoundExceptionがスローされることをAssertJで確認
		assertThatThrownBy(() -> bikeService.registerBike(commonBikeCreateRequest))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("ユーザーID " + commonBikeCreateRequest.getUserId() + " が見つかりません。");

		// userRepositoryの呼び出しのみが行われたことを検証
		verify(userRepository, times(1)).findById(commonBikeCreateRequest.getUserId());
		verify(bikeMapper, never()).toEntity(any(BikeCreateRequest.class), any(User.class));
		verify(bikeRepository, never()).save(any(Bike.class));
	}

	@Test
	void 既存のバイク情報が正常に更新され更新されたバイク情報が返されること() {
		// Given: 更新リクエストの準備
		BikeCreateRequest updateRequest = BikeCreateRequest.builder()
				.manufacturer("Kawasaki")
				.modelName("Ninja 400")
				.modelYear(2024)
				.currentMileage(1500)
				.purchaseDate(LocalDate.of(2023, 1, 1))
				.imageUrl("http://example.com/ninja.jpg")
				.userId(commonBikeCreateRequest.getUserId())
				.build();

		// testUserモックに必要な振る舞いを設定
		when(testUser.getId()).thenReturn(updateRequest.getUserId());

		// testBikeモックに必要な振る舞いを設定（更新前の初期状態）
		when(testBike.getId()).thenReturn(101);

		// bikeRepositoryが既存のバイク（testBikeモック）を返すように設定
		when(bikeRepository.findById(testBike.getId())).thenReturn(Optional.of(testBike));

		// bikeMapper.updateEntityFromDto はvoidメソッドなのでdoNothing
		doNothing().when(bikeMapper).updateEntityFromDto(eq(updateRequest), eq(testBike));

		// bikeRepository.saveが呼ばれた後に、testBikeのプロパティが更新された状態を返すようにスタブする
		when(bikeRepository.save(eq(testBike))).thenReturn(testBike);

		BikeResponse updatedBikeResponse = BikeResponse.builder()
				.id(testBike.getId())
				.manufacturer(updateRequest.getManufacturer())
				.modelName(updateRequest.getModelName())
				.modelCode(testBike.getModelCode())
				.modelYear(updateRequest.getModelYear())
				.currentMileage(updateRequest.getCurrentMileage())
				.purchaseDate(updateRequest.getPurchaseDate())
				.imageUrl(updateRequest.getImageUrl())
				.createdAt(testBike.getCreatedAt())
				.updatedAt(LocalDateTime.now())
				.userId(testUser.getId())
				.build();
		when(bikeMapper.toResponse(eq(testBike))).thenReturn(updatedBikeResponse);

		// When: updateBikeメソッドを呼び出し
		BikeResponse result = bikeService.updateBike(testBike.getId(), updateRequest);

		// Then: 期待される更新結果が返され、関連メソッドが呼び出されたことをAssertJで確認
		assertThat(result).isNotNull();
		assertThat(result.getModelName()).isEqualTo("Ninja 400");
		assertThat(result.getModelYear()).isEqualTo(2024);
		assertThat(result.getCurrentMileage()).isEqualTo(1500);

		verify(bikeRepository, times(1)).findById(eq(testBike.getId()));
		verify(bikeMapper, times(1)).updateEntityFromDto(eq(updateRequest), eq(testBike));
		verify(bikeRepository, times(1)).save(eq(testBike));
		verify(bikeMapper, times(1)).toResponse(eq(testBike));
	}

	@Test
	void バイク更新時に指定されたバイクが見つからない場合にResourceNotFoundExceptionをスローすること() {
		// Given: 存在しないバイクIDを指定
		Integer nonExistentBikeId = 999;
		BikeCreateRequest updateRequest = BikeCreateRequest.builder().build();
		when(bikeRepository.findById(eq(nonExistentBikeId))).thenReturn(Optional.empty());

		// When & Then: ResourceNotFoundExceptionがスローされること
		assertThatThrownBy(() -> bikeService.updateBike(nonExistentBikeId, updateRequest))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("バイクID " + nonExistentBikeId + " が見つかりません。");

		// findByIdの呼び出しのみが行われたことを検証
		verify(bikeRepository, times(1)).findById(eq(nonExistentBikeId));
		verify(bikeMapper, never()).updateEntityFromDto(any(BikeCreateRequest.class), any(Bike.class));
		verify(bikeRepository, never()).save(any(Bike.class));
	}

	@Test
	void 登録されているバイクが正常に論理削除されること() {
		// Given: testBikeモックに必要な振る舞いを設定
		when(testBike.getId()).thenReturn(101);
		when(testBike.isDeleted()).thenReturn(false); // 初期状態
		when(testBike.getUpdatedAt()).thenReturn(LocalDateTime.now().minusHours(1)); // 初期状態

		// bikeRepositoryが既存のバイク（testBikeモック）を返すように設定
		when(bikeRepository.findById(eq(testBike.getId()))).thenReturn(Optional.of(testBike));

		// testBike.softDelete()が呼び出された際に、testBikeモックのisDeleted()とgetUpdatedAt()の振る舞いを変更する
		doAnswer(invocation -> {
			// softDelete()が呼び出されたことをシミュレート
			when(testBike.isDeleted()).thenReturn(true); // 呼び出し後にtrueを返すように設定
			when(testBike.getUpdatedAt()).thenReturn(LocalDateTime.now()); // updatedAtも更新される想定
			return null; // voidメソッドなのでnullを返す
		}).when(testBike).softDelete();

		// bikeRepository.saveがtestBikeを保存し、そのtestBikeを返すように設定
		when(bikeRepository.save(eq(testBike))).thenReturn(testBike);

		// When: softDeleteBikeメソッドを呼び出し
		bikeService.softDeleteBike(testBike.getId());

		// Then: 関連メソッドが呼び出され、モックのBikeオブジェクトの状態が期待通りに変化したことをAssertJで確認
		verify(bikeRepository, times(1)).findById(eq(testBike.getId()));
		verify(testBike, times(1)).softDelete();
		verify(bikeRepository, times(1)).save(eq(testBike));

		// softDelete呼び出し後に、モックのtestBikeのgetterが期待する値を返すか確認
		assertThat(testBike.isDeleted()).isTrue();
		assertThat(testBike.getUpdatedAt()).isNotNull();
	}

	@Test
	void 論理削除時に指定されたバイクが見つからない場合にResourceNotFoundExceptionをスローすること() {
		// Given: 存在しないバイクIDを指定
		Integer nonExistentBikeId = 999;
		when(bikeRepository.findById(eq(nonExistentBikeId))).thenReturn(Optional.empty());

		// When & Then: ResourceNotFoundExceptionがスローされること
		assertThatThrownBy(() -> bikeService.softDeleteBike(nonExistentBikeId))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("バイクID " + nonExistentBikeId + " が見つかりません。");

		// findByIdの呼び出しのみが行われたことを検証
		verify(bikeRepository, times(1)).findById(eq(nonExistentBikeId));
		verify(testBike, never()).softDelete();
		verify(bikeRepository, never()).save(any(Bike.class));
	}
}