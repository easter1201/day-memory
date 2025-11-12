import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "../ui/Button";
import { Input } from "../ui/Input";
import { Textarea } from "../ui/Textarea";
import { useGetEventsQuery } from "../../store/services/eventsApi";
import { GIFT_CATEGORIES } from "../../constants";
import type { RecommendationRequest } from "../../types/recommendation";

const recommendationSchema = z.object({
  eventId: z.number({
    required_error: "이벤트를 선택해주세요",
  }),
  budget: z
    .number({
      required_error: "예산을 입력해주세요",
      invalid_type_error: "올바른 숫자를 입력해주세요",
    })
    .min(1000, "예산은 최소 1,000원 이상이어야 합니다")
    .max(100000000, "예산은 1억 이하로 입력해주세요"),
  preferredCategories: z
    .array(z.string())
    .min(1, "최소 1개 이상의 카테고리를 선택해주세요"),
  additionalMessage: z.string().max(500, "메시지는 최대 500자까지 입력 가능합니다").optional(),
});

type RecommendationFormData = z.infer<typeof recommendationSchema>;

interface RecommendationFormProps {
  onSubmit: (data: RecommendationRequest) => void;
  onCancel: () => void;
  isLoading?: boolean;
}

export const RecommendationForm = ({ onSubmit, onCancel, isLoading }: RecommendationFormProps) => {
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<RecommendationFormData>({
    resolver: zodResolver(recommendationSchema),
    defaultValues: {
      preferredCategories: [],
      additionalMessage: "",
    },
  });

  const { data: eventsData } = useGetEventsQuery({
    page: 0,
    size: 100,
  });

  const selectedCategories = watch("preferredCategories") || [];

  const handleCategoryToggle = (category: string) => {
    const current = selectedCategories;
    if (current.includes(category)) {
      setValue(
        "preferredCategories",
        current.filter((c) => c !== category)
      );
    } else {
      setValue("preferredCategories", [...current, category]);
    }
  };

  const handleFormSubmit = (data: RecommendationFormData) => {
    onSubmit({
      eventId: data.eventId,
      budget: data.budget,
      preferredCategories: data.preferredCategories,
      additionalMessage: data.additionalMessage || undefined,
    });
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
      {/* 이벤트 선택 */}
      <div>
        <label htmlFor="eventId" className="mb-2 block text-sm font-medium">
          이벤트 선택 <span className="text-red-500">*</span>
        </label>
        <select
          id="eventId"
          {...register("eventId", { valueAsNumber: true })}
          className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
        >
          <option value="">이벤트를 선택하세요</option>
          {eventsData?.content.map((event) => (
            <option key={event.id} value={event.id}>
              {event.title} - {event.recipientName}
            </option>
          ))}
        </select>
        {errors.eventId && <p className="mt-1 text-sm text-red-500">{errors.eventId.message}</p>}
      </div>

      {/* 예산 입력 */}
      <div>
        <label htmlFor="budget" className="mb-2 block text-sm font-medium">
          예산 (원) <span className="text-red-500">*</span>
        </label>
        <Input
          id="budget"
          type="number"
          {...register("budget", { valueAsNumber: true })}
          placeholder="예산을 입력하세요"
          error={errors.budget?.message}
        />
      </div>

      {/* 선호 카테고리 선택 */}
      <div>
        <label className="mb-2 block text-sm font-medium">
          선호 카테고리 (다중 선택) <span className="text-red-500">*</span>
        </label>
        <div className="grid grid-cols-2 gap-2 md:grid-cols-4">
          {GIFT_CATEGORIES.map((category) => (
            <button
              key={category.value}
              type="button"
              onClick={() => handleCategoryToggle(category.value)}
              className={`rounded-md border px-4 py-2 text-sm font-medium transition-colors ${
                selectedCategories.includes(category.value)
                  ? "border-primary bg-primary text-primary-foreground"
                  : "border-input bg-background hover:bg-accent"
              }`}
            >
              {category.label}
            </button>
          ))}
        </div>
        {errors.preferredCategories && (
          <p className="mt-1 text-sm text-red-500">{errors.preferredCategories.message}</p>
        )}
      </div>

      {/* 추가 메시지 */}
      <div>
        <label htmlFor="additionalMessage" className="mb-2 block text-sm font-medium">
          추가 메시지 (선택)
        </label>
        <Textarea
          id="additionalMessage"
          {...register("additionalMessage")}
          placeholder="AI에게 전달할 추가 정보를 입력하세요 (예: 취미, 선호 브랜드 등)"
          rows={4}
        />
        {errors.additionalMessage && (
          <p className="mt-1 text-sm text-red-500">{errors.additionalMessage.message}</p>
        )}
      </div>

      {/* 버튼 */}
      <div className="flex space-x-3">
        <Button type="submit" disabled={isLoading} className="flex-1">
          {isLoading ? "AI가 추천하는 중..." : "추천 받기"}
        </Button>
        <Button type="button" variant="outline" onClick={onCancel} disabled={isLoading} className="flex-1">
          취소
        </Button>
      </div>
    </form>
  );
};
