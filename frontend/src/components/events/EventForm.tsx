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
  description: z.string().optional(),
  recipientName: z.string().optional(),
  relationship: z.string().optional(),
  eventDate: z.string().min(1, "날짜는 필수입니다"),
  eventType: z.string().min(1, "이벤트 타입은 필수입니다"),
  isRecurring: z.boolean().optional(),
  isTracking: z.boolean().optional(),
  reminderDays: z.array(z.number()).optional(),
});

type EventFormData = z.infer<typeof eventSchema>;

interface EventFormProps {
  onSubmit: (data: CreateEventRequest) => void;
  onCancel: () => void;
  isLoading?: boolean;
  defaultValues?: Partial<EventFormData>;
}

const EVENT_TYPE_OPTIONS = [
  { value: "", label: "선택하세요" },
  { value: "BIRTHDAY", label: "생일" },
  { value: "ANNIVERSARY_100", label: "100일 기념일" },
  { value: "ANNIVERSARY_200", label: "200일 기념일" },
  { value: "ANNIVERSARY_1YEAR", label: "1주년" },
  { value: "ANNIVERSARY_CUSTOM", label: "커스텀 기념일" },
  { value: "VALENTINES_DAY", label: "발렌타인데이" },
  { value: "WHITE_DAY", label: "화이트데이" },
  { value: "PEPERO_DAY", label: "빼빼로데이" },
  { value: "CHRISTMAS", label: "크리스마스" },
  { value: "HOLIDAY", label: "기타 공휴일" },
  { value: "CUSTOM", label: "사용자 정의" },
];

const REMINDER_OPTIONS = [
  { value: 30, label: "30일 전" },
  { value: 14, label: "14일 전" },
  { value: 7, label: "7일 전" },
  { value: 3, label: "3일 전" },
  { value: 1, label: "1일 전" },
];

export const EventForm = ({ onSubmit, onCancel, isLoading, defaultValues }: EventFormProps) => {
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<EventFormData>({
    resolver: zodResolver(eventSchema),
    defaultValues: {
      title: defaultValues?.title || "",
      description: defaultValues?.description || "",
      recipientName: defaultValues?.recipientName || "",
      relationship: defaultValues?.relationship || "",
      eventDate: defaultValues?.eventDate || "",
      eventType: defaultValues?.eventType || "",
      isRecurring: defaultValues?.isRecurring || false,
      isTracking: defaultValues?.isTracking !== undefined ? defaultValues.isTracking : true,
      reminderDays: defaultValues?.reminderDays || [],
    },
  });

  const selectedReminders = watch("reminderDays") || [];

  const handleReminderToggle = (value: number) => {
    const currentReminders = selectedReminders;
    if (currentReminders.includes(value)) {
      setValue(
        "reminderDays",
        currentReminders.filter((r) => r !== value)
      );
    } else {
      setValue("reminderDays", [...currentReminders, value]);
    }
  };

  const handleFormSubmit = (data: EventFormData) => {
    // isRecurring과 isTracking의 기본값 명시적으로 설정
    const submitData = {
      ...data,
      isRecurring: data.isRecurring ?? false,
      isTracking: data.isTracking ?? true,
    };
    onSubmit(submitData);
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
        label="대상자명"
        placeholder="예: 김철수"
        error={errors.recipientName?.message}
        {...register("recipientName")}
      />

      <Input
        label="관계"
        placeholder="예: 아버지, 친구"
        error={errors.relationship?.message}
        {...register("relationship")}
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

      <Textarea
        label="설명"
        placeholder="이벤트 설명을 입력하세요..."
        rows={4}
        error={errors.description?.message}
        {...register("description")}
      />

      <div className="space-y-3">
        <Checkbox
          id="isRecurring"
          label="매년 반복"
          {...register("isRecurring")}
        />
        <Checkbox
          id="isTracking"
          label="이 이벤트 추적하기"
          {...register("isTracking")}
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
