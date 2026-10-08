import React from "react";

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  variant?: "default" | "opportunity" | "elevated";
  children: React.ReactNode;
}

export const Card: React.FC<CardProps> = ({
  variant = "default",
  className = "",
  children,
  ...props
}) => {
  const baseStyles =
    "rounded-lg p-5 transition-all border border-border-subtle dark:border-border-dark-subtle";

  const variantStyles = {
    default: "bg-surface-card dark:bg-surface-dark-card shadow-sm",
    opportunity:
      "bg-surface-card dark:bg-surface-dark-card shadow-sm hover:shadow-md hover:border-brand-primary/50 dark:hover:border-brand-primary/50",
    elevated:
      "bg-surface-elevated dark:bg-surface-dark-elevated shadow-md",
  };

  return (
    <div
      className={`${baseStyles} ${variantStyles[variant]} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};
