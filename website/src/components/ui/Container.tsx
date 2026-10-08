import React from "react";

export interface ContainerProps extends React.HTMLAttributes<HTMLDivElement> {
  size?: "sm" | "md" | "lg" | "xl";
  children: React.ReactNode;
}

export const Container: React.FC<ContainerProps> = ({
  size = "xl",
  className = "",
  children,
  ...props
}) => {
  const sizeStyles = {
    sm: "max-w-md",
    md: "max-w-2xl",
    lg: "max-w-4xl",
    xl: "max-w-7xl",
  };

  return (
    <div
      className={`${sizeStyles[size]} mx-auto px-4 sm:px-6 lg:px-8 ${className}`}
      {...props}
    >
      {children}
    </div>
  );
};
