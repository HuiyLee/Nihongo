import { useNavigate } from 'react-router-dom';
import { Typography } from 'antd';
import { LearningBrowsePage } from '../../components/learning/LearningBrowsePage';
import { kanjiApi } from '../../api/kanjiApi';
import type { Kanji } from '../../types/content';

const { Title, Text } = Typography;

export default function KanjiListPage() {
  const navigate = useNavigate();

  return (
    <LearningBrowsePage<Kanji>
      title="Kanji"
      searchPlaceholder="Search character, meaning, reading..."
      emptyDescription="No kanji found"
      fetchList={(params) => kanjiApi.listPublic(params)}
      getKey={(item) => item.id}
      onSelect={(item) => navigate(`/kanji/${item.id}`)}
      renderCard={(item) => (
        <>
          <Title level={2} style={{ margin: 0, textAlign: 'center' }}>
            {item.character}
          </Title>
          <div style={{ marginTop: 8, textAlign: 'center' }}>{item.meaning}</div>
          {item.onyomi && (
            <Text type="secondary" style={{ display: 'block', textAlign: 'center' }}>
              {item.onyomi}
            </Text>
          )}
        </>
      )}
    />
  );
}
