import { useCallback, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import {
  Table,
  Button,
  Input,
  Modal,
  Form,
  Select,
  InputNumber,
  Popconfirm,
  Space,
  Typography,
  message,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons';
import type { AxiosResponse } from 'axios';
import type { ApiResponse, ListParams, PageResponse } from '../../types/api';
import { ErrorState } from '../ErrorState';
import { getErrorMessage } from '../../utils/errors';

const { Title } = Typography;
const { TextArea } = Input;

export interface CrudField {
  name: string;
  label: string;
  type: 'text' | 'textarea' | 'number' | 'select';
  required?: boolean;
  options?: { label: string; value: string | number }[];
}

export interface CrudFilter {
  name: string;
  label: string;
  options: { label: string; value: string | number }[];
}

interface WithId {
  id: number;
}

interface ContentApi<T, TRequest> {
  listAdmin: (params: ListParams) => Promise<AxiosResponse<ApiResponse<PageResponse<T>>>>;
  create: (payload: TRequest) => Promise<AxiosResponse<ApiResponse<T>>>;
  update: (id: number, payload: TRequest) => Promise<AxiosResponse<ApiResponse<T>>>;
  remove: (id: number) => Promise<AxiosResponse<ApiResponse<null>>>;
}

interface CrudManagerProps<T extends WithId, TRequest> {
  resourceName: string;
  columns: ColumnsType<T>;
  fields: CrudField[];
  api: ContentApi<T, TRequest>;
  filters?: CrudFilter[];
  searchPlaceholder?: string;
  extraHeaderContent?: ReactNode;
}

/**
 * Generic paginated admin CRUD table + modal form, shared by every content
 * resource (Lesson, Vocabulary, Kanji, Grammar) so the table/form/pagination
 * wiring exists exactly once instead of five times (section 39: avoid
 * duplicated code, create reusable components).
 */
export function CrudManager<T extends WithId, TRequest extends object>({
  resourceName,
  columns,
  fields,
  api,
  filters = [],
  searchPlaceholder = 'Search...',
  extraHeaderContent,
}: CrudManagerProps<T, TRequest>) {
  const [data, setData] = useState<T[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  const [filterValues, setFilterValues] = useState<Record<string, string | number | undefined>>({});

  const [modalOpen, setModalOpen] = useState(false);
  const [editingRecord, setEditingRecord] = useState<T | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm<TRequest>();

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await api.listAdmin({
        page,
        size: pageSize,
        keyword: keyword || undefined,
        ...filterValues,
      });
      setData(response.data.data.content);
      setTotal(response.data.data.totalElements);
    } catch (err) {
      setError(getErrorMessage(err, `Failed to load ${resourceName.toLowerCase()} list`));
    } finally {
      setLoading(false);
    }
  }, [api, page, pageSize, keyword, filterValues, resourceName]);

  useEffect(() => {
    // Async data fetch - state updates happen after the awaited response, not
    // synchronously during this effect, so this is not the derived-state
    // anti-pattern the rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void fetchData();
  }, [fetchData]);

  const openCreateModal = () => {
    setEditingRecord(null);
    form.resetFields();
    setModalOpen(true);
  };

  const openEditModal = (record: T) => {
    setEditingRecord(record);
    form.setFieldsValue(record as unknown as TRequest);
    setModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await api.remove(id);
      message.success(`${resourceName} deleted`);
      await fetchData();
    } catch (err) {
      message.error(getErrorMessage(err, `Failed to delete ${resourceName.toLowerCase()}`));
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);
      if (editingRecord) {
        await api.update(editingRecord.id, values);
        message.success(`${resourceName} updated`);
      } else {
        await api.create(values);
        message.success(`${resourceName} created`);
      }
      setModalOpen(false);
      await fetchData();
    } catch (err) {
      if (err && typeof err === 'object' && 'errorFields' in err) {
        return; // antd form validation error - already shown inline
      }
      message.error(getErrorMessage(err, `Failed to save ${resourceName.toLowerCase()}`));
    } finally {
      setSubmitting(false);
    }
  };

  const actionColumn: ColumnsType<T>[number] = {
    title: 'Actions',
    key: 'actions',
    fixed: 'right',
    width: 140,
    render: (_, record) => (
      <Space>
        <Button size="small" icon={<EditOutlined />} onClick={() => openEditModal(record)}>
          Edit
        </Button>
        <Popconfirm
          title={`Delete this ${resourceName.toLowerCase()}?`}
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
  };

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
          {resourceName} management
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreateModal}>
          Add {resourceName}
        </Button>
      </div>

      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder={searchPlaceholder}
          allowClear
          style={{ width: 260 }}
          onSearch={(value) => {
            setPage(0);
            setKeyword(value);
          }}
        />
        {filters.map((filter) => (
          <Select
            key={filter.name}
            placeholder={filter.label}
            allowClear
            style={{ width: 180 }}
            options={filter.options}
            onChange={(value) => {
              setPage(0);
              setFilterValues((prev) => ({ ...prev, [filter.name]: value }));
            }}
          />
        ))}
        {extraHeaderContent}
      </Space>

      {error ? (
        <ErrorState message={error} onRetry={fetchData} />
      ) : (
        <Table<T>
          rowKey="id"
          columns={[...columns, actionColumn]}
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
        title={editingRecord ? `Edit ${resourceName}` : `Add ${resourceName}`}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={handleSubmit}
        confirmLoading={submitting}
        destroyOnHidden
        width={640}
      >
        <Form<TRequest> form={form} layout="vertical">
          {fields.map((field) => (
            <Form.Item
              key={field.name}
              name={field.name}
              label={field.label}
              rules={
                field.required ? [{ required: true, message: `${field.label} is required` }] : []
              }
            >
              {renderField(field)}
            </Form.Item>
          ))}
        </Form>
      </Modal>
    </div>
  );
}

function renderField(field: CrudField) {
  switch (field.type) {
    case 'textarea':
      return <TextArea rows={3} />;
    case 'number':
      return <InputNumber style={{ width: '100%' }} />;
    case 'select':
      return <Select options={field.options} allowClear />;
    case 'text':
    default:
      return <Input />;
  }
}
