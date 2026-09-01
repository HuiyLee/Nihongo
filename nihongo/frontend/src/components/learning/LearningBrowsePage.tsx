import { useCallback, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import { Row, Col, Card, Input, Select, Pagination, Typography, Space } from 'antd';
import type { AxiosResponse } from 'axios';
import { levelApi } from '../../api/levelApi';
import { LoadingState } from '../LoadingState';
import { ErrorState } from '../ErrorState';
import { EmptyState } from '../EmptyState';
import { getErrorMessage } from '../../utils/errors';
import type { ApiResponse, ListParams, PageResponse } from '../../types/api';
import type { Level } from '../../types/content';

const { Title } = Typography;
const { Search } = Input;

const PAGE_SIZE = 12;

export interface LearningBrowsePageProps<T> {
  title: string;
  emptyDescription?: string;
  searchPlaceholder?: string;
  fetchList: (params: ListParams) => Promise<AxiosResponse<ApiResponse<PageResponse<T>>>>;
  renderCard: (item: T) => ReactNode;
  getKey: (item: T) => number;
  onSelect: (item: T) => void;
}

/**
 * Generic browse/search/filter grid shared by the Vocabulary, Kanji, and
 * Grammar learning pages (requirements section 9/12/13) - the read-only,
 * card-grid counterpart to the admin CrudManager table.
 */
export function LearningBrowsePage<T>({
  title,
  emptyDescription = 'No items found',
  searchPlaceholder = 'Search...',
  fetchList,
  renderCard,
  getKey,
  onSelect,
}: LearningBrowsePageProps<T>) {
  const [items, setItems] = useState<T[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState('');
  const [levelId, setLevelId] = useState<number | undefined>(undefined);
  const [levels, setLevels] = useState<Level[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchItems = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetchList({
        page,
        size: PAGE_SIZE,
        keyword: keyword || undefined,
        levelId,
      });
      setItems(response.data.data.content);
      setTotalElements(response.data.data.totalElements);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load items'));
    } finally {
      setLoading(false);
    }
  }, [fetchList, page, keyword, levelId]);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void fetchItems();
  }, [fetchItems]);

  useEffect(() => {
    levelApi
      .listPublic()
      .then((response) => setLevels(response.data.data))
      .catch(() => {
        // Level filter is a nice-to-have; silently skip if it fails to load.
      });
  }, []);

  return (
    <div>
      <Title level={3} style={{ marginTop: 0 }}>
        {title}
      </Title>

      <Space style={{ marginBottom: 16 }} wrap>
        <Search
          placeholder={searchPlaceholder}
          allowClear
          onSearch={(value) => {
            setPage(0);
            setKeyword(value);
          }}
          style={{ width: 260 }}
        />
        <Select<number>
          placeholder="Filter by level"
          allowClear
          style={{ width: 160 }}
          options={levels.map((l) => ({ label: l.code, value: l.id }))}
          value={levelId}
          onChange={(value) => {
            setPage(0);
            setLevelId(value);
          }}
        />
      </Space>

      {error ? (
        <ErrorState message={error} onRetry={fetchItems} />
      ) : loading ? (
        <LoadingState />
      ) : items.length === 0 ? (
        <EmptyState description={emptyDescription} />
      ) : (
        <>
          <Row gutter={[16, 16]}>
            {items.map((item) => (
              <Col key={getKey(item)} xs={24} sm={12} md={8} lg={6}>
                <Card hoverable onClick={() => onSelect(item)}>
                  {renderCard(item)}
                </Card>
              </Col>
            ))}
          </Row>
          <div style={{ marginTop: 24, textAlign: 'center' }}>
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
