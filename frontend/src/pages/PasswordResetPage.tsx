import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";
import { AuthLayout } from "../components/layout/AuthLayout";
import { Input } from "../components/ui/Input";
import { Button } from "../components/ui/Button";
import { usePasswordResetMutation } from "../store/services/authApi";

const passwordResetSchema = z.object({
  email: z.string().email("유효한 이메일을 입력해주세요"),
});

type PasswordResetFormData = z.infer<typeof passwordResetSchema>;

export const PasswordResetPage = () => {
  const [passwordReset, { isLoading }] = usePasswordResetMutation();
  const [isSubmitted, setIsSubmitted] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<PasswordResetFormData>({
    resolver: zodResolver(passwordResetSchema),
  });

  const onSubmit = async (data: PasswordResetFormData) => {
    try {
      await passwordReset(data).unwrap();
      setIsSubmitted(true);
      toast.success("비밀번호 재설정 이메일이 발송되었습니다.");
    } catch (error: any) {
      const message =
        error?.data?.message ||
        "이메일 발송에 실패했습니다. 다시 시도해주세요.";
      toast.error(message);
    }
  };

  if (isSubmitted) {
    return (
      <AuthLayout
        title="이메일 확인"
        subtitle="비밀번호 재설정 안내 메일을 발송했습니다"
      >
        <div className="space-y-4 text-center">
          <div className="rounded-lg bg-green-50 p-4 text-green-800">
            <p className="font-medium">이메일이 발송되었습니다!</p>
            <p className="mt-2 text-sm">
              입력하신 이메일 주소로 비밀번호 재설정 링크를 보냈습니다.
              이메일을 확인하여 비밀번호를 재설정해주세요.
            </p>
          </div>

          <div className="space-y-2 text-sm text-muted-foreground">
            <p>이메일이 도착하지 않았나요?</p>
            <ul className="ml-4 list-disc space-y-1 text-left">
              <li>스팸 메일함을 확인해주세요</li>
              <li>이메일 주소가 정확한지 확인해주세요</li>
              <li>몇 분 후 다시 시도해주세요</li>
            </ul>
          </div>

          <div className="pt-4">
            <Link to="/login">
              <Button variant="outline" className="w-full">
                로그인 페이지로 돌아가기
              </Button>
            </Link>
          </div>
        </div>
      </AuthLayout>
    );
  }

  return (
    <AuthLayout
      title="비밀번호 찾기"
      subtitle="가입하신 이메일 주소를 입력해주세요"
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <Input
          label="이메일"
          type="email"
          placeholder="email@example.com"
          error={errors.email?.message}
          {...register("email")}
        />

        <div className="text-sm text-muted-foreground">
          <p>
            입력하신 이메일 주소로 비밀번호 재설정 링크를 보내드립니다.
            이메일을 확인하여 비밀번호를 재설정해주세요.
          </p>
        </div>

        <Button type="submit" className="w-full" isLoading={isLoading}>
          재설정 링크 발송
        </Button>

        <div className="text-center text-sm">
          비밀번호가 기억나셨나요?{" "}
          <Link to="/login" className="font-medium text-primary hover:underline">
            로그인
          </Link>
        </div>
      </form>
    </AuthLayout>
  );
};
