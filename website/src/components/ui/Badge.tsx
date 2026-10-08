import React from "react";

export type OpportunityBadgeType =
  | "INTERNSHIP"
  | "JOB"
  | "HACKATHON"
  | "SCHOLARSHIP"
  | "COMPETITION"
  | "EVENT"
  | "CONFERENCE"
  | "COURSE"
  | "OTHER";

export interface BadgeProps {
  type: OpportunityBadgeType;
  className?: string;
}

const typeStyles: Record<OpportunityBadgeType, string> = {
  INTERNSHIP: "bg-indigo-50 text-indigo-700 border-indigo-200 dark:bg-indigo-950 dark:text-indigo-300 dark:border-indigo-800",
  JOB: "bg-blue-50 text-blue-700 border-blue-200 dark:bg-blue-950 dark:text-blue-300 dark:border-blue-800",
  HACKATHON: "bg-purple-50 text-purple-700 border-purple-200 dark:bg-purple-950 dark:text-purple-300 dark:border-purple-800",
  SCHOLARSHIP: "bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950 dark:text-emerald-300 dark:border-emerald-800",
  COMPETITION: "bg-amber-50 text-amber-700 border-amber-200 dark:bg-amber-950 dark:text-amber-300 dark:border-amber-800",
  EVENT: "bg-orange-50 text-orange-700 border-orange-200 dark:bg-orange-950 dark:text-orange-300 dark:border-orange-800",
  CONFERENCE: "bg-cyan-50 text-cyan-700 border-cyan-200 dark:bg-cyan-950 dark:text-cyan-300 dark:border-cyan-800",
  COURSE: "bg-teal-50 text-teal-700 border-teal-200 dark:bg-teal-950 dark:text-teal-300 dark:border-teal-800",
  OTHER: "bg-slate-50 text-slate-700 border-slate-200 dark:bg-slate-900 dark:text-slate-300 dark:border-slate-800",
};

export const Badge: React.FC<BadgeProps> = ({ type, className = "" }) => {
  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-[11px] font-bold tracking-wide uppercase border ${typeStyles[type]} ${className}`}
    >
      {type}
    </span>
  );
};
