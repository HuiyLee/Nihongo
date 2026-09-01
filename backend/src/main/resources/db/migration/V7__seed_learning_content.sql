-- Seed starter learning content (vocabulary, kanji, grammar, listening
-- exercises) across all five JLPT levels (N5-N1) so the browse/search
-- pages have real data to show instead of empty states. Levels themselves
-- were already seeded by V2. lesson_id is left NULL throughout - these
-- items are standalone, browsable directly by level (matches how
-- VocabularyRepository.search / KanjiRepository.search / etc. already
-- support a null lessonId filter).

-- =========================================================================
-- VOCABULARY
-- =========================================================================

INSERT INTO vocabularies (level_id, word, kanji, hiragana, katakana, romaji, meaning, part_of_speech, example, example_meaning) VALUES
-- N5
((SELECT id FROM levels WHERE code = 'N5'), '私', '私', 'わたし', NULL, 'watashi', 'I / me', 'pronoun', '私は学生です。', 'I am a student.'),
((SELECT id FROM levels WHERE code = 'N5'), '学生', '学生', 'がくせい', NULL, 'gakusei', 'student', 'noun', '私は学生です。', 'I am a student.'),
((SELECT id FROM levels WHERE code = 'N5'), '先生', '先生', 'せんせい', NULL, 'sensei', 'teacher', 'noun', '田中先生は優しいです。', 'Teacher Tanaka is kind.'),
((SELECT id FROM levels WHERE code = 'N5'), '食べる', '食べる', 'たべる', NULL, 'taberu', 'to eat', 'verb', '朝ごはんを食べます。', 'I eat breakfast.'),
((SELECT id FROM levels WHERE code = 'N5'), '飲む', '飲む', 'のむ', NULL, 'nomu', 'to drink', 'verb', '水を飲みます。', 'I drink water.'),
((SELECT id FROM levels WHERE code = 'N5'), '学校', '学校', 'がっこう', NULL, 'gakkou', 'school', 'noun', '学校へ行きます。', 'I go to school.'),
((SELECT id FROM levels WHERE code = 'N5'), '本', '本', 'ほん', NULL, 'hon', 'book', 'noun', '本を読みます。', 'I read a book.'),
((SELECT id FROM levels WHERE code = 'N5'), '大きい', '大きい', 'おおきい', NULL, 'ookii', 'big', 'adjective', 'この家は大きいです。', 'This house is big.'),
((SELECT id FROM levels WHERE code = 'N5'), '小さい', '小さい', 'ちいさい', NULL, 'chiisai', 'small', 'adjective', '猫は小さいです。', 'The cat is small.'),
((SELECT id FROM levels WHERE code = 'N5'), '今日', '今日', 'きょう', NULL, 'kyou', 'today', 'noun', '今日は晴れです。', 'Today is sunny.'),
-- N4
((SELECT id FROM levels WHERE code = 'N4'), '旅行', '旅行', 'りょこう', NULL, 'ryokou', 'travel', 'noun', '来月旅行に行きます。', 'I''m going on a trip next month.'),
((SELECT id FROM levels WHERE code = 'N4'), '経験', '経験', 'けいけん', NULL, 'keiken', 'experience', 'noun', 'いい経験でした。', 'It was a good experience.'),
((SELECT id FROM levels WHERE code = 'N4'), '準備', '準備', 'じゅんび', NULL, 'junbi', 'preparation', 'noun', '準備をします。', 'I will prepare.'),
((SELECT id FROM levels WHERE code = 'N4'), '説明', '説明', 'せつめい', NULL, 'setsumei', 'explanation', 'noun', '説明してください。', 'Please explain.'),
((SELECT id FROM levels WHERE code = 'N4'), '天気', '天気', 'てんき', NULL, 'tenki', 'weather', 'noun', '今日の天気はいいです。', 'Today''s weather is nice.'),
((SELECT id FROM levels WHERE code = 'N4'), '元気', '元気', 'げんき', NULL, 'genki', 'healthy / energetic', 'adjective', '元気ですか。', 'Are you well?'),
((SELECT id FROM levels WHERE code = 'N4'), '忙しい', '忙しい', 'いそがしい', NULL, 'isogashii', 'busy', 'adjective', '最近忙しいです。', 'I''ve been busy lately.'),
((SELECT id FROM levels WHERE code = 'N4'), '始める', '始める', 'はじめる', NULL, 'hajimeru', 'to start', 'verb', '仕事を始めます。', 'I start work.'),
((SELECT id FROM levels WHERE code = 'N4'), '終わる', '終わる', 'おわる', NULL, 'owaru', 'to end', 'verb', '授業が終わります。', 'Class ends.'),
((SELECT id FROM levels WHERE code = 'N4'), '約束', '約束', 'やくそく', NULL, 'yakusoku', 'promise', 'noun', '約束を守ります。', 'I keep my promise.'),
-- N3
((SELECT id FROM levels WHERE code = 'N3'), '環境', '環境', 'かんきょう', NULL, 'kankyou', 'environment', 'noun', '環境を守りましょう。', 'Let''s protect the environment.'),
((SELECT id FROM levels WHERE code = 'N3'), '影響', '影響', 'えいきょう', NULL, 'eikyou', 'influence', 'noun', '天気に影響されます。', 'Affected by the weather.'),
((SELECT id FROM levels WHERE code = 'N3'), '経済', '経済', 'けいざい', NULL, 'keizai', 'economy', 'noun', '経済が発展しています。', 'The economy is developing.'),
((SELECT id FROM levels WHERE code = 'N3'), '状況', '状況', 'じょうきょう', NULL, 'joukyou', 'situation', 'noun', '状況を説明します。', 'I''ll explain the situation.'),
((SELECT id FROM levels WHERE code = 'N3'), '判断', '判断', 'はんだん', NULL, 'handan', 'judgment', 'noun', '自分で判断します。', 'I''ll judge for myself.'),
((SELECT id FROM levels WHERE code = 'N3'), '対応', '対応', 'たいおう', NULL, 'taiou', 'to handle / respond', 'noun', '問題に対応します。', 'I''ll deal with the problem.'),
((SELECT id FROM levels WHERE code = 'N3'), '迷惑', '迷惑', 'めいわく', NULL, 'meiwaku', 'trouble / nuisance', 'noun', '迷惑をかけないでください。', 'Please don''t cause trouble.'),
((SELECT id FROM levels WHERE code = 'N3'), '増える', '増える', 'ふえる', NULL, 'fueru', 'to increase', 'verb', '人口が増えます。', 'The population increases.'),
((SELECT id FROM levels WHERE code = 'N3'), '減る', '減る', 'へる', NULL, 'heru', 'to decrease', 'verb', '収入が減ります。', 'Income decreases.'),
((SELECT id FROM levels WHERE code = 'N3'), '複雑', '複雑', 'ふくざつ', NULL, 'fukuzatsu', 'complicated', 'adjective', 'この問題は複雑です。', 'This problem is complicated.'),
-- N2
((SELECT id FROM levels WHERE code = 'N2'), '実施', '実施', 'じっし', NULL, 'jisshi', 'implementation', 'noun', '新しい制度を実施します。', 'We''ll implement the new system.'),
((SELECT id FROM levels WHERE code = 'N2'), '傾向', '傾向', 'けいこう', NULL, 'keikou', 'tendency', 'noun', '増加の傾向にあります。', 'There is a tendency to increase.'),
((SELECT id FROM levels WHERE code = 'N2'), '一致', '一致', 'いっち', NULL, 'icchi', 'agreement / match', 'noun', '意見が一致しました。', 'Opinions matched.'),
((SELECT id FROM levels WHERE code = 'N2'), '抽象', '抽象', 'ちゅうしょう', NULL, 'chuushou', 'abstract', 'noun', '抽象的な話です。', 'It''s an abstract topic.'),
((SELECT id FROM levels WHERE code = 'N2'), '矛盾', '矛盾', 'むじゅん', NULL, 'mujun', 'contradiction', 'noun', '話が矛盾しています。', 'The story is contradictory.'),
((SELECT id FROM levels WHERE code = 'N2'), '妥協', '妥協', 'だきょう', NULL, 'dakyou', 'compromise', 'noun', '妥協しましょう。', 'Let''s compromise.'),
((SELECT id FROM levels WHERE code = 'N2'), '促進', '促進', 'そくしん', NULL, 'sokushin', 'promotion', 'noun', '交流を促進します。', 'We promote exchange.'),
((SELECT id FROM levels WHERE code = 'N2'), '曖昧', '曖昧', 'あいまい', NULL, 'aimai', 'vague', 'adjective', '返事が曖昧です。', 'The answer is vague.'),
((SELECT id FROM levels WHERE code = 'N2'), '深刻', '深刻', 'しんこく', NULL, 'shinkoku', 'serious', 'adjective', '深刻な問題です。', 'It''s a serious problem.'),
((SELECT id FROM levels WHERE code = 'N2'), '見なす', '見なす', 'みなす', NULL, 'minasu', 'to regard as', 'verb', '賛成と見なします。', 'I''ll regard it as agreement.'),
-- N1
((SELECT id FROM levels WHERE code = 'N1'), '遂行', '遂行', 'すいこう', NULL, 'suikou', 'execution / carrying out', 'noun', '任務を遂行します。', 'Carry out the mission.'),
((SELECT id FROM levels WHERE code = 'N1'), '逸脱', '逸脱', 'いつだつ', NULL, 'itsudatsu', 'deviation', 'noun', '規則から逸脱しています。', 'It deviates from the rules.'),
((SELECT id FROM levels WHERE code = 'N1'), '折衝', '折衝', 'せっしょう', NULL, 'sesshou', 'negotiation', 'noun', '折衝を重ねます。', 'Repeated negotiations.'),
((SELECT id FROM levels WHERE code = 'N1'), '融合', '融合', 'ゆうごう', NULL, 'yuugou', 'fusion', 'noun', '文化の融合です。', 'A fusion of cultures.'),
((SELECT id FROM levels WHERE code = 'N1'), '潜在', '潜在', 'せんざい', NULL, 'senzai', 'latent / potential', 'noun', '潜在能力があります。', 'There is latent potential.'),
((SELECT id FROM levels WHERE code = 'N1'), '是正', '是正', 'ぜせい', NULL, 'zesei', 'correction / rectification', 'noun', '問題を是正します。', 'Correct the problem.'),
((SELECT id FROM levels WHERE code = 'N1'), '醸成', '醸成', 'じょうせい', NULL, 'jousei', 'fostering / brewing', 'noun', '雰囲気を醸成します。', 'Foster the atmosphere.'),
((SELECT id FROM levels WHERE code = 'N1'), '顕著', '顕著', 'けんちょ', NULL, 'kencho', 'remarkable', 'adjective', '効果が顕著です。', 'The effect is remarkable.'),
((SELECT id FROM levels WHERE code = 'N1'), '煩わしい', '煩わしい', 'わずらわしい', NULL, 'wazurawashii', 'troublesome', 'adjective', '手続きが煩わしいです。', 'The procedure is troublesome.'),
((SELECT id FROM levels WHERE code = 'N1'), '覆す', '覆す', 'くつがえす', NULL, 'kutsugaesu', 'to overturn', 'verb', '判決を覆します。', 'Overturn the verdict.');

-- =========================================================================
-- KANJI
-- =========================================================================

INSERT INTO kanjis (level_id, character, meaning, onyomi, kunyomi, stroke_count, example, example_meaning) VALUES
-- N5
((SELECT id FROM levels WHERE code = 'N5'), '日', 'day / sun', 'ニチ、ジツ', 'ひ、か', 4, '今日', 'today'),
((SELECT id FROM levels WHERE code = 'N5'), '一', 'one', 'イチ', 'ひと(つ)', 1, '一月', 'January'),
((SELECT id FROM levels WHERE code = 'N5'), '人', 'person', 'ジン、ニン', 'ひと', 2, '日本人', 'Japanese person'),
((SELECT id FROM levels WHERE code = 'N5'), '大', 'big', 'ダイ、タイ', 'おお(きい)', 3, '大学', 'university'),
((SELECT id FROM levels WHERE code = 'N5'), '小', 'small', 'ショウ', 'ちい(さい)', 3, '小学校', 'elementary school'),
((SELECT id FROM levels WHERE code = 'N5'), '山', 'mountain', 'サン', 'やま', 3, '富士山', 'Mt. Fuji'),
((SELECT id FROM levels WHERE code = 'N5'), '水', 'water', 'スイ', 'みず', 4, '水曜日', 'Wednesday'),
((SELECT id FROM levels WHERE code = 'N5'), '学', 'study / learning', 'ガク', 'まな(ぶ)', 8, '学生', 'student'),
-- N4
((SELECT id FROM levels WHERE code = 'N4'), '曜', 'day of the week', 'ヨウ', NULL, 18, '火曜日', 'Tuesday'),
((SELECT id FROM levels WHERE code = 'N4'), '週', 'week', 'シュウ', NULL, 11, '来週', 'next week'),
((SELECT id FROM levels WHERE code = 'N4'), '予', 'beforehand', 'ヨ', NULL, 4, '予定', 'schedule'),
((SELECT id FROM levels WHERE code = 'N4'), '定', 'fix / decide', 'テイ、ジョウ', 'さだ(める)', 8, '予定', 'schedule'),
((SELECT id FROM levels WHERE code = 'N4'), '験', 'test / experience', 'ケン', NULL, 18, '経験', 'experience'),
((SELECT id FROM levels WHERE code = 'N4'), '準', 'standard / semi-', 'ジュン', NULL, 13, '準備', 'preparation'),
((SELECT id FROM levels WHERE code = 'N4'), '備', 'equip / prepare', 'ビ', 'そな(える)', 12, '準備', 'preparation'),
((SELECT id FROM levels WHERE code = 'N4'), '忙', 'busy', 'ボウ', 'いそが(しい)', 6, '忙しい', 'busy'),
-- N3
((SELECT id FROM levels WHERE code = 'N3'), '境', 'boundary', 'キョウ', 'さかい', 14, '環境', 'environment'),
((SELECT id FROM levels WHERE code = 'N3'), '環', 'ring / surround', 'カン', NULL, 17, '環境', 'environment'),
((SELECT id FROM levels WHERE code = 'N3'), '済', 'settle / finish', 'サイ', 'す(む)', 11, '経済', 'economy'),
((SELECT id FROM levels WHERE code = 'N3'), '状', 'condition', 'ジョウ', NULL, 7, '状況', 'situation'),
((SELECT id FROM levels WHERE code = 'N3'), '況', 'circumstance', 'キョウ', NULL, 8, '状況', 'situation'),
((SELECT id FROM levels WHERE code = 'N3'), '判', 'judge', 'ハン', NULL, 7, '判断', 'judgment'),
((SELECT id FROM levels WHERE code = 'N3'), '断', 'cut off / decide', 'ダン', 'ことわ(る)', 11, '判断', 'judgment'),
((SELECT id FROM levels WHERE code = 'N3'), '複', 'duplicate / complex', 'フク', NULL, 14, '複雑', 'complicated'),
-- N2
((SELECT id FROM levels WHERE code = 'N2'), '傾', 'incline / tend', 'ケイ', 'かたむ(く)', 13, '傾向', 'tendency'),
((SELECT id FROM levels WHERE code = 'N2'), '致', 'send / cause', 'チ', 'いた(す)', 10, '一致', 'agreement'),
((SELECT id FROM levels WHERE code = 'N2'), '抽', 'pull out', 'チュウ', NULL, 8, '抽象', 'abstract'),
((SELECT id FROM levels WHERE code = 'N2'), '象', 'elephant / phenomenon', 'ショウ、ゾウ', NULL, 12, '対象', 'target / object'),
((SELECT id FROM levels WHERE code = 'N2'), '矛', 'spear', 'ム', 'ほこ', 5, '矛盾', 'contradiction'),
((SELECT id FROM levels WHERE code = 'N2'), '盾', 'shield', 'ジュン', 'たて', 9, '矛盾', 'contradiction'),
((SELECT id FROM levels WHERE code = 'N2'), '妥', 'appropriate', 'ダ', NULL, 7, '妥協', 'compromise'),
((SELECT id FROM levels WHERE code = 'N2'), '促', 'urge', 'ソク', 'うなが(す)', 9, '促進', 'promotion'),
-- N1
((SELECT id FROM levels WHERE code = 'N1'), '遂', 'carry out', 'スイ', 'と(げる)', 12, '遂行', 'execution'),
((SELECT id FROM levels WHERE code = 'N1'), '逸', 'deviate / excel', 'イツ', 'そ(れる)', 11, '逸脱', 'deviation'),
((SELECT id FROM levels WHERE code = 'N1'), '脱', 'escape / remove', 'ダツ', 'ぬ(ぐ)', 11, '脱出', 'escape'),
((SELECT id FROM levels WHERE code = 'N1'), '折', 'fold / negotiate', 'セツ', 'お(る)', 7, '折衝', 'negotiation'),
((SELECT id FROM levels WHERE code = 'N1'), '衝', 'collide / thoroughfare', 'ショウ', NULL, 15, '衝突', 'collision'),
((SELECT id FROM levels WHERE code = 'N1'), '融', 'melt / fuse', 'ユウ', NULL, 16, '融合', 'fusion'),
((SELECT id FROM levels WHERE code = 'N1'), '潜', 'hide / dive', 'セン', 'ひそ(む)', 15, '潜在', 'latent'),
((SELECT id FROM levels WHERE code = 'N1'), '顕', 'manifest', 'ケン', NULL, 18, '顕著', 'remarkable');

-- =========================================================================
-- GRAMMAR
-- =========================================================================

INSERT INTO grammars (level_id, pattern, meaning, formation, explanation, example, example_meaning) VALUES
-- N5
((SELECT id FROM levels WHERE code = 'N5'), '〜は〜です', 'X is Y', 'Noun + は + Noun + です', 'Basic sentence pattern: marks the topic with は and states a fact with です.', '私は学生です。', 'I am a student.'),
((SELECT id FROM levels WHERE code = 'N5'), '〜を〜ます', 'to do (verb) [object]', 'Noun + を + Verb (ます form)', 'を marks the direct object of a verb.', '水を飲みます。', 'I drink water.'),
((SELECT id FROM levels WHERE code = 'N5'), '〜へ行きます', 'to go to ~', 'Place + へ + 行きます', 'へ marks direction or destination.', '学校へ行きます。', 'I go to school.'),
((SELECT id FROM levels WHERE code = 'N5'), '〜があります／います', 'there is/are ~', 'Noun + が + あります (things) / います (living beings)', 'Existence pattern - あります for inanimate objects, います for people/animals.', '机の上に本があります。', 'There is a book on the desk.'),
((SELECT id FROM levels WHERE code = 'N5'), '〜ない form', 'plain negative', 'Verb stem + ない', 'The plain (dictionary-style) negative form of a verb.', '食べない。', '(I) don''t eat.'),
-- N4
((SELECT id FROM levels WHERE code = 'N4'), '〜ながら', 'while doing ~', 'Verb (ます stem) + ながら', 'Describes two actions happening at the same time, with the ながら clause being the secondary action.', '音楽を聞きながら勉強します。', 'I study while listening to music.'),
((SELECT id FROM levels WHERE code = 'N4'), '〜たら', 'if / when ~', 'Verb (た form) + ら', 'A common conditional form.', '雨が降ったら、行きません。', 'If it rains, I won''t go.'),
((SELECT id FROM levels WHERE code = 'N4'), '〜てもいいです', 'it''s okay to ~', 'Verb (て form) + もいいです', 'Used to give or ask for permission.', 'ここに座ってもいいですか。', 'May I sit here?'),
((SELECT id FROM levels WHERE code = 'N4'), '〜なければなりません', 'must ~', 'Verb (ない form, drop い) + ければなりません', 'Expresses obligation or necessity.', '宿題をしなければなりません。', 'I must do my homework.'),
((SELECT id FROM levels WHERE code = 'N4'), '〜そうです', 'looks/seems like ~', 'Adjective stem / Verb (ます stem) + そうです', 'Expresses a conjecture based on visual appearance.', '雨が降りそうです。', 'It looks like it''s going to rain.'),
-- N3
((SELECT id FROM levels WHERE code = 'N3'), '〜によって', 'depending on / by means of ~', 'Noun + によって', 'Indicates a cause, means, or that something varies depending on the noun.', '人によって考え方が違います。', 'Ways of thinking differ depending on the person.'),
((SELECT id FROM levels WHERE code = 'N3'), '〜ばかり', 'only / just ~', 'Noun + ばかり / Verb (た form) + ばかり', 'Limits an action to only that thing, or means "just did X".', '彼はゲームばかりしています。', 'He only plays games.'),
((SELECT id FROM levels WHERE code = 'N3'), '〜ことになる', 'it has been decided that ~', 'Verb (dictionary / ない form) + ことになる', 'A decision made by circumstance rather than the speaker''s own will.', '来月、大阪に転勤することになりました。', 'It''s been decided that I''ll transfer to Osaka next month.'),
((SELECT id FROM levels WHERE code = 'N3'), '〜わけではない', 'it''s not that ~', 'Plain form + わけではない', 'A partial denial - softens a negative statement.', '嫌いなわけではありません。', 'It''s not that I dislike it.'),
((SELECT id FROM levels WHERE code = 'N3'), '〜として', 'as ~', 'Noun + として', 'Indicates a role or capacity.', '彼は先生として働いています。', 'He works as a teacher.'),
-- N2
((SELECT id FROM levels WHERE code = 'N2'), '〜にもかかわらず', 'despite / in spite of ~', 'Noun / Plain form + にもかかわらず', 'Expresses contrast with an unexpected result.', '雨にもかかわらず、試合が行われました。', 'Despite the rain, the match was held.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜あげく', 'after all that ~ (negative outcome)', 'Verb (た form) + あげく', 'A negative result after a long, drawn-out process.', 'さんざん悩んだあげく、諦めました。', 'After agonizing over it, I gave up.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜を問わず', 'regardless of ~', 'Noun + を問わず', 'States that the noun is irrelevant to what follows.', '年齢を問わず、参加できます。', 'Anyone can participate regardless of age.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜ものの', 'although ~', 'Plain form + ものの', 'A concession - similar to だが/けれども but more formal.', '約束したものの、行けませんでした。', 'Although I promised, I couldn''t go.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜がちだ', 'tend to ~ (negative tendency)', 'Verb (ます stem) / Noun + がちだ', 'Describes a frequent, usually undesirable, tendency.', '最近、忘れがちです。', 'I''ve been forgetful lately.'),
-- N1
((SELECT id FROM levels WHERE code = 'N1'), '〜を余儀なくされる', 'be forced to ~', 'Noun + を余儀なくされる', 'A formal expression for being forced into something by circumstance.', '台風で中止を余儀なくされた。', 'The typhoon forced the cancellation.'),
((SELECT id FROM levels WHERE code = 'N1'), '〜ずにはいられない', 'can''t help but ~', 'Verb (ない form, drop ない) + ずにはいられない', 'Expresses an impulse that can''t be resisted.', '笑わずにはいられない。', 'I can''t help but laugh.'),
((SELECT id FROM levels WHERE code = 'N1'), '〜きらいがある', 'tend to ~ (formal negative tendency)', 'Verb (dictionary form) / Noun + の + きらいがある', 'A formal, literary way to describe an undesirable tendency.', '彼は人を疑うきらいがある。', 'He tends to be suspicious of people.'),
((SELECT id FROM levels WHERE code = 'N1'), '〜ことなしに', 'without doing ~', 'Verb (dictionary form) + ことなしに', 'A formal way to say "without doing something".', '努力することなしに、成功はない。', 'There''s no success without effort.'),
((SELECT id FROM levels WHERE code = 'N1'), '〜んばかりに', 'as if about to ~', 'Verb (ない form, drop ない) + んばかりに', 'Describes something that looks almost like it''s about to happen.', '泣かんばかりに頼んだ。', 'He begged as if about to cry.');

-- =========================================================================
-- LISTENING EXERCISES (Exercise type = LISTENING) + answer options
-- No real audio files are attached (audio_url left NULL) - the question
-- text stands in for the audio script, same convention used for other
-- seeded exercise types in this app.
-- =========================================================================

INSERT INTO exercises (level_id, type, question, explanation, difficulty) VALUES
((SELECT id FROM levels WHERE code = 'N5'), 'LISTENING', '次の会話を聞いてください。「すみません、今何時ですか。」「今、3時です。」 質問:今何時ですか。', '「今、3時です」と言っているので、答えは3時です。', 'EASY'),
((SELECT id FROM levels WHERE code = 'N5'), 'LISTENING', '女の人が話しています。「明日は雨ですから、傘を持って行ってください。」 質問:明日の天気は何ですか。', '「明日は雨です」と言っています。', 'EASY'),
((SELECT id FROM levels WHERE code = 'N4'), 'LISTENING', '男の人と女の人が話しています。男:「今度の週末、映画を見に行きませんか。」女:「いいですね。何時にしましょうか。」男:「2時はどうですか。」女:「大丈夫です。」 質問:二人は何をしますか。', '週末に映画を見に行く約束をしています。', 'MEDIUM'),
((SELECT id FROM levels WHERE code = 'N4'), 'LISTENING', '天気予報です。「明日は朝から雨が降りますが、午後には晴れるでしょう。」 質問:明日の午後の天気はどうなりますか。', '「午後には晴れるでしょう」と言っています。', 'MEDIUM'),
((SELECT id FROM levels WHERE code = 'N3'), 'LISTENING', '会社で上司と部下が話しています。上司:「この資料、明日の会議までに直しておいてください。」部下:「わかりました。何か特に気をつける点はありますか。」上司:「グラフの数字をもう一度確認してください。」 質問:部下は何をしなければなりませんか。', '上司はグラフの数字を確認するように頼んでいます。', 'MEDIUM'),
((SELECT id FROM levels WHERE code = 'N3'), 'LISTENING', 'ラジオのニュースです。「今年の夏は例年より気温が高くなる見込みです。」 質問:今年の夏について、何と言っていますか。', '「気温が高くなる見込み」なので、例年より暑いです。', 'MEDIUM'),
((SELECT id FROM levels WHERE code = 'N2'), 'LISTENING', '講演で話者が言っています。「経済成長には教育への投資が不可欠です。」 質問:話者は何が重要だと言っていますか。', '「教育への投資が不可欠」と言っています。', 'HARD'),
((SELECT id FROM levels WHERE code = 'N2'), 'LISTENING', '会議で司会者が話しています。「本日の議題は来年度の予算についてです。まず、各部署から報告をお願いします。」 質問:この会議の議題は何ですか。', '「本日の議題は来年度の予算について」と言っています。', 'HARD'),
((SELECT id FROM levels WHERE code = 'N1'), 'LISTENING', '評論家がテレビで話しています。「近年の技術革新は、雇用の在り方そのものを根底から覆しつつあります。」 質問:評論家は何について話していますか。', '技術革新が雇用に与える影響について話しています。', 'HARD'),
((SELECT id FROM levels WHERE code = 'N1'), 'LISTENING', '討論会で参加者が発言しています。「規制を緩和すれば、必ずしも市場が活性化するとは限らない。」 質問:この人はどう考えていますか。', '規制緩和が市場活性化に必ずつながるわけではない、と述べています。', 'HARD');

INSERT INTO exercise_answers (exercise_id, answer_text, is_correct, order_index) VALUES
((SELECT id FROM exercises WHERE question LIKE '次の会話を聞いてください。「すみません、今何時ですか%'), '2時', false, 0),
((SELECT id FROM exercises WHERE question LIKE '次の会話を聞いてください。「すみません、今何時ですか%'), '3時', true, 1),
((SELECT id FROM exercises WHERE question LIKE '次の会話を聞いてください。「すみません、今何時ですか%'), '4時', false, 2),
((SELECT id FROM exercises WHERE question LIKE '次の会話を聞いてください。「すみません、今何時ですか%'), '5時', false, 3),

((SELECT id FROM exercises WHERE question LIKE '女の人が話しています。「明日は雨ですから%'), '晴れ', false, 0),
((SELECT id FROM exercises WHERE question LIKE '女の人が話しています。「明日は雨ですから%'), '雨', true, 1),
((SELECT id FROM exercises WHERE question LIKE '女の人が話しています。「明日は雨ですから%'), '雪', false, 2),
((SELECT id FROM exercises WHERE question LIKE '女の人が話しています。「明日は雨ですから%'), '曇り', false, 3),

((SELECT id FROM exercises WHERE question LIKE '男の人と女の人が話しています。男:「今度の週末%'), '買い物に行く', false, 0),
((SELECT id FROM exercises WHERE question LIKE '男の人と女の人が話しています。男:「今度の週末%'), '映画を見に行く', true, 1),
((SELECT id FROM exercises WHERE question LIKE '男の人と女の人が話しています。男:「今度の週末%'), '旅行に行く', false, 2),
((SELECT id FROM exercises WHERE question LIKE '男の人と女の人が話しています。男:「今度の週末%'), '食事に行く', false, 3),

((SELECT id FROM exercises WHERE question LIKE '天気予報です。「明日は朝から雨が降りますが%'), '雨が降り続く', false, 0),
((SELECT id FROM exercises WHERE question LIKE '天気予報です。「明日は朝から雨が降りますが%'), '晴れる', true, 1),
((SELECT id FROM exercises WHERE question LIKE '天気予報です。「明日は朝から雨が降りますが%'), '雪が降る', false, 2),
((SELECT id FROM exercises WHERE question LIKE '天気予報です。「明日は朝から雨が降りますが%'), '曇りのまま', false, 3),

((SELECT id FROM exercises WHERE question LIKE '会社で上司と部下が話しています%'), '資料を作り直す', false, 0),
((SELECT id FROM exercises WHERE question LIKE '会社で上司と部下が話しています%'), 'グラフの数字を確認する', true, 1),
((SELECT id FROM exercises WHERE question LIKE '会社で上司と部下が話しています%'), '会議を延期する', false, 2),
((SELECT id FROM exercises WHERE question LIKE '会社で上司と部下が話しています%'), '上司に報告する', false, 3),

((SELECT id FROM exercises WHERE question LIKE 'ラジオのニュースです。「今年の夏は例年より%'), '例年より涼しい', false, 0),
((SELECT id FROM exercises WHERE question LIKE 'ラジオのニュースです。「今年の夏は例年より%'), '例年より暑い', true, 1),
((SELECT id FROM exercises WHERE question LIKE 'ラジオのニュースです。「今年の夏は例年より%'), '例年と変わらない', false, 2),
((SELECT id FROM exercises WHERE question LIKE 'ラジオのニュースです。「今年の夏は例年より%'), '雨が多い', false, 3),

((SELECT id FROM exercises WHERE question LIKE '講演で話者が言っています%'), '教育への投資', true, 0),
((SELECT id FROM exercises WHERE question LIKE '講演で話者が言っています%'), '税金の削減', false, 1),
((SELECT id FROM exercises WHERE question LIKE '講演で話者が言っています%'), '貿易の拡大', false, 2),
((SELECT id FROM exercises WHERE question LIKE '講演で話者が言っています%'), '人口の増加', false, 3),

((SELECT id FROM exercises WHERE question LIKE '会議で司会者が話しています%'), '来年度の予算', true, 0),
((SELECT id FROM exercises WHERE question LIKE '会議で司会者が話しています%'), '新製品の開発', false, 1),
((SELECT id FROM exercises WHERE question LIKE '会議で司会者が話しています%'), '社員の採用', false, 2),
((SELECT id FROM exercises WHERE question LIKE '会議で司会者が話しています%'), '会社の移転', false, 3),

((SELECT id FROM exercises WHERE question LIKE '評論家がテレビで話しています%'), '技術革新が雇用に与える影響', true, 0),
((SELECT id FROM exercises WHERE question LIKE '評論家がテレビで話しています%'), '教育制度の問題点', false, 1),
((SELECT id FROM exercises WHERE question LIKE '評論家がテレビで話しています%'), '環境保護の重要性', false, 2),
((SELECT id FROM exercises WHERE question LIKE '評論家がテレビで話しています%'), '経済成長の鈍化', false, 3),

((SELECT id FROM exercises WHERE question LIKE '討論会で参加者が発言しています%'), '規制緩和は市場活性化に直結しない', true, 0),
((SELECT id FROM exercises WHERE question LIKE '討論会で参加者が発言しています%'), '規制はすべて撤廃すべきだ', false, 1),
((SELECT id FROM exercises WHERE question LIKE '討論会で参加者が発言しています%'), '市場には規制が不要だ', false, 2),
((SELECT id FROM exercises WHERE question LIKE '討論会で参加者が発言しています%'), '規制強化が必要だ', false, 3);
