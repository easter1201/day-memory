import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { PageLayout } from "../components/layout/PageLayout";
import { SettingsTabs } from "../components/settings/SettingsTabs";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import { Label } from "../components/ui/Label";
import {
  useChangePasswordMutation,
  useDeleteAccountMutation,
} from "../store/services/usersApi";
import { passwordChangeSchema } from "../utils/validation";
import { logout } from "../store/slices/authSlice";
import Toast from "../components/common/Toast";
import { useConfirmDialog } from "../components/common/ConfirmDialog";

type PasswordChangeFormData = {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
};

export const AccountSettingsPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [changePassword, { isLoading: isChangingPassword }] = useChangePasswordMutation();
  const [deleteAccount, { isLoading: isDeletingAccount }] = useDeleteAccountMutation();
  const { confirm, ConfirmDialogComponent } = useConfirmDialog();

  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
  } = useForm<PasswordChangeFormData>({
    resolver: zodResolver(passwordChangeSchema),
  });

  const onSubmitPasswordChange = async (data: PasswordChangeFormData) => {
    try {
      await changePassword(data).unwrap();
      Toast.success("비밀번호가 변경되었습니다");
      reset();
    } catch (error: any) {
      console.error("Failed to change password:", error);
      if (error.status === 401) {
        Toast.error("현재 비밀번호가 일치하지 않습니다");
      } else {
        Toast.error("비밀번호 변경에 실패했습니다");
      }
    }
  };

  const handleLogout = () => {
    dispatch(logout());
    navigate("/login");
    Toast.success("로그아웃되었습니다");
  };

  const handleDeleteAccount = async () => {
    confirm({
      title: "계정 삭제",
      message: "정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없으며, 모든 데이터가 영구적으로 삭제됩니다.",
      variant: "danger",
      confirmText: "삭제",
      cancelText: "취소",
      onConfirm: async () => {
        try {
          await deleteAccount().unwrap();
          dispatch(logout());
          navigate("/login");
          Toast.success("계정이 삭제되었습니다");
        } catch (error) {
          console.error("Failed to delete account:", error);
          Toast.error("계정 삭제에 실패했습니다");
        }
      },
    });
  };

  return (
    <PageLayout>
      <div className="mx-auto max-w-4xl space-y-6">
        {/* Header */}
        <div>
          <h1 className="text-3xl font-bold">설정</h1>
          <p className="mt-1 text-muted-foreground">
            계정 정보를 관리하고 설정을 변경하세요
          </p>
        </div>

        {/* Tabs */}
        <SettingsTabs />

        {/* Password Change Form */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">비밀번호 변경</h2>

          <form onSubmit={handleSubmit(onSubmitPasswordChange)} className="space-y-4">
            <div>
              <Label htmlFor="currentPassword" required>
                현재 비밀번호
              </Label>
              <Input
                id="currentPassword"
                type="password"
                {...register("currentPassword")}
                placeholder="현재 비밀번호를 입력하세요"
              />
              {errors.currentPassword && (
                <p className="mt-1 text-sm text-red-600">
                  {errors.currentPassword.message}
                </p>
              )}
            </div>

            <div>
              <Label htmlFor="newPassword" required>
                새 비밀번호
              </Label>
              <Input
                id="newPassword"
                type="password"
                {...register("newPassword")}
                placeholder="새 비밀번호를 입력하세요"
              />
              {errors.newPassword && (
                <p className="mt-1 text-sm text-red-600">
                  {errors.newPassword.message}
                </p>
              )}
            </div>

            <div>
              <Label htmlFor="confirmPassword" required>
                새 비밀번호 확인
              </Label>
              <Input
                id="confirmPassword"
                type="password"
                {...register("confirmPassword")}
                placeholder="새 비밀번호를 다시 입력하세요"
              />
              {errors.confirmPassword && (
                <p className="mt-1 text-sm text-red-600">
                  {errors.confirmPassword.message}
                </p>
              )}
            </div>

            <div className="flex justify-end">
              <Button type="submit" disabled={isChangingPassword}>
                {isChangingPassword ? "변경 중..." : "비밀번호 변경"}
              </Button>
            </div>
          </form>
        </div>

        {/* Logout Section */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-2 text-xl font-semibold">로그아웃</h2>
          <p className="mb-4 text-sm text-muted-foreground">
            현재 세션에서 로그아웃합니다
          </p>
          <Button variant="outline" onClick={handleLogout}>
            로그아웃
          </Button>
        </div>

        {/* Delete Account Section */}
        <div className="rounded-lg border border-red-200 bg-card p-6 shadow-sm">
          <h2 className="mb-2 text-xl font-semibold text-red-600">계정 삭제</h2>
          <p className="mb-4 text-sm text-muted-foreground">
            계정을 삭제하면 모든 데이터가 영구적으로 삭제되며 복구할 수 없습니다.
            신중하게 결정해주세요.
          </p>
          <Button
            variant="outline"
            onClick={handleDeleteAccount}
            disabled={isDeletingAccount}
            className="border-red-600 text-red-600 hover:bg-red-50"
          >
            {isDeletingAccount ? "삭제 중..." : "계정 삭제"}
          </Button>
        </div>
      </div>

      {/* Confirm Dialog */}
      <ConfirmDialogComponent />
    </PageLayout>
  );
};
