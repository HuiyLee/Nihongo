import { useEffect, useState } from 'react';
import type { ColumnsType } from 'antd/es/table';
import { CrudManager, type CrudField, type CrudFilter } from '../../components/admin/CrudManager';
import { kanjiApi } from '../../api/kanjiApi';
import { levelApi } from '../../api/levelApi';
import { lessonApi } from '../../api/lessonApi';
import type { Kanji, KanjiRequest } from '../../types/content';

export default function KanjiManagementPage() {
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

  const columns: ColumnsType<Kanji> = [
    { title: 'Character', dataIndex: 'character', width: 100 },
    { title: 'Meaning', dataIndex: 'meaning' },
    { title: 'Onyomi', dataIndex: 'onyomi', width: 140 },
    { title: 'Kunyomi', dataIndex: 'kunyomi', width: 140 },
    { title: 'Strokes', dataIndex: 'strokeCount', width: 90 },
    { title: 'Level', dataIndex: 'levelCode', width: 80 },
  ];

  const fields: CrudField[] = [
    { name: 'levelId', label: 'Level', type: 'select', required: true, options: levelOptions },
    { name: 'lessonId', label: 'Lesson (optional)', type: 'select', options: lessonOptions },
    { name: 'character', label: 'Character', type: 'text', required: true },
    { name: 'meaning', label: 'Meaning', type: 'textarea', required: true },
    { name: 'onyomi', label: 'Onyomi', type: 'text' },
    { name: 'kunyomi', label: 'Kunyomi', type: 'text' },
    { name: 'strokeCount', label: 'Stroke count', type: 'number' },
    { name: 'strokeOrderImageUrl', label: 'Stroke order image URL', type: 'text' },
    { name: 'example', label: 'Example', type: 'textarea' },
    { name: 'exampleMeaning', label: 'Example meaning', type: 'textarea' },
    { name: 'audioUrl', label: 'Audio URL', type: 'text' },
  ];

  const filters: CrudFilter[] = [
    { name: 'levelId', label: 'Level', options: levelOptions },
    { name: 'lessonId', label: 'Lesson', options: lessonOptions },
  ];

  return (
    <CrudManager<Kanji, KanjiRequest>
      resourceName="Kanji"
      columns={columns}
      fields={fields}
      filters={filters}
      api={kanjiApi}
      searchPlaceholder="Search character, meaning..."
    />
  );
}
