import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { PageLayout } from "../components/layout/PageLayout";
import { SettingsTabs } from "../components/settings/SettingsTabs";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import { Label } from "../components/ui/Label";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import {
  useGetProfileQuery,
  useUpdateProfileMutation,
} from "../store/services/usersApi";
import Toast from "../components/common/Toast";

const profileSchema = z.object({
  nickname: z.string().min(2, "닉네임은 최소 2자 이상이어야 합니다").max(20, "닉네임은 최대 20자까지 가능합니다"),
  profileImageUrl: z.string().url("올바른 URL 형식이 아닙니다").optional().or(z.literal("")),
});

type ProfileFormData = z.infer<typeof profileSchema>;

export const ProfileSettingsPage = () => {
  const { data: profile, isLoading } = useGetProfileQuery();
  const [updateProfile, { isLoading: isUpdating }] = useUpdateProfileMutation();
  const [imagePreview, setImagePreview] = useState<string | undefined>(undefined);

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
    values: profile
      ? {
          nickname: profile.nickname,
          profileImageUrl: profile.profileImageUrl || "",
        }
      : undefined,
  });

  const profileImageUrl = watch("profileImageUrl");

  const onSubmit = async (data: ProfileFormData) => {
    try {
      await updateProfile({
        nickname: data.nickname,
        profileImageUrl: data.profileImageUrl || undefined,
      }).unwrap();
      Toast.success("프로필이 업데이트되었습니다");
    } catch (error) {
      console.error("Failed to update profile:", error);
      Toast.error("프로필 업데이트에 실패했습니다");
    }
  };

  const handleImageUrlChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const url = e.target.value;
    if (url && url.startsWith("http")) {
      setImagePreview(url);
    } else {
      setImagePreview(undefined);
    }
  };

  if (isLoading) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <LoadingSpinner size="lg" text="프로필을 불러오는 중..." />
        </div>
      </PageLayout>
    );
  }

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

        {/* Profile Form */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">프로필 설정</h2>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            {/* Profile Image */}
            <div>
              <Label>프로필 이미지</Label>
              <div className="mt-2 flex items-center gap-4">
                <div className="h-20 w-20 overflow-hidden rounded-full bg-muted">
                  {(imagePreview || profileImageUrl || profile?.profileImageUrl) ? (
                    <img
                      src={imagePreview || profileImageUrl || profile?.profileImageUrl}
                      alt="Profile"
                      className="h-full w-full object-cover"
                      onError={(e) => {
                        (e.target as HTMLImageElement).src = "";
                        (e.target as HTMLImageElement).style.display = "none";
                      }}
                    />
                  ) : (
                    <div className="flex h-full w-full items-center justify-center text-2xl font-semibold text-muted-foreground">
                      {profile?.nickname?.charAt(0)?.toUpperCase() || "?"}
                    </div>
                  )}
                </div>
                <div className="flex-1">
                  <Input
                    {...register("profileImageUrl")}
                    onChange={(e) => {
                      register("profileImageUrl").onChange(e);
                      handleImageUrlChange(e);
                    }}
                    placeholder="이미지 URL을 입력하세요"
                  />
                  {errors.profileImageUrl && (
                    <p className="mt-1 text-sm text-red-600">
                      {errors.profileImageUrl.message}
                    </p>
                  )}
                  <p className="mt-1 text-xs text-muted-foreground">
                    이미지 URL을 입력하면 프로필 이미지로 설정됩니다
                  </p>
                </div>
              </div>
            </div>

            {/* Nickname */}
            <div>
              <Label htmlFor="nickname" required>
                닉네임
              </Label>
              <Input
                id="nickname"
                {...register("nickname")}
                placeholder="닉네임을 입력하세요"
              />
              {errors.nickname && (
                <p className="mt-1 text-sm text-red-600">{errors.nickname.message}</p>
              )}
            </div>

            {/* Email (Read-only) */}
            <div>
              <Label htmlFor="email">이메일</Label>
              <Input
                id="email"
                value={profile?.email || ""}
                readOnly
                disabled
                className="bg-muted"
              />
              <p className="mt-1 text-xs text-muted-foreground">
                이메일은 변경할 수 없습니다
              </p>
            </div>

            {/* Submit Button */}
            <div className="flex justify-end">
              <Button type="submit" disabled={isUpdating}>
                {isUpdating ? "저장 중..." : "저장"}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </PageLayout>
  );
};
