INSERT INTO users (id, nickname, display_email, unique_user_id, roles)
VALUES (99999999, 'ゲストユーザー', NULL, 'guest-user', 'GUEST')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO bikes (id, user_id, manufacturer, model_name, model_code, model_year, current_mileage, purchase_date)
VALUES (9999998, 99999999, 'Honda', 'CBR1000RR', 'SC59', 2012, 12345, '2020-01-01')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO bikes (id, user_id, manufacturer, model_name, model_code, model_year, current_mileage, purchase_date)
VALUES (99999997, 99999999, 'Honda', 'CBR1000RR', 'SC59', 2012, 12345, '2020-01-01')
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO ai_questions (id, user_id, bike_id, category_id, question, answer)
VALUES (99999991, 99999999, 9999998, 1, 'バイクのエンジンオイルの選び方を教えてください。', 'エンジンオイルは、粘度、ベースオイルの種類、API規格などを考慮して選びます。メーカー推奨のオイルを使用するのが最も安全です。'),
       (99999992, 99999999, 9999998, 4, 'タイヤの空気圧は', 'タイヤの空気圧は、メーカー指定の値を参考にしてください。運転席ドアの内部や取扱説明書に記載されています。'),
       (99999993, 99999999, 99999997, 2, 'ブレーキフルードの交換時期は？', 'ブレーキフルードは吸湿性が高いため、通常は2年ごとの交換が推奨されます。'),
       (99999994, 99999999, 99999997, 8, 'チェーンのメンテナンス方法を教えてください。', 'チェーンのメンテナンスは、清掃、注油、張り調整が基本です。専用のクリーナーとルブを使って定期的に行いましょう。');

INSERT INTO maintenance_tasks (id, user_id, bike_id, title, description, mileage, created_at, updated_at)
VALUES (99999981, 99999999, 9999998, 'エンジンオイル交換', 'AIの回答を参考に、バイクの推奨エンジンオイルに交換した。', 12345, '2023-05-15 10:00:00', '2023-05-15 10:00:00'),
       (99999982, 99999999, 9999998, 'タイヤ空気圧調整', 'AIの回答を参考に、ツーリング前にタイヤの空気圧をメーカー指定値に調整した。', 12450, '2023-05-20 14:30:00', '2023-05-20 14:30:00'),
       (99999983, 99999999, 99999997, 'ブレーキフルード交換', 'AIの回答を参考に、ブレーキフルードを交換した。', 12600, '2023-06-01 11:45:00', '2023-06-01 11:45:00'),
       (99999984, 99999999, 99999997, 'チェーン清掃・注油', 'AIの回答を参考に、チェーンの清掃と注油を行った。', 12650, '2023-06-05 09:10:00', '2023-06-05 09:10:00');