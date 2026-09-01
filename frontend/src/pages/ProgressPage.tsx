import { useCallback, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import { Typography, Row, Col, Card, Progress as ProgressBar } from 'antd';
import {
  FontSizeOutlined,
  FormOutlined,
  ReadOutlined,
  BookOutlined,
  TrophyOutlined,
} from '@ant-design/icons';
import { progressApi } from '../api/progressApi';
import { LoadingState } from '../components/LoadingState';
import { ErrorState } from '../components/ErrorState';
import { getErrorMessage } from '../utils/errors';
import type { CategoryProgress, ProgressOverview } from '../types/progress';

const { Title, Text } = Typography;

const CATEGORIES: { key: keyof ProgressOverview; label: string; icon: ReactNode }[] = [
  { key: 'vocabulary', label: 'Vocabulary', icon: <FontSizeOutlined /> },
  { key: 'kanji', label: 'Kanji', icon: <FormOutlined /> },
  { key: 'grammar', label: 'Grammar', icon: <ReadOutlined /> },
  { key: 'lessons', label: 'Lessons', icon: <BookOutlined /> },
  { key: 'exams', label: 'Exams', icon: <TrophyOutlined /> },
];

/** Requirements section 20 - the full per-category breakdown; DashboardPage shows a shorter summary of the same data. */
export default function ProgressPage() {
  const [overview, setOverview] = useState<ProgressOverview | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await progressApi.overview();
      setOverview(response.data.data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load progress'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void load();
  }, [load]);

  if (loading) return <LoadingState />;
  if (error) return <ErrorState message={error} onRetry={load} />;
  if (!overview) return null;

  return (
    <div>
      <Title level={3} style={{ marginTop: 0 }}>
        Learning progress
      </Title>
      <Row gutter={[16, 16]}>
        {CATEGORIES.map(({ key, label, icon }) => {
          const category = overview[key] as CategoryProgress;
          return (
            <Col key={key} xs={24} sm={12} lg={8}>
              <Card>
                <Text>
                  {icon} {label}
                </Text>
                <ProgressBar percent={category.percent} style={{ marginTop: 8 }} />
                <Text type="secondary">
                  {category.known} / {category.total} known
                </Text>
              </Card>
            </Col>
          );
        })}
      </Row>
    </div>
  );
}
