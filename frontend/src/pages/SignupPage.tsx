import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Link, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { AuthLayout } from "../components/layout/AuthLayout";
import { Input } from "../components/ui/Input";
import { Button } from "../components/ui/Button";
import { useSignupMutation } from "../store/services/authApi";

const signupSchema = z
  .object({
    email: z.string().email("유효한 이메일을 입력해주세요"),
    password: z
      .string()
      .min(8, "비밀번호는 최소 8자 이상이어야 합니다")
      .regex(
        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
        "비밀번호는 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다"
      ),
    passwordConfirm: z.string(),
    nickname: z
      .string()
      .min(2, "닉네임은 최소 2자 이상이어야 합니다")
      .max(20, "닉네임은 최대 20자까지 가능합니다"),
  })
  .refine((data) => data.password === data.passwordConfirm, {
    message: "비밀번호가 일치하지 않습니다",
    path: ["passwordConfirm"],
  });

type SignupFormData = z.infer<typeof signupSchema>;

export const SignupPage = () => {
  const navigate = useNavigate();
  const [signup, { isLoading }] = useSignupMutation();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<SignupFormData>({
    resolver: zodResolver(signupSchema),
  });

  const onSubmit = async (data: SignupFormData) => {
    try {
      await signup({
        email: data.email,
        password: data.password,
        nickname: data.nickname,
      }).unwrap();

      toast.success("회원가입이 완료되었습니다! 로그인해주세요.");
      navigate("/login");
    } catch (error: any) {
      const message =
        error?.data?.message ||
        "회원가입에 실패했습니다. 다시 시도해주세요.";
      toast.error(message);
    }
  };

  return (
    <AuthLayout
      title="회원가입"
      subtitle="Day Memory와 함께 특별한 순간을 기억하세요"
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="이메일"
          type="email"
          placeholder="email@example.com"
          error={errors.email?.message}
          {...register("email")}
        />

        <Input
          label="비밀번호"
          type="password"
          placeholder="••••••••"
          error={errors.password?.message}
          {...register("password")}
        />

        <Input
          label="비밀번호 확인"
          type="password"
          placeholder="••••••••"
          error={errors.passwordConfirm?.message}
          {...register("passwordConfirm")}
        />

        <Input
          label="닉네임"
          type="text"
          placeholder="닉네임을 입력해주세요"
          error={errors.nickname?.message}
          {...register("nickname")}
        />

        <div className="text-xs text-muted-foreground">
          <p>비밀번호 요구사항:</p>
          <ul className="ml-4 mt-1 list-disc space-y-0.5">
            <li>최소 8자 이상</li>
            <li>대문자, 소문자, 숫자, 특수문자 포함</li>
          </ul>
        </div>

        <Button type="submit" className="w-full" isLoading={isLoading}>
          회원가입
        </Button>

        <div className="text-center text-sm">
          이미 계정이 있으신가요?{" "}
          <Link to="/login" className="font-medium text-primary hover:underline">
            로그인
          </Link>
        </div>
      </form>
    </AuthLayout>
  );
};
