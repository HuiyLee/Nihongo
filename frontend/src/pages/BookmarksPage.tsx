import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Typography, Select, List, Button, Pagination, Tag, Popconfirm, message } from 'antd';
import { DeleteOutlined } from '@ant-design/icons';
import { bookmarkApi } from '../api/bookmarkApi';
import { LoadingState } from '../components/LoadingState';
import { ErrorState } from '../components/ErrorState';
import { EmptyState } from '../components/EmptyState';
import { getErrorMessage } from '../utils/errors';
import type { Bookmark, BookmarkTargetType } from '../types/learning';

const { Title } = Typography;
const PAGE_SIZE = 20;

const TARGET_TYPE_OPTIONS: { label: string; value: BookmarkTargetType }[] = [
  { label: 'Vocabulary', value: 'VOCABULARY' },
  { label: 'Kanji', value: 'KANJI' },
  { label: 'Grammar', value: 'GRAMMAR' },
  { label: 'Reading', value: 'READING' },
];

const DETAIL_PATH: Record<BookmarkTargetType, string> = {
  VOCABULARY: '/vocabulary',
  KANJI: '/kanji',
  GRAMMAR: '/grammar',
  READING: '/reading',
};

/** Requirements section 23 - lists every bookmark the caller owns, across all target types. */
export default function BookmarksPage() {
  const navigate = useNavigate();
  const [bookmarks, setBookmarks] = useState<Bookmark[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [page, setPage] = useState(0);
  const [targetType, setTargetType] = useState<BookmarkTargetType | undefined>(undefined);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchBookmarks = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await bookmarkApi.list({ page, size: PAGE_SIZE, targetType });
      setBookmarks(response.data.data.content);
      setTotalElements(response.data.data.totalElements);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load bookmarks'));
    } finally {
      setLoading(false);
    }
  }, [page, targetType]);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void fetchBookmarks();
  }, [fetchBookmarks]);

  const handleRemove = async (id: number) => {
    try {
      await bookmarkApi.remove(id);
      message.success('Bookmark removed');
      await fetchBookmarks();
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to remove bookmark'));
    }
  };

  return (
    <div>
      <Title level={3} style={{ marginTop: 0 }}>
        Bookmarks
      </Title>

      <Select<BookmarkTargetType>
        placeholder="Filter by type"
        allowClear
        style={{ width: 200, marginBottom: 16 }}
        options={TARGET_TYPE_OPTIONS}
        value={targetType}
        onChange={(value) => {
          setPage(0);
          setTargetType(value);
        }}
      />

      {error ? (
        <ErrorState message={error} onRetry={fetchBookmarks} />
      ) : loading ? (
        <LoadingState />
      ) : bookmarks.length === 0 ? (
        <EmptyState description="No bookmarks yet" />
      ) : (
        <>
          <List
            dataSource={bookmarks}
            renderItem={(bookmark) => (
              <List.Item
                actions={[
                  <Popconfirm
                    key="remove"
                    title="Remove this bookmark?"
                    onConfirm={() => handleRemove(bookmark.id)}
                    okText="Remove"
                    okButtonProps={{ danger: true }}
                  >
                    <Button size="small" danger icon={<DeleteOutlined />}>
                      Remove
                    </Button>
                  </Popconfirm>,
                ]}
              >
                <List.Item.Meta
                  title={
                    <Button
                      type="link"
                      style={{ padding: 0, height: 'auto' }}
                      onClick={() =>
                        navigate(`${DETAIL_PATH[bookmark.targetType]}/${bookmark.targetId}`)
                      }
                    >
                      {bookmark.displayText ?? `#${bookmark.targetId}`}
                    </Button>
                  }
                  description={<Tag>{bookmark.targetType}</Tag>}
                />
              </List.Item>
            )}
          />
          <div style={{ marginTop: 16, textAlign: 'center' }}>
            <Pagination
              current={page + 1}
              pageSize={PAGE_SIZE}
              total={totalElements}
              onChange={(p) => setPage(p - 1)}
              showSizeChanger={false}
            />
          </div>
        </>
      )}
    </div>
  );
}
