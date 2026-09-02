import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, Card, Result, Space, Statistic, Row, Col, Tag, Typography, Divider } from 'antd';
import {
  ArrowLeftOutlined,
  RedoOutlined,
  CheckCircleFilled,
  CloseCircleFilled,
  MinusCircleOutlined,
} from '@ant-design/icons';
import { examApi } from '../../api/examApi';
import { AudioPlayer } from '../../components/learning/AudioPlayer';
import { LoadingState } from '../../components/LoadingState';
import { ErrorState } from '../../components/ErrorState';
import { getErrorMessage } from '../../utils/errors';
import { parseServerDateTime } from '../../utils/serverDate';
import type { ExamResult } from '../../types/exam';

const { Title, Text, Paragraph } = Typography;

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
    <div style={{ maxWidth: 720, margin: '0 auto' }}>
      <Card>
        <Result
          status={passed ? 'success' : 'warning'}
          title={`${result.examTitle}: ${result.score}%`}
          subTitle={
            result.status === 'EXPIRED'
              ? 'Time ran out before this attempt was submitted.'
              : `Completed ${result.submittedAt ? parseServerDateTime(result.submittedAt).toLocaleString() : ''}`
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

      {result.questions && result.questions.length > 0 && (
        <>
          <Divider>Đáp án</Divider>
          {result.questions.map((q, index) => {
            const answered = q.selectedAnswerIds.length > 0;
            return (
              <Card key={q.examQuestionId} style={{ marginBottom: 16 }}>
                <Space style={{ marginBottom: 8 }}>
                  <Text type="secondary">Question {index + 1} of {result.questions?.length}</Text>
                  {!answered ? (
                    <Tag icon={<MinusCircleOutlined />} color="default">Not answered</Tag>
                  ) : q.correct ? (
                    <Tag icon={<CheckCircleFilled />} color="success">Correct</Tag>
                  ) : (
                    <Tag icon={<CloseCircleFilled />} color="error">Incorrect</Tag>
                  )}
                </Space>
                <Title level={5} style={{ marginTop: 0 }}>
                  {q.exercise.question}
                </Title>
                {q.exercise.audioUrl && (
                  <div style={{ marginBottom: 12 }}>
                    <AudioPlayer src={q.exercise.audioUrl} />
                  </div>
                )}

                <Space direction="vertical" style={{ width: '100%' }}>
                  {q.exercise.answers.map((a) => {
                    const wasSelected = q.selectedAnswerIds.includes(a.id);
                    const isKey = a.correct;
                    const background = isKey ? '#f6ffed' : wasSelected ? '#fff1f0' : undefined;
                    const border = isKey
                      ? '1px solid #b7eb8f'
                      : wasSelected
                        ? '1px solid #ffa39e'
                        : '1px solid #f0f0f0';
                    return (
                      <div
                        key={a.id}
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          gap: 8,
                          padding: '6px 10px',
                          borderRadius: 6,
                          background,
                          border,
                        }}
                      >
                        {isKey ? (
                          <CheckCircleFilled style={{ color: '#52c41a' }} />
                        ) : wasSelected ? (
                          <CloseCircleFilled style={{ color: '#ff4d4f' }} />
                        ) : (
                          <span style={{ width: 14, display: 'inline-block' }} />
                        )}
                        <span>{a.answerText}</span>
                        {isKey && (
                          <Tag color="green" style={{ marginLeft: 'auto' }}>
                            Correct answer
                          </Tag>
                        )}
                        {wasSelected && !isKey && (
                          <Tag color="red" style={{ marginLeft: isKey ? 0 : 'auto' }}>
                            Your answer
                          </Tag>
                        )}
                      </div>
                    );
                  })}
                </Space>

                {q.exercise.explanation && (
                  <Paragraph type="secondary" style={{ marginTop: 12, marginBottom: 0 }}>
                    {q.exercise.explanation}
                  </Paragraph>
                )}
              </Card>
            );
          })}
        </>
      )}
    </div>
  );
}
