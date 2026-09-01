import { useEffect, useState } from 'react';
import type { ColumnsType } from 'antd/es/table';
import { CrudManager, type CrudField, type CrudFilter } from '../../components/admin/CrudManager';
import { grammarApi } from '../../api/grammarApi';
import { levelApi } from '../../api/levelApi';
import { lessonApi } from '../../api/lessonApi';
import type { Grammar, GrammarRequest } from '../../types/content';

export default function GrammarManagementPage() {
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

  const columns: ColumnsType<Grammar> = [
    { title: 'Pattern', dataIndex: 'pattern', width: 200 },
    { title: 'Meaning', dataIndex: 'meaning' },
    { title: 'Level', dataIndex: 'levelCode', width: 80 },
  ];

  const fields: CrudField[] = [
    { name: 'levelId', label: 'Level', type: 'select', required: true, options: levelOptions },
    { name: 'lessonId', label: 'Lesson (optional)', type: 'select', options: lessonOptions },
    { name: 'pattern', label: 'Pattern', type: 'text', required: true },
    { name: 'meaning', label: 'Meaning', type: 'textarea', required: true },
    { name: 'formation', label: 'Formation', type: 'textarea' },
    { name: 'explanation', label: 'Explanation', type: 'textarea' },
    { name: 'example', label: 'Example', type: 'textarea' },
    { name: 'exampleMeaning', label: 'Example meaning', type: 'textarea' },
    { name: 'notes', label: 'Notes', type: 'textarea' },
  ];

  const filters: CrudFilter[] = [
    { name: 'levelId', label: 'Level', options: levelOptions },
    { name: 'lessonId', label: 'Lesson', options: lessonOptions },
  ];

  return (
    <CrudManager<Grammar, GrammarRequest>
      resourceName="Grammar"
      columns={columns}
      fields={fields}
      filters={filters}
      api={grammarApi}
      searchPlaceholder="Search pattern, meaning..."
    />
  );
}
