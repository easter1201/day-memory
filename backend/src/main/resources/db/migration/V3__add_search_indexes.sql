-- PostgreSQL Full-Text Search 최적화
-- GIN 인덱스 및 pg_trgm 확장 추가

-- 1. pg_trgm 확장 설치 (부분 문자열 검색 최적화)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 2. GiftItem 테이블 검색 인덱스
-- 2-1. 선물 이름에 대한 트라이그램 인덱스 (부분 일치 검색)
CREATE INDEX IF NOT EXISTS idx_gift_name_trgm
ON gift_items USING GIN (name gin_trgm_ops);

-- 2-2. 선물 설명에 대한 트라이그램 인덱스
CREATE INDEX IF NOT EXISTS idx_gift_description_trgm
ON gift_items USING GIN (description gin_trgm_ops);

-- 2-3. 선물 메모에 대한 트라이그램 인덱스
CREATE INDEX IF NOT EXISTS idx_gift_memo_trgm
ON gift_items USING GIN (memo gin_trgm_ops);

-- 2-4. 복합 검색을 위한 인덱스 (name + description)
CREATE INDEX IF NOT EXISTS idx_gift_name_description_trgm
ON gift_items USING GIN ((name || ' ' || COALESCE(description, '')) gin_trgm_ops);

-- 3. RecommendedGiftItem 테이블 검색 인덱스
-- 3-1. AI 추천 선물 이름 검색
CREATE INDEX IF NOT EXISTS idx_rec_gift_name_trgm
ON recommended_gift_items USING GIN (name gin_trgm_ops);

-- 3-2. AI 추천 선물 설명 검색
CREATE INDEX IF NOT EXISTS idx_rec_gift_description_trgm
ON recommended_gift_items USING GIN (description gin_trgm_ops);

-- 4. Event 테이블 검색 인덱스 (선택적)
-- 4-1. 이벤트 제목 검색
CREATE INDEX IF NOT EXISTS idx_event_title_trgm
ON events USING GIN (title gin_trgm_ops);

-- 4-2. 받는 사람 이름 검색
CREATE INDEX IF NOT EXISTS idx_event_recipient_trgm
ON events USING GIN (recipient_name gin_trgm_ops);

-- 5. 검색 성능 확인을 위한 통계 업데이트
ANALYZE gift_items;
ANALYZE recommended_gift_items;
ANALYZE events;

-- 인덱스 생성 완료 로그
COMMENT ON INDEX idx_gift_name_trgm IS 'GIN 트라이그램 인덱스 - 선물 이름 부분 검색 최적화';
COMMENT ON INDEX idx_gift_description_trgm IS 'GIN 트라이그램 인덱스 - 선물 설명 부분 검색 최적화';
