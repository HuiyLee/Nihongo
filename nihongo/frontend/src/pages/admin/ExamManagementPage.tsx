import { useCallback, useEffect, useState } from 'react';
import {
  Table,
  Button,
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  Popconfirm,
  Space,
  Typography,
  Tag,
  message,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined, MinusCircleOutlined } from '@ant-design/icons';
import { examApi } from '../../api/examApi';
import { exerciseApi } from '../../api/exerciseApi';
import { levelApi } from '../../api/levelApi';
import { ErrorState } from '../../components/ErrorState';
import { getErrorMessage } from '../../utils/errors';
import { CONTENT_STATUS_OPTIONS } from '../../types/content';
import type { AdminExam, ExamRequest } from '../../types/exam';

const { Title } = Typography;
const { TextArea } = Input;

/**
 * Exams need a dynamic questions list (Form.List, each row picking an
 * existing Exercise), which the generic CrudManager can't express - same
 * reasoning as ExerciseManagementPage being standalone.
 */
export default function ExamManagementPage() {
  const [data, setData] = useState<AdminExam[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [levelFilter, setLevelFilter] = useState<number | undefined>(undefined);

  const [levelOptions, setLevelOptions] = useState<{ label: string; value: number }[]>([]);
  const [exerciseOptions, setExerciseOptions] = useState<{ label: string; value: number }[]>([]);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<AdminExam | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm<ExamRequest>();

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await examApi.listAdmin({
        page,
        size: pageSize,
        keyword: keyword || undefined,
        levelId: levelFilter,
      });
      setData(response.data.data.content);
      setTotal(response.data.data.totalElements);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load exams'));
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
    exerciseApi
      .listAdmin({ page: 0, size: 500 })
      .then((res) =>
        setExerciseOptions(
          res.data.data.content.map((e) => ({
            label: `[${e.levelCode}] ${e.question.slice(0, 60)}`,
            value: e.id,
          }))
        )
      )
      .catch(() => setExerciseOptions([]));
  }, []);

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    form.setFieldsValue({
      questions: [{ exerciseId: undefined, orderIndex: 1 }],
    } as unknown as ExamRequest);
    setModalOpen(true);
  };

  const openEdit = (exam: AdminExam) => {
    setEditing(exam);
    form.setFieldsValue({
      levelId: exam.levelId,
      title: exam.title,
      description: exam.description,
      durationMinutes: exam.durationMinutes,
      status: exam.status,
      questions: exam.questions
        .slice()
        .sort((a, b) => a.orderIndex - b.orderIndex)
        .map((q) => ({ exerciseId: q.exercise.id, orderIndex: q.orderIndex })),
    });
    setModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await examApi.remove(id);
      message.success('Exam deleted');
      await fetchData();
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to delete exam'));
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      const payload: ExamRequest = {
        ...values,
        questions: values.questions.map((q, index) => ({ ...q, orderIndex: index + 1 })),
      };

      setSubmitting(true);
      if (editing) {
        await examApi.update(editing.id, payload);
        message.success('Exam updated');
      } else {
        await examApi.create(payload);
        message.success('Exam created');
      }
      setModalOpen(false);
      await fetchData();
    } catch (err) {
      if (err && typeof err === 'object' && 'errorFields' in err) {
        return;
      }
      message.error(getErrorMessage(err, 'Failed to save exam'));
    } finally {
      setSubmitting(false);
    }
  };

  const columns: ColumnsType<AdminExam> = [
    { title: 'Title', dataIndex: 'title', ellipsis: true },
    { title: 'Level', dataIndex: 'levelCode', width: 80 },
    { title: 'Duration (min)', dataIndex: 'durationMinutes', width: 130 },
    { title: 'Questions', dataIndex: 'totalQuestions', width: 100 },
    {
      title: 'Status',
      dataIndex: 'status',
      width: 110,
      render: (status: AdminExam['status']) => (
        <Tag color={status === 'PUBLISHED' ? 'green' : status === 'DRAFT' ? 'gold' : 'default'}>
          {status}
        </Tag>
      ),
    },
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
            title="Delete this exam?"
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
          Exam management
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
          Add Exam
        </Button>
      </div>

      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="Search title..."
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
        <Table<AdminExam>
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
        title={editing ? 'Edit Exam' : 'Add Exam'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        confirmLoading={submitting}
        destroyOnHidden
        width={760}
      >
        <Form<ExamRequest> form={form} layout="vertical">
          <Form.Item
            name="levelId"
            label="Level"
            rules={[{ required: true, message: 'Level is required' }]}
          >
            <Select options={levelOptions} />
          </Form.Item>
          <Form.Item
            name="title"
            label="Title"
            rules={[{ required: true, message: 'Title is required' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item name="description" label="Description">
            <TextArea rows={2} />
          </Form.Item>
          <Form.Item
            name="durationMinutes"
            label="Duration (minutes)"
            rules={[{ required: true, message: 'Duration is required' }]}
          >
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item
            name="status"
            label="Status"
            rules={[{ required: true, message: 'Status is required' }]}
          >
            <Select options={CONTENT_STATUS_OPTIONS} />
          </Form.Item>

          <Form.Item label="Questions" required>
            <Form.List name="questions">
              {(fieldsList, { add, remove }) => (
                <>
                  {fieldsList.map((field) => (
                    <Space
                      key={field.key}
                      align="baseline"
                      style={{ display: 'flex', marginBottom: 8 }}
                    >
                      <Form.Item
                        name={[field.name, 'exerciseId']}
                        rules={[{ required: true, message: 'Pick an exercise' }]}
                        style={{ marginBottom: 0, width: 560 }}
                      >
                        <Select
                          placeholder="Select an exercise"
                          showSearch
                          optionFilterProp="label"
                          options={exerciseOptions}
                        />
                      </Form.Item>
                      {fieldsList.length > 1 && (
                        <MinusCircleOutlined onClick={() => remove(field.name)} />
                      )}
                    </Space>
                  ))}
                  <Button
                    type="dashed"
                    onClick={() =>
                      add({ exerciseId: undefined, orderIndex: fieldsList.length + 1 })
                    }
                    block
                    icon={<PlusOutlined />}
                  >
                    Add question
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
