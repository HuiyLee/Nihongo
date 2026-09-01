import { Spin } from 'antd';

/** Reusable loading indicator - use instead of ad-hoc spinners scattered across pages. */
export function LoadingState({ tip = 'Loading...' }: { tip?: string }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', padding: '4rem' }}>
      <Spin size="large" tip={tip} />
    </div>
  );
}
