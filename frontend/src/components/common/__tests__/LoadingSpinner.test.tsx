import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { LoadingSpinner } from "../LoadingSpinner";

describe("LoadingSpinner Component", () => {
  it("should render spinner", () => {
    const { container } = render(<LoadingSpinner />);
    const spinner = container.querySelector("svg");
    expect(spinner).toBeInTheDocument();
    expect(spinner).toHaveClass("animate-spin");
  });

  it("should render with different sizes", () => {
    const { container, rerender } = render(<LoadingSpinner size="sm" />);
    let spinner = container.querySelector("svg");
    expect(spinner).toHaveClass("h-4", "w-4");

    rerender(<LoadingSpinner size="md" />);
    spinner = container.querySelector("svg");
    expect(spinner).toHaveClass("h-8", "w-8");

    rerender(<LoadingSpinner size="lg" />);
    spinner = container.querySelector("svg");
    expect(spinner).toHaveClass("h-12", "w-12");
  });

  it("should render without text by default", () => {
    render(<LoadingSpinner />);
    const text = screen.queryByText(/.+/);
    expect(text).not.toBeInTheDocument();
  });

  it("should render with text when provided", () => {
    render(<LoadingSpinner text="Loading..." />);
    expect(screen.getByText("Loading...")).toBeInTheDocument();
  });

  it("should apply custom className", () => {
    const { container } = render(<LoadingSpinner className="custom-class" />);
    const spinner = container.querySelector("svg");
    expect(spinner).toHaveClass("custom-class");
  });

  it("should have correct default size (md)", () => {
    const { container } = render(<LoadingSpinner />);
    const spinner = container.querySelector("svg");
    expect(spinner).toHaveClass("h-8", "w-8");
  });
});
