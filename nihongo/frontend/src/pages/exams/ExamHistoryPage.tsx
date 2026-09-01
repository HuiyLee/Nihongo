import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Table, Tag, Typography, Space } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { examApi } from '../../api/examApi';
import { ErrorState } from '../../components/ErrorState';
import { EmptyState } from '../../components/EmptyState';
import { getErrorMessage } from '../../utils/errors';
import type { ExamResult } from '../../types/exam';

const { Title } = Typography;
const PAGE_SIZE = 10;

/** Requirements section 38 Phase 5 ("History") - every past exam attempt the caller has concluded, newest first. */
export default function ExamHistoryPage() {
  const navigate = useNavigate();
  const [items, setItems] = useState<ExamResult[]>([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await examApi.getHistory({ page, size: PAGE_SIZE });
      setItems(response.data.data.content);
      setTotal(response.data.data.totalElements);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load exam history'));
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void load();
  }, [load]);

  const columns: ColumnsType<ExamResult> = [
    { title: 'Exam', dataIndex: 'examTitle', ellipsis: true },
    {
      title: 'Status',
      dataIndex: 'status',
      width: 120,
      render: (status: ExamResult['status']) => (
        <Tag color={status === 'COMPLETED' ? 'blue' : 'default'}>{status}</Tag>
      ),
    },
    { title: 'Score', dataIndex: 'score', width: 90, render: (score: number) => `${score}%` },
    { title: 'Correct', dataIndex: 'correctCount', width: 90 },
    { title: 'Wrong', dataIndex: 'wrongCount', width: 90 },
    {
      title: 'Submitted',
      dataIndex: 'submittedAt',
      width: 180,
      render: (value?: string) => (value ? new Date(value).toLocaleString() : '-'),
    },
    {
      title: '',
      key: 'actions',
      width: 100,
      render: (_, record) => (
        <Button size="small" onClick={() => navigate(`/exams/${record.examId}/result`)}>
          View
        </Button>
      ),
    },
  ];

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/exams')}>
          Back
        </Button>
        <Title level={3} style={{ margin: 0 }}>
          Exam History
        </Title>
      </Space>

      {error ? (
        <ErrorState message={error} onRetry={load} />
      ) : !loading && items.length === 0 ? (
        <EmptyState description="You haven't completed any exams yet" />
      ) : (
        <Table<ExamResult>
          rowKey="attemptId"
          columns={columns}
          dataSource={items}
          loading={loading}
          scroll={{ x: 'max-content' }}
          pagination={{
            current: page + 1,
            pageSize: PAGE_SIZE,
            total,
            onChange: (newPage) => setPage(newPage - 1),
          }}
        />
      )}
    </div>
  );
}
