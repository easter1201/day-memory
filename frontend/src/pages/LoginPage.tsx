import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Link, useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import { AuthLayout } from "../components/layout/AuthLayout";
import { Input } from "../components/ui/Input";
import { Button } from "../components/ui/Button";
import { useLoginMutation } from "../store/services/authApi";
import { useDispatch } from "react-redux";
import { setCredentials } from "../store/slices/authSlice";

const loginSchema = z.object({
  email: z.string().email("유효한 이메일을 입력해주세요"),
  password: z.string().min(6, "비밀번호는 최소 6자 이상이어야 합니다"),
});

type LoginFormData = z.infer<typeof loginSchema>;

export const LoginPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [login, { isLoading }] = useLoginMutation();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    try {
      const response = await login(data).unwrap();
      dispatch(setCredentials(response));
      toast.success("로그인에 성공했습니다!");
      navigate("/dashboard");
    } catch (error: any) {
      const message =
        error?.data?.message || "로그인에 실패했습니다. 다시 시도해주세요.";
      toast.error(message);
    }
  };

  return (
    <AuthLayout title="로그인" subtitle="Day Memory에 오신 것을 환영합니다">
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

        <div className="flex items-center justify-between">
          <Link
            to="/password-reset"
            className="text-sm text-primary hover:underline"
          >
            비밀번호 찾기
          </Link>
        </div>

        <Button type="submit" className="w-full" isLoading={isLoading}>
          로그인
        </Button>

        <div className="text-center text-sm">
          계정이 없으신가요?{" "}
          <Link to="/signup" className="font-medium text-primary hover:underline">
            회원가입
          </Link>
        </div>
      </form>
    </AuthLayout>
  );
};
