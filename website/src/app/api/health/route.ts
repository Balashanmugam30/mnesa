import { NextResponse } from "next/server";

export async function GET() {
  return NextResponse.json({
    status: "UP",
    service: "mnesa-website",
    version: "0.1.0",
    timestamp: new Date().toISOString(),
  });
}
