import { Typography, Row, Col, Card, Statistic } from 'antd';
import {
  UserOutlined,
  BookOutlined,
  FontSizeOutlined,
  FormOutlined,
  ReadOutlined,
  TrophyOutlined,
} from '@ant-design/icons';

const { Title, Paragraph } = Typography;

/**
 * Phase 1 placeholder for the admin dashboard (spec section 35). Real counts
 * arrive once the admin CRUD APIs (Phase 2) exist.
 */
export default function AdminDashboardPage() {
  return (
    <div>
      <Title level={3}>Admin overview</Title>
      <Paragraph type="secondary">
        Content statistics will populate once level/lesson/vocabulary/kanji/grammar management is
        implemented.
      </Paragraph>
      <Row gutter={[16, 16]}>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Users" value={0} prefix={<UserOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Lessons" value={0} prefix={<BookOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Vocabulary" value={0} prefix={<FontSizeOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Kanji" value={0} prefix={<FormOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Grammar" value={0} prefix={<ReadOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Exams" value={0} prefix={<TrophyOutlined />} />
          </Card>
        </Col>
      </Row>
    </div>
  );
}
