import { useNavigate } from 'react-router-dom';
import { Typography, Tag } from 'antd';
import { ClockCircleOutlined, OrderedListOutlined } from '@ant-design/icons';
import { LearningBrowsePage } from '../../components/learning/LearningBrowsePage';
import { examApi } from '../../api/examApi';
import type { Exam } from '../../types/exam';

const { Title, Paragraph } = Typography;

export default function ExamListPage() {
  const navigate = useNavigate();

  return (
    <LearningBrowsePage<Exam>
      title="JLPT Exams"
      searchPlaceholder="Search exam title..."
      emptyDescription="No exams found"
      fetchList={(params) => examApi.listPublic(params)}
      getKey={(item) => item.id}
      onSelect={(item) => navigate(`/exams/${item.id}`)}
      renderCard={(item) => (
        <>
          <Tag>{item.levelCode}</Tag>
          <Title level={5} style={{ marginTop: 8, marginBottom: 8 }}>
            {item.title}
          </Title>
          {item.description && (
            <Paragraph type="secondary" style={{ margin: 0 }} ellipsis={{ rows: 2 }}>
              {item.description}
            </Paragraph>
          )}
          <div style={{ marginTop: 8 }}>
            <Tag icon={<ClockCircleOutlined />}>{item.durationMinutes} min</Tag>
            <Tag icon={<OrderedListOutlined />}>{item.totalQuestions} questions</Tag>
          </div>
        </>
      )}
    />
  );
}
