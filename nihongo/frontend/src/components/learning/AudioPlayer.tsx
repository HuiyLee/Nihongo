import { useRef, useState } from 'react';
import { Button, Select, Space } from 'antd';
import { PauseCircleOutlined, PlayCircleOutlined, RedoOutlined } from '@ant-design/icons';

/** Requirements section 15 - the exact five speeds the spec requires, nothing more. */
const SPEED_OPTIONS = [0.5, 0.75, 1, 1.25, 1.5].map((speed) => ({
  label: `${speed}x`,
  value: speed,
}));

/**
 * Shared audio player for Listening content (section 15) - play/pause,
 * replay from the start, and a fixed set of playback speeds. Used by both
 * ExerciseAttemptPage and ExamAttemptPage wherever a question carries an
 * audioUrl, so the control set exists exactly once.
 */
export function AudioPlayer({ src }: { src: string }) {
  const audioRef = useRef<HTMLAudioElement>(null);
  const [playing, setPlaying] = useState(false);
  const [speed, setSpeed] = useState(1);

  const toggle = () => {
    const audio = audioRef.current;
    if (!audio) {
      return;
    }
    if (playing) {
      audio.pause();
    } else {
      void audio.play();
    }
  };

  const replay = () => {
    const audio = audioRef.current;
    if (!audio) {
      return;
    }
    audio.currentTime = 0;
    void audio.play();
  };

  const changeSpeed = (value: number) => {
    setSpeed(value);
    if (audioRef.current) {
      audioRef.current.playbackRate = value;
    }
  };

  return (
    <Space>
      <audio
        ref={audioRef}
        src={src}
        style={{ display: 'none' }}
        onPlay={() => setPlaying(true)}
        onPause={() => setPlaying(false)}
        onEnded={() => setPlaying(false)}
      />
      <Button
        shape="circle"
        icon={playing ? <PauseCircleOutlined /> : <PlayCircleOutlined />}
        onClick={toggle}
      />
      <Button shape="circle" icon={<RedoOutlined />} title="Replay" onClick={replay} />
      <Select
        size="small"
        style={{ width: 90 }}
        value={speed}
        options={SPEED_OPTIONS}
        onChange={changeSpeed}
      />
    </Space>
  );
}
