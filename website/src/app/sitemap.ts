import type { MetadataRoute } from "next";

export default function sitemap(): MetadataRoute.Sitemap {
  const baseUrl = "https://mnesa.ai";
  const routes = [
    "",
    "/how-it-works",
    "/features",
    "/ai",
    "/demo",
    "/security",
    "/pricing",
    "/faq",
    "/download",
    "/about",
    "/contact",
    "/privacy",
    "/terms",
    "/data-deletion",
    "/maintenance",
    "/offline",
    "/unavailable",
  ];

  return routes.map((route) => ({
    url: `${baseUrl}${route}`,
    lastModified: new Date(),
    changeFrequency: route === "" ? "daily" : "weekly",
    priority: route === "" ? 1.0 : route === "/demo" || route === "/download" ? 0.9 : 0.7,
  }));
}
