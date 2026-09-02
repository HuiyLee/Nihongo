-- N2 content batch: vocabulary, kanji, grammar, reading passages + comprehension
-- exercises, listening exercises (with real generated audio), and a full N2 mock
-- exam assembled from a subset of the exercises created here. Original content
-- authored for this app (not copied from any copyrighted textbook or official
-- past JLPT paper) - matches the N2 syllabus scope and the thematic-list /
-- pattern-explanation style those books use.
--
-- Audio files are bundled as static resources under
-- backend/src/main/resources/static/audio/n2/ and served publicly at
-- https://nihongo-backend-uh7f.onrender.com/audio/n2/... (added to SecurityConfig's PUBLIC_ENDPOINTS separately)
-- since the <audio> tag can't send an Authorization header. Voice is an offline
-- formant synthesizer (espeak-ng) - understandable but not a natural human voice.

-- =========================================================================
-- N2 VOCABULARY
-- =========================================================================

INSERT INTO vocabularies (level_id, word, kanji, hiragana, katakana, romaji, meaning, part_of_speech, example, example_meaning, audio_url) VALUES
((SELECT id FROM levels WHERE code = 'N2'), '契約', '契約', 'けいやく', NULL, 'keiyaku', 'contract', 'noun', '契約を結びます。', 'We conclude a contract.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/01_keiyaku.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '交渉', '交渉', 'こうしょう', NULL, 'koushou', 'negotiation', 'noun', '取引先と交渉します。', 'We negotiate with the client.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/02_koushou.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '責任', '責任', 'せきにん', NULL, 'sekinin', 'responsibility', 'noun', '責任を持って仕事をします。', 'I do my work responsibly.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/03_sekinin.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '効率', '効率', 'こうりつ', NULL, 'kouritsu', 'efficiency', 'noun', '効率を上げましょう。', 'Let''s improve efficiency.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/04_kouritsu.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '発展', '発展', 'はってん', NULL, 'hatten', 'development', 'noun', '経済が発展します。', 'The economy develops.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/05_hatten.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '維持', '維持', 'いじ', NULL, 'iji', 'maintenance', 'noun', '健康を維持します。', 'I maintain my health.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/06_iji.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '改善', '改善', 'かいぜん', NULL, 'kaizen', 'improvement', 'noun', 'サービスを改善します。', 'We improve the service.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/07_kaizen.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '割合', '割合', 'わりあい', NULL, 'wariai', 'ratio / proportion', 'noun', '女性の割合が増えました。', 'The proportion of women increased.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/08_wariai.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '傾く', '傾く', 'かたむく', NULL, 'katamuku', 'to lean / incline', 'verb', '太陽が傾きます。', 'The sun leans (sets).', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/09_katamuku.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '悩む', '悩む', 'なやむ', NULL, 'nayamu', 'to worry / be troubled', 'verb', '進路について悩んでいます。', 'I''m worried about my future path.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/10_nayamu.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '支える', '支える', 'ささえる', NULL, 'sasaeru', 'to support', 'verb', '家族を支えます。', 'I support my family.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/11_sasaeru.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '訴える', '訴える', 'うったえる', NULL, 'uttaeru', 'to appeal / sue', 'verb', '権利を訴えます。', 'I appeal for my rights.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/12_uttaeru.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '逆らう', '逆らう', 'さからう', NULL, 'sakarau', 'to go against', 'verb', '親に逆らいます。', 'I go against my parents.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/13_sakarau.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '焦る', '焦る', 'あせる', NULL, 'aseru', 'to be impatient / hasty', 'verb', '時間がなくて焦ります。', 'I''m anxious because there''s no time.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/14_aseru.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '抱える', '抱える', 'かかえる', NULL, 'kakaeru', 'to hold / carry (a problem)', 'verb', '問題を抱えています。', 'I''m dealing with a problem.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/15_kakaeru.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '快適', '快適', 'かいてき', NULL, 'kaiteki', 'comfortable', 'adjective', '快適な部屋です。', 'It''s a comfortable room.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/16_kaiteki.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '貴重', '貴重', 'きちょう', NULL, 'kichou', 'precious / valuable', 'adjective', '貴重な経験でした。', 'It was a precious experience.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/17_kichou.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '単純', '単純', 'たんじゅん', NULL, 'tanjun', 'simple', 'adjective', '単純な問題ではありません。', 'It''s not a simple problem.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/18_tanjun.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '柔軟', '柔軟', 'じゅうなん', NULL, 'juunan', 'flexible', 'adjective', '柔軟に対応します。', 'We respond flexibly.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/19_juunan.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '鈍い', '鈍い', 'にぶい', NULL, 'nibui', 'dull / slow', 'adjective', '反応が鈍いです。', 'The reaction is dull/slow.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/20_nibui.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '徐々に', '徐々に', 'じょじょに', NULL, 'jojoni', 'gradually', 'adverb', '徐々に慣れてきました。', 'I''ve gradually gotten used to it.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/21_jojoni.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '一斉に', '一斉に', 'いっせいに', NULL, 'issei ni', 'all at once', 'adverb', 'みんな一斉に立ちました。', 'Everyone stood up all at once.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/22_isseini.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '相互', '相互', 'そうご', NULL, 'sougo', 'mutual', 'noun', '相互に理解します。', 'We understand each other mutually.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/23_sougo.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '恐れ', '恐れ', 'おそれ', NULL, 'osore', 'fear', 'noun', '失敗を恐れないでください。', 'Please don''t fear failure.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/24_osore.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '割引', '割引', 'わりびき', NULL, 'waribiki', 'discount', 'noun', '割引があります。', 'There is a discount.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/25_waribiki.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '需要', '需要', 'じゅよう', NULL, 'juyou', 'demand', 'noun', '需要が高まっています。', 'Demand is rising.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/26_juyou.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '供給', '供給', 'きょうきゅう', NULL, 'kyoukyuu', 'supply', 'noun', '需要と供給のバランスです。', 'It''s the balance of supply and demand.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/27_kyoukyuu.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '見通し', '見通し', 'みとおし', NULL, 'mitooshi', 'outlook / prospect', 'noun', '将来の見通しが明るいです。', 'The future outlook is bright.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/28_mitooshi.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '前提', '前提', 'ぜんてい', NULL, 'zentei', 'premise / prerequisite', 'noun', 'それを前提として話します。', 'I''ll talk with that as a premise.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/29_zentei.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), '主張', '主張', 'しゅちょう', NULL, 'shuchou', 'assertion / claim', 'noun', '自分の意見を主張します。', 'I assert my own opinion.', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/vocab/30_shuchou.mp3');

-- =========================================================================
-- N2 KANJI
-- =========================================================================

INSERT INTO kanjis (level_id, character, meaning, onyomi, kunyomi, stroke_count, example, example_meaning) VALUES
((SELECT id FROM levels WHERE code = 'N2'), '契', 'pledge / promise', 'ケイ', NULL, 9, '契約', 'contract'),
((SELECT id FROM levels WHERE code = 'N2'), '約', 'promise / approximately', 'ヤク', NULL, 9, '契約', 'contract'),
((SELECT id FROM levels WHERE code = 'N2'), '効', 'effect', 'コウ', 'き(く)', 8, '効果', 'effect'),
((SELECT id FROM levels WHERE code = 'N2'), '率', 'rate / ratio', 'リツ、ソツ', NULL, 11, '効率', 'efficiency'),
((SELECT id FROM levels WHERE code = 'N2'), '維', 'fiber / hold', 'イ', NULL, 14, '維持', 'maintenance'),
((SELECT id FROM levels WHERE code = 'N2'), '善', 'good / improve', 'ゼン', 'よ(い)', 12, '改善', 'improvement'),
((SELECT id FROM levels WHERE code = 'N2'), '需', 'demand', 'ジュ', NULL, 14, '需要', 'demand'),
((SELECT id FROM levels WHERE code = 'N2'), '供', 'supply / offer', 'キョウ', 'そな(える)', 8, '供給', 'supply'),
((SELECT id FROM levels WHERE code = 'N2'), '訴', 'appeal / sue', 'ソ', 'うった(える)', 12, '訴える', 'to appeal'),
((SELECT id FROM levels WHERE code = 'N2'), '抱', 'hold / embrace', 'ホウ', 'だ(く)、かか(える)', 8, '抱える', 'to carry (a problem)'),
((SELECT id FROM levels WHERE code = 'N2'), '柔', 'soft / flexible', 'ジュウ、ニュウ', 'やわ(らかい)', 9, '柔軟', 'flexible'),
((SELECT id FROM levels WHERE code = 'N2'), '軟', 'soft / pliable', 'ナン', 'やわ(らかい)', 11, '柔軟', 'flexible');

-- =========================================================================
-- N2 GRAMMAR
-- =========================================================================

INSERT INTO grammars (level_id, pattern, meaning, formation, explanation, example, example_meaning) VALUES
((SELECT id FROM levels WHERE code = 'N2'), '〜に応じて', 'according to / in response to ~', 'Noun + に応じて', 'Something changes according to the situation described by the noun.', '状況に応じて対応します。', 'We respond according to the situation.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜反面', 'on the other hand ~', 'Plain form + 反面', 'Describes two contrasting aspects of the same thing.', 'この仕事は大変な反面、やりがいがあります。', 'This job is tough, but on the other hand it''s rewarding.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜おかげで', 'thanks to ~', 'Noun / Plain form + おかげで', 'A positive result attributed to something or someone.', '先生のおかげで合格しました。', 'Thanks to my teacher, I passed.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜せいで', 'because of ~ (negative)', 'Noun / Plain form + せいで', 'A negative result blamed on something.', '雨のせいで遅れました。', 'I was late because of the rain.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜たびに', 'every time ~', 'Verb (dictionary form) / Noun + の + たびに', 'Something happens every time the stated action or event occurs.', '彼に会うたびに元気をもらいます。', 'Every time I meet him, I get energized.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜に伴って', 'along with / as ~', 'Noun / Verb (dictionary form) + に伴って', 'A change that accompanies another change.', '人口の増加に伴って、住宅が不足しています。', 'Along with population growth, housing is in short supply.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜上で', 'in the process of ~ / after ~', 'Verb (た form) / Noun + の + 上で', 'Something done as a necessary step before another.', 'よく考えた上で決めます。', 'I''ll decide after thinking it over carefully.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜つつ', 'while ~ (formal)', 'Verb (ます stem) + つつ', 'A formal equivalent of ながら, often implying a contradiction.', '悪いと知りつつ、嘘をついた。', 'Knowing it was wrong, I still lied.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜末に', 'after ~, finally', 'Verb (た form) / Noun + の + 末に', 'The final result reached after a long process.', '長い議論の末に、結論が出ました。', 'After a long discussion, a conclusion was reached.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜に限らず', 'not limited to ~', 'Noun + に限らず', 'Extends beyond just the stated noun.', '学生に限らず、誰でも参加できます。', 'Not just students, anyone can participate.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜っこない', 'there''s no way ~ (casual)', 'Verb (ます stem) + っこない', 'A strong casual denial of possibility.', 'そんなに簡単にできっこない。', 'There''s no way it can be done that easily.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜きり', 'only / ever since ~', 'Noun / Verb (た form) + きり', 'Limits to only one instance, or means "ever since".', '一度会ったきり、連絡がありません。', 'I haven''t heard from them since we met once.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜てはじめて', 'only after ~ (do you realize)', 'Verb (て form) + はじめて', 'A realization that only comes after experiencing something.', '病気になってはじめて、健康の大切さがわかりました。', 'Only after getting sick did I realize the importance of health.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜にしたら／にすれば', 'from the standpoint of ~', 'Noun + にしたら／にすれば', 'Considering something from a particular person''s perspective.', '子供にしたら、それは難しいでしょう。', 'From a child''s perspective, that would be difficult.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜どころか', 'far from ~, not only not...', 'Noun / Plain form + どころか', 'A strong contrast that negates an expectation.', '忙しいどころか、暇で困っています。', 'Far from busy, I''m troubled by having too much free time.');

-- =========================================================================
-- N2 READING PASSAGES + comprehension exercises
-- =========================================================================

INSERT INTO readings (level_id, title, content, translation, difficulty) VALUES
((SELECT id FROM levels WHERE code = 'N2'), '働き方の変化', '近年、日本では働き方が大きく変わりつつある。従来は一つの会社に定年まで勤めることが一般的だったが、最近では転職やフリーランスという働き方を選ぶ人が増えている。特に若い世代を中心に、自分の時間を大切にしながら働きたいという考え方が広がっている。企業側も、こうした変化に対応するため、在宅勤務やフレックスタイム制度を導入するところが増えてきた。', 'In recent years, ways of working in Japan have been changing significantly. In the past, it was common to work at a single company until retirement, but recently more people are choosing to change jobs or work as freelancers. Especially among the younger generation, the idea of wanting to work while valuing one''s own time is spreading. To respond to these changes, more companies have started introducing remote work and flextime systems.', 'MEDIUM'),
((SELECT id FROM levels WHERE code = 'N2'), '環境問題への取り組み', '地球温暖化をはじめとする環境問題は、世界共通の課題となっている。日本でも、プラスチックごみを減らすための取り組みが各地で進められている。例えば、スーパーではレジ袋が有料化され、多くの人がマイバッグを持参するようになった。また、企業の中には、環境に配慮した製品開発に力を入れるところも増えている。一人一人の小さな行動が、大きな変化につながると考えられている。', 'Environmental issues, starting with global warming, have become a shared challenge worldwide. In Japan too, efforts to reduce plastic waste are underway in various places. For example, supermarkets now charge for plastic bags, and many people have started bringing their own bags. In addition, more companies are putting effort into developing environmentally friendly products. It is believed that each person''s small actions can lead to major change.', 'MEDIUM'),
((SELECT id FROM levels WHERE code = 'N2'), '高齢化社会と地域のつながり', '日本は世界でも有数の高齢化社会である。高齢者が安心して暮らせる地域を作るために、様々な取り組みが行われている。その一つが、地域の住民同士が助け合う仕組みづくりである。例えば、買い物や通院が難しい高齢者のために、近所の人が付き添うボランティア活動が広がっている。行政だけに頼るのではなく、地域全体で支え合うことが、これからますます重要になるだろう。', 'Japan is one of the world''s leading aging societies. Various efforts are being made to create communities where elderly people can live with peace of mind. One of them is building a system where local residents help each other. For example, volunteer activities where neighbors accompany elderly people who have difficulty shopping or visiting the hospital are spreading. Rather than relying only on the government, mutual support across the whole community will likely become increasingly important.', 'HARD');

INSERT INTO exercises (level_id, type, question, explanation, difficulty, reading_id) VALUES
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', 'この文章によると、以前の日本では働き方はどうでしたか。', NULL, 'MEDIUM', (SELECT id FROM readings WHERE title = '働き方の変化')),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', '企業はどのように変化に対応していますか。', NULL, 'MEDIUM', (SELECT id FROM readings WHERE title = '働き方の変化')),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', 'スーパーでは、プラスチックごみを減らすために何をしましたか。', NULL, 'MEDIUM', (SELECT id FROM readings WHERE title = '環境問題への取り組み')),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', 'この文章の内容と合っているのはどれですか。', NULL, 'MEDIUM', (SELECT id FROM readings WHERE title = '環境問題への取り組み')),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', 'この文章で紹介されているボランティア活動とは何ですか。', NULL, 'HARD', (SELECT id FROM readings WHERE title = '高齢化社会と地域のつながり')),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', '筆者は何が重要になると考えていますか。', NULL, 'HARD', (SELECT id FROM readings WHERE title = '高齢化社会と地域のつながり'));

INSERT INTO exercise_answers (exercise_id, answer_text, is_correct, order_index) VALUES
((SELECT id FROM exercises WHERE question = 'この文章によると、以前の日本では働き方はどうでしたか。'), '一つの会社で定年まで働くのが一般的だった', true, 0),
((SELECT id FROM exercises WHERE question = 'この文章によると、以前の日本では働き方はどうでしたか。'), 'みんなフリーランスで働いていた', false, 1),
((SELECT id FROM exercises WHERE question = 'この文章によると、以前の日本では働き方はどうでしたか。'), '在宅勤務が一般的だった', false, 2),
((SELECT id FROM exercises WHERE question = 'この文章によると、以前の日本では働き方はどうでしたか。'), '転職する人が多かった', false, 3),
((SELECT id FROM exercises WHERE question = '企業はどのように変化に対応していますか。'), '給料を上げている', false, 0),
((SELECT id FROM exercises WHERE question = '企業はどのように変化に対応していますか。'), '在宅勤務やフレックスタイム制度を導入している', true, 1),
((SELECT id FROM exercises WHERE question = '企業はどのように変化に対応していますか。'), '採用をやめている', false, 2),
((SELECT id FROM exercises WHERE question = '企業はどのように変化に対応していますか。'), '会社を移転している', false, 3),
((SELECT id FROM exercises WHERE question = 'スーパーでは、プラスチックごみを減らすために何をしましたか。'), 'レジ袋を無料にした', false, 0),
((SELECT id FROM exercises WHERE question = 'スーパーでは、プラスチックごみを減らすために何をしましたか。'), 'レジ袋を有料化した', true, 1),
((SELECT id FROM exercises WHERE question = 'スーパーでは、プラスチックごみを減らすために何をしましたか。'), 'レジ袋をなくした', false, 2),
((SELECT id FROM exercises WHERE question = 'スーパーでは、プラスチックごみを減らすために何をしましたか。'), '紙袋を配った', false, 3),
((SELECT id FROM exercises WHERE question = 'この文章の内容と合っているのはどれですか。'), '環境問題は日本だけの課題だ', false, 0),
((SELECT id FROM exercises WHERE question = 'この文章の内容と合っているのはどれですか。'), '一人一人の行動は変化につながらない', false, 1),
((SELECT id FROM exercises WHERE question = 'この文章の内容と合っているのはどれですか。'), '企業は環境に配慮した製品開発を進めている', true, 2),
((SELECT id FROM exercises WHERE question = 'この文章の内容と合っているのはどれですか。'), 'マイバッグを使う人は減っている', false, 3),
((SELECT id FROM exercises WHERE question = 'この文章で紹介されているボランティア活動とは何ですか。'), '高齢者の買い物や通院に付き添う', true, 0),
((SELECT id FROM exercises WHERE question = 'この文章で紹介されているボランティア活動とは何ですか。'), '高齢者に料理を教える', false, 1),
((SELECT id FROM exercises WHERE question = 'この文章で紹介されているボランティア活動とは何ですか。'), '高齢者を雇用する', false, 2),
((SELECT id FROM exercises WHERE question = 'この文章で紹介されているボランティア活動とは何ですか。'), '高齢者向けの住宅を建てる', false, 3),
((SELECT id FROM exercises WHERE question = '筆者は何が重要になると考えていますか。'), '行政だけに頼ること', false, 0),
((SELECT id FROM exercises WHERE question = '筆者は何が重要になると考えていますか。'), '地域全体で支え合うこと', true, 1),
((SELECT id FROM exercises WHERE question = '筆者は何が重要になると考えていますか。'), '高齢者が働くこと', false, 2),
((SELECT id FROM exercises WHERE question = '筆者は何が重要になると考えていますか。'), '都市に引っ越すこと', false, 3);

-- =========================================================================
-- N2 LISTENING EXERCISES (new, with real generated audio)
-- =========================================================================

INSERT INTO exercises (level_id, type, question, explanation, difficulty, audio_url) VALUES
((SELECT id FROM levels WHERE code = 'N2'), 'LISTENING', '会社で女の人と男の人が話しています。女:「来週のプレゼン、資料はもうできましたか。」男:「はい、大体できましたが、グラフの部分がまだです。」女:「では、明日までに仕上げてもらえますか。」男:「わかりました。」 質問:男の人はこれから何をしますか。', '男の人は「わかりました」とグラフの部分を仕上げることを了承しています。', 'MEDIUM', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/listening/01_presentation.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), 'LISTENING', '大学で先生が話しています。「レポートの締め切りは来週の金曜日です。参考文献のリストを必ずつけてください。」 質問:先生は何について注意していますか。', '「参考文献のリストを必ずつけてください」と注意しています。', 'MEDIUM', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/listening/02_report.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), 'LISTENING', 'ニュースです。「今月から、市内のバス料金が一部変更されます。詳しくは市のホームページをご覧ください。」 質問:このニュースは何について伝えていますか。', '「バス料金が一部変更されます」と伝えています。', 'MEDIUM', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/listening/03_bus_fare.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), 'LISTENING', '会議で部長が話しています。「今回のプロジェクトは予算が限られているので、無駄な出費はできるだけ抑えてください。」 質問:部長は何を求めていますか。', '「無駄な出費はできるだけ抑えてください」と求めています。', 'HARD', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/listening/04_budget.mp3'),
((SELECT id FROM levels WHERE code = 'N2'), 'LISTENING', '電話で女の人が話しています。「申し訳ございませんが、ご注文いただいた商品は在庫切れのため、発送が来週になります。」 質問:何が来週になりますか。', '「発送が来週になります」と言っています。', 'MEDIUM', 'https://nihongo-backend-uh7f.onrender.com/audio/n2/listening/05_out_of_stock.mp3');

INSERT INTO exercise_answers (exercise_id, answer_text, is_correct, order_index) VALUES
((SELECT id FROM exercises WHERE question = '会社で女の人と男の人が話しています。女:「来週のプレゼン、資料はもうできましたか。」男:「はい、大体できましたが、グラフの部分がまだです。」女:「では、明日までに仕上げてもらえますか。」男:「わかりました。」 質問:男の人はこれから何をしますか。'), 'プレゼンをする', false, 0),
((SELECT id FROM exercises WHERE question = '会社で女の人と男の人が話しています。女:「来週のプレゼン、資料はもうできましたか。」男:「はい、大体できましたが、グラフの部分がまだです。」女:「では、明日までに仕上げてもらえますか。」男:「わかりました。」 質問:男の人はこれから何をしますか。'), '資料のグラフを仕上げる', true, 1),
((SELECT id FROM exercises WHERE question = '会社で女の人と男の人が話しています。女:「来週のプレゼン、資料はもうできましたか。」男:「はい、大体できましたが、グラフの部分がまだです。」女:「では、明日までに仕上げてもらえますか。」男:「わかりました。」 質問:男の人はこれから何をしますか。'), '会議に出席する', false, 2),
((SELECT id FROM exercises WHERE question = '会社で女の人と男の人が話しています。女:「来週のプレゼン、資料はもうできましたか。」男:「はい、大体できましたが、グラフの部分がまだです。」女:「では、明日までに仕上げてもらえますか。」男:「わかりました。」 質問:男の人はこれから何をしますか。'), '資料を印刷する', false, 3),
((SELECT id FROM exercises WHERE question = '大学で先生が話しています。「レポートの締め切りは来週の金曜日です。参考文献のリストを必ずつけてください。」 質問:先生は何について注意していますか。'), 'レポートの内容', false, 0),
((SELECT id FROM exercises WHERE question = '大学で先生が話しています。「レポートの締め切りは来週の金曜日です。参考文献のリストを必ずつけてください。」 質問:先生は何について注意していますか。'), '参考文献のリストをつけること', true, 1),
((SELECT id FROM exercises WHERE question = '大学で先生が話しています。「レポートの締め切りは来週の金曜日です。参考文献のリストを必ずつけてください。」 質問:先生は何について注意していますか。'), '発表の順番', false, 2),
((SELECT id FROM exercises WHERE question = '大学で先生が話しています。「レポートの締め切りは来週の金曜日です。参考文献のリストを必ずつけてください。」 質問:先生は何について注意していますか。'), '授業の出席', false, 3),
((SELECT id FROM exercises WHERE question = 'ニュースです。「今月から、市内のバス料金が一部変更されます。詳しくは市のホームページをご覧ください。」 質問:このニュースは何について伝えていますか。'), 'バス料金の変更', true, 0),
((SELECT id FROM exercises WHERE question = 'ニュースです。「今月から、市内のバス料金が一部変更されます。詳しくは市のホームページをご覧ください。」 質問:このニュースは何について伝えていますか。'), '電車の時刻表', false, 1),
((SELECT id FROM exercises WHERE question = 'ニュースです。「今月から、市内のバス料金が一部変更されます。詳しくは市のホームページをご覧ください。」 質問:このニュースは何について伝えていますか。'), '道路工事の予定', false, 2),
((SELECT id FROM exercises WHERE question = 'ニュースです。「今月から、市内のバス料金が一部変更されます。詳しくは市のホームページをご覧ください。」 質問:このニュースは何について伝えていますか。'), '市役所の移転', false, 3),
((SELECT id FROM exercises WHERE question = '会議で部長が話しています。「今回のプロジェクトは予算が限られているので、無駄な出費はできるだけ抑えてください。」 質問:部長は何を求めていますか。'), '予算を増やすこと', false, 0),
((SELECT id FROM exercises WHERE question = '会議で部長が話しています。「今回のプロジェクトは予算が限られているので、無駄な出費はできるだけ抑えてください。」 質問:部長は何を求めていますか。'), '無駄な出費を抑えること', true, 1),
((SELECT id FROM exercises WHERE question = '会議で部長が話しています。「今回のプロジェクトは予算が限られているので、無駄な出費はできるだけ抑えてください。」 質問:部長は何を求めていますか。'), 'プロジェクトを中止すること', false, 2),
((SELECT id FROM exercises WHERE question = '会議で部長が話しています。「今回のプロジェクトは予算が限られているので、無駄な出費はできるだけ抑えてください。」 質問:部長は何を求めていますか。'), '新しい社員を雇うこと', false, 3),
((SELECT id FROM exercises WHERE question = '電話で女の人が話しています。「申し訳ございませんが、ご注文いただいた商品は在庫切れのため、発送が来週になります。」 質問:何が来週になりますか。'), '注文', false, 0),
((SELECT id FROM exercises WHERE question = '電話で女の人が話しています。「申し訳ございませんが、ご注文いただいた商品は在庫切れのため、発送が来週になります。」 質問:何が来週になりますか。'), '商品の発送', true, 1),
((SELECT id FROM exercises WHERE question = '電話で女の人が話しています。「申し訳ございませんが、ご注文いただいた商品は在庫切れのため、発送が来週になります。」 質問:何が来週になりますか。'), '在庫の確認', false, 2),
((SELECT id FROM exercises WHERE question = '電話で女の人が話しています。「申し訳ございませんが、ご注文いただいた商品は在庫切れのため、発送が来週になります。」 質問:何が来週になりますか。'), '電話', false, 3);

-- Attach real audio to the two N2 listening exercises seeded in V7 (they had no audio yet)
UPDATE exercises SET audio_url = 'https://nihongo-backend-uh7f.onrender.com/audio/n2/listening/06_education_investment.mp3' WHERE question LIKE '講演で話者が言っています%';
UPDATE exercises SET audio_url = 'https://nihongo-backend-uh7f.onrender.com/audio/n2/listening/07_budget_meeting.mp3' WHERE question LIKE '会議で司会者が話しています%';

-- =========================================================================
-- N2 MOCK EXAM - language-knowledge (vocab/grammar) exercises
-- =========================================================================

INSERT INTO exercises (level_id, type, question, explanation, difficulty) VALUES
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', '「維持」の読み方はどれですか。', '「維持」は「いじ」と読みます。', 'EASY'),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', '「柔軟」の読み方はどれですか。', '「柔軟」は「じゅうなん」と読みます。', 'EASY'),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', '健康を＿＿＿ために、毎日運動しています。', '「健康を維持する」で「健康な状態を保つ」という意味になります。', 'MEDIUM'),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', 'この問題は＿＿＿ではありません。すぐに解決できます。', '「単純」は「簡単で複雑でない」という意味です。', 'MEDIUM'),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', '彼は忙しい＿＿＿、いつも笑顔で仕事をしている。', '「〜にもかかわらず」は「〜のに」という逆接の意味です。', 'MEDIUM'),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', '先生の＿＿＿、試験に合格しました。', '「〜おかげで」はよい結果の理由を表します。', 'MEDIUM'),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', '何度も練習した＿＿＿、ようやく上手になった。', '「〜末に」は長い過程の後の結果を表します。', 'HARD'),
((SELECT id FROM levels WHERE code = 'N2'), 'MULTIPLE_CHOICE', '会議で自分の意見を強く＿＿＿。', '「主張する」は「自分の考えを強く言う」という意味です。', 'MEDIUM');

INSERT INTO exercise_answers (exercise_id, answer_text, is_correct, order_index) VALUES
((SELECT id FROM exercises WHERE question = '「維持」の読み方はどれですか。'), 'いじ', true, 0),
((SELECT id FROM exercises WHERE question = '「維持」の読み方はどれですか。'), 'いも', false, 1),
((SELECT id FROM exercises WHERE question = '「維持」の読み方はどれですか。'), 'いち', false, 2),
((SELECT id FROM exercises WHERE question = '「維持」の読み方はどれですか。'), 'いす', false, 3),
((SELECT id FROM exercises WHERE question = '「柔軟」の読み方はどれですか。'), 'じゅうなん', true, 0),
((SELECT id FROM exercises WHERE question = '「柔軟」の読み方はどれですか。'), 'にゅうなん', false, 1),
((SELECT id FROM exercises WHERE question = '「柔軟」の読み方はどれですか。'), 'なんじゅう', false, 2),
((SELECT id FROM exercises WHERE question = '「柔軟」の読み方はどれですか。'), 'しなん', false, 3),
((SELECT id FROM exercises WHERE question = '健康を＿＿＿ために、毎日運動しています。'), '維持する', true, 0),
((SELECT id FROM exercises WHERE question = '健康を＿＿＿ために、毎日運動しています。'), '訴える', false, 1),
((SELECT id FROM exercises WHERE question = '健康を＿＿＿ために、毎日運動しています。'), '抱える', false, 2),
((SELECT id FROM exercises WHERE question = '健康を＿＿＿ために、毎日運動しています。'), '逆らう', false, 3),
((SELECT id FROM exercises WHERE question = 'この問題は＿＿＿ではありません。すぐに解決できます。'), '単純', true, 0),
((SELECT id FROM exercises WHERE question = 'この問題は＿＿＿ではありません。すぐに解決できます。'), '柔軟', false, 1),
((SELECT id FROM exercises WHERE question = 'この問題は＿＿＿ではありません。すぐに解決できます。'), '貴重', false, 2),
((SELECT id FROM exercises WHERE question = 'この問題は＿＿＿ではありません。すぐに解決できます。'), '快適', false, 3),
((SELECT id FROM exercises WHERE question = '彼は忙しい＿＿＿、いつも笑顔で仕事をしている。'), 'にもかかわらず', true, 0),
((SELECT id FROM exercises WHERE question = '彼は忙しい＿＿＿、いつも笑顔で仕事をしている。'), 'たびに', false, 1),
((SELECT id FROM exercises WHERE question = '彼は忙しい＿＿＿、いつも笑顔で仕事をしている。'), 'どころか', false, 2),
((SELECT id FROM exercises WHERE question = '彼は忙しい＿＿＿、いつも笑顔で仕事をしている。'), 'っこない', false, 3),
((SELECT id FROM exercises WHERE question = '先生の＿＿＿、試験に合格しました。'), 'おかげで', true, 0),
((SELECT id FROM exercises WHERE question = '先生の＿＿＿、試験に合格しました。'), 'せいで', false, 1),
((SELECT id FROM exercises WHERE question = '先生の＿＿＿、試験に合格しました。'), '反面', false, 2),
((SELECT id FROM exercises WHERE question = '先生の＿＿＿、試験に合格しました。'), '末に', false, 3),
((SELECT id FROM exercises WHERE question = '何度も練習した＿＿＿、ようやく上手になった。'), '末に', true, 0),
((SELECT id FROM exercises WHERE question = '何度も練習した＿＿＿、ようやく上手になった。'), 'たびに', false, 1),
((SELECT id FROM exercises WHERE question = '何度も練習した＿＿＿、ようやく上手になった。'), 'つつ', false, 2),
((SELECT id FROM exercises WHERE question = '何度も練習した＿＿＿、ようやく上手になった。'), 'きり', false, 3),
((SELECT id FROM exercises WHERE question = '会議で自分の意見を強く＿＿＿。'), '主張しました', true, 0),
((SELECT id FROM exercises WHERE question = '会議で自分の意見を強く＿＿＿。'), '支えました', false, 1),
((SELECT id FROM exercises WHERE question = '会議で自分の意見を強く＿＿＿。'), '焦りました', false, 2),
((SELECT id FROM exercises WHERE question = '会議で自分の意見を強く＿＿＿。'), '悩みました', false, 3);

-- =========================================================================
-- N2 MOCK EXAM - assembled from the 8 language-knowledge questions above,
-- the 6 reading-comprehension questions, and 7 listening questions (5 new +
-- the 2 existing N2 ones that just got real audio attached).
-- =========================================================================

INSERT INTO exams (level_id, title, description, duration_minutes, total_questions, status) VALUES
((SELECT id FROM levels WHERE code = 'N2'), 'JLPT N2 模擬試験 第1回', '文字・語彙、文法、読解、聴解を組み合わせたN2レベルの模擬試験です。オリジナル問題です。', 90, 21, 'PUBLISHED');

INSERT INTO exam_questions (exam_id, exercise_id, order_index) VALUES
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '「維持」の読み方はどれですか。'), 0),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '「柔軟」の読み方はどれですか。'), 1),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '健康を＿＿＿ために、毎日運動しています。'), 2),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = 'この問題は＿＿＿ではありません。すぐに解決できます。'), 3),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '彼は忙しい＿＿＿、いつも笑顔で仕事をしている。'), 4),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '先生の＿＿＿、試験に合格しました。'), 5),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '何度も練習した＿＿＿、ようやく上手になった。'), 6),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '会議で自分の意見を強く＿＿＿。'), 7),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = 'この文章によると、以前の日本では働き方はどうでしたか。'), 8),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '企業はどのように変化に対応していますか。'), 9),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = 'スーパーでは、プラスチックごみを減らすために何をしましたか。'), 10),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = 'この文章の内容と合っているのはどれですか。'), 11),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = 'この文章で紹介されているボランティア活動とは何ですか。'), 12),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '筆者は何が重要になると考えていますか。'), 13),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '会社で女の人と男の人が話しています。女:「来週のプレゼン、資料はもうできましたか。」男:「はい、大体できましたが、グラフの部分がまだです。」女:「では、明日までに仕上げてもらえますか。」男:「わかりました。」 質問:男の人はこれから何をしますか。'), 14),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '大学で先生が話しています。「レポートの締め切りは来週の金曜日です。参考文献のリストを必ずつけてください。」 質問:先生は何について注意していますか。'), 15),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = 'ニュースです。「今月から、市内のバス料金が一部変更されます。詳しくは市のホームページをご覧ください。」 質問:このニュースは何について伝えていますか。'), 16),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '会議で部長が話しています。「今回のプロジェクトは予算が限られているので、無駄な出費はできるだけ抑えてください。」 質問:部長は何を求めていますか。'), 17),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '電話で女の人が話しています。「申し訳ございませんが、ご注文いただいた商品は在庫切れのため、発送が来週になります。」 質問:何が来週になりますか。'), 18),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '講演で話者が言っています。「経済成長には教育への投資が不可欠です。」 質問:話者は何が重要だと言っていますか。'), 19),
((SELECT id FROM exams WHERE title = 'JLPT N2 模擬試験 第1回'), (SELECT id FROM exercises WHERE question = '会議で司会者が話しています。「本日の議題は来年度の予算についてです。まず、各部署から報告をお願いします。」 質問:この会議の議題は何ですか。'), 20);
