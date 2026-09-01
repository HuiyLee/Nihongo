import { useNavigate } from 'react-router-dom';
import { Typography, Tag } from 'antd';
import { LearningBrowsePage } from '../../components/learning/LearningBrowsePage';
import { exerciseApi } from '../../api/exerciseApi';
import { EXERCISE_DIFFICULTY_COLOR } from '../../types/exercise';
import type { Exercise } from '../../types/exercise';

const { Title, Paragraph } = Typography;

export default function ExerciseListPage() {
  const navigate = useNavigate();

  return (
    <LearningBrowsePage<Exercise>
      title="Exercises"
      searchPlaceholder="Search question..."
      emptyDescription="No exercises found"
      fetchList={(params) => exerciseApi.listPublic(params)}
      getKey={(item) => item.id}
      onSelect={(item) => navigate(`/exercises/${item.id}`)}
      renderCard={(item) => (
        <>
          <Tag>{item.type.replaceAll('_', ' ')}</Tag>
          <Tag color={EXERCISE_DIFFICULTY_COLOR[item.difficulty]}>{item.difficulty}</Tag>
          <Title level={5} style={{ marginTop: 8, marginBottom: 0 }}>
            {item.question.length > 80 ? `${item.question.slice(0, 80)}...` : item.question}
          </Title>
          {item.lessonTitle && (
            <Paragraph type="secondary" style={{ margin: 0 }}>
              {item.lessonTitle}
            </Paragraph>
          )}
        </>
      )}
    />
  );
}
