import { useNavigate } from 'react-router-dom';
import { Typography, Tag } from 'antd';
import { SoundOutlined } from '@ant-design/icons';
import { LearningBrowsePage } from '../../components/learning/LearningBrowsePage';
import { exerciseApi } from '../../api/exerciseApi';
import { EXERCISE_DIFFICULTY_COLOR } from '../../types/exercise';
import type { Exercise } from '../../types/exercise';

const { Title, Paragraph } = Typography;

/**
 * Requirements section 15. Listening has no entity of its own - it's an
 * Exercise with type LISTENING (audioUrl and all), so this page is just
 * ExerciseListPage pre-filtered to that type; attempting one reuses the
 * exact same /exercises/:id attempt page and AudioPlayer.
 */
export default function ListeningListPage() {
  const navigate = useNavigate();

  return (
    <LearningBrowsePage<Exercise>
      title="Listening"
      searchPlaceholder="Search question..."
      emptyDescription="No listening exercises found"
      fetchList={(params) => exerciseApi.listPublic({ ...params, type: 'LISTENING' })}
      getKey={(item) => item.id}
      onSelect={(item) => navigate(`/exercises/${item.id}`)}
      renderCard={(item) => (
        <>
          <Tag icon={<SoundOutlined />} color={EXERCISE_DIFFICULTY_COLOR[item.difficulty]}>
            {item.difficulty}
          </Tag>
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
