import { useNavigate } from "react-router-dom";
import { Button } from "../components/ui/Button";

export const NotFoundPage = () => {
  const navigate = useNavigate();

  const handleGoHome = () => {
    navigate("/");
  };

  const handleGoBack = () => {
    navigate(-1);
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-background">
      <div className="text-center">
        <h1 className="mb-4 text-9xl font-bold text-primary">404</h1>
        <h2 className="mb-2 text-3xl font-semibold">페이지를 찾을 수 없습니다</h2>
        <p className="mb-8 text-muted-foreground">
          요청하신 페이지가 존재하지 않거나 삭제되었습니다.
        </p>
        <div className="flex justify-center space-x-4">
          <Button onClick={handleGoBack} variant="outline">
            이전 페이지로
          </Button>
          <Button onClick={handleGoHome}>홈으로 가기</Button>
        </div>
      </div>
    </div>
  );
};
