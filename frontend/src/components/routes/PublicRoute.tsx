import { Navigate, Outlet } from "react-router-dom";
import { useSelector } from "react-redux";
import type { RootState } from "../../store";

interface PublicRouteProps {
  redirectTo?: string;
  restricted?: boolean; // true면 인증된 사용자는 접근 불가 (로그인/회원가입 페이지)
}

export const PublicRoute = ({ redirectTo = "/", restricted = false }: PublicRouteProps) => {
  const isAuthenticated = useSelector((state: RootState) => state.auth.isAuthenticated);

  if (restricted && isAuthenticated) {
    // 인증된 사용자가 로그인/회원가입 페이지 접근 시 홈으로 리다이렉트
    return <Navigate to={redirectTo} replace />;
  }

  // 비인증 사용자 또는 제한 없는 공개 페이지는 자식 라우트 렌더링
  return <Outlet />;
};
