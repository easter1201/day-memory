import { z } from "zod";

// 로그인 스키마
export const loginSchema = z.object({
  email: z
    .string()
    .min(1, "이메일을 입력해주세요")
    .email("올바른 이메일 형식이 아닙니다"),
  password: z
    .string()
    .min(1, "비밀번호를 입력해주세요")
    .min(8, "비밀번호는 최소 8자 이상이어야 합니다"),
});

export type LoginFormData = z.infer<typeof loginSchema>;

// 회원가입 스키마
export const signupSchema = z
  .object({
    email: z
      .string()
      .min(1, "이메일을 입력해주세요")
      .email("올바른 이메일 형식이 아닙니다"),
    password: z
      .string()
      .min(1, "비밀번호를 입력해주세요")
      .min(8, "비밀번호는 최소 8자 이상이어야 합니다")
      .regex(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
        "비밀번호는 영문 대소문자와 숫자를 포함해야 합니다"
      ),
    passwordConfirm: z.string().min(1, "비밀번호 확인을 입력해주세요"),
    name: z
      .string()
      .min(1, "이름을 입력해주세요")
      .min(2, "이름은 최소 2자 이상이어야 합니다")
      .max(20, "이름은 최대 20자까지 입력 가능합니다"),
    agreeToTerms: z.boolean().refine((val) => val === true, {
      message: "서비스 이용약관에 동의해주세요",
    }),
  })
  .refine((data) => data.password === data.passwordConfirm, {
    message: "비밀번호가 일치하지 않습니다",
    path: ["passwordConfirm"],
  });

export type SignupFormData = z.infer<typeof signupSchema>;

// 이벤트 스키마
export const eventSchema = z.object({
  title: z
    .string()
    .min(1, "이벤트 제목을 입력해주세요")
    .max(100, "제목은 최대 100자까지 입력 가능합니다"),
  eventDate: z.string().min(1, "이벤트 날짜를 선택해주세요"),
  eventType: z.string().min(1, "이벤트 타입을 선택해주세요"),
  recipientName: z
    .string()
    .min(1, "받는 사람 이름을 입력해주세요")
    .max(50, "이름은 최대 50자까지 입력 가능합니다"),
  relationship: z.string().optional(),
  memo: z.string().max(500, "메모는 최대 500자까지 입력 가능합니다").optional(),
  isTracked: z.boolean(),
  reminders: z.array(z.number()).optional(),
});

export type EventFormData = z.infer<typeof eventSchema>;

// 선물 스키마
export const giftSchema = z.object({
  name: z
    .string()
    .min(1, "선물명을 입력해주세요")
    .max(100, "선물명은 최대 100자까지 입력 가능합니다"),
  category: z.string().min(1, "카테고리를 선택해주세요"),
  price: z
    .number({
      required_error: "가격을 입력해주세요",
      invalid_type_error: "올바른 숫자를 입력해주세요",
    })
    .min(0, "가격은 0 이상이어야 합니다")
    .max(100000000, "가격은 1억 이하로 입력해주세요"),
  url: z
    .string()
    .url("올바른 URL 형식이 아닙니다")
    .optional()
    .or(z.literal("")),
  memo: z.string().max(500, "메모는 최대 500자까지 입력 가능합니다").optional(),
  isPurchased: z.boolean(),
  eventId: z.number().optional(),
});

export type GiftFormData = z.infer<typeof giftSchema>;

// 비밀번호 재설정 스키마
export const passwordResetSchema = z.object({
  email: z
    .string()
    .min(1, "이메일을 입력해주세요")
    .email("올바른 이메일 형식이 아닙니다"),
});

export type PasswordResetFormData = z.infer<typeof passwordResetSchema>;

// 비밀번호 변경 스키마 (재설정 링크로 접근 시)
export const passwordChangeSchema = z
  .object({
    password: z
      .string()
      .min(1, "새 비밀번호를 입력해주세요")
      .min(8, "비밀번호는 최소 8자 이상이어야 합니다")
      .regex(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
        "비밀번호는 영문 대소문자와 숫자를 포함해야 합니다"
      ),
    passwordConfirm: z.string().min(1, "비밀번호 확인을 입력해주세요"),
  })
  .refine((data) => data.password === data.passwordConfirm, {
    message: "비밀번호가 일치하지 않습니다",
    path: ["passwordConfirm"],
  });

export type PasswordChangeFormData = z.infer<typeof passwordChangeSchema>;
