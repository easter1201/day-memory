import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "../ui/Button";
import { Input } from "../ui/Input";
import { Textarea } from "../ui/Textarea";
import { Checkbox } from "../ui/Checkbox";
import { useGetEventsQuery } from "../../store/services/eventsApi";
import type { CreateGiftRequest } from "../../types/gift";

const giftSchema = z.object({
  name: z.string().min(1, "선물명을 입력해주세요"),
  category: z.string().min(1, "카테고리를 선택해주세요"),
  price: z.number().min(0, "가격은 0 이상이어야 합니다"),
  url: z.string().optional(),
  description: z.string().optional(),
  eventId: z.number().optional(),
});

type GiftFormData = z.infer<typeof giftSchema>;

interface GiftFormProps {
  onSubmit: (data: CreateGiftRequest) => void;
  onCancel: () => void;
  isLoading?: boolean;
  defaultValues?: Partial<GiftFormData>;
}

const CATEGORIES = [
  { value: "FLOWER", label: "꽃" },
  { value: "JEWELRY", label: "주얼리" },
  { value: "COSMETICS", label: "화장품" },
  { value: "FASHION", label: "패션" },
  { value: "ELECTRONICS", label: "전자기기" },
  { value: "FOOD", label: "음식/디저트" },
  { value: "EXPERIENCE", label: "체험/이벤트" },
  { value: "BOOK", label: "책" },
  { value: "HOBBY", label: "취미용품" },
  { value: "OTHER", label: "기타" },
];

export const GiftForm = ({ onSubmit, onCancel, isLoading, defaultValues }: GiftFormProps) => {
  const {
    register,
    handleSubmit,
    watch,
    setValue,
    formState: { errors },
  } = useForm<GiftFormData>({
    resolver: zodResolver(giftSchema),
    defaultValues: {
      name: defaultValues?.name || "",
      category: defaultValues?.category || "",
      price: defaultValues?.price || 0,
      url: defaultValues?.url || "",
      description: defaultValues?.description || "",
      eventId: defaultValues?.eventId || undefined,
    },
  });

  const { data: eventsData } = useGetEventsQuery({
    page: 0,
    size: 100,
  });

  const handleFormSubmit = (data: GiftFormData) => {
    const submitData: CreateGiftRequest = {
      name: data.name,
      category: data.category,
      price: data.price,
      url: data.url || undefined,
      description: data.description || undefined,
      eventId: data.eventId || undefined,
    };
    onSubmit(submitData);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
      <div>
        <label htmlFor="name" className="mb-2 block text-sm font-medium">
          선물명 <span className="text-red-500">*</span>
        </label>
        <Input
          id="name"
          {...register("name")}
          placeholder="선물명을 입력하세요"
          error={errors.name?.message}
        />
      </div>

      <div>
        <label htmlFor="category" className="mb-2 block text-sm font-medium">
          카테고리 <span className="text-red-500">*</span>
        </label>
        <select
          id="category"
          {...register("category")}
          className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
        >
          <option value="">카테고리를 선택하세요</option>
          {CATEGORIES.map((category) => (
            <option key={category.value} value={category.value}>
              {category.label}
            </option>
          ))}
        </select>
        {errors.category && (
          <p className="mt-1 text-sm text-red-500">{errors.category.message}</p>
        )}
      </div>

      <div>
        <label htmlFor="price" className="mb-2 block text-sm font-medium">
          가격 <span className="text-red-500">*</span>
        </label>
        <Input
          id="price"
          type="text"
          inputMode="numeric"
          {...register("price", {
            valueAsNumber: true,
            setValueAs: (v) => v === "" ? 0 : parseInt(v.replace(/[^0-9]/g, ""), 10)
          })}
          placeholder="가격을 입력하세요"
          error={errors.price?.message}
        />
      </div>

      <div>
        <label htmlFor="url" className="mb-2 block text-sm font-medium">
          URL
        </label>
        <Input
          id="url"
          {...register("url")}
          placeholder="구매 링크를 입력하세요"
          error={errors.url?.message}
        />
      </div>

      <div>
        <label htmlFor="description" className="mb-2 block text-sm font-medium">
          설명
        </label>
        <Textarea
          id="description"
          {...register("description")}
          placeholder="선물에 대한 설명을 입력하세요"
          rows={4}
        />
        {errors.description && (
          <p className="mt-1 text-sm text-red-500">{errors.description.message}</p>
        )}
      </div>

      <div>
        <label htmlFor="eventId" className="mb-2 block text-sm font-medium">
          연결 이벤트
        </label>
        <select
          id="eventId"
          {...register("eventId", { valueAsNumber: true })}
          className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
        >
          <option value="">이벤트를 선택하세요 (선택사항)</option>
          {eventsData?.content.map((event) => (
            <option key={event.id} value={event.id}>
              {event.title} {event.recipientName ? `(${event.recipientName})` : ""}
            </option>
          ))}
        </select>
        {errors.eventId && (
          <p className="mt-1 text-sm text-red-500">{errors.eventId.message}</p>
        )}
      </div>

      <div className="flex space-x-3">
        <Button type="submit" disabled={isLoading} className="flex-1">
          {isLoading ? "저장 중..." : "저장"}
        </Button>
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
          disabled={isLoading}
          className="flex-1"
        >
          취소
        </Button>
      </div>
    </form>
  );
};
