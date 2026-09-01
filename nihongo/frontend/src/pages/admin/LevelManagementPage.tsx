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
  message,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { levelApi } from '../../api/levelApi';
import { ErrorState } from '../../components/ErrorState';
import { getErrorMessage } from '../../utils/errors';
import { CONTENT_STATUS_OPTIONS } from '../../types/content';
import type { Level, LevelRequest } from '../../types/content';

const { Title } = Typography;

/**
 * Levels are reference data (only N5-N1) so this skips server-side
 * pagination/search and just manages the full list in memory - the generic
 * CrudManager is for the larger, paginated resources (Lesson/Vocabulary/
 * Kanji/Grammar).
 */
export default function LevelManagementPage() {
  const [levels, setLevels] = useState<Level[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Level | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm<LevelRequest>();

  const fetchLevels = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await levelApi.listAdmin();
      setLevels(response.data.data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load levels'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void fetchLevels();
  }, [fetchLevels]);

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    setModalOpen(true);
  };

  const openEdit = (level: Level) => {
    setEditing(level);
    form.setFieldsValue(level);
    setModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await levelApi.remove(id);
      message.success('Level deleted');
      await fetchLevels();
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to delete level'));
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);
      if (editing) {
        await levelApi.update(editing.id, values);
        message.success('Level updated');
      } else {
        await levelApi.create(values);
        message.success('Level created');
      }
      setModalOpen(false);
      await fetchLevels();
    } catch (err) {
      if (err && typeof err === 'object' && 'errorFields' in err) {
        return;
      }
      message.error(getErrorMessage(err, 'Failed to save level'));
    } finally {
      setSubmitting(false);
    }
  };

  const columns: ColumnsType<Level> = [
    {
      title: 'Order',
      dataIndex: 'orderIndex',
      width: 80,
      sorter: (a, b) => a.orderIndex - b.orderIndex,
    },
    { title: 'Code', dataIndex: 'code', width: 100 },
    { title: 'Name', dataIndex: 'name' },
    { title: 'Status', dataIndex: 'status', width: 120 },
    {
      title: 'Actions',
      key: 'actions',
      width: 140,
      render: (_, record) => (
        <Space>
          <Button size="small" icon={<EditOutlined />} onClick={() => openEdit(record)}>
            Edit
          </Button>
          <Popconfirm
            title="Delete this level?"
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
        }}
      >
        <Title level={3} style={{ margin: 0 }}>
          Level management
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
          Add Level
        </Button>
      </div>

      {error ? (
        <ErrorState message={error} onRetry={fetchLevels} />
      ) : (
        <Table<Level>
          rowKey="id"
          columns={columns}
          dataSource={levels}
          loading={loading}
          pagination={false}
        />
      )}

      <Modal
        title={editing ? 'Edit Level' : 'Add Level'}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        confirmLoading={submitting}
        destroyOnHidden
      >
        <Form<LevelRequest> form={form} layout="vertical">
          <Form.Item
            name="code"
            label="Code"
            rules={[{ required: true, message: 'Code is required' }]}
          >
            <Input placeholder="N5" />
          </Form.Item>
          <Form.Item
            name="name"
            label="Name"
            rules={[{ required: true, message: 'Name is required' }]}
          >
            <Input placeholder="JLPT N5" />
          </Form.Item>
          <Form.Item name="description" label="Description">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item
            name="orderIndex"
            label="Order index"
            rules={[{ required: true, message: 'Order index is required' }]}
          >
            <InputNumber style={{ width: '100%' }} min={1} />
          </Form.Item>
          <Form.Item
            name="status"
            label="Status"
            rules={[{ required: true, message: 'Status is required' }]}
          >
            <Select options={CONTENT_STATUS_OPTIONS} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
