INSERT INTO users (id, nickname, unique_user_id, roles)
VALUES (99999999, 'ゲストユーザー', 'guest-user', 'GUEST');

INSERT INTO bikes (id, user_id, manufacturer, model_name, model_code, model_year, current_mileage, purchase_date)
VALUES (9999998, 99999999, 'Honda', 'CBR1000RR', 'SC59', 2012, 12345, '2020-01-01');
INSERT INTO bikes (id, user_id, manufacturer, model_name, model_code, model_year, current_mileage, purchase_date)
VALUES (99999997, 99999999, 'Kawasaki', 'ZEPHYR', 'ZR400C', 1989, 12345, '2020-01-01');

INSERT INTO ai_questions (id, user_id, bike_id, category_id, question, answer)
VALUES (99999991, 99999999, 9999998, 1, 'バイクのエンジンオイルの選び方を教えてください。', 'エンジンオイルは、粘度、ベースオイルの種類、API規格などを考慮して選びます。メーカー推奨のオイルを使用するのが最も安全です。');
INSERT INTO ai_questions (id, user_id, bike_id, category_id, question, answer)
VALUES (99999992, 99999999, 9999998, 4, 'タイヤの空気圧は', 'タイヤの空気圧は、メーカー指定の値を参考にしてください。運転席ドアの内部や取扱説明書に記載されています。');
INSERT INTO ai_questions (id, user_id, bike_id, category_id, question, answer)
VALUES (99999993, 99999999, 99999997, 2, 'ブレーキフルードの交換時期は？', 'ブレーキフルードは吸湿性が高いため、通常は2年ごとの交換が推奨されます。');
INSERT INTO ai_questions (id, user_id, bike_id, category_id, question, answer)
VALUES (99999994, 99999999, 99999997, 8, 'チェーンのメンテナンス方法を教えてください。', 'チェーンのメンテナンスは、清掃、注油、張り調整が基本です。専用のクリーナーとルブを使って定期的に行いましょう。');

INSERT INTO maintenance_tasks (id, category_id, name, description, bike_id)
VALUES (99999981, 1, 'エンジンオイル交換', 'AIの回答を参考に、バイクの推奨エンジンオイルに交換した。', 9999998);
INSERT INTO maintenance_tasks (id, category_id, name, description, bike_id)
VALUES (99999982, 4, 'タイヤ空気圧調整', 'AIの回答を参考に、ツーリング前にタイヤの空気圧をメーカー指定値に調整した。', 9999998);
INSERT INTO maintenance_tasks (id, category_id, name, description, bike_id)
VALUES (99999983, 2, 'ブレーキフルード交換', 'AIの回答を参考に、ブレーキフルードを交換した。', 99999997);
INSERT INTO maintenance_tasks (id, category_id, name, description, bike_id)
VALUES (99999984, 8, 'チェーン清掃・注油', 'AIの回答を参考に、チェーンの清掃と注油を行った。', 99999997);