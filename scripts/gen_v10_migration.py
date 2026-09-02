#!/usr/bin/env python3
"""Generates V10__expand_n2_content.sql - a second, larger batch of N2
vocabulary/kanji/grammar (no audio, no exam changes) so the N2 level isn't
so thin. Distinct from every word/kanji/pattern already seeded in V7 and
V9 (checked by hand against both files before writing this list).
Original content, not copied from any copyrighted textbook.
"""

LEVEL = "N2"


def esc(s):
    if s is None:
        return "NULL"
    return "'" + s.replace("'", "''") + "'"


# ---------------------------------------------------------------------------
# VOCAB: word, hiragana, romaji, meaning, pos, example, example_meaning
# ---------------------------------------------------------------------------
VOCAB = [
    ("概念", "がいねん", "gainen", "concept", "noun", "新しい概念を学びます。", "I learn a new concept."),
    ("要素", "ようそ", "youso", "element / factor", "noun", "成功の要素は何ですか。", "What are the factors of success?"),
    ("基準", "きじゅん", "kijun", "standard / criterion", "noun", "判断の基準を決めます。", "We decide the criteria for judgment."),
    ("分野", "ぶんや", "bunya", "field / domain", "noun", "専門の分野を選びます。", "I choose my specialized field."),
    ("手段", "しゅだん", "shudan", "means / method", "noun", "目的のために手段を選びます。", "I choose the means for the purpose."),
    ("制限", "せいげん", "seigen", "restriction / limit", "noun", "時間に制限があります。", "There is a time limit."),
    ("保証", "ほしょう", "hoshou", "guarantee", "noun", "品質を保証します。", "We guarantee the quality."),
    ("負担", "ふたん", "futan", "burden", "noun", "経済的な負担が大きいです。", "The financial burden is large."),
    ("対策", "たいさく", "taisaku", "countermeasure", "noun", "対策を立てます。", "We come up with a countermeasure."),
    ("見解", "けんかい", "kenkai", "view / opinion", "noun", "専門家の見解を聞きます。", "I listen to the expert's view."),
    ("把握", "はあく", "haaku", "grasp / comprehension", "noun", "状況を把握します。", "I grasp the situation."),
    ("普及", "ふきゅう", "fukyuu", "spread / popularization", "noun", "スマートフォンが普及しました。", "Smartphones have become widespread."),
    ("削減", "さくげん", "sakugen", "reduction / cut", "noun", "コストを削減します。", "We cut costs."),
    ("拡大", "かくだい", "kakudai", "expansion", "noun", "事業を拡大します。", "We expand the business."),
    ("縮小", "しゅくしょう", "shukushou", "reduction / shrinkage", "noun", "規模を縮小します。", "We shrink the scale."),
    ("抑制", "よくせい", "yokusei", "suppression / control", "noun", "感情を抑制します。", "I suppress my emotions."),
    ("依存", "いぞん", "izon", "dependence", "noun", "スマホに依存しています。", "I'm dependent on my smartphone."),
    ("相違", "そうい", "soui", "difference", "noun", "意見の相違があります。", "There is a difference of opinion."),
    ("該当", "がいとう", "gaitou", "applicability", "noun", "該当する項目を選びます。", "Choose the applicable item."),
    ("検討", "けんとう", "kentou", "consideration / examination", "noun", "提案を検討します。", "We consider the proposal."),
    ("適切", "てきせつ", "tekisetsu", "appropriate", "adjective", "適切な対応をします。", "We respond appropriately."),
    ("微妙", "びみょう", "bimyou", "subtle / delicate", "adjective", "微妙な違いがあります。", "There is a subtle difference."),
    ("過剰", "かじょう", "kajou", "excessive", "adjective", "過剰な反応です。", "It's an excessive reaction."),
    ("明確", "めいかく", "meikaku", "clear / definite", "adjective", "明確な答えが必要です。", "A clear answer is needed."),
    ("一律", "いちりつ", "ichiritsu", "uniform / across the board", "adjective", "一律に値上げします。", "We raise prices uniformly."),
    ("円滑", "えんかつ", "enkatsu", "smooth", "adjective", "交渉が円滑に進みました。", "The negotiation proceeded smoothly."),
    ("慎重", "しんちょう", "shinchou", "careful / cautious", "adjective", "慎重に考えます。", "I think carefully."),
    ("積極的", "せっきょくてき", "sekkyokuteki", "proactive / positive", "adjective", "積極的に参加します。", "I participate proactively."),
    ("消極的", "しょうきょくてき", "shoukyokuteki", "passive / negative", "adjective", "消極的な態度です。", "It's a passive attitude."),
    ("不可欠", "ふかけつ", "fukaketsu", "essential / indispensable", "adjective", "水は生活に不可欠です。", "Water is essential to life."),
    ("伴う", "ともなう", "tomonau", "to accompany / entail", "verb", "リスクを伴います。", "It entails risk."),
    ("及ぶ", "およぶ", "oyobu", "to reach / extend to", "verb", "影響が全国に及びます。", "The effect extends nationwide."),
    ("巡る", "めぐる", "meguru", "to go around / concern", "verb", "その問題を巡って議論します。", "We debate concerning that issue."),
    ("携わる", "たずさわる", "tazusawaru", "to be engaged in", "verb", "開発に携わっています。", "I'm engaged in the development."),
    ("心がける", "こころがける", "kokorogakeru", "to be mindful of", "verb", "健康に心がけます。", "I'm mindful of my health."),
    ("見込む", "みこむ", "mikomu", "to expect / anticipate", "verb", "成長を見込んでいます。", "We anticipate growth."),
    ("果たす", "はたす", "hatasu", "to fulfill / accomplish", "verb", "役割を果たします。", "I fulfill my role."),
    ("費やす", "ついやす", "tsuiyasu", "to spend / expend", "verb", "時間を費やします。", "I spend time on it."),
    ("図る", "はかる", "hakaru", "to plan / aim for", "verb", "効率化を図ります。", "We aim for greater efficiency."),
    ("兼ねる", "かねる", "kaneru", "to combine / serve also as", "verb", "趣味と実益を兼ねます。", "It combines hobby and practical benefit."),
    ("阻む", "はばむ", "habamu", "to hinder / block", "verb", "計画の実行を阻みます。", "It hinders the execution of the plan."),
    ("妨げる", "さまたげる", "samatageru", "to hinder / obstruct", "verb", "発展を妨げます。", "It hinders development."),
    ("導く", "みちびく", "michibiku", "to guide / lead", "verb", "成功へ導きます。", "It leads to success."),
    ("見直す", "みなおす", "minaosu", "to reconsider / review", "verb", "計画を見直します。", "We review the plan."),
    ("補う", "おぎなう", "oginau", "to make up for / supplement", "verb", "不足を補います。", "We make up for the shortage."),
    ("一概に", "いちがいに", "ichigaini", "unconditionally / categorically", "adverb", "一概には言えません。", "You can't say categorically."),
    ("一層", "いっそう", "issou", "all the more / still more", "adverb", "一層努力します。", "I'll try all the more."),
    ("依然として", "いぜんとして", "izentoshite", "still / as before", "adverb", "問題は依然として残っています。", "The problem still remains."),
    ("果たして", "はたして", "hatashite", "indeed / really", "adverb", "果たして成功するでしょうか。", "Will it really succeed?"),
    ("とりわけ", "とりわけ", "toriwake", "especially / particularly", "adverb", "今年はとりわけ暑いです。", "This year is especially hot."),
]

# ---------------------------------------------------------------------------
# KANJI: character, meaning, onyomi, kunyomi, stroke_count, example, example_meaning
# ---------------------------------------------------------------------------
KANJI = [
    ("概", "outline / approximate", "ガイ", "おおむ(ね)", 13, "概念", "concept"),
    ("素", "element / plain", "ソ、ス", None, 10, "要素", "element"),
    ("基", "basis / foundation", "キ", "もと", 11, "基準", "standard"),
    ("野", "field / plain", "ヤ", "の", 11, "分野", "field"),
    ("段", "step / grade", "ダン", None, 9, "手段", "means"),
    ("限", "limit", "ゲン", "かぎ(る)", 9, "制限", "restriction"),
    ("証", "proof / evidence", "ショウ", None, 12, "保証", "guarantee"),
    ("担", "carry / bear", "タン", "かつ(ぐ)、にな(う)", 8, "負担", "burden"),
    ("策", "policy / scheme", "サク", None, 12, "対策", "countermeasure"),
    ("解", "solve / understand", "カイ、ゲ", "と(く)", 13, "見解", "view"),
    ("把", "grasp / hold", "ハ", None, 7, "把握", "grasp"),
    ("握", "grip / grasp", "アク", "にぎ(る)", 12, "把握", "grasp"),
    ("及", "reach / extend", "キュウ", "およ(ぶ)", 3, "及ぶ", "to reach"),
    ("普", "universal / general", "フ", None, 12, "普及", "spread"),
    ("削", "cut / pare down", "サク", "けず(る)", 9, "削減", "reduction"),
    ("拡", "expand / widen", "カク", None, 8, "拡大", "expansion"),
    ("縮", "shrink / contract", "シュク", "ちぢ(む)", 17, "縮小", "shrinkage"),
    ("抑", "suppress / hold back", "ヨク", "おさ(える)", 7, "抑制", "suppression"),
    ("依", "depend / rely", "イ", None, 8, "依存", "dependence"),
    ("該", "the said / corresponding", "ガイ", None, 13, "該当", "applicability"),
    ("討", "discuss / examine", "トウ", "う(つ)", 10, "検討", "consideration"),
    ("適", "suitable / fit", "テキ", None, 14, "適切", "appropriate"),
    ("微", "slight / delicate", "ビ", None, 13, "微妙", "subtle"),
    ("確", "certain / sure", "カク", "たし(か)", 15, "明確", "clear"),
    ("慎", "cautious / careful", "シン", "つつし(む)", 13, "慎重", "careful"),
]

# ---------------------------------------------------------------------------
# GRAMMAR: pattern, meaning, formation, explanation, example, example_meaning
# ---------------------------------------------------------------------------
GRAMMAR = [
    ("〜ことか", "how ~! (exclamation)", "Plain form + ことか", "An exclamatory expression of strong feeling.", "何度注意したことか。", "How many times have I warned him!"),
    ("〜ものだ", "used to ~ / it's natural that ~", "Verb (plain, often past) + ものだ", "Fond reminiscence about the past, or a statement of what's naturally expected.", "昔はよくここで遊んだものだ。", "I used to play here often in the past."),
    ("〜わけにはいかない", "cannot afford to ~", "Verb (dictionary form) + わけにはいかない", "Something can't be done because of social, moral, or situational reasons.", "約束したから、休むわけにはいかない。", "Since I promised, I can't afford to take a day off."),
    ("〜さえ〜ば", "if only ~", "Noun + さえ + Verb (ば form)", "Emphasizes that the stated condition alone is sufficient.", "時間さえあれば、行きます。", "If only I had time, I would go."),
    ("〜ことなく", "without ~ing", "Verb (dictionary form) + ことなく", "A formal way to say something happens without a certain action taking place.", "休むことなく働きました。", "I worked without resting."),
    ("〜に加えて", "in addition to ~", "Noun + に加えて", "Adds something on top of what was already mentioned.", "給料に加えて、ボーナスも出ます。", "In addition to salary, a bonus is also given."),
    ("〜のもとで", "under ~ (guidance / conditions)", "Noun + のもとで", "Indicates the influence, guidance, or conditions under which something happens.", "先生の指導のもとで研究します。", "I do research under the teacher's guidance."),
    ("〜に先立って", "prior to ~", "Noun / Verb (dictionary form) + に先立って", "Something happens before the main event, often as preparation.", "開会に先立って、挨拶があります。", "There will be a greeting prior to the opening."),
    ("〜からして", "judging from ~", "Noun + からして", "Uses one obvious example as evidence for a broader judgment.", "話し方からして、外国人でしょう。", "Judging from the way he speaks, he's probably a foreigner."),
    ("〜まい", "will not / probably not ~", "Verb (dictionary form) + まい", "A literary way to express negative volition or negative conjecture.", "二度と失敗はするまい。", "I won't fail again."),
    ("〜べきではない", "should not ~", "Verb (dictionary form) + べきではない", "States that something is not the right thing to do.", "人を批判するべきではない。", "You should not criticize people."),
    ("〜てからでないと", "unless ~ first", "Verb (て form) + からでないと", "The following action is impossible without first doing this one.", "確認してからでないと、始められません。", "We can't start unless we confirm first."),
    ("〜にすぎない", "merely ~ / nothing more than ~", "Noun / Plain form + にすぎない", "Downplays something as being only that much and no more.", "それはうわさにすぎません。", "That's nothing more than a rumor."),
    ("〜としたら", "if we assume ~", "Plain form + としたら", "A hypothetical assumption used to reason about its consequence.", "もし本当だとしたら、大変です。", "If that's really true, it's a serious problem."),
    ("〜だけあって", "as one would expect from ~", "Noun / Plain form + だけあって", "A result that matches what you'd expect given the stated reason.", "経験者だけあって、上手です。", "As one would expect from an experienced person, they're skilled."),
    ("〜に違いない", "must be ~ / no doubt ~", "Plain form + に違いない", "A confident conjecture based on available evidence.", "彼は忙しいに違いない。", "He must be busy."),
    ("〜次第だ", "depends on ~", "Noun + 次第だ", "The outcome is entirely determined by the stated noun.", "結果は努力次第です。", "The result depends on effort."),
    ("〜つつある", "in the process of ~ing", "Verb (ます stem) + つつある", "A formal way to describe a gradual, ongoing change.", "状況は改善しつつあります。", "The situation is in the process of improving."),
    ("〜てまで", "even to the extent of ~ing", "Verb (て form) + まで", "Emphasizes that going that far isn't warranted or necessary.", "借金をしてまで買う必要はない。", "There's no need to buy it even to the extent of going into debt."),
    ("〜はもとより", "not to mention ~ / needless to say ~", "Noun + はもとより", "The stated noun is obviously included, and something further is also true.", "平日はもとより、休日も働きます。", "Not to mention weekdays, I work on holidays too."),
]


def validate():
    words = [w for w, *_ in VOCAB]
    assert len(words) == len(set(words)), "duplicate vocab word in V10 list"
    chars = [c for c, *_ in KANJI]
    assert len(chars) == len(set(chars)), "duplicate kanji character in V10 list"
    patterns = [p for p, *_ in GRAMMAR]
    assert len(patterns) == len(set(patterns)), "duplicate grammar pattern in V10 list"
    print(f"OK: {len(VOCAB)} vocab, {len(KANJI)} kanji, {len(GRAMMAR)} grammar - all unique.")


def main():
    validate()
    out = []
    out.append(
        "-- Second N2 content batch (requirements follow-up: \"vẫn đang ít từ vựng, ngữ\n"
        "-- pháp, kanji quá\" - still too little vocab/grammar/kanji). Adds 50 more N2\n"
        "-- vocabulary words, 25 more N2 kanji, and 20 more N2 grammar patterns, all\n"
        "-- distinct from what V7 and V9 already seeded. Original content, matching\n"
        "-- only the syllabus scope of reference books like Mimikara/Shinkanzen, not\n"
        "-- copied from them. No audio (consistent with the V7 non-listening vocab rows).\n\n"
    )

    out.append("-- =========================================================================\n")
    out.append("-- N2 VOCABULARY (batch 2)\n")
    out.append("-- =========================================================================\n\n")
    out.append(
        "INSERT INTO vocabularies (level_id, word, kanji, hiragana, katakana, romaji, meaning, "
        "part_of_speech, example, example_meaning) VALUES\n"
    )
    rows = []
    for word, hira, romaji, meaning, pos, example, example_meaning in VOCAB:
        rows.append(
            f"((SELECT id FROM levels WHERE code = '{LEVEL}'), {esc(word)}, {esc(word)}, {esc(hira)}, NULL, "
            f"{esc(romaji)}, {esc(meaning)}, {esc(pos)}, {esc(example)}, {esc(example_meaning)})"
        )
    out.append(",\n".join(rows) + ";\n\n")

    out.append("-- =========================================================================\n")
    out.append("-- N2 KANJI (batch 2)\n")
    out.append("-- =========================================================================\n\n")
    out.append(
        "INSERT INTO kanjis (level_id, character, meaning, onyomi, kunyomi, stroke_count, example, example_meaning) VALUES\n"
    )
    rows = []
    for char, meaning, onyomi, kunyomi, strokes, example, example_meaning in KANJI:
        rows.append(
            f"((SELECT id FROM levels WHERE code = '{LEVEL}'), {esc(char)}, {esc(meaning)}, {esc(onyomi)}, "
            f"{esc(kunyomi)}, {strokes}, {esc(example)}, {esc(example_meaning)})"
        )
    out.append(",\n".join(rows) + ";\n\n")

    out.append("-- =========================================================================\n")
    out.append("-- N2 GRAMMAR (batch 2)\n")
    out.append("-- =========================================================================\n\n")
    out.append(
        "INSERT INTO grammars (level_id, pattern, meaning, formation, explanation, example, example_meaning) VALUES\n"
    )
    rows = []
    for pattern, meaning, formation, explanation, example, example_meaning in GRAMMAR:
        rows.append(
            f"((SELECT id FROM levels WHERE code = '{LEVEL}'), {esc(pattern)}, {esc(meaning)}, {esc(formation)}, "
            f"{esc(explanation)}, {esc(example)}, {esc(example_meaning)})"
        )
    out.append(",\n".join(rows) + ";\n")

    text = "".join(out)
    out_path = "/home/claude/nihongo/backend/src/main/resources/db/migration/V10__expand_n2_content.sql"
    with open(out_path, "w", encoding="utf-8") as f:
        f.write(text)
    print(f"Wrote {out_path} ({len(text)} bytes)")


if __name__ == "__main__":
    main()
