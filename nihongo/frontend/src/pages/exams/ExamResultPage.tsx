import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, Card, Result, Space, Statistic, Row, Col } from 'antd';
import { ArrowLeftOutlined, RedoOutlined } from '@ant-design/icons';
import { examApi } from '../../api/examApi';
import { LoadingState } from '../../components/LoadingState';
import { ErrorState } from '../../components/ErrorState';
import { getErrorMessage } from '../../utils/errors';
import type { ExamResult } from '../../types/exam';

export default function ExamResultPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const examId = Number(id);

  const [result, setResult] = useState<ExamResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await examApi.getResult(examId);
      setResult(response.data.data);
    } catch (err) {
      setError(getErrorMessage(err, 'No result found for this exam yet - take it first'));
    } finally {
      setLoading(false);
    }
  }, [examId]);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void load();
  }, [load]);

  if (loading) return <LoadingState />;
  if (error) return <ErrorState message={error} onRetry={load} />;
  if (!result) return null;

  const passed = result.score >= 60;

  return (
    <div style={{ maxWidth: 640, margin: '0 auto' }}>
      <Card>
        <Result
          status={passed ? 'success' : 'warning'}
          title={`${result.examTitle}: ${result.score}%`}
          subTitle={
            result.status === 'EXPIRED'
              ? 'Time ran out before this attempt was submitted.'
              : `Completed ${result.submittedAt ? new Date(result.submittedAt).toLocaleString() : ''}`
          }
        />
        <Row gutter={16} style={{ textAlign: 'center', marginBottom: 24 }}>
          <Col span={8}>
            <Statistic title="Score" value={result.score} suffix="%" />
          </Col>
          <Col span={8}>
            <Statistic
              title="Correct"
              value={result.correctCount}
              valueStyle={{ color: '#3f8600' }}
            />
          </Col>
          <Col span={8}>
            <Statistic title="Wrong" value={result.wrongCount} valueStyle={{ color: '#cf1322' }} />
          </Col>
        </Row>
        <Space style={{ width: '100%', justifyContent: 'center' }}>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/exams')}>
            Back to exams
          </Button>
          <Button
            type="primary"
            icon={<RedoOutlined />}
            onClick={() => navigate(`/exams/${examId}`)}
          >
            Retake
          </Button>
        </Space>
      </Card>
    </div>
  );
}
