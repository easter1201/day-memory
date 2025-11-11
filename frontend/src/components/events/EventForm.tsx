import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Input } from "../ui/Input";
import { Select } from "../ui/Select";
import { Textarea } from "../ui/Textarea";
import { Checkbox } from "../ui/Checkbox";
import { Button } from "../ui/Button";
import type { CreateEventRequest } from "../../types/event";

const eventSchema = z.object({
  title: z.string().min(1, "이벤트명은 필수입니다"),
  eventDate: z.string().min(1, "날짜는 필수입니다"),
  eventType: z.string().min(1, "이벤트 타입은 필수입니다"),
  recipientName: z.string().min(1, "대상자명은 필수입니다"),
  relationship: z.string().optional(),
  memo: z.string().optional(),
  isTracked: z.boolean(),
  reminders: z.array(z.number()).optional(),
});

type EventFormData = z.infer<typeof eventSchema>;

interface EventFormProps {
  onSubmit: (data: CreateEventRequest) => void;
  onCancel: () => void;
  isLoading?: boolean;
}

const EVENT_TYPE_OPTIONS = [
  { value: "", label: "선택하세요" },
  { value: "BIRTHDAY", label: "생일" },
  { value: "ANNIVERSARY", label: "기념일" },
  { value: "HOLIDAY", label: "명절" },
  { value: "OTHER", label: "기타" },
];

const RELATIONSHIP_OPTIONS = [
  { value: "", label: "선택하세요" },
  { value: "FAMILY", label: "가족" },
  { value: "FRIEND", label: "친구" },
  { value: "COLLEAGUE", label: "동료" },
  { value: "ACQUAINTANCE", label: "지인" },
  { value: "OTHER", label: "기타" },
];

const REMINDER_OPTIONS = [
  { value: 30, label: "30일 전" },
  { value: 14, label: "14일 전" },
  { value: 7, label: "7일 전" },
  { value: 3, label: "3일 전" },
  { value: 1, label: "1일 전" },
];

export const EventForm = ({ onSubmit, onCancel, isLoading }: EventFormProps) => {
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<EventFormData>({
    resolver: zodResolver(eventSchema),
    defaultValues: {
      title: "",
      eventDate: "",
      eventType: "",
      recipientName: "",
      relationship: "",
      memo: "",
      isTracked: true,
      reminders: [],
    },
  });

  const selectedReminders = watch("reminders") || [];

  const handleReminderToggle = (value: number) => {
    const currentReminders = selectedReminders;
    if (currentReminders.includes(value)) {
      setValue(
        "reminders",
        currentReminders.filter((r) => r !== value)
      );
    } else {
      setValue("reminders", [...currentReminders, value]);
    }
  };

  const handleFormSubmit = (data: EventFormData) => {
    onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
      <Input
        label="이벤트명 *"
        placeholder="예: 엄마 생신"
        error={errors.title?.message}
        {...register("title")}
      />

      <Input
        label="날짜 *"
        type="date"
        error={errors.eventDate?.message}
        {...register("eventDate")}
      />

      <Select
        label="이벤트 타입 *"
        options={EVENT_TYPE_OPTIONS}
        error={errors.eventType?.message}
        {...register("eventType")}
      />

      <Input
        label="대상자 *"
        placeholder="예: 홍길동"
        error={errors.recipientName?.message}
        {...register("recipientName")}
      />

      <Select
        label="관계"
        options={RELATIONSHIP_OPTIONS}
        error={errors.relationship?.message}
        {...register("relationship")}
      />

      <Textarea
        label="메모"
        placeholder="메모를 입력하세요..."
        rows={4}
        error={errors.memo?.message}
        {...register("memo")}
      />

      <div className="space-y-3">
        <Checkbox
          id="isTracked"
          label="이 이벤트 추적하기"
          {...register("isTracked")}
        />
      </div>

      <div className="space-y-3">
        <label className="text-sm font-medium">리마인더 설정</label>
        <div className="space-y-2">
          {REMINDER_OPTIONS.map((option) => (
            <Checkbox
              key={option.value}
              id={`reminder-${option.value}`}
              label={option.label}
              checked={selectedReminders.includes(option.value)}
              onChange={() => handleReminderToggle(option.value)}
            />
          ))}
        </div>
      </div>

      <div className="flex space-x-3">
        <Button type="submit" className="flex-1" isLoading={isLoading}>
          저장
        </Button>
        <Button
          type="button"
          variant="outline"
          className="flex-1"
          onClick={onCancel}
          disabled={isLoading}
        >
          취소
        </Button>
      </div>
    </form>
  );
};
