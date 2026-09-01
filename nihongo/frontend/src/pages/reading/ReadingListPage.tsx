import { useNavigate } from 'react-router-dom';
import { Typography, Tag } from 'antd';
import { LearningBrowsePage } from '../../components/learning/LearningBrowsePage';
import { readingApi } from '../../api/readingApi';
import { EXERCISE_DIFFICULTY_COLOR } from '../../types/exercise';
import type { Reading } from '../../types/content';

const { Title } = Typography;

export default function ReadingListPage() {
  const navigate = useNavigate();

  return (
    <LearningBrowsePage<Reading>
      title="Reading"
      searchPlaceholder="Search title..."
      emptyDescription="No reading passages found"
      fetchList={(params) => readingApi.listPublic(params)}
      getKey={(item) => item.id}
      onSelect={(item) => navigate(`/reading/${item.id}`)}
      renderCard={(item) => (
        <>
          <Tag>{item.levelCode}</Tag>
          <Tag color={EXERCISE_DIFFICULTY_COLOR[item.difficulty]}>{item.difficulty}</Tag>
          <Title level={5} style={{ marginTop: 8, marginBottom: 0 }}>
            {item.title}
          </Title>
        </>
      )}
    />
  );
}
