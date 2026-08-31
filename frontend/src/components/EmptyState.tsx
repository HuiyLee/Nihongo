import { Empty } from 'antd';

/** Reusable "no data" placeholder for lists (vocabulary, kanji, grammar, ...). */
export function EmptyState({ description = 'No data' }: { description?: string }) {
  return <Empty description={description} />;
}
