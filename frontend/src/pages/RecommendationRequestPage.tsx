import { useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { RecommendationForm } from "../components/recommendations/RecommendationForm";
import { useCreateRecommendationMutation } from "../store/services/recommendationsApi";
import type { RecommendationRequest } from "../types/recommendation";
import Toast from "../components/common/Toast";

export const RecommendationRequestPage = () => {
  const navigate = useNavigate();
  const [createRecommendation, { isLoading }] = useCreateRecommendationMutation();

  const handleSubmit = async (data: RecommendationRequest) => {
    try {
      const result = await createRecommendation(data).unwrap();
      Toast.success("AI 선물 추천이 완료되었습니다");
      // 추천 결과를 state로 전달하며 결과 페이지로 이동
      navigate("/recommendations/result", { state: { recommendation: result, request: data } });
    } catch (error) {
      console.error("Failed to create recommendation:", error);
      Toast.error("추천 요청에 실패했습니다. 다시 시도해주세요");
    }
  };

  const handleCancel = () => {
    navigate("/recommendations");
  };

  return (
    <PageLayout>
      <div className="mx-auto max-w-2xl">
        <div className="mb-6">
          <h1 className="text-3xl font-bold">AI 선물 추천</h1>
          <p className="mt-2 text-muted-foreground">
            AI가 이벤트와 예산에 맞는 완벽한 선물을 추천해드립니다
          </p>
        </div>

        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <RecommendationForm
            onSubmit={handleSubmit}
            onCancel={handleCancel}
            isLoading={isLoading}
          />
        </div>

        {isLoading && (
          <div className="mt-6 rounded-lg border border-blue-200 bg-blue-50 p-4">
            <div className="flex items-center space-x-3">
              <svg
                className="h-5 w-5 animate-spin text-blue-600"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                />
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                />
              </svg>
              <div>
                <p className="text-sm font-medium text-blue-900">AI가 선물을 분석하고 있습니다</p>
                <p className="text-xs text-blue-700">잠시만 기다려주세요...</p>
              </div>
            </div>
          </div>
        )}
      </div>
    </PageLayout>
  );
};
