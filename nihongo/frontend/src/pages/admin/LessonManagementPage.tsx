import { useEffect, useState } from 'react';
import { Tag } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { CrudManager, type CrudField, type CrudFilter } from '../../components/admin/CrudManager';
import { lessonApi } from '../../api/lessonApi';
import { levelApi } from '../../api/levelApi';
import { CONTENT_STATUS_OPTIONS } from '../../types/content';
import type { Lesson, LessonRequest } from '../../types/content';

const STATUS_COLOR: Record<string, string> = {
  DRAFT: 'default',
  PUBLISHED: 'green',
  ARCHIVED: 'orange',
};

export default function LessonManagementPage() {
  const [levelOptions, setLevelOptions] = useState<{ label: string; value: number }[]>([]);

  useEffect(() => {
    levelApi
      .listAdmin()
      .then((res) =>
        setLevelOptions(res.data.data.map((l) => ({ label: `${l.code} - ${l.name}`, value: l.id })))
      )
      .catch(() => setLevelOptions([]));
  }, []);

  const columns: ColumnsType<Lesson> = [
    { title: 'Order', dataIndex: 'orderIndex', width: 80 },
    { title: 'Level', dataIndex: 'levelCode', width: 90 },
    { title: 'Title', dataIndex: 'title' },
    {
      title: 'Status',
      dataIndex: 'status',
      width: 120,
      render: (status: string) => <Tag color={STATUS_COLOR[status]}>{status}</Tag>,
    },
  ];

  const fields: CrudField[] = [
    { name: 'levelId', label: 'Level', type: 'select', required: true, options: levelOptions },
    { name: 'title', label: 'Title', type: 'text', required: true },
    { name: 'description', label: 'Description', type: 'textarea' },
    { name: 'thumbnailUrl', label: 'Thumbnail URL', type: 'text' },
    { name: 'orderIndex', label: 'Order index', type: 'number', required: true },
    {
      name: 'status',
      label: 'Status',
      type: 'select',
      required: true,
      options: CONTENT_STATUS_OPTIONS,
    },
  ];

  const filters: CrudFilter[] = [
    { name: 'levelId', label: 'Level', options: levelOptions },
    { name: 'status', label: 'Status', options: CONTENT_STATUS_OPTIONS },
  ];

  return (
    <CrudManager<Lesson, LessonRequest>
      resourceName="Lesson"
      columns={columns}
      fields={fields}
      filters={filters}
      api={lessonApi}
      searchPlaceholder="Search by title..."
    />
  );
}
