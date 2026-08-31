import { useCallback, useEffect, useState } from 'react';
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  Select,
  Checkbox,
  Popconfirm,
  Space,
  Typography,
  Tag,
  message,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined, MinusCircleOutlined } from '@ant-design/icons';
import { exerciseApi } from '../../api/exerciseApi';
import { levelApi } from '../../api/levelApi';
import { lessonApi } from '../../api/lessonApi';
import { ErrorState } from '../../components/ErrorState';
import { getErrorMessage } from '../../utils/errors';
import {
  EXERCISE_DIFFICULTY_COLOR,
  EXERCISE_DIFFICULTY_OPTIONS,
  EXERCISE_TYPE_OPTIONS,
} from '../../types/exercise';
import type { AdminExercise, ExerciseRequest } from '../../types/exercise';

const { Title } = Typography;
const { TextArea } = Input;

/**
 * Exercises need a dynamic answers list (Form.List), which the generic
 * CrudManager's flat field config can't express, so this page is
 * standalone - same reasoning as LevelManagementPage being standalone,
 * just for a different limitation (nested array vs. no pagination).
 */
export default function ExerciseManagementPage() {
  const [data, setData] = useState<AdminExercise[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [levelFilter, setLevelFilter] = useState<number | undefined>(undefined);

  const [levelOptions, setLevelOptions] = useState<{ label: string; value: number }[]>([]);
  const [lessonOptions, setLessonOptions] = useState<{ label: string; value: number }[]>([]);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<AdminExercise | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm<ExerciseRequest>();

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await exerciseApi.listAdmin({
        page,
        size: pageSize,
        keyword: keyword || undefined,
        levelId: levelFilter,
      });
      setData(response.data.data.content);
      setTotal(response.data.data.totalElements);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load exercises'));
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, keyword, levelFilter]);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void fetchData();
  }, [fetchData]);

  useEffect(() => {
    levelApi
      .listAdmin()
      .then((res) =>
        setLevelOptions(res.data.data.map((l) => ({ label: `${l.code} - ${l.name}`, value: l.id })))
      )
      .catch(() => setLevelOptions([]));
    lessonApi
      .listAdmin({ page: 0, size: 200 })
      .then((res) =>
        setLessonOptions(res.data.data.content.map((l) => ({ label: l.title, value: l.id })))
      )
      .catch(() => setLessonOptions([]));
  }, []);

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    form.setFieldsValue({
      answers: [
        { answerText: '', correct: true, orderIndex: 1 },
        { answerText: '', correct: false, orderIndex: 2 },
      ],
    } as unknown as ExerciseRequest);
    setModalOpen(true);
  };

  const openEdit = (exercise: AdminExercise) => {
    setEditing(exercise);
    form.setFieldsValue({
      lessonId: exercise.lessonId,
      levelId: exercise.levelId,
      type: exercise.type,
      question: exercise.question,
      explanation: exercise.explanation,
      audioUrl: exercise.audioUrl,
      imageUrl: exercise.imageUrl,
      difficulty: exercise.difficulty,
      answers: exercise.answers.map((a) => ({
        answerText: a.answerText,
        correct: a.correct,
        orderIndex: a.orderIndex,
      })),
    });
    setModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await exerciseApi.remove(id);
      message.success('Exercise deleted');
      await fetchData();
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to delete exercise'));
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      if (!values.answers.some((a) => a.correct)) {
        message.error('At least one answer must be marked correct');
        return;
      }

      const payload: ExerciseRequest = {
        ...values,
        answers: values.answers.map((a, index) => ({ ...a, orderIndex: index + 1 })),
      };

      setSubmitting(true);
      if (editing) {
        await exerciseApi.update(editing.id, payload);
        message.success('Exercise updated');
      } else {
        await exerciseApi.create(payload);
        message.success('Exercise created');
      }
      setModalOpen(false);
      await fetchData();
    } catch (err) {
      if (err && typeof err === 'object' && 'errorFields' in err) {
        return;
      }
      message.error(getErrorMessage(err, 'Failed to save exercise'));
    } finally {
      setSubmitting(false);
    }
  };

  const columns: ColumnsType<AdminExercise> = [
    { title: 'Question', dataIndex: 'question', ellipsis: true },
    {
      title: 'Type',
      dataIndex: 'type',
      width: 160,
      render: (type: AdminExercise['type']) => <Tag>{type.replaceAll('_', ' ')}</Tag>,
    },
    {
      title: 'Difficulty',
      dataIndex: 'difficulty',
      width: 110,
      render: (difficulty: AdminExercise['difficulty']) => (
        <Tag color={EXERCISE_DIFFICULTY_COLOR[difficulty]}>{difficulty}</Tag>
      ),
    },
    { title: 'Level', dataIndex: 'levelCode', width: 80 },
    {
      title: 'Actions',
      key: 'actions',
      fixed: 'right',
      width: 140,
      render: (_, record) => (
        <Space>
          <Button size="small" icon={<EditOutlined />} onClick={() => openEdit(record)}>
            Edit
          </Button>
          <Popconfirm
            title="Delete this exercise?"
            onConfirm={() => handleDelete(record.id)}
            okText="Delete"
            okButtonProps={{ danger: true }}
          >
            <Button size="small" danger icon={<DeleteOutlined />}>
              Delete
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: 16,
          flexWrap: 'wrap',
          gap: 12,
        }}
      >
        <Title level={3} style={{ margin: 0 }}>
          Exercise management
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
          Add Exercise
        </Button>
      </div>

      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="Search question..."
          allowClear
          style={{ width: 260 }}
          onSearch={(value) => {
            setPage(0);
            setKeyword(value);
          }}
        />
        <Select
          placeholder="Level"
          allowClear
          style={{ width: 180 }}
          options={levelOptions}
          onChange={(value) => {
            setPage(0);
            setLevelFilter(value);
          }}
        />
      </Space>

      {error ? (
        <ErrorState message={error} onRetry={fetchData} />
      ) : (
        <Table<AdminExercise>
          rowKey="id"
          columns={columns}
          dataSource={data}
          loading={loading}
          scroll={{ x: 'max-content' }}
          pagination={{
            current: page + 1,
            pageSize,
            total,
            showSizeChanger: true,
            onChange: (newPage, newPageSize) => {
              setPage(newPage - 1);
              setPageSize(newPageSize);
            },
          }}
        />
      )}

      <Modal
        title={editing ? 'Edit Exercise' : 'Add Exercise'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        confirmLoading={submitting}
        destroyOnHidden
        width={720}
      >
        <Form<ExerciseRequest> form={form} layout="vertical">
          <Form.Item
            name="levelId"
            label="Level"
            rules={[{ required: true, message: 'Level is required' }]}
          >
            <Select options={levelOptions} />
          </Form.Item>
          <Form.Item name="lessonId" label="Lesson (optional)">
            <Select options={lessonOptions} allowClear />
          </Form.Item>
          <Form.Item
            name="type"
            label="Type"
            rules={[{ required: true, message: 'Type is required' }]}
          >
            <Select options={EXERCISE_TYPE_OPTIONS} />
          </Form.Item>
          <Form.Item
            name="difficulty"
            label="Difficulty"
            rules={[{ required: true, message: 'Difficulty is required' }]}
          >
            <Select options={EXERCISE_DIFFICULTY_OPTIONS} />
          </Form.Item>
          <Form.Item
            name="question"
            label="Question"
            rules={[{ required: true, message: 'Question is required' }]}
          >
            <TextArea rows={2} />
          </Form.Item>
          <Form.Item name="explanation" label="Explanation (shown after submission)">
            <TextArea rows={2} />
          </Form.Item>
          <Form.Item name="audioUrl" label="Audio URL (optional)">
            <Input />
          </Form.Item>
          <Form.Item name="imageUrl" label="Image URL (optional)">
            <Input />
          </Form.Item>

          <Form.Item label="Answers" required>
            <Form.List name="answers">
              {(fieldsList, { add, remove }) => (
                <>
                  {fieldsList.map((field) => (
                    <Space
                      key={field.key}
                      align="baseline"
                      style={{ display: 'flex', marginBottom: 8 }}
                    >
                      <Form.Item
                        name={[field.name, 'answerText']}
                        rules={[{ required: true, message: 'Answer text is required' }]}
                        style={{ marginBottom: 0, width: 380 }}
                      >
                        <Input placeholder="Answer text" />
                      </Form.Item>
                      <Form.Item
                        name={[field.name, 'correct']}
                        valuePropName="checked"
                        style={{ marginBottom: 0 }}
                      >
                        <Checkbox>Correct</Checkbox>
                      </Form.Item>
                      {fieldsList.length > 2 && (
                        <MinusCircleOutlined onClick={() => remove(field.name)} />
                      )}
                    </Space>
                  ))}
                  <Button
                    type="dashed"
                    onClick={() =>
                      add({ answerText: '', correct: false, orderIndex: fieldsList.length + 1 })
                    }
                    block
                    icon={<PlusOutlined />}
                  >
                    Add answer
                  </Button>
                </>
              )}
            </Form.List>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
