import { useNavigate } from 'react-router-dom';
import { Typography } from 'antd';
import { LearningBrowsePage } from '../../components/learning/LearningBrowsePage';
import { vocabularyApi } from '../../api/vocabularyApi';
import type { Vocabulary } from '../../types/content';

const { Title, Text } = Typography;

export default function VocabularyListPage() {
  const navigate = useNavigate();

  return (
    <LearningBrowsePage<Vocabulary>
      title="Vocabulary"
      searchPlaceholder="Search word, meaning, romaji..."
      emptyDescription="No vocabulary found"
      fetchList={(params) => vocabularyApi.listPublic(params)}
      getKey={(item) => item.id}
      onSelect={(item) => navigate(`/vocabulary/${item.id}`)}
      renderCard={(item) => (
        <>
          <Title level={4} style={{ margin: 0 }}>
            {item.kanji || item.word}
          </Title>
          {item.hiragana && <Text type="secondary">{item.hiragana}</Text>}
          <div style={{ marginTop: 8 }}>{item.meaning}</div>
        </>
      )}
    />
  );
}
