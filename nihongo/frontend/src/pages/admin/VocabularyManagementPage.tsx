import { useEffect, useState } from 'react';
import type { ColumnsType } from 'antd/es/table';
import { CrudManager, type CrudField, type CrudFilter } from '../../components/admin/CrudManager';
import { vocabularyApi } from '../../api/vocabularyApi';
import { levelApi } from '../../api/levelApi';
import { lessonApi } from '../../api/lessonApi';
import type { Vocabulary, VocabularyRequest } from '../../types/content';

export default function VocabularyManagementPage() {
  const [levelOptions, setLevelOptions] = useState<{ label: string; value: number }[]>([]);
  const [lessonOptions, setLessonOptions] = useState<{ label: string; value: number }[]>([]);

  useEffect(() => {
    levelApi
      .listAdmin()
      .then((res) =>
        setLevelOptions(res.data.data.map((l) => ({ label: `${l.code} - ${l.name}`, value: l.id })))
      )
      .catch(() => setLevelOptions([]));
    lessonApi
      .listAdmin({ page: 0, size: 200 })
      .then((res) =>
        setLessonOptions(res.data.data.content.map((l) => ({ label: l.title, value: l.id })))
      )
      .catch(() => setLessonOptions([]));
  }, []);

  const columns: ColumnsType<Vocabulary> = [
    { title: 'Word', dataIndex: 'word', width: 140 },
    { title: 'Kanji', dataIndex: 'kanji', width: 100 },
    { title: 'Hiragana', dataIndex: 'hiragana', width: 120 },
    { title: 'Romaji', dataIndex: 'romaji', width: 120 },
    { title: 'Meaning', dataIndex: 'meaning' },
    { title: 'Level', dataIndex: 'levelCode', width: 80 },
  ];

  const fields: CrudField[] = [
    { name: 'levelId', label: 'Level', type: 'select', required: true, options: levelOptions },
    { name: 'lessonId', label: 'Lesson (optional)', type: 'select', options: lessonOptions },
    { name: 'word', label: 'Word', type: 'text', required: true },
    { name: 'kanji', label: 'Kanji', type: 'text' },
    { name: 'hiragana', label: 'Hiragana', type: 'text' },
    { name: 'katakana', label: 'Katakana', type: 'text' },
    { name: 'romaji', label: 'Romaji', type: 'text' },
    { name: 'meaning', label: 'Meaning', type: 'textarea', required: true },
    { name: 'partOfSpeech', label: 'Part of speech', type: 'text' },
    { name: 'example', label: 'Example', type: 'textarea' },
    { name: 'exampleMeaning', label: 'Example meaning', type: 'textarea' },
    { name: 'audioUrl', label: 'Audio URL', type: 'text' },
    { name: 'imageUrl', label: 'Image URL', type: 'text' },
  ];

  const filters: CrudFilter[] = [
    { name: 'levelId', label: 'Level', options: levelOptions },
    { name: 'lessonId', label: 'Lesson', options: lessonOptions },
  ];

  return (
    <CrudManager<Vocabulary, VocabularyRequest>
      resourceName="Vocabulary"
      columns={columns}
      fields={fields}
      filters={filters}
      api={vocabularyApi}
      searchPlaceholder="Search word, kanji, meaning..."
    />
  );
}
