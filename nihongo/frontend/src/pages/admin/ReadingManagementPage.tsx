import { useEffect, useState } from 'react';
import type { ColumnsType } from 'antd/es/table';
import { Tag } from 'antd';
import { CrudManager, type CrudField, type CrudFilter } from '../../components/admin/CrudManager';
import { readingApi } from '../../api/readingApi';
import { levelApi } from '../../api/levelApi';
import { EXERCISE_DIFFICULTY_COLOR, EXERCISE_DIFFICULTY_OPTIONS } from '../../types/exercise';
import type { Reading, ReadingRequest } from '../../types/content';

export default function ReadingManagementPage() {
  const [levelOptions, setLevelOptions] = useState<{ label: string; value: number }[]>([]);

  useEffect(() => {
    levelApi
      .listAdmin()
      .then((res) =>
        setLevelOptions(res.data.data.map((l) => ({ label: `${l.code} - ${l.name}`, value: l.id })))
      )
      .catch(() => setLevelOptions([]));
  }, []);

  const columns: ColumnsType<Reading> = [
    { title: 'Title', dataIndex: 'title' },
    { title: 'Level', dataIndex: 'levelCode', width: 80 },
    {
      title: 'Difficulty',
      dataIndex: 'difficulty',
      width: 110,
      render: (difficulty: Reading['difficulty']) => (
        <Tag color={EXERCISE_DIFFICULTY_COLOR[difficulty]}>{difficulty}</Tag>
      ),
    },
  ];

  const fields: CrudField[] = [
    { name: 'levelId', label: 'Level', type: 'select', required: true, options: levelOptions },
    { name: 'title', label: 'Title', type: 'text', required: true },
    {
      name: 'difficulty',
      label: 'Difficulty',
      type: 'select',
      required: true,
      options: EXERCISE_DIFFICULTY_OPTIONS,
    },
    {
      name: 'content',
      label: 'Content (HTML - use <ruby>text<rt>furigana</rt></ruby> for furigana)',
      type: 'textarea',
      required: true,
    },
    { name: 'translation', label: 'Translation (revealed after completion)', type: 'textarea' },
  ];

  const filters: CrudFilter[] = [{ name: 'levelId', label: 'Level', options: levelOptions }];

  return (
    <CrudManager<Reading, ReadingRequest>
      resourceName="Reading"
      columns={columns}
      fields={fields}
      filters={filters}
      api={readingApi}
      searchPlaceholder="Search title..."
    />
  );
}
