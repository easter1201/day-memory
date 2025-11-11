import * as React from "react";
import { Label } from "../ui/Label";
import { Input, type InputProps } from "../ui/Input";
import { Textarea, type TextareaProps } from "../ui/Textarea";
import { Select, type SelectProps } from "../ui/Select";
import { cn } from "../../utils/cn";

type FormFieldBaseProps = {
  label?: string;
  error?: string;
  required?: boolean;
  description?: string;
  className?: string;
};

type FormFieldInputProps = FormFieldBaseProps & {
  type: "input";
  inputProps: Omit<InputProps, "label" | "error">;
};

type FormFieldTextareaProps = FormFieldBaseProps & {
  type: "textarea";
  textareaProps: Omit<TextareaProps, "label" | "error">;
};

type FormFieldSelectProps = FormFieldBaseProps & {
  type: "select";
  selectProps: Omit<SelectProps, "label" | "error">;
};

type FormFieldProps = FormFieldInputProps | FormFieldTextareaProps | FormFieldSelectProps;

export const FormField = React.forwardRef<
  HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement,
  FormFieldProps
>(({ label, error, required, description, className, ...props }, ref) => {
  return (
    <div className={cn("w-full space-y-1.5", className)}>
      {label && <Label required={required}>{label}</Label>}
      {description && (
        <p className="text-sm text-muted-foreground">{description}</p>
      )}

      {props.type === "input" && (
        <Input
          ref={ref as React.Ref<HTMLInputElement>}
          error={error}
          {...props.inputProps}
        />
      )}

      {props.type === "textarea" && (
        <Textarea
          ref={ref as React.Ref<HTMLTextAreaElement>}
          error={error}
          {...props.textareaProps}
        />
      )}

      {props.type === "select" && (
        <Select
          ref={ref as React.Ref<HTMLSelectElement>}
          error={error}
          {...props.selectProps}
        />
      )}

      {error && <p className="text-sm text-red-500">{error}</p>}
    </div>
  );
});

FormField.displayName = "FormField";
