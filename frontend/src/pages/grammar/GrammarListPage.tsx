import { useNavigate } from 'react-router-dom';
import { Typography } from 'antd';
import { LearningBrowsePage } from '../../components/learning/LearningBrowsePage';
import { grammarApi } from '../../api/grammarApi';
import type { Grammar } from '../../types/content';

const { Title, Text } = Typography;

export default function GrammarListPage() {
  const navigate = useNavigate();

  return (
    <LearningBrowsePage<Grammar>
      title="Grammar"
      searchPlaceholder="Search pattern or meaning..."
      emptyDescription="No grammar points found"
      fetchList={(params) => grammarApi.listPublic(params)}
      getKey={(item) => item.id}
      onSelect={(item) => navigate(`/grammar/${item.id}`)}
      renderCard={(item) => (
        <>
          <Title level={4} style={{ margin: 0 }}>
            {item.pattern}
          </Title>
          <Text type="secondary">{item.meaning}</Text>
        </>
      )}
    />
  );
}
