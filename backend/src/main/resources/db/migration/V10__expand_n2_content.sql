-- Second N2 content batch (requirements follow-up: "vẫn đang ít từ vựng, ngữ
-- pháp, kanji quá" - still too little vocab/grammar/kanji). Adds 50 more N2
-- vocabulary words, 25 more N2 kanji, and 20 more N2 grammar patterns, all
-- distinct from what V7 and V9 already seeded. Original content, matching
-- only the syllabus scope of reference books like Mimikara/Shinkanzen, not
-- copied from them. No audio (consistent with the V7 non-listening vocab rows).

-- =========================================================================
-- N2 VOCABULARY (batch 2)
-- =========================================================================

INSERT INTO vocabularies (level_id, word, kanji, hiragana, katakana, romaji, meaning, part_of_speech, example, example_meaning) VALUES
((SELECT id FROM levels WHERE code = 'N2'), '概念', '概念', 'がいねん', NULL, 'gainen', 'concept', 'noun', '新しい概念を学びます。', 'I learn a new concept.'),
((SELECT id FROM levels WHERE code = 'N2'), '要素', '要素', 'ようそ', NULL, 'youso', 'element / factor', 'noun', '成功の要素は何ですか。', 'What are the factors of success?'),
((SELECT id FROM levels WHERE code = 'N2'), '基準', '基準', 'きじゅん', NULL, 'kijun', 'standard / criterion', 'noun', '判断の基準を決めます。', 'We decide the criteria for judgment.'),
((SELECT id FROM levels WHERE code = 'N2'), '分野', '分野', 'ぶんや', NULL, 'bunya', 'field / domain', 'noun', '専門の分野を選びます。', 'I choose my specialized field.'),
((SELECT id FROM levels WHERE code = 'N2'), '手段', '手段', 'しゅだん', NULL, 'shudan', 'means / method', 'noun', '目的のために手段を選びます。', 'I choose the means for the purpose.'),
((SELECT id FROM levels WHERE code = 'N2'), '制限', '制限', 'せいげん', NULL, 'seigen', 'restriction / limit', 'noun', '時間に制限があります。', 'There is a time limit.'),
((SELECT id FROM levels WHERE code = 'N2'), '保証', '保証', 'ほしょう', NULL, 'hoshou', 'guarantee', 'noun', '品質を保証します。', 'We guarantee the quality.'),
((SELECT id FROM levels WHERE code = 'N2'), '負担', '負担', 'ふたん', NULL, 'futan', 'burden', 'noun', '経済的な負担が大きいです。', 'The financial burden is large.'),
((SELECT id FROM levels WHERE code = 'N2'), '対策', '対策', 'たいさく', NULL, 'taisaku', 'countermeasure', 'noun', '対策を立てます。', 'We come up with a countermeasure.'),
((SELECT id FROM levels WHERE code = 'N2'), '見解', '見解', 'けんかい', NULL, 'kenkai', 'view / opinion', 'noun', '専門家の見解を聞きます。', 'I listen to the expert''s view.'),
((SELECT id FROM levels WHERE code = 'N2'), '把握', '把握', 'はあく', NULL, 'haaku', 'grasp / comprehension', 'noun', '状況を把握します。', 'I grasp the situation.'),
((SELECT id FROM levels WHERE code = 'N2'), '普及', '普及', 'ふきゅう', NULL, 'fukyuu', 'spread / popularization', 'noun', 'スマートフォンが普及しました。', 'Smartphones have become widespread.'),
((SELECT id FROM levels WHERE code = 'N2'), '削減', '削減', 'さくげん', NULL, 'sakugen', 'reduction / cut', 'noun', 'コストを削減します。', 'We cut costs.'),
((SELECT id FROM levels WHERE code = 'N2'), '拡大', '拡大', 'かくだい', NULL, 'kakudai', 'expansion', 'noun', '事業を拡大します。', 'We expand the business.'),
((SELECT id FROM levels WHERE code = 'N2'), '縮小', '縮小', 'しゅくしょう', NULL, 'shukushou', 'reduction / shrinkage', 'noun', '規模を縮小します。', 'We shrink the scale.'),
((SELECT id FROM levels WHERE code = 'N2'), '抑制', '抑制', 'よくせい', NULL, 'yokusei', 'suppression / control', 'noun', '感情を抑制します。', 'I suppress my emotions.'),
((SELECT id FROM levels WHERE code = 'N2'), '依存', '依存', 'いぞん', NULL, 'izon', 'dependence', 'noun', 'スマホに依存しています。', 'I''m dependent on my smartphone.'),
((SELECT id FROM levels WHERE code = 'N2'), '相違', '相違', 'そうい', NULL, 'soui', 'difference', 'noun', '意見の相違があります。', 'There is a difference of opinion.'),
((SELECT id FROM levels WHERE code = 'N2'), '該当', '該当', 'がいとう', NULL, 'gaitou', 'applicability', 'noun', '該当する項目を選びます。', 'Choose the applicable item.'),
((SELECT id FROM levels WHERE code = 'N2'), '検討', '検討', 'けんとう', NULL, 'kentou', 'consideration / examination', 'noun', '提案を検討します。', 'We consider the proposal.'),
((SELECT id FROM levels WHERE code = 'N2'), '適切', '適切', 'てきせつ', NULL, 'tekisetsu', 'appropriate', 'adjective', '適切な対応をします。', 'We respond appropriately.'),
((SELECT id FROM levels WHERE code = 'N2'), '微妙', '微妙', 'びみょう', NULL, 'bimyou', 'subtle / delicate', 'adjective', '微妙な違いがあります。', 'There is a subtle difference.'),
((SELECT id FROM levels WHERE code = 'N2'), '過剰', '過剰', 'かじょう', NULL, 'kajou', 'excessive', 'adjective', '過剰な反応です。', 'It''s an excessive reaction.'),
((SELECT id FROM levels WHERE code = 'N2'), '明確', '明確', 'めいかく', NULL, 'meikaku', 'clear / definite', 'adjective', '明確な答えが必要です。', 'A clear answer is needed.'),
((SELECT id FROM levels WHERE code = 'N2'), '一律', '一律', 'いちりつ', NULL, 'ichiritsu', 'uniform / across the board', 'adjective', '一律に値上げします。', 'We raise prices uniformly.'),
((SELECT id FROM levels WHERE code = 'N2'), '円滑', '円滑', 'えんかつ', NULL, 'enkatsu', 'smooth', 'adjective', '交渉が円滑に進みました。', 'The negotiation proceeded smoothly.'),
((SELECT id FROM levels WHERE code = 'N2'), '慎重', '慎重', 'しんちょう', NULL, 'shinchou', 'careful / cautious', 'adjective', '慎重に考えます。', 'I think carefully.'),
((SELECT id FROM levels WHERE code = 'N2'), '積極的', '積極的', 'せっきょくてき', NULL, 'sekkyokuteki', 'proactive / positive', 'adjective', '積極的に参加します。', 'I participate proactively.'),
((SELECT id FROM levels WHERE code = 'N2'), '消極的', '消極的', 'しょうきょくてき', NULL, 'shoukyokuteki', 'passive / negative', 'adjective', '消極的な態度です。', 'It''s a passive attitude.'),
((SELECT id FROM levels WHERE code = 'N2'), '不可欠', '不可欠', 'ふかけつ', NULL, 'fukaketsu', 'essential / indispensable', 'adjective', '水は生活に不可欠です。', 'Water is essential to life.'),
((SELECT id FROM levels WHERE code = 'N2'), '伴う', '伴う', 'ともなう', NULL, 'tomonau', 'to accompany / entail', 'verb', 'リスクを伴います。', 'It entails risk.'),
((SELECT id FROM levels WHERE code = 'N2'), '及ぶ', '及ぶ', 'およぶ', NULL, 'oyobu', 'to reach / extend to', 'verb', '影響が全国に及びます。', 'The effect extends nationwide.'),
((SELECT id FROM levels WHERE code = 'N2'), '巡る', '巡る', 'めぐる', NULL, 'meguru', 'to go around / concern', 'verb', 'その問題を巡って議論します。', 'We debate concerning that issue.'),
((SELECT id FROM levels WHERE code = 'N2'), '携わる', '携わる', 'たずさわる', NULL, 'tazusawaru', 'to be engaged in', 'verb', '開発に携わっています。', 'I''m engaged in the development.'),
((SELECT id FROM levels WHERE code = 'N2'), '心がける', '心がける', 'こころがける', NULL, 'kokorogakeru', 'to be mindful of', 'verb', '健康に心がけます。', 'I''m mindful of my health.'),
((SELECT id FROM levels WHERE code = 'N2'), '見込む', '見込む', 'みこむ', NULL, 'mikomu', 'to expect / anticipate', 'verb', '成長を見込んでいます。', 'We anticipate growth.'),
((SELECT id FROM levels WHERE code = 'N2'), '果たす', '果たす', 'はたす', NULL, 'hatasu', 'to fulfill / accomplish', 'verb', '役割を果たします。', 'I fulfill my role.'),
((SELECT id FROM levels WHERE code = 'N2'), '費やす', '費やす', 'ついやす', NULL, 'tsuiyasu', 'to spend / expend', 'verb', '時間を費やします。', 'I spend time on it.'),
((SELECT id FROM levels WHERE code = 'N2'), '図る', '図る', 'はかる', NULL, 'hakaru', 'to plan / aim for', 'verb', '効率化を図ります。', 'We aim for greater efficiency.'),
((SELECT id FROM levels WHERE code = 'N2'), '兼ねる', '兼ねる', 'かねる', NULL, 'kaneru', 'to combine / serve also as', 'verb', '趣味と実益を兼ねます。', 'It combines hobby and practical benefit.'),
((SELECT id FROM levels WHERE code = 'N2'), '阻む', '阻む', 'はばむ', NULL, 'habamu', 'to hinder / block', 'verb', '計画の実行を阻みます。', 'It hinders the execution of the plan.'),
((SELECT id FROM levels WHERE code = 'N2'), '妨げる', '妨げる', 'さまたげる', NULL, 'samatageru', 'to hinder / obstruct', 'verb', '発展を妨げます。', 'It hinders development.'),
((SELECT id FROM levels WHERE code = 'N2'), '導く', '導く', 'みちびく', NULL, 'michibiku', 'to guide / lead', 'verb', '成功へ導きます。', 'It leads to success.'),
((SELECT id FROM levels WHERE code = 'N2'), '見直す', '見直す', 'みなおす', NULL, 'minaosu', 'to reconsider / review', 'verb', '計画を見直します。', 'We review the plan.'),
((SELECT id FROM levels WHERE code = 'N2'), '補う', '補う', 'おぎなう', NULL, 'oginau', 'to make up for / supplement', 'verb', '不足を補います。', 'We make up for the shortage.'),
((SELECT id FROM levels WHERE code = 'N2'), '一概に', '一概に', 'いちがいに', NULL, 'ichigaini', 'unconditionally / categorically', 'adverb', '一概には言えません。', 'You can''t say categorically.'),
((SELECT id FROM levels WHERE code = 'N2'), '一層', '一層', 'いっそう', NULL, 'issou', 'all the more / still more', 'adverb', '一層努力します。', 'I''ll try all the more.'),
((SELECT id FROM levels WHERE code = 'N2'), '依然として', '依然として', 'いぜんとして', NULL, 'izentoshite', 'still / as before', 'adverb', '問題は依然として残っています。', 'The problem still remains.'),
((SELECT id FROM levels WHERE code = 'N2'), '果たして', '果たして', 'はたして', NULL, 'hatashite', 'indeed / really', 'adverb', '果たして成功するでしょうか。', 'Will it really succeed?'),
((SELECT id FROM levels WHERE code = 'N2'), 'とりわけ', 'とりわけ', 'とりわけ', NULL, 'toriwake', 'especially / particularly', 'adverb', '今年はとりわけ暑いです。', 'This year is especially hot.');

-- =========================================================================
-- N2 KANJI (batch 2)
-- =========================================================================

INSERT INTO kanjis (level_id, character, meaning, onyomi, kunyomi, stroke_count, example, example_meaning) VALUES
((SELECT id FROM levels WHERE code = 'N2'), '概', 'outline / approximate', 'ガイ', 'おおむ(ね)', 13, '概念', 'concept'),
((SELECT id FROM levels WHERE code = 'N2'), '素', 'element / plain', 'ソ、ス', NULL, 10, '要素', 'element'),
((SELECT id FROM levels WHERE code = 'N2'), '基', 'basis / foundation', 'キ', 'もと', 11, '基準', 'standard'),
((SELECT id FROM levels WHERE code = 'N2'), '野', 'field / plain', 'ヤ', 'の', 11, '分野', 'field'),
((SELECT id FROM levels WHERE code = 'N2'), '段', 'step / grade', 'ダン', NULL, 9, '手段', 'means'),
((SELECT id FROM levels WHERE code = 'N2'), '限', 'limit', 'ゲン', 'かぎ(る)', 9, '制限', 'restriction'),
((SELECT id FROM levels WHERE code = 'N2'), '証', 'proof / evidence', 'ショウ', NULL, 12, '保証', 'guarantee'),
((SELECT id FROM levels WHERE code = 'N2'), '担', 'carry / bear', 'タン', 'かつ(ぐ)、にな(う)', 8, '負担', 'burden'),
((SELECT id FROM levels WHERE code = 'N2'), '策', 'policy / scheme', 'サク', NULL, 12, '対策', 'countermeasure'),
((SELECT id FROM levels WHERE code = 'N2'), '解', 'solve / understand', 'カイ、ゲ', 'と(く)', 13, '見解', 'view'),
((SELECT id FROM levels WHERE code = 'N2'), '把', 'grasp / hold', 'ハ', NULL, 7, '把握', 'grasp'),
((SELECT id FROM levels WHERE code = 'N2'), '握', 'grip / grasp', 'アク', 'にぎ(る)', 12, '把握', 'grasp'),
((SELECT id FROM levels WHERE code = 'N2'), '及', 'reach / extend', 'キュウ', 'およ(ぶ)', 3, '及ぶ', 'to reach'),
((SELECT id FROM levels WHERE code = 'N2'), '普', 'universal / general', 'フ', NULL, 12, '普及', 'spread'),
((SELECT id FROM levels WHERE code = 'N2'), '削', 'cut / pare down', 'サク', 'けず(る)', 9, '削減', 'reduction'),
((SELECT id FROM levels WHERE code = 'N2'), '拡', 'expand / widen', 'カク', NULL, 8, '拡大', 'expansion'),
((SELECT id FROM levels WHERE code = 'N2'), '縮', 'shrink / contract', 'シュク', 'ちぢ(む)', 17, '縮小', 'shrinkage'),
((SELECT id FROM levels WHERE code = 'N2'), '抑', 'suppress / hold back', 'ヨク', 'おさ(える)', 7, '抑制', 'suppression'),
((SELECT id FROM levels WHERE code = 'N2'), '依', 'depend / rely', 'イ', NULL, 8, '依存', 'dependence'),
((SELECT id FROM levels WHERE code = 'N2'), '該', 'the said / corresponding', 'ガイ', NULL, 13, '該当', 'applicability'),
((SELECT id FROM levels WHERE code = 'N2'), '討', 'discuss / examine', 'トウ', 'う(つ)', 10, '検討', 'consideration'),
((SELECT id FROM levels WHERE code = 'N2'), '適', 'suitable / fit', 'テキ', NULL, 14, '適切', 'appropriate'),
((SELECT id FROM levels WHERE code = 'N2'), '微', 'slight / delicate', 'ビ', NULL, 13, '微妙', 'subtle'),
((SELECT id FROM levels WHERE code = 'N2'), '確', 'certain / sure', 'カク', 'たし(か)', 15, '明確', 'clear'),
((SELECT id FROM levels WHERE code = 'N2'), '慎', 'cautious / careful', 'シン', 'つつし(む)', 13, '慎重', 'careful');

-- =========================================================================
-- N2 GRAMMAR (batch 2)
-- =========================================================================

INSERT INTO grammars (level_id, pattern, meaning, formation, explanation, example, example_meaning) VALUES
((SELECT id FROM levels WHERE code = 'N2'), '〜ことか', 'how ~! (exclamation)', 'Plain form + ことか', 'An exclamatory expression of strong feeling.', '何度注意したことか。', 'How many times have I warned him!'),
((SELECT id FROM levels WHERE code = 'N2'), '〜ものだ', 'used to ~ / it''s natural that ~', 'Verb (plain, often past) + ものだ', 'Fond reminiscence about the past, or a statement of what''s naturally expected.', '昔はよくここで遊んだものだ。', 'I used to play here often in the past.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜わけにはいかない', 'cannot afford to ~', 'Verb (dictionary form) + わけにはいかない', 'Something can''t be done because of social, moral, or situational reasons.', '約束したから、休むわけにはいかない。', 'Since I promised, I can''t afford to take a day off.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜さえ〜ば', 'if only ~', 'Noun + さえ + Verb (ば form)', 'Emphasizes that the stated condition alone is sufficient.', '時間さえあれば、行きます。', 'If only I had time, I would go.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜ことなく', 'without ~ing', 'Verb (dictionary form) + ことなく', 'A formal way to say something happens without a certain action taking place.', '休むことなく働きました。', 'I worked without resting.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜に加えて', 'in addition to ~', 'Noun + に加えて', 'Adds something on top of what was already mentioned.', '給料に加えて、ボーナスも出ます。', 'In addition to salary, a bonus is also given.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜のもとで', 'under ~ (guidance / conditions)', 'Noun + のもとで', 'Indicates the influence, guidance, or conditions under which something happens.', '先生の指導のもとで研究します。', 'I do research under the teacher''s guidance.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜に先立って', 'prior to ~', 'Noun / Verb (dictionary form) + に先立って', 'Something happens before the main event, often as preparation.', '開会に先立って、挨拶があります。', 'There will be a greeting prior to the opening.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜からして', 'judging from ~', 'Noun + からして', 'Uses one obvious example as evidence for a broader judgment.', '話し方からして、外国人でしょう。', 'Judging from the way he speaks, he''s probably a foreigner.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜まい', 'will not / probably not ~', 'Verb (dictionary form) + まい', 'A literary way to express negative volition or negative conjecture.', '二度と失敗はするまい。', 'I won''t fail again.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜べきではない', 'should not ~', 'Verb (dictionary form) + べきではない', 'States that something is not the right thing to do.', '人を批判するべきではない。', 'You should not criticize people.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜てからでないと', 'unless ~ first', 'Verb (て form) + からでないと', 'The following action is impossible without first doing this one.', '確認してからでないと、始められません。', 'We can''t start unless we confirm first.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜にすぎない', 'merely ~ / nothing more than ~', 'Noun / Plain form + にすぎない', 'Downplays something as being only that much and no more.', 'それはうわさにすぎません。', 'That''s nothing more than a rumor.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜としたら', 'if we assume ~', 'Plain form + としたら', 'A hypothetical assumption used to reason about its consequence.', 'もし本当だとしたら、大変です。', 'If that''s really true, it''s a serious problem.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜だけあって', 'as one would expect from ~', 'Noun / Plain form + だけあって', 'A result that matches what you''d expect given the stated reason.', '経験者だけあって、上手です。', 'As one would expect from an experienced person, they''re skilled.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜に違いない', 'must be ~ / no doubt ~', 'Plain form + に違いない', 'A confident conjecture based on available evidence.', '彼は忙しいに違いない。', 'He must be busy.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜次第だ', 'depends on ~', 'Noun + 次第だ', 'The outcome is entirely determined by the stated noun.', '結果は努力次第です。', 'The result depends on effort.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜つつある', 'in the process of ~ing', 'Verb (ます stem) + つつある', 'A formal way to describe a gradual, ongoing change.', '状況は改善しつつあります。', 'The situation is in the process of improving.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜てまで', 'even to the extent of ~ing', 'Verb (て form) + まで', 'Emphasizes that going that far isn''t warranted or necessary.', '借金をしてまで買う必要はない。', 'There''s no need to buy it even to the extent of going into debt.'),
((SELECT id FROM levels WHERE code = 'N2'), '〜はもとより', 'not to mention ~ / needless to say ~', 'Noun + はもとより', 'The stated noun is obviously included, and something further is also true.', '平日はもとより、休日も働きます。', 'Not to mention weekdays, I work on holidays too.');
