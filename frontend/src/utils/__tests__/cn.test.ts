import { describe, it, expect } from "vitest";
import { cn } from "../cn";

describe("cn utility", () => {
  it("should merge class names", () => {
    expect(cn("class1", "class2")).toBe("class1 class2");
  });

  it("should handle conditional classes", () => {
    const isActive = true;
    expect(cn("base", isActive && "active")).toBe("base active");
  });

  it("should merge tailwind classes correctly", () => {
    // Tailwind classes with same property should be merged
    expect(cn("px-2 py-1", "px-4")).toBe("py-1 px-4");
  });

  it("should handle undefined and null values", () => {
    expect(cn("class1", undefined, "class2", null)).toBe("class1 class2");
  });

  it("should handle arrays", () => {
    expect(cn(["class1", "class2"])).toBe("class1 class2");
  });

  it("should handle objects", () => {
    expect(cn({ class1: true, class2: false, class3: true })).toBe("class1 class3");
  });
});
