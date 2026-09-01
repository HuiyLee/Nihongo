import { useCallback, useEffect, useState } from 'react';
import { Typography, Row, Col, Card, Statistic } from 'antd';
import {
  FontSizeOutlined,
  FormOutlined,
  ReadOutlined,
  BookOutlined,
  FireOutlined,
} from '@ant-design/icons';
import { useAuth } from '../hooks/useAuth';
import { progressApi } from '../api/progressApi';
import { streakApi } from '../api/streakApi';
import type { ProgressOverview } from '../types/progress';
import type { Streak } from '../types/learning';

const { Title, Paragraph } = Typography;

/** Requirements sections 20 and 22 - real per-category percentages and the current study streak. */
export default function DashboardPage() {
  const { user } = useAuth();
  const [overview, setOverview] = useState<ProgressOverview | null>(null);
  const [streak, setStreak] = useState<Streak | null>(null);

  const load = useCallback(async () => {
    try {
      const [progressResponse, streakResponse] = await Promise.all([
        progressApi.overview(),
        streakApi.get(),
      ]);
      setOverview(progressResponse.data.data);
      setStreak(streakResponse.data.data);
    } catch {
      // Non-fatal - the cards below just fall back to a 0% / no-streak placeholder.
    }
  }, []);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void load();
  }, [load]);

  return (
    <div>
      <Title level={3}>Welcome back, {user?.fullName ?? user?.username}!</Title>
      <Paragraph type="secondary">Here&apos;s a snapshot of your JLPT learning progress.</Paragraph>
      <Row gutter={[16, 16]}>
        <Col xs={12} md={6}>
          <Card>
            <Statistic
              title="Vocabulary"
              value={overview?.vocabulary.percent ?? 0}
              suffix="%"
              prefix={<FontSizeOutlined />}
            />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card>
            <Statistic
              title="Kanji"
              value={overview?.kanji.percent ?? 0}
              suffix="%"
              prefix={<FormOutlined />}
            />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card>
            <Statistic
              title="Grammar"
              value={overview?.grammar.percent ?? 0}
              suffix="%"
              prefix={<ReadOutlined />}
            />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card>
            <Statistic
              title="Lessons"
              value={overview?.lessons.percent ?? 0}
              suffix="%"
              prefix={<BookOutlined />}
            />
          </Card>
        </Col>
        <Col xs={24} md={6}>
          <Card>
            <Statistic
              title="Current streak"
              value={streak?.currentStreak ?? 0}
              suffix="days"
              prefix={<FireOutlined style={{ color: '#fa8c16' }} />}
            />
            {streak && streak.longestStreak > 0 && (
              <Paragraph type="secondary" style={{ margin: 0, marginTop: 4 }}>
                Longest: {streak.longestStreak} days
              </Paragraph>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
}
